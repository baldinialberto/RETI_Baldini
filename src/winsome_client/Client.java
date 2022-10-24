package winsome_client;

import winsome_communication.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to represent the client
 * uses a TCP connection to communicate with the server
 * uses an RMI connection to receive updates from the server
 *  and to register a new user
 * listens at a Multicast address to receive updates from the server
 */
public class Client {
	// Member variables
	SocketChannel socket_channel;
	WinsomeServerSender sender;
	private final ClientCLI c_interface;
	private final ClientProperties properties;
	private ServerRMI_Interface server_rmi_interface;
	private ClientRMI_Imp client_rmi;
	private ClientRMI_Interface client_rmi_stub;
	private LocalUser user;
	private boolean _on = false;
	private boolean connected = false;
	private boolean logged = false;
	private boolean rewards_updated = false;
	private int multicast_port;
	private String multicast_address;
	private String multicast_network_name;

	private ClientNotificationThread notification_thread;

	// Constructors

	/**
	 * Default constructor
	 */
	public Client(String properties_filepath) {
		/*
		 * client constructor
		 *
		 * 1. load server properties
		 * 2. create client interface
		 * 3. connect to server's RMI
		 * 4. create client's RMI
		 * 5. create client's notification thread and start it
		 */

		// 1. load server properties
		properties = new ClientProperties(properties_filepath);

		// 2. create client interface
		c_interface = new ClientCLI(this);

		// 3. connect to server's  RMI
		try {
			Registry r = LocateRegistry.getRegistry(properties.get_server_address(), properties.get_registry_port());
			server_rmi_interface = (ServerRMI_Interface) r.lookup(
					"rmi://" + properties.get_server_address() + "/" + properties.get_rmi_name()
			);

			// 4. create client's RMI
			client_rmi = new ClientRMI_Imp(this);
			client_rmi_stub = (ClientRMI_Interface) UnicastRemoteObject.exportObject(client_rmi, 0);
			_on = true;
		} catch (Exception e) {
			System.err.println("Client exception: " + e.getMessage());
		}
	}

	// Methods

	/**
	 * This method is used to retrieve the client's current logged user
	 */
	public String get_username() {
		return user.get_username();
	}

	/**
	 * This method is used to get the current status of the client
	 * @return true if the client is alive, false otherwise
	 */
	public boolean is_on() {
		// DEBUG
		return _on;
	}

	/**
	 * This method start the Command Line Interface
	 */
	public void start_CLI() {
		c_interface.exec();
	}

	/**
	 * This method is used to notify the client that the server has sent a multicast message
	 */
	public void new_rewards_available() {
		rewards_updated = true;
	}

	/**
	 * This method is used to remove any pending notifications
	 */
	public void rewards_read() {
		rewards_updated = false;
	}

	/**
	 * This method is used to check if there are any pending notifications
	 * @return true if there are pending notifications, false otherwise
	 */
	public boolean isRewards_updated() {
		return rewards_updated;
	}

