package winsome_client;

import winsome_comunication.*;

import java.io.IOException;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Client {
	private ClientCLI c_interface;
	private Client_properties properties;
	SocketChannel socket_channel;
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

		// 5. create client's notification thread and start it
		notification_thread = new Client_notification_Thread(this);
		notification_thread.start();
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
	 * @param username
	 * @param password
	 * @return
	 */
	public int login(String username, String password) {
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

			login_request.addString(Win_message.LOGIN_REQUEST);
			login_request.addString(username);
			login_request.addString(password);


			// 2. Send login request to server
			login_request.send(socket_channel);

			// 3. Receive login response from server of unknown size
			Win_message login_response = Win_message.receive(socket_channel);

			// DEBUG
			System.out.println("response: " + login_response);

			// 4. If login is successful, set _logged to true
			if (login_response.getString(0).equals(Win_message.SUCCESS)) {
				logged = true;
				this.user = new LocalUser(username);
				try {
					if (server_rmi_interface.receive_updates(client_rmi_stub, username)==0) {
						System.out.println("RMI registered");
					} else {
						System.out.println("RMI not registered");
						logout();
						return -1;
					}
				} catch (RemoteException e) {
					System.out.println(e.getMessage());
					System.out.println("RMI not registered");
					logout();
					return -1;
				}
				return 0;
			} else if (login_response.getString(0).equals(Win_message.ERROR)) {
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

		if (!connected) {
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
			logout_request.addString(Win_message.LOGOUT_REQUEST);
			logout_request.send(socket_channel);

			// 2. Receive logout response from server
			Win_message logout_response = Win_message.receive(socket_channel);

			// 3. If logout is successful, set _logged to false
			if (!logout_response.getString(0).equals(Win_message.SUCCESS)) {
				System.out.println("Logout failed : " + logout_response.getString(1));
				return -1;
			}

			logged = false;

			user = null;

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
			list_users_request.addString(Win_message.LIST_USERS_REQUEST);
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
	public List<String> listFollowers() {
		/*
		 * list followers
		 *
		 * 1. Return the list of followers
		 */

		System.out.println("list followers");

		if (!connected) {
			System.out.println("Not connected");
			return null;
		}
		if (!logged) {
			System.out.println("Not logged");
			return null;
		}

		// 1. Return the list of followers
		return user.get_followers();
	}

	/**
	 * Utilizzata da un utente per visualizzare la lista degli utenti di cui è
	 * follower. Questo metodo è corrispondente al comando list following.
	 *
	 * @return
	 */
	public List<String> listFollowing() {
		/*
		 * list following
		 *
		 * 1. Send list following request to server
		 * 2. Receive list following response from server
		 * 3. Return the list of following
		 */

		System.out.println("list following");

		if (!connected) {
			System.out.println("Not connected");
			return null;
		}

		if (!logged) {
			System.out.println("Not logged");
			return null;
		}

		try {
			// 1. Send list following request to server
			Win_message list_following_request = new Win_message();
			list_following_request.addString(Win_message.LIST_FOLLOWING_REQUEST);
			list_following_request.send(socket_channel);

			// 2. Receive list following response from server
			Win_message list_following_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (list_following_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("List following failed : " + list_following_response.getString(1));
				return null;
			} else if (list_following_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. Return the list of following
				return list_following_response.getStrings().subList(1, list_following_response.getStrings().size());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

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
		 * 3. Return true
		 */

		System.out.println("follow user " + idUser);

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
			follow_user_request.addString(Win_message.FOLLOW_REQUEST);
			follow_user_request.addString(idUser);
			follow_user_request.send(socket_channel);

			// 2. Receive follow user response from server
			Win_message follow_user_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (follow_user_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Follow user failed : " + follow_user_response.getString(1));
				return false;
			} else if (follow_user_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. Return true
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
		 * 3. Return true
		 */

		System.out.println("unfollow user " + idUser);

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
			unfollow_user_request.addString(Win_message.UNFOLLOW_REQUEST);
			unfollow_user_request.addString(idUser);
			unfollow_user_request.send(socket_channel);

			// 2. Receive unfollow user response from server
			Win_message unfollow_user_response = Win_message.receive(socket_channel);

			// check if the response is an error
			if (unfollow_user_response.getString(0).equals(Win_message.ERROR)) {
				System.out.println("Unfollow user failed : " + unfollow_user_response.getString(1));
				return false;
			} else if (unfollow_user_response.getString(0).equals(Win_message.SUCCESS)) {
				// 3. Return true
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
	public List<Post_simple> viewBlog() {
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
				List<Post_simple> posts = new ArrayList<>();
				for (int i = 1; i < view_blog_response.size(); i++) {
					posts.add(new Post_simple(view_blog_response.getString(i)));
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
	public List<Post_simple> showFeed() {
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
				List<Post_simple> posts = new ArrayList<>();
				for (int i = 1; i < show_feed_response.size(); i++) {
					posts.add(new Post_simple(show_feed_response.getString(i)));
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
	public Post_detailed showPost(String idPost) {
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
				return new Post_detailed(show_post_response.getString(1));
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
	public Wallet_simple getWallet()
	{
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
				Wallet_simple wallet = new Wallet_simple();
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

}
