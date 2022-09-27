package winsome_client;

import winsome_comunication.Win_message;
import winsome_server.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Client {
	private ClientInterface c_interface;
	private Client_properties properties;
	private Socket socket;

	SocketChannel socket_channel;
	private RMI_registration_int remote_registration_result;
	private LocalUser user;
	private boolean _on = false;
	private boolean connected = false;
	private boolean logged = false;

	public Client(String properties_filepath) {
		/*
		 * client constructor
		 *
		 * 1. load server properties
		 * 2. create client interface
		 * 3. connect to server's RMI
		 */

		// 1. load server properties
		properties = new Client_properties("client_config.txt");

		// 2. create client interface
		c_interface = new ClientInterface(this);

		// 3. connect to server's RMI
		try {
			Registry r = LocateRegistry.getRegistry(properties.get_registry_port());
			remote_registration_result = (RMI_registration_int) r.lookup(properties.get_rmi_name());
			_on = true;
		}
		catch (Exception e) {
			System.err.println("Client exception: " + e);
		}
	}

	public String get_username() {
		return user.get_username();
	}

	public boolean isLogged() {
		return logged;
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean is_on() {
		return _on;
	}

	public void start_CLI() {
		c_interface.exec();
	}

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
			connected = true;
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

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

		Win_message message = new Win_message();
		message.addString(Win_message.EXIT);
		message.send(socket_channel);

		connected = false;
		user = null;
		socket_channel.close();
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
	 * @param username
	 * @param password
	 * @param tags
	 * @throws Winsome_exceptions.UsernameAlreadyExists
	 */
	public void register(String username, String password, List<String> tags)
			throws Winsome_exceptions.UsernameAlreadyExists, RemoteException {
		/*
		 * register a new user
		 *
		 * 1. Call RMI register method
		 * 2. Print the result
		 */

		// 1. Call RMI register method

		String[] tags_array = new String[tags.size()];
		tags_array = tags.toArray(tags_array);
		int result = remote_registration_result.registerUser(username, password, tags_array);

		// 2. Print the result
		if (result == 0)
			System.out.println("Registration successful");
		else if (result == 1)
			System.out.println("Username already exists");
		else if (result == 2)
			System.out.println("Password is empty");
		else
			System.out.println("Unknown error");
	}

	/**
	 * Login di un utente già registrato per accedere al servizio. Il server
	 * risponde con un codice che può indicare l’avvenuto login, oppure, se
	 * l’utente ha già effettuato la login o la password è errata, restituisce un
	 * messaggio d’errore. Il comando corrispondente è login <username> <password>.
	 *
	 * @param username
	 * @param password
	 * @return
	 * @throws Winsome_exceptions.WrongPassword
	 */
	public int login(String username, String password)
			throws Winsome_exceptions.WrongPassword {
		/*
		 * login to server
		 *
		 * 1. Connect to server
		 * 2. Send login request to server
		 * 3. Receive login response from server
		 * 4. If login is successful, set _logged to true
		 * 5. Return the result
		 */

		System.out.println("login");


		try {
			// 1. Connect to server
			if (!connected) connect();

			Win_message login_request = new Win_message();

			login_request.addString("login");
			login_request.addString(username);
			login_request.addString(password);


			// 2. Send login request to server
			login_request.send(socket_channel);

			// 3. Receive login response from server of unknown size
			Win_message login_response = Win_message.receive(socket_channel);

			// DEBUG
			System.out.println("response: " + login_response);

			// 4. If login is successful, set _logged to true
			if (login_response.getString(0).equals(Win_message.SUCCESS))
			{
				logged = true;
				this.user = new LocalUser(username);
				return 0;
			} else if (login_response.getString(0).equals(Win_message.ERROR))
			{
				logged = false;
				this.user = null;
				System.out.println("Login failed : " + login_response.getString(1));
				return -1;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/**
	 * Effettua il logout dell’utente dal servizio. Corrisponde al comando logout.
	 *
	 * @throws NullPointerException
	 */
	public int logout()
			throws NullPointerException {
		/*
		 * logout from server
		 *
		 * 1. Send logout request to server
		 * 2. Receive logout response from server
		 * 3. If logout is successful, set _logged to false
		 * 4. Disconnect from server
		 * 5. Set user to null
		 * 6. set _connected to false
		 */

		System.out.println("logout");

		if (!connected)
		{
			System.out.println("Not connected");
			return -1;
		}
		if (!logged) {
			System.out.println("Not logged in");
			return -1;
		}

		try {
			// 1. Send logout request to server
			Win_message logout_request = new Win_message();
			logout_request.addString("logout");
			logout_request.send(socket_channel);

			// 2. Receive logout response from server
			Win_message logout_response = Win_message.receive(socket_channel);

			// 3. If logout is successful, set _logged to false
			if (!logout_response.getString(0).equals(Win_message.SUCCESS)) {
				System.out.println("Logout failed : " + logout_response.getString(1));
				return -1;
			}

			logged = false;

			//4. Disconnect from server
			disconnect();

			return 0;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	/**
	 * Utilizzata da un utente per visualizzare la lista (parziale) degli utenti
	 * registrati al servizio. Il server restituisce la lista di utenti che hanno
	 * almeno un tag in comune con l’utente che ha fatto la richiesta. Il comando
	 * associato è list users.
	 *
	 * @return
	 * @throws NullPointerException
	 */
	public List<String> listUsers()
			throws NullPointerException {

		/*
		 * list users
		 *
		 * 1. Send list users request to server
		 * 2. Receive list users response from server
		 * 3. Return the list of users
		 */

		System.out.println("list users");

		if (!connected) {
			System.out.println("Not connected");
			return null;
		}
		if (!logged) {
			System.out.println("Not logged");
			return null;
		}

		try {
			// 1. Send list users request to server
			Win_message list_users_request = new Win_message();
			list_users_request.addString("list_users");
			list_users_request.send(socket_channel);

			// 2. Receive list users response from server
			Win_message list_users_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (list_users_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("List users failed : " + list_users_response.getString(1));
				return null;
			} else if (list_users_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. Return the list of users
				return list_users_response.getStrings().subList(1, list_users_response.getStrings().size());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Operazione lato client per visualizzare la lista dei propri follower. Questo
	 * comando dell’utente non scatena una richiesta sincrona dal client al server.
	 * Il client restituisce la lista dei follower mantenuta localmente che viene
	 * via via aggiornata grazie a notifiche “asincrone” ricevute dal server. Vedere
	 * i dettagli di implementazione nella sezione successiva. Corrisponde al
	 * comando list followers.
	 *
	 * @return
	 * @throws NullPointerException
	 */
	public List<String> listFollowers()
			throws NullPointerException {
		return null;
	}

	/**
	 * Utilizzata da un utente per visualizzare la lista degli utenti di cui è
	 * follower. Questo metodo è corrispondente al comando list following.
	 *
	 * @return
	 * @throws NullPointerException
	 */
	public List<String> listFollowing()
			throws NullPointerException {
		return null;
	}

	/**
	 * L’utente chiede di seguire l’utente che ha per username idUser. Da quel
	 * momento in poi può ricevere tutti i post pubblicati da idUser. Il comando
	 * associato è follow <username>.
	 *
	 * @param idUser
	 * @return
	 */
	public boolean followUser(String idUser) {
		/*
		 * follow user <idUser> if not already following
		 *
		 * 1. Send follow user request to server
		 * 2. Receive follow user response from server
		 * 3. If follow user is successful, add idUser to following list
		 * 4. Return true
		 */

		if (!connected) {
			System.out.println("Not connected");
			return false;
		}
		if (!logged) {
			System.out.println("Not logged");
			return false;
		}

		try {
			// 1. Send follow user request to server
			Win_message follow_user_request = new Win_message();
			follow_user_request.addString("follow");
			follow_user_request.addString(idUser);
			follow_user_request.send(socket_channel);

			// 2. Receive follow user response from server
			Win_message follow_user_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (follow_user_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Follow user failed : " + follow_user_response.getString(1));
				return false;
			} else if (follow_user_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If follow user is successful, add idUser to following list
				user.add_following(idUser);
				// 4. Return true
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * L’utente chiede di non seguire più l’utente che ha per username idUser. Il
	 * comando associato è unfollow <username>.
	 *
	 * @param idUser
	 * @return
	 */
	public boolean unfollowUser(String idUser) {
		/*
		 * unfollow user <idUser> if following
		 *
		 * 1. Send unfollow user request to server
		 * 2. Receive unfollow user response from server
		 * 3. If unfollow user is successful, remove idUser from following list
		 * 4. Return true
		 */

		if (!connected) {
			System.out.println("Not connected");
			return false;
		}
		if (!logged) {
			System.out.println("Not logged");
			return false;
		}

		try {
			// 1. Send unfollow user request to server
			Win_message unfollow_user_request = new Win_message();
			unfollow_user_request.addString("unfollow");
			unfollow_user_request.addString(idUser);
			unfollow_user_request.send(socket_channel);

			// 2. Receive unfollow user response from server
			Win_message unfollow_user_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (unfollow_user_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Unfollow user failed : " + unfollow_user_response.getString(1));
				return false;
			} else if (unfollow_user_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If unfollow user is successful, remove idUser from following list
				user.remove_following(idUser);
				// 4. Return true
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * operazione per recuperare la lista dei post di cui l’utente è autore. Viene
	 * restituita una lista dei post presenti nel blog dell’utente. Per ogni post
	 * viene fornito id del post, autore e titolo. Viene attivata con il comando
	 * blog.
	 *
	 * @return
	 */
	public List<Post> viewBlog() {
		return null;
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
	 * @param titolo
	 * @param contenuto
	 * @return
	 */
    public Post createPost(String titolo, String contenuto) {
        return null;
    }

	/**
	 * Operazione per recuperare la lista dei post nel proprio feed. Viene
	 * restituita una lista dei post. Per ogni post viene fornito id, autore e
	 * titolo del post. La funzione viene attivata mediante il comando show feed.
	 *
	 * @return
	 */
	public List<Post> showFeed() {
		return null;
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
	 * @param idPost
	 * @return
	 */
	public Post showPost(String idPost) {
		return null;
	}

	/**
	 * Operazione per cancellare un post. La richiesta viene accettata ed eseguita
	 * solo se l’utente è l’autore del post. Il server cancella il post con tutto
	 * il suo contenuto associato (commenti e voti). Non vengono calcolate
	 * ricompense “parziali”, ovvero se un contenuto recente (post, voto o commento)
	 * non era stato conteggiato nel calcolo delle ricompense perché ancora il
	 * periodo non era scaduto, non viene considerato nel calcolo delle ricompense.
	 * Il comando corrispondente alla cancellazione è delete <idPost>.
	 *
	 * @param idPost
	 * @return
	 */
	public boolean deletePost(String idPost) {
		return false;
	}

	/**
	 * operazione per effettuare il rewin di un post, ovvero per pubblicare nel
	 * proprio blog un post presente nel proprio feed. La funzione viene attivata
	 * mediante il comando rewin <idPost>.
	 *
	 * @param idPost
	 * @return
	 */
	public boolean rewinPost(String idPost) {
		return false;
	}

	/**
	 * Operazione per assegnare un voto positivo o negativo ad un post. Se l’utente
	 * ha il post nel proprio feed e non ha ancora espresso un voto, il voto viene
	 * accettato, negli altri casi (ad es. ha già votato il post, non ha il post
	 * nel proprio feed, è l’autore del post) il voto non viene accettato e il
	 * server restituisce un messaggio di errore. Il comando per assegnare un voto
	 * al post è rate <idPost> <vote>. Nel comando i voti sono così codificati:
	 * voto positivo +1, voto negativo -1.
	 *
	 * @param idPost
	 * @param voto
	 * @return
	 */
	public boolean ratePost(String idPost, boolean voto) {
		return false;
	}

	/**
	 * Operazione per aggiungere un commento ad un post. Se l’utente ha il post nel
	 * proprio feed, il commento viene accettato, negli altri casi (ad es. l’utente
	 * non ha il post nel proprio feed oppure è l’autore del post) il commento non
	 * viene accettato e il server restituisce un messaggio di errore. Un utente
	 * può aggiungere più di un commento ad un post. La sintassi utilizzata dagli
	 * utenti per commentare è: comment <idPost> <comment>.
	 *
	 * @param idPost
	 * @param comment
	 * @return
	 */
	public boolean addComment(String idPost, Comment comment) {
		return false;
	}

	/**
	 * Operazione per recuperare il valore del proprio portafoglio. Il server
	 * restituisce il totale e la storia delle transazioni (ad es. <incremento>
	 * <timestamp>). Il comando corrispondente è wallet.
	 *
	 * @return
	 */
	public Wallet getWallet() {
		return null;
	}

	/**
	 * Operazione per recuperare il valore del proprio portafoglio convertito in
	 * bitcoin. Il server utilizza il servizio di generazione di valori random
	 * decimali fornito da RANDOM.ORG per ottenere un tasso di cambio casuale e
	 * quindi calcola la conversione. L’operazione è eseguita mediante il comando:
	 * wallet btc.
	 *
	 * @return
	 */
	public float getWalletInBitcoin() {
		return 0.0f;
	}

	public void test() {
		System.out.println("Client:test()");
	}

	public Post create_post(String title, String content) throws Winsome_exceptions.UserNotLogged {
		if (user == null) {
			throw new Winsome_exceptions.UserNotLogged();
		}
		return null;//new Post(this.user.getUsername(), title, content);
	}

	public Comment create_comment(String comment) throws Winsome_exceptions.UserNotLogged {
		if (user == null) {
			throw new Winsome_exceptions.UserNotLogged();
		}
		return null;//new Comment(this.user.getUsername(), comment);
	}

	public void exit() {
		logout();
		_on = false;
	}


}