	/**
	 * This method is used to connect to the server
	 * @throws IOException if the connection fails
	 */
	private void connect() throws IOException {
		/*
		 * connect to server
		 *
		 */
		if (connected) {
			return;
		}

		try {
			socket_channel = SocketChannel.open(
					new InetSocketAddress(properties.get_server_address(), properties.get_tcp_port()));
			sender = new WinsomeServerSender(socket_channel);
			connected = true;
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	/**
	 * This method is used to disconnect from the server
	 * @throws IOException if the disconnection fails
	 */
	private void disconnect() throws IOException {
		/*
		 * disconnect from server
		 *
		 * 1. Close the TCP socket
		 * 2. Close the UDP socket
		 * 3. Set _connected to false
		 */

		if (!connected)
			return;

		try {
			sender.disconnect();
		} catch (WinsomeException e) {
			System.err.println(e.niceMessage());
		}

		connected = false;
		user = null;
		socket_channel.close();

		this.stop_notification_thread();
	}

	/**
	 * Per inserire un nuovo utente, il server mette a disposizione una operazione
	 * di registrazione di un utente. L’utente deve fornire username, password e una
	 * lista di tag (massimo 5 tag). Il server risponde con un codice che può
	 * indicare l’avvenuta registrazione, oppure, se lo username è già presente, o
	 * se la password è vuota, restituisce un messaggio d’errore. Lo username
	 * dell’utente deve essere univoco. Come specificato in seguito, le
	 * registrazioni sono tra le informazioni da persistere lato server. Il comando
	 * da digitare per la registrazione ha la sintassi seguente: register <username>
	 * <password> <tags>, dove <tags> è una lista di tag separati da uno spazio.
	 * Per semplicità i tag devono essere salvati in caratteri minuscoli. L’utente
	 * deve poter scegliere liberamente la lista (il server non gestisce quindi una
	 * lista predefinita di tag).
	 *
	 * @param username the username of the user to register
	 * @param password the password of the user to register
	 * @param tags     the interests of the user to register
	 * @throws RemoteException if the server is not reachable
	 */
	public void register(String username, String password, List<String> tags)
			throws RemoteException {
		/*
		 * register a new user
		 *
		 * 1. Call RMI register method
		 * 2. Print the result
		 */

		// 1. Call RMI register method

		String[] tags_array = new String[tags.size()];
		tags_array = tags.toArray(tags_array);
		// 2. Print the result
		System.out.println(server_rmi_interface.register_user(username, password, tags_array));
	}

	/**
	 * Login di un utente già registrato per accedere al servizio. Il server
	 * risponde con un codice che può indicare l’avvenuto login, oppure, se
	 * l’utente ha già effettuato la login o la password è errata, restituisce un
	 * messaggio d’errore. Il comando corrispondente è login <username> <password>.
	 *
	 * @param username the username of the user to login
	 * @param password the password of the user to login
	 * @throws WinsomeException if the operation is not successful
	 */
	public void login(String username, String password)
			throws WinsomeException {
		/*
		 * login to server
		 *
		 * 1. if already logged, print error and return
		 * 2. connect to server
		 * 3. login with username and password
		 * 4. check for exceptions
		 */

		// 1. if already logged, print error and return
		if (logged) {
			throw new WinsomeExceptionGeneric("You are already logged in");
		}

		// 2. connect to server
		try {
			connect();
		} catch (IOException e) {
			throw new WinsomeExceptionGeneric("Cannot connect to server");
		}

		// 3. login with username and password
		try {
			sender.login(username, password);
			user = new LocalUser(username);
			server_rmi_interface.receive_updates(client_rmi_stub, username);
			start_notification_thread();
			logged = true;
			rewards_read();
		} catch (RemoteException e) {
			throw new WinsomeExceptionGeneric("Cannot connect to server");
		}
	}

	/**
	 * Effettua il logout dell’utente dal servizio. Corrisponde al comando logout.
	 * @throws WinsomeException if the operation is not successful
	 */
	public void logout()
			throws WinsomeException {
		/*
		 * logout from server
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. logout
		 * 4. disconnect from server
		 * 5. set logged to false
		 * 6. set user to null
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}

		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. logout
		sender.logout();

		// 4. disconnect from server
		try {
			this.disconnect();
			// 5. set logged to false
			logged = false;
			// 6. set user to null
			user = null;
			rewards_read();
		} catch (IOException e) {
			throw new WinsomeExceptionGeneric("Cannot disconnect from server");
		}
	}

	/**
	 * Utilizzata da un utente per visualizzare la lista (parziale) degli utenti
	 * registrati al servizio. Il server restituisce la lista di utenti che hanno
	 * almeno un tag in comune con l’utente che ha fatto la richiesta. Il comando
	 * associato è list users.
	 *
	 * @return the list of users with at least one tag in common with the user
	 * if the operation is successful, null otherwise
	 * @throws WinsomeException if the operation is not successful
	 */
	public List<String> listUsers()
			throws WinsomeException {
		/*
		 * list users
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. get list of users
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}

		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. get list of users
		List<String> users;
		users = Arrays.asList(sender.list_users());

		return users;
	}

	/**
	 * Operazione lato client per visualizzare la lista dei propri follower. Questo
	 * comando dell’utente non scatena una richiesta sincrona dal client al server.
	 * Il client restituisce la lista dei follower mantenuta localmente che viene
	 * via via aggiornata grazie a notifiche "asincrone" ricevute dal server. Vedere
	 * i dettagli di implementazione nella sezione successiva. Corrisponde al
	 * comando list followers.
	 *
	 * @return the list of followers if the user is logged, null otherwise
	 * @throws WinsomeException if the operation is not successful
	 */
	public List<String> listFollowers()
			throws WinsomeException {
		/*
		 * list followers
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. return list of followers
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. Return the list of followers
		return user.get_followers();
	}

	/**
	 * Utilizzata da un utente per visualizzare la lista degli utenti di cui è
	 * follower. Questo metodo è corrispondente al comando list following.
	 *
	 * @return the list of following if the operation is successful, null otherwise
	 * @throws WinsomeException if the operation is not successful
	 */
	public List<String> listFollowing()
			throws WinsomeException {
		/*
		 * list following
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. return list of following
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. Return the list of following
		return Arrays.asList(sender.list_following());
	}

	/**
	 * L’utente chiede di seguire l’utente che ha per username idUser. Da quel
	 * momento in poi può ricevere tutti i post pubblicati da idUser. Il comando
	 * associato è follow <username>.
	 *
	 * @param idUser the id of the user to follow
	 * @throws WinsomeException if the operation is not successful
	 */
	public void followUser(String idUser)
			throws WinsomeException {
		/*
		 * follow user
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. follow user
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. follow user
		sender.follow(idUser);
	}

	/**
	 * L’utente chiede di non seguire più l’utente che ha per username idUser. Il
	 * comando associato è unfollow <username>.
	 *
	 * @param idUser the id of the user to unfollow
	 * @throws WinsomeException if the operation is not successful
	 */
	public void unfollowUser(String idUser)
			throws WinsomeException {
		/*
		 * unfollow user
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. unfollow user
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. unfollow user
		sender.unfollow(idUser);
	}

	/**
	 * operazione per recuperare la lista dei post di cui l’utente è autore. Viene
	 * restituita una lista dei post presenti nel blog dell’utente. Per ogni post
	 * viene fornito id del post, autore e titolo. Viene attivata con il comando
	 * blog.
	 *
	 * @return the list of posts of the user's blog if successful, null otherwise
	 * @throws WinsomeException if the operation is not successful
	 */
	public List<PostReprSimple> viewBlog()
			throws WinsomeException {
		/*
		 * view blog
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. return list of posts
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. Return the list of posts
		return Arrays.asList(sender.blog());
	}

	/**
	 * Operazione per pubblicare un nuovo post. L’utente deve fornire titolo e
	 * contenuto del post. Il titolo ha lunghezza massima di 20 caratteri e il
	 * contenuto una lunghezza massima di 500 caratteri. Se l’operazione va a buon
	 * fine, il post è creato e disponibile per i follower dell’autore del post.
	 * Il sistema assegna un identificatore univoco a ciascun post creato (idPost).
	 * Il comando che l’utente digita per creare un post ha la seguente sintassi:
	 * post <title> <content>.
	 *
	 * @param titolo    the title of the post
	 * @param contenuto the content of the post
	 * @throws WinsomeException if the operation is not successful
	 */
	public void createPost(String titolo, String contenuto)
			throws WinsomeException {
		/*
		 * create post
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. create post
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. create post
		sender.create_post(titolo, contenuto);
	}

	/**
	 * Operazione per recuperare la lista dei post nel proprio feed. Viene
	 * restituita una lista dei post. Per ogni post viene fornito id, autore e
	 * titolo del post. La funzione viene attivata mediante il comando show feed.
	 *
	 * @return the list of posts in the user's feed if successful, null otherwise
	 * @throws WinsomeException if the operation is not successful
	 */
	public List<PostReprSimple> showFeed()
			throws WinsomeException {
		/*
		 * show feed
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. return list of posts
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. Return the list of posts
		return Arrays.asList(sender.feed());
	}

	/**
	 * Il server restituisce titolo, contenuto, numero di voti positivi, numero di
	 * voti negativi e commenti del post. Se l’utente è autore del post può
	 * cancellare il post con tutto il suo contenuto associato (commenti e voti). Se
	 * l’utente ha il post nel proprio feed può esprimere un voto, positivo o
	 * negativo (solo un voto, successivi tentativi di voto non saranno accettati
	 * dal server, che restituirà un messaggio di errore) e/o inserire un commento.
	 * Il comando digitato dall’utente è show post <id>.
	 *
	 * @param idPost the id of the post
	 * @return the post representation if successful, null otherwise
	 * @throws WinsomeException if the operation is not successful
	 */
	public PostReprDetailed showPost(String idPost)
			throws WinsomeException {
		/*
		 * show post
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. return post
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. Return the post
		return sender.show_post(Integer.parseInt(idPost));
	}

	/**
	 * Operazione per cancellare un post. La richiesta viene accettata ed eseguita
	 * solo se l’utente è l’autore del post. Il server cancella il post con tutto
	 * il suo contenuto associato (commenti e voti). Non vengono calcolate
	 * ricompense "parziali", ovvero se un contenuto recente (post, voto o commento)
	 * non era stato conteggiato nel calcolo delle ricompense perche' ancora il
	 * periodo non era scaduto, non viene considerato nel calcolo delle ricompense.
	 * Il comando corrispondente alla cancellazione è delete <idPost>.
	 *
	 * @param idPost the id of the post to delete
	 * @throws WinsomeException if the operation is not successful
	 */
	public void deletePost(String idPost)
			throws WinsomeException {
		/*
		 * delete post
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. delete post
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. delete post
		sender.delete_post(Integer.parseInt(idPost));
	}

	/**
	 * operazione per effettuare il rewin di un post, ovvero per pubblicare nel
	 * proprio blog un post presente nel proprio feed. La funzione viene attivata
	 * mediante il comando rewin <idPost>.
	 *
	 * @param idPost the id of the post to rewin
	 * @throws WinsomeException if the operation is not successful
	 */
	public void rewinPost(String idPost)
			throws WinsomeException {
		/*
		 * rewin post
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. rewin post
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. rewin post
		sender.rewin_post(Integer.parseInt(idPost));
	}

	/**
	 * Operazione per assegnare un voto positivo o negativo ad un post. Se l’utente
	 * ha il post nel proprio feed e non ha ancora espresso un voto, il voto viene
	 * accettato, negli altri casi (ad es. ha già votato il post, non ha il post
	 * nel proprio feed, e' l’autore del post) il voto non viene accettato e il
	 * server restituisce un messaggio di errore. Il comando per assegnare un voto
	 * al post è rate <idPost> <vote>. Nel comando i voti sono così codificati:
	 * voto positivo +1, voto negativo -1.
	 *
	 * @param idPost the id of the post to rate
	 * @param voto   the rate to assign
	 * @throws WinsomeException if the operation is not successful
	 */
	public void ratePost(String idPost, boolean voto)
			throws WinsomeException {
		/*
		 * rate post
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. rate post
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. rate post
		sender.rate_post(Integer.parseInt(idPost), voto);
	}

	/**
	 * Operazione per aggiungere un commento ad un post. Se l’utente ha il post nel
	 * proprio feed, il commento viene accettato, negli altri casi (ad es. l’utente
	 * non ha il post nel proprio feed oppure è l’autore del post) il commento non
	 * viene accettato e il server restituisce un messaggio di errore. Un utente
	 * può aggiungere più di un commento ad un post. La sintassi utilizzata dagli
	 * utenti per commentare è: comment <idPost> <comment>.
	 *
	 * @param idPost  the id of the post to comment
	 * @param comment the comment to add
	 * @throws WinsomeException if the operation is not successful
	 */
	public void addComment(String idPost, String comment)
			throws WinsomeException {
		/*
		 * add comment
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. add comment
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. add comment
		sender.comment_post(Integer.parseInt(idPost), comment);
	}

	/**
	 * Operazione per recuperare il valore del proprio portafoglio. Il server
	 * restituisce il totale e la storia delle transazioni (ad es. <incremento>
	 * <timestamp>). Il comando corrispondente è wallet.
	 *
	 * @return the Wallet_representation of the user's wallet if the operation is
	 * successful, null otherwise
	 * @throws WinsomeException if the operation is not successful
	 */
	public WalletRepr getWallet()
			throws WinsomeException {
		/*
		 * get wallet
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. get wallet
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. get wallet
		WalletRepr wallet = sender.wallet();
		this.rewards_read();
		return wallet;
	}

	/**
	 * Operazione per recuperare il valore del proprio portafoglio convertito in
	 * bitcoin. Il server utilizza il servizio di generazione di valori random
	 * decimali fornito da RANDOM.ORG per ottenere un tasso di cambio casuale e
	 * quindi calcola la conversione. L’operazione è eseguita mediante il comando:
	 * wallet btc.
	 *
	 * @return the value of the user's wallet in bitcoin if successful, -1 otherwise
	 * @throws WinsomeException if the operation is not successful
	 */
	public double getWalletInBitcoin()
			throws WinsomeException {
		/*
		 * get wallet in bitcoin
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. get wallet in bitcoin
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			throw new WinsomeExceptionGeneric("You are not logged in");
		}
		// 2. if not connected, print error and return
		if (!connected) {
			throw new WinsomeExceptionGeneric("You are not connected to server");
		}

		// 3. get wallet in bitcoin
		double wallet_btc = sender.wallet_btc();
		this.rewards_read();
		return wallet_btc;
	}

	/**
	 * This method is used to exit the application.
	 */
	public void exit() {

		try {
			UnicastRemoteObject.unexportObject(client_rmi, true);
		} catch (NoSuchObjectException ignored) {
		}

		System.out.flush();

		try {
			logout();
		} catch (WinsomeException ignored) {
		}
		_on = false;
	}

	/**
	 * This method is usually called by the server to notify the client that a new
	 * follower has been added.
	 *
	 * @param username the username of the new follower
	 */
	public void add_follower(String username) {
		/*
		 * this method is used to add a follower to the local user
		 *
		 * 1. add the follower to the local user if it is not already present
		 * 2. return the result
		 */

		if (username == null) return;

		if (user.get_followers().contains(username)) {
		} else {
			user.add_follower(username);
		}
	}

	/**
	 * This method is usually called by the server when the client logs into
	 *
	 * @param followers the list of followers of the user
	 */
	public void addAll_followers(String[] followers) {
		/*
		 * this method is used to add a list of followers to the local user
		 *
		 * 1. add the followers to the local user if they are not already present
		 * 2. return the result
		 */

		if (followers == null) return;

		for (String follower : followers) {
			if (!user.get_followers().contains(follower)) {
				user.add_follower(follower);
			}
		}

	}

	/**
	 * This method is usually called by the server to set the multicast address, port and group
	 *
	 * @param ip           the multicast address
	 * @param port         the multicast port
	 * @param network_name the multicast group
	 */
	public void set_multicast(String ip, int port, String network_name) {
		/*
		 * this method is used to set the multicast ip, port and network name
		 *
		 * 1. set the multicast ip, port and network name
		 * 2. return the result
		 */

		if (ip == null || port < 0 || network_name == null) return;

		this.multicast_address = ip;
		this.multicast_port = port;
		this.multicast_network_name = network_name;
	}

	/**
	 * This method is usually called by the server to notify the client that a
	 * user has been removed from the list of followers
	 *
	 * @param username the username of the removed follower
	 */
	public void remove_follower(String username) {
		/*
		 * this method is used to remove a follower from the local user
		 *
		 * 1. remove the follower from the local user if it is present
		 * 2. return the result
		 */

		if (username == null) return;

		if (user.get_followers().contains(username)) {
			user.remove_follower(username);
		} else {
		}
	}

	/**
	 * This method returns the multicast port
	 * @return the multicast port
	 */
	public int getMulticast_port() {
		return multicast_port;
	}

	/**
	 * This method returns the multicast group
	 * @return the name of the multicast group
	 */
	public String getNetwork_interface() {
		return multicast_network_name;
	}

	/**
	 * This method returns the multicast address
	 * @return the multicast address
	 */
	public InetAddress getMulticast_address() {
		try {
			return InetAddress.getByName(multicast_address);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method is used to start the Notification Thread
	 */
	public void start_notification_thread() {
		/*
		 * this method is used to start the reward thread
		 *
		 * 1. start the reward thread
		 */

		// 1. start the reward thread
		notification_thread = new ClientNotificationThread(this);
		notification_thread.start();
	}

	/**
	 * This method is used to stop the Notification Thread
	 */
	public void stop_notification_thread() {
		/*
		 * this method is used to stop the reward thread
		 *
		 * 1. stop the reward thread
		 */

		// 1. stop the reward thread
		notification_thread.interrupt();
	}
}
