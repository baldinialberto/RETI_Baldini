package winsome_client;

import winsome_comunication.*;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {
	SocketChannel socket_channel;
	Winsome_Server_Sender sender;
	private final ClientCLI c_interface;
	private final Client_properties properties;
	private Server_RMI_Interface server_rmi_interface;
	private Client_RMI_Imp client_rmi;
	private Client_RMI_Interface client_rmi_stub;
	private LocalUser user;
	private boolean _on = false;
	private boolean connected = false;
	private boolean logged = false;
	private int multicast_port;
	private String multicast_address;
	private String multicast_network_name;


	private Client_notification_Thread notification_thread;

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
		properties = new Client_properties("client_config.txt");

		// 2. create client interface
		c_interface = new ClientCLI(this);

		// 3. connect to server's RMI
		try {
			Registry r = LocateRegistry.getRegistry(properties.get_registry_port());
			server_rmi_interface = (Server_RMI_Interface) r.lookup(properties.get_rmi_name());
			_on = true;
		} catch (Exception e) {
			System.err.println("Client exception: " + e);
		}

		// 4. create client's RMI
		try {
			client_rmi = new Client_RMI_Imp(this);
			client_rmi_stub = (Client_RMI_Interface) UnicastRemoteObject.exportObject(client_rmi, 0);
		} catch (RemoteException e) {
			System.err.println("Client exception: " + e.getMessage());
			_on = false;
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
		// DEBUG
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
	 * @param username
	 * @param password
	 * @param tags
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
		int result = server_rmi_interface.register_user(username, password, tags_array);

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
	 * @param username the username of the user to login
	 * @param password the password of the user to login
	 * @return 0 if the login is successful, -1 otherwise
	 */
	public int login(String username, String password) {
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
			System.out.println("You are already logged in");
			return -1;
		}

		// 2. connect to server
		try {
			this.connect();
		} catch (IOException e) {
			System.err.println("Error connecting to server");
			return -1;
		}

		// 3. login with username and password
		try{
			sender.login(username, password);
		} catch (Winsome_Exception w) {
			System.err.println(w.niceMessage());
			return -1;
		}

		return 0;
	}

	/**
	 * Effettua il logout dell’utente dal servizio. Corrisponde al comando logout.
	 * @return 0 if the logout is successful, -1 otherwise
	 */
	public int logout() {
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
			System.out.println("You are not logged in");
			return -1;
		}

		// 2. if not connected, print error and return
		if (!connected) {
			System.out.println("You are not connected to the server");
			return -1;
		}

		// 3. logout
		try {
			sender.logout();
		} catch (Winsome_Exception w) {
			System.err.println(w.niceMessage());
			return -1;
		}

		// 4. disconnect from server
		try {
			this.disconnect();
			// 5. set logged to false
			logged = false;
			// 6. set user to null
			user = null;
		} catch (IOException e) {
			System.err.println("Error disconnecting from server");
			return -1;
		}

		return 0;
	}

	/**
	 * Utilizzata da un utente per visualizzare la lista (parziale) degli utenti
	 * registrati al servizio. Il server restituisce la lista di utenti che hanno
	 * almeno un tag in comune con l’utente che ha fatto la richiesta. Il comando
	 * associato è list users.
	 *
	 * @return the list of users
	 */
	public List<String> listUsers() {
		/*
		 * list users
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. get list of users
		 */

		// 1. if not logged, print error and return
		if (!logged) {
			System.out.println("You are not logged in");
			return null;
		}

		// 2. if not connected, print error and return
		if (!connected) {
			System.out.println("You are not connected to the server");
			return null;
		}

		// 3. get list of users
		List<String> users;
		try {
			users = Arrays.asList(sender.list_users());
		} catch (Winsome_Exception w) {
			System.err.println(w.niceMessage());
			return null;
		}

		return users;
	}

	/**
	 * Operazione lato client per visualizzare la lista dei propri follower. Questo
	 * comando dell’utente non scatena una richiesta sincrona dal client al server.
	 * Il client restituisce la lista dei follower mantenuta localmente che viene
	 * via via aggiornata grazie a notifiche “asincrone” ricevute dal server. Vedere
	 * i dettagli di implementazione nella sezione successiva. Corrisponde al
	 * comando list followers.
	 *
	 * @return the list of followers
	 */
	public List<String> listFollowers() {
		/*
		 * list followers
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. return list of followers
		 */

		System.out.println("list followers");

		// 1. if not logged, print error and return
		if (!logged) {
			System.out.println("Not logged");
			return null;
		}
		// 2. if not connected, print error and return
		if (!connected) {
			System.out.println("Not connected");
			return null;
		}

		// 3. Return the list of followers
		return user.get_followers();
	}

	/**
	 * Utilizzata da un utente per visualizzare la lista degli utenti di cui è
	 * follower. Questo metodo è corrispondente al comando list following.
	 *
	 * @return the list of following
	 */
	public List<String> listFollowing() {
		/*
		 * list following
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. return list of following
		 */

		System.out.println("list following");

		// 1. if not logged, print error and return
		if (!logged) {
			System.out.println("Not logged");
			return null;
		}
		// 2. if not connected, print error and return
		if (!connected) {
			System.out.println("Not connected");
			return null;
		}

		// 3. Return the list of following
		List<String> following;
		try {
			following = Arrays.asList(sender.list_following());
		} catch (Winsome_Exception w) {
			System.err.println(w.niceMessage());
			return null;
		}

		return following;
	}

	/**
	 * L’utente chiede di seguire l’utente che ha per username idUser. Da quel
	 * momento in poi può ricevere tutti i post pubblicati da idUser. Il comando
	 * associato è follow <username>.
	 *
	 * @param idUser the id of the user to follow
	 * @return true if the user is followed, false otherwise
	 */
	public boolean followUser(String idUser) {
		/*
		 * follow user
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. follow user
		 */

		System.out.println("follow user");

		// 1. if not logged, print error and return
		if (!logged) {
			System.out.println("Not logged");
			return false;
		}
		// 2. if not connected, print error and return
		if (!connected) {
			System.out.println("Not connected");
			return false;
		}

		// 3. follow user
		try {
			sender.follow(idUser);
		} catch (Winsome_Exception w) {
			System.err.println(w.niceMessage());
			return false;
		}

		return true;
	}

	/**
	 * L’utente chiede di non seguire più l’utente che ha per username idUser. Il
	 * comando associato è unfollow <username>.
	 *
	 * @param idUser the id of the user to unfollow
	 * @return true if the user is unfollowed, false otherwise
	 */
	public boolean unfollowUser(String idUser) {
		/*
		 * unfollow user
		 *
		 * 1. if not logged, print error and return
		 * 2. if not connected, print error and return
		 * 3. unfollow user
		 */

		System.out.println("unfollow user");

		// 1. if not logged, print error and return
		if (!logged) {
			System.out.println("Not logged");
			return false;
		}
		// 2. if not connected, print error and return
		if (!connected) {
			System.out.println("Not connected");
			return false;
		}

		// 3. unfollow user
		try {
			sender.unfollow(idUser);
		} catch (Winsome_Exception w) {
			System.err.println(w.niceMessage());
			return false;
		}

		return true;
	}

	/**
	 * operazione per recuperare la lista dei post di cui l’utente è autore. Viene
	 * restituita una lista dei post presenti nel blog dell’utente. Per ogni post
	 * viene fornito id del post, autore e titolo. Viene attivata con il comando
	 * blog.
	 *
	 * @return
	 */
	public List<Post_representation_simple> viewBlog() {
		/*
		 * view blog
		 *
		 * 1. Send view blog request to server
		 * 2. Receive view blog response from server
		 * 3. If view blog is successful, return list of posts
		 * 4. Return null
		 */

		System.out.println("view blog");

		if (!connected) {
			System.out.println("Not connected");
			return null;
		}

		if (!logged) {
			System.out.println("Not logged");
			return null;
		}

		try {
			// 1. Send view blog request to server
			Win_message view_blog_request = new Win_message();
			view_blog_request.addString(Win_message.BLOG_REQUEST);
			view_blog_request.send(socket_channel);

			// 2. Receive view blog response from server
			Win_message view_blog_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (view_blog_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("View blog failed : " + view_blog_response.getString(1));
				return null;
			} else if (view_blog_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If view blog is successful, return list of posts
				List<Post_representation_simple> posts = new ArrayList<>();
				for (int i = 1; i < view_blog_response.size(); i++) {
					posts.add(new Post_representation_simple(view_blog_response.getString(i)));
				}
				return posts;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

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
	public boolean createPost(String titolo, String contenuto) {
		/*
		 * create post <titolo> <contenuto>
		 *
		 * 1. Send create post request to server
		 * 2. Receive create post response from server
		 * 3. If the operation is not successful, also print the error message
		 * 4. Return true
		 */

		System.out.println("create post " + titolo + " " + contenuto);

		if (!connected) {
			System.out.println("Not connected");
			return false;

		}
		if (!logged) {
			System.out.println("Not logged");
			return false;
		}

		if (titolo.length() > 20) {
			System.out.println("Title too long (max 20 characters)");
			// trim the title to 20 characters
			titolo = titolo.substring(0, 20);
		}

		if (contenuto.length() > 500) {
			System.out.println("Content too long (max 500 characters)");
			// trim the content to 500 characters
			contenuto = contenuto.substring(0, 500);
		}

		try {
			// 1. Send create post request to server
			Win_message create_post_request = new Win_message();
			create_post_request.addString(Win_message.POST_REQUEST);
			create_post_request.addString(titolo);
			create_post_request.addString(contenuto);
			create_post_request.send(socket_channel);

			// 2. Receive create post response from server
			Win_message create_post_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (create_post_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Create post failed : " + create_post_response.getString(1));
				return false;
			} else if (create_post_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If the operation is not successful, also print the error message
				System.out.println("Post created");
				// 4. Return true
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Operazione per recuperare la lista dei post nel proprio feed. Viene
	 * restituita una lista dei post. Per ogni post viene fornito id, autore e
	 * titolo del post. La funzione viene attivata mediante il comando show feed.
	 *
	 * @return
	 */
	public List<Post_representation_simple> showFeed() {
		/*
		 * show feed
		 *
		 * 1. Send show feed request to server
		 * 2. Receive show feed response from server
		 * 3. If the operation is not successful, also print the error message
		 * 4. Return list of posts
		 */

		System.out.println("show feed");

		if (!connected) {
			System.out.println("Not connected");
			return null;
		}
		if (!logged) {
			System.out.println("Not logged");
			return null;
		}

		try {
			// 1. Send show feed request to server
			Win_message show_feed_request = new Win_message();
			show_feed_request.addString(Win_message.SHOW_FEED_REQUEST);
			show_feed_request.send(socket_channel);

			// 2. Receive show feed response from server
			Win_message show_feed_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (show_feed_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Show feed failed : " + show_feed_response.getString(1));
				return null;
			} else if (show_feed_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If the operation is not successful, also print the error message
				// 4. Return list of posts
				List<Post_representation_simple> posts = new ArrayList<>();
				for (int i = 1; i < show_feed_response.size(); i++) {
					posts.add(new Post_representation_simple(show_feed_response.getString(i)));
				}
				return posts;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	public Post_representation_detailed showPost(String idPost) {
		/*
		 * show post <idPost>
		 *
		 * 1. Send show post request to server
		 * 2. Receive show post response from server
		 * 3. If the operation is not successful, also print the error message
		 * 4. Return post
		 */

		System.out.println("show post " + idPost);

		if (!connected) {
			System.out.println("Not connected");
			return null;
		}

		if (!logged) {
			System.out.println("Not logged");
			return null;
		}

		try {
			// 1. Send show post request to server
			Win_message show_post_request = new Win_message();
			show_post_request.addString(Win_message.SHOW_POST_REQUEST);
			show_post_request.addString(idPost);
			show_post_request.send(socket_channel);

			// 2. Receive show post response from server
			Win_message show_post_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (show_post_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Show post failed : " + show_post_response.getString(1));
				return null;
			} else if (show_post_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If the operation is not successful, also print the error message
				System.out.println("Post shown");
				// 4. Return post
				return new Post_representation_detailed(show_post_response.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		/*
		 * delete <idPost>
		 *
		 * 1. Send delete request to server
		 * 2. Receive delete response from server
		 * 3. If the operation is not successful, also print the error message
		 * 4. Return true if the operation is successful
		 */

		System.out.println("delete " + idPost);

		if (!connected) {
			System.out.println("Not connected");
			return false;
		}

		if (!logged) {
			System.out.println("Not logged");
			return false;
		}

		try {
			// 1. Send delete request to server
			Win_message delete_request = new Win_message();
			delete_request.addString(Win_message.DELETE_REQUEST);
			delete_request.addString(idPost);
			delete_request.send(socket_channel);

			// 2. Receive delete response from server
			Win_message delete_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (delete_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Delete failed : " + delete_response.getString(1));
				return false;
			} else if (delete_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If the operation is not successful, also print the error message
				System.out.println("Post deleted");
				// 4. Return true if the operation is successful
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		/*
		 * rewin <idPost>
		 *
		 * 1. Send rewin request to server
		 * 2. Receive rewin response from server
		 * 3. If the operation is not successful, also print the error message
		 * 4. Return true if the operation is successful
		 */

		System.out.println("rewin " + idPost);

		if (!connected) {
			System.out.println("Not connected");
			return false;
		}

		if (!logged) {
			System.out.println("Not logged");
			return false;
		}

		try {
			// 1. Send rewin request to server
			Win_message rewin_request = new Win_message();
			rewin_request.addString(Win_message.REWIN_REQUEST);
			rewin_request.addString(idPost);
			rewin_request.send(socket_channel);

			// 2. Receive rewin response from server
			Win_message rewin_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (rewin_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Rewin failed : " + rewin_response.getString(1));
				return false;
			} else if (rewin_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If the operation is not successful, also print the error message
				System.out.println("Post rewin");
				// 4. Return true if the operation is successful
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		/*
		 * rate <idPost> <vote>
		 *
		 * 1. Send rate request to server
		 * 2. Receive rate response from server
		 * 3. If the operation is not successful, also print the error message
		 * 4. Return true if the operation is successful
		 */

		System.out.println("rate " + idPost + " " + (voto ? "1" : "-1"));

		if (!connected) {
			System.out.println("Not connected");
			return false;
		}

		if (!logged) {
			System.out.println("Not logged");
			return false;
		}

		try {
			// 1. Send rate request to server
			Win_message rate_request = new Win_message();
			rate_request.addString(Win_message.RATE_REQUEST);
			rate_request.addString(idPost);
			rate_request.addString(voto ? "+1" : "-1");
			rate_request.send(socket_channel);

			// 2. Receive rate response from server
			Win_message rate_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (rate_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Rate failed : " + rate_response.getString(1));
				return false;
			} else if (rate_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If the operation is not successful, also print the error message
				System.out.println("Post rated");
				// 4. Return true if the operation is successful
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	public boolean addComment(String idPost, String comment) {
		/*
		 * comment <idPost> <comment>
		 *
		 * 1. Send comment request to server
		 * 2. Receive comment response from server
		 * 3. If the operation is not successful, also print the error message
		 * 4. Return true if the operation is successful
		 */

		System.out.println("comment " + idPost + " " + comment);

		if (!connected) {
			System.out.println("Not connected");
			return false;
		}

		if (!logged) {
			System.out.println("Not logged");
			return false;
		}

		try {
			// 1. Send comment request to server
			Win_message comment_request = new Win_message();
			comment_request.addString(Win_message.COMMENT_REQUEST);
			comment_request.addString(idPost);
			comment_request.addString(comment);
			comment_request.send(socket_channel);

			// 2. Receive comment response from server
			Win_message comment_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (comment_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Comment failed : " + comment_response.getString(1));
				return false;
			} else if (comment_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If the operation is not successful, also print the error message
				System.out.println("Comment added");
				// 4. Return true if the operation is successful
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Operazione per recuperare il valore del proprio portafoglio. Il server
	 * restituisce il totale e la storia delle transazioni (ad es. <incremento>
	 * <timestamp>). Il comando corrispondente è wallet.
	 *
	 * @return
	 */
	public Wallet_representation getWallet() {
		/*
		 * wallet
		 *
		 * 1. Send wallet request to server
		 * 2. Receive wallet response from server
		 * 3. If the operation is not successful, also print the error message
		 */

		System.out.println("wallet");

		if (!connected) {
			System.out.println("Not connected");
			return null;
		}

		if (!logged) {
			System.out.println("Not logged");
			return null;
		}

		try {
			// 1. Send wallet request to server
			Win_message wallet_request = new Win_message();
			wallet_request.addString(Win_message.WALLET_REQUEST);
			wallet_request.send(socket_channel);

			// 2. Receive wallet response from server
			Win_message wallet_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (wallet_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Wallet failed : " + wallet_response.getString(1));
				return null;
			} else if (wallet_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If the operation is not successful, also print the error message
				System.out.println("Wallet received");
				Wallet_representation wallet = new Wallet_representation();
				wallet.deserialize(wallet_response.getString(1));
				return wallet;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	public double getWalletInBitcoin() {
		/*
		 * wallet btc
		 *
		 * 1. Send wallet btc request to server
		 * 2. Receive wallet btc response from server
		 * 3. If the operation is not successful, also print the error message
		 * 4. Return the wallet value in bitcoin if the operation is successful
		 */

		System.out.println("wallet btc");

		if (!connected) {
			System.out.println("Not connected");
			return -1;
		}

		if (!logged) {
			System.out.println("Not logged");
			return -1;
		}

		try {
			// 1. Send wallet btc request to server
			Win_message wallet_btc_request = new Win_message();
			wallet_btc_request.addString(Win_message.WALLET_BTC_REQUEST);
			wallet_btc_request.send(socket_channel);

			// 2. Receive wallet btc response from server
			Win_message wallet_btc_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (wallet_btc_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Wallet btc failed : " + wallet_btc_response.getString(1));
				return -1;
			} else if (wallet_btc_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. If the operation is not successful, also print the error message
				System.out.println("Wallet btc value : " + wallet_btc_response.getString(1));
				// 4. Return the wallet value in bitcoin if the operation is successful
				return Double.parseDouble(wallet_btc_response.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return .0;
	}

	public void test() {
		System.out.println("Client:test()");
	}


	public void exit() {

		try {
			UnicastRemoteObject.unexportObject(client_rmi, true);
		} catch (NoSuchObjectException ignored) {
		}

		System.out.flush();

		logout();
		_on = false;
	}


	public int add_follower(String username) {
		/*
		 * this method is used to add a follower to the local user
		 *
		 * 1. add the follower to the local user if it is not already present
		 * 2. return the result
		 */

		if (username == null) return -1;

		if (user.get_followers().contains(username)) {
			return -1;
		} else {
			user.add_follower(username);
			return 0;
		}
	}

	public int addAll_followers(String[] followers) {
		/*
		 * this method is used to add a list of followers to the local user
		 *
		 * 1. add the followers to the local user if they are not already present
		 * 2. return the result
		 */

		if (followers == null) return -1;

		for (String follower : followers) {
			if (!user.get_followers().contains(follower)) {
				user.add_follower(follower);
			}
		}

		return 0;
	}

	public int set_multicast(String ip, int port, String network_name) {
		/*
		 * this method is used to set the multicast ip, port and network name
		 *
		 * 1. set the multicast ip, port and network name
		 * 2. return the result
		 */

		if (ip == null || port < 0 || network_name == null) return -1;

		this.multicast_address = ip;
		this.multicast_port = port;
		this.multicast_network_name = network_name;

		// DEBUG
		System.out.println("Client:set_multicast() - multicast_address: " + multicast_address +
				" multicast_port: " + multicast_port +
				" multicast_network_name: " + multicast_network_name);

		return 0;
	}

	public int remove_follower(String username) {
		/*
		 * this method is used to remove a follower from the local user
		 *
		 * 1. remove the follower from the local user if it is present
		 * 2. return the result
		 */

		if (username == null) return -1;

		if (user.get_followers().contains(username)) {
			user.remove_follower(username);
			return 0;
		} else {
			return -1;
		}
	}

	public int getMulticast_port() {
		return multicast_port;
	}

	public String getNetwork_interface() {
		return multicast_network_name;
	}

	public InetAddress getMulticast_address() {
		try {
			return InetAddress.getByName(multicast_address);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public void start_notification_thread() {
		/*
		 * this method is used to start the reward thread
		 *
		 * 1. start the reward thread
		 */

		// 1. start the reward thread
		notification_thread = new Client_notification_Thread(this);
		notification_thread.start();
	}

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
