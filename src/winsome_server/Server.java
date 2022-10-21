package winsome_server;

import winsome_DB.RateDB;
import winsome_DB.WinsomeDatabase;
import winsome_comunication.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.channels.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
	// Server Properties
	private final ServerProperties properties;
	// Worker thread-pool
	private final ExecutorService workers_thread_poll;
	// Rewards thread
	private final Server_Rewards_Thread rewards_thread;

	// Server Database
	private final WinsomeDatabase server_db;
	// Connection Manager
	private final CConnectionsManager connections_manager = new CConnectionsManager();
	// Server socket and selector
	private ServerSocketChannel server_socket;
	private Selector selector;
	ServerRMI_Imp server_rmi;

	boolean is_running = true;

	// constructor
	public Server(String serverProperties_configFile, String clientProperties_configFile) {
		/*
		 * create a new server object
		 *
		 * 1. Create a new server properties object
		 * 2. Create a new worker thread poll
		 * 3. Create a new server database object
		 * 3.1 Try to load the server database
		 * 3.2 If an error occurred, exit the program
		 * 4. Create a new tcp server socket
		 * 5. Create a new udp server socket
		 * 6. Create a new server RMI object and bind it to the registry
		 * 7. Start the reward thread
		 *
		 */
		// 1. Create a new server properties object
		this.properties = new ServerProperties(serverProperties_configFile, clientProperties_configFile);
		// 1.1 Store the server address in the properties file
		try {
			this.properties.set_server_address(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		// 1.2 save the properties to the client properties file
		this.properties.write_properties();

		// 2. Create a new worker thread poll
		this.workers_thread_poll = Executors.newFixedThreadPool(properties.get_workers());

		// 3. Create a new server database object
		this.server_db = WinsomeDatabase.getInstance(
				this.properties.get_posts_database(), this.properties.get_users_database(), true);
		try {
			this.server_db.load_DB();
		} catch (WinsomeException e) {
			System.err.println(e.getMessage());
		}

		// 3.1 Try to load the server database
//		if (this.server_db.load_DB() != 0) {
//			// 3.2 If an error occurred, exit the program
//			System.out.println("Error: failed to load the server database");
//			System.exit(1);
//		}


		try {
			// 4. Create a new tcp server socket
			this.server_socket = ServerSocketChannel.open();
			this.server_socket.socket().bind(new InetSocketAddress(this.properties.get_tcp_port()));
			this.server_socket.configureBlocking(false);
			this.selector = Selector.open();
			this.server_socket.register(this.selector, SelectionKey.OP_ACCEPT);

			// 5. Create a new udp server socket

			// 6. Create a new server RMI object and bind it to the registry
			server_rmi = new ServerRMI_Imp(this);
			ServerRMI_Interface stub = (ServerRMI_Interface) UnicastRemoteObject.exportObject(server_rmi, 0);
			LocateRegistry.createRegistry(this.properties.get_registry_port());
			Registry registry = LocateRegistry.getRegistry(this.properties.get_registry_port());
			registry.rebind(this.properties.get_rmi_name(), stub);

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			System.err.println("Server exception: " + e);
			e.printStackTrace();
		}

		// 7. Start the reward thread
		this.rewards_thread = new Server_Rewards_Thread(this, this.properties.get_reward_time());
		this.rewards_thread.start();
	}

	/**
	 * Server Welcome Function
	 * <p></p>
	 * This function is called when the server is started
	 * listen to the server socketChannel and accept new connections
	 * The socketChannel is non-blocking and uses a selector to listen to the socket
	 * <p></p>
	 * When a new connection is accepted, the connection is added to the selector
	 * When a connection is ready to be read, the connection is sent to a worker task
	 * When a connection is ready to be written, the response is sent to the connection
	 * When a connection is closed, the connection is removed from the selector
	 */
	public void server_welcome_service() {
		/*
		 * Welcome_service for the server :
		 *
		 * 1. Loop
		 * 1.1. Wait for a new connection or a new request
		 * 1.2. If a new connection is accepted, put it in the selector
		 * 1.3. If a new request is received, add it to the queue of requests to serve
		 * 1.4. If a client is ready to write, write the response to it
		 * 2. Close the server socket channel
		 */

		int seconds = 0;
		int max_seconds = properties.get_server_timeout();

		// 1. Loop
		while (true) {
			// 1.1. Wait for a new connection or a new request
			try {
				selector.select(1000);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}

			// Debug print the number of channel in the selector
			// System.out.println("Number of channels in the selector: " + selector.keys().size());

			Set<SelectionKey> ready_keys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = ready_keys.iterator();

			// if there are no clients connected, increase the seconds counter
			if (!iterator.hasNext() && selector.keys().size() == 1) {
				// increment the seconds counter and check if it is greater than the max seconds
				if (++seconds > max_seconds) {
					// if it is, close the server
					break;
				}
			} else {
				// if there are ready keys, reset the seconds counter
				seconds = 0;
			}

			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();
				try {
					// 1.2. If a new connection is accepted, put it in the selector
					if (key.isAcceptable()) {
						ServerSocketChannel server_socket = (ServerSocketChannel) key.channel();
						SocketChannel client = server_socket.accept();
						client.configureBlocking(false);

						// Debug
						System.out.println("new client accepted : " + client);

						client.register(selector, SelectionKey.OP_READ);
					}

					// 1.3. If a new request is received, add it to the queue of requests to serve
					if (key.isReadable()) {
						SocketChannel client = (SocketChannel) key.channel();

						// Debug
						System.out.println("client : " + client + " is ready to read");

						key.interestOps(0);

						workers_thread_poll.submit(new WorkerTask(this, selector, key));

						continue;
					}

					// 1.4. If a client is ready to write, write the response to it
					if (key.isWritable()) {
						SocketChannel client = (SocketChannel) key.channel();

						// Debug
						System.out.println("client : " + client + " is ready to write");

						WinMessage win_message = (WinMessage) key.attachment();
						win_message.send(client);

						// Debug
						System.out.println("Server: " + win_message + " inviato al client " + client.getRemoteAddress());

						key.interestOps(SelectionKey.OP_READ);
					}
				} catch (IOException e) {
					key.cancel();
					try {
						// Debug
						System.out.println("client disconnected : " + key.channel());

						key.channel().close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} catch (CancelledKeyException e) {
					try {
						// Debug
						System.out.println("client disconnected : " + key.channel());

						key.channel().close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}

		// 2. Close the server
		this.close();
	}

	// Client Interactions
	public void register_request(String username, String password, String[] tags) throws WinsomeException {
		/*
		 * Register a new user
		 *
		 * 1. Register the user
		 */

		server_db.create_user(username, password, tags);
	}

	public WinMessage login_request(String username, String password, String address) {
		/*
		 * Login a user
		 *
		 * 1. Check if the user is already logged in
		 * 2. If the user/client is already logged in, return an error message
		 * 3. If the user is not logged in, check if the username and password are correct
		 * 4. If the username and password are correct, add the user to the logged in users
		 * 5. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is already logged in
		if (this.connections_manager.is_user_connected(username)) {
			// 2. If the user is already logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User already logged in");
			return result;
		} else if (this.connections_manager.is_address_connected(address)) {
			// 2. If the client is already logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("Client already logged in with another user");
			return result;
		}

		// 3. If the user is not logged in, check if the username and password are correct
		try {
			if (this.server_db.check_credentials(username, password)) {
				// 4. If the username and password are correct, add the user to the logged in users
				if (this.connections_manager.add_connection(new CConnection(address, username)) != 0) {
					// DEBUG
					System.out.println("Error: failed to add the connection to the connections manager");

					// 5. Return the result
					result.addString(WinMessage.ERROR);
					result.addString("Error: failed to add the user to the logged in users");
					return result;
				}

				// DEBUG
				System.out.println("User " + username + " logged in from " + address);

				// 5. Return the result
				result.addString(WinMessage.SUCCESS);
			} else {
				// 5. Return the result
				result.addString(WinMessage.ERROR);
				result.addString("Wrong username or password");
			}
		} catch (WinsomeException e) {
			// 5. Return the result
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}
		return result;
	}

	public WinMessage logout_request(String address) {
		/*
		 * Logout a user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, remove the user from the logged-in users
		 * 4. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// DEBUG
		System.out.println("User " + this.connections_manager.get_username(address) + " logged out from " + address);

		// 3. If the user is logged in, remove the user from the logged in users
		this.connections_manager.remove_connection(address);

		// 4. Return the result
		result.addString(WinMessage.SUCCESS);
		return result;
	}

	public WinMessage list_users_request(String address) {
		/*
		 * List all the users
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, return the list of users
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, return the list of users
		try {
			String[] users = this.server_db.get_similar_users(this.connections_manager.get_username(address));
			result.addString(WinMessage.SUCCESS);
			result.addStrings(users);
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage list_followings_request(String address) {
		/*
		 * list all the users that the user is following
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, return the list of users
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, return the list of users
		try {
			String[] users = this.server_db.get_user_following(this.connections_manager.get_username(address));
			result.addString(WinMessage.SUCCESS);
			result.addStrings(users);
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage follow_request(String username, String address) {
		/*
		 * Follow a user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to follow the user
		 * 4. notify <username> (if online) that he is followed
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to follow the user
		try {
			this.server_db.user_follows(this.connections_manager.get_username(address), username);
			result.addString(WinMessage.SUCCESS);
			// 4. notify <username> (if online) that he is followed
			ClientRMI_Interface callback = this.connections_manager.get_callback_of_username(username);
			if (callback != null) {
				callback.send_follower_update(this.connections_manager.get_username(address), true);
			}
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		} catch (RemoteException e) {
			System.err.println("Error while sending follower update to " + username);
		}

		return result;
	}

	public WinMessage unfollow_request(String username, String address) {
		/*
		 * Unfollow a user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to unfollow the user
		 * 4. notify <username> (if online) that he is unfollowed
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to unfollow the user
		try {
			this.server_db.user_unfollows(this.connections_manager.get_username(address), username);
			result.addString(WinMessage.SUCCESS);
			// 4. notify <username> (if online) that he is unfollowed
			ClientRMI_Interface callback = this.connections_manager.get_callback_of_username(username);
			if (callback != null) {
				callback.send_follower_update(this.connections_manager.get_username(address), false);
			}
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		} catch (RemoteException e) {
			System.err.println("Error while sending follower update to " + username);
		}

		return result;
	}

	public WinMessage create_post_request(String address, String title, String content) {
		/*
		 * Create a post
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to create the post
		 * 4. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to create the post
		try {
			this.server_db.create_post(this.connections_manager.get_username(address), title, content);
			result.addString(WinMessage.SUCCESS);
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage blog_request(String address) {
		/*
		 * Get the blog of the user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to get the blog
		 * 4. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to get the blog
		try {
			PostReprSimple[] posts = this.server_db.get_user_blog(this.connections_manager.get_username(address));
			result.addString(WinMessage.SUCCESS);
			// add an array of strings that are the serialized posts
			result.addStrings(Arrays.stream(posts).map(PostReprSimple::serialize).toArray(String[]::new));
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage show_post_request(String address, String post_id) {
		/*
		 * Get the blog of the user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to get the post
		 * 4. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to get the post
		try {
			PostReprDetailed post = this.server_db.get_post(post_id);
			result.addString(WinMessage.SUCCESS);
			// add an array of strings that are the serialized posts
			result.addString(post.serialize());
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage show_feed_request(String address) {
		/*
		 * Get the feed of the user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to get the feed
		 * 4. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to get the feed
		try {
			PostReprSimple[] posts = this.server_db.get_user_feed(this.connections_manager.get_username(address));
			result.addString(WinMessage.SUCCESS);
			// add an array of strings that are the serialized posts
			result.addStrings(Arrays.stream(posts).map(PostReprSimple::serialize).toArray(String[]::new));
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage delete_post_request(String address, String postId) {
		/*
		 * Delete the post identified by postId
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to delete the post
		 * 4. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to delete the post
		try {
			this.server_db.remove_post(this.connections_manager.get_username(address), postId);
			result.addString(WinMessage.SUCCESS);
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage rewin_post_request(String address, String postId) {
		/*
		 * Rewin the post identified by postId
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to rewin the post
		 * 4. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to rewin the post
		try {
			this.server_db.rewin_post(this.connections_manager.get_username(address), postId);
			result.addString(WinMessage.SUCCESS);
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage comment_request(String address, String postId, String comment) {
		/*
		 * Comment on the post identified by postId
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to comment on the post
		 * 4. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to comment on the post
		try {
			this.server_db.comment_on_post(this.connections_manager.get_username(address), postId, comment);
			result.addString(WinMessage.SUCCESS);
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage rate_request(String address, String postId, String rate) {
		/*
		 * Rate the post identified by postId
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to rate the post
		 * 4. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to rate the post
		try {
			this.server_db.rate_post(this.connections_manager.get_username(address), postId, rate.equals(RateDB.UPVOTE));
			result.addString(WinMessage.SUCCESS);
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage wallet_request(String address) {
		/*
		 * Get the wallet of the user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to get the wallet
		 * 4. Return the result
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to get the wallet
		try {
			result.addString(WinMessage.SUCCESS);
			result.addString(this.server_db.get_user_wallet(this.connections_manager.get_username(address)).serialize());
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		}

		return result;
	}

	public WinMessage wallet_btc_request(String address) {
		/*
		 * Get the user's wallet balance in BTC currency
		 * The BTC currency is randomly generated querying the website random.org
		 * The HTTP request ask for a random number between 0 and 1000
		 *  as the example https://www.random.org/integers/?num=1&min=0&max=1000&col=1&base=10&format=plain&rnd=new
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to get the user's wallet balance
		 * 4. Get the BTC currency with the random.org website
		 * 5. Return the wallet balance in BTC currency
		 */

		WinMessage result = new WinMessage();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(WinMessage.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to get the user's wallet balance
		try {
			double balance = this.server_db.get_user_wallet(this.connections_manager.get_username(address)).getBalance();
			// 4. Get the BTC currency with the random.org website
			double btc = this.get_btc_rate();
			// 5. Return the wallet balance in BTC currency
			result.addString(WinMessage.SUCCESS);
			result.addString(String.valueOf(balance * btc));
		} catch (WinsomeException e) {
			result.addString(WinMessage.ERROR);
			result.addString(e.niceMessage());
		} catch (IOException ee) {
			result.addString(WinMessage.ERROR);
			result.addString("Error while getting the BTC rate");
		}

		return result;
	}

	private double get_btc_rate() throws IOException {
		URL random_org_url = new URL("https://www.random.org/integers/?num=1&min=0&max=1000&col=1&base=10&format=plain&rnd=new");
		HttpURLConnection random_org_connection = (HttpURLConnection) random_org_url.openConnection();
		random_org_connection.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(random_org_connection.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		double btc;
		try {
			btc = Double.parseDouble(response.toString())/1000;
		} catch (NumberFormatException e) {
			btc = 0;
		}

		return btc;
	}

	/**
	 * receive_updates
	 * this method register the client to receive updates about the followers
	 *
	 * @param callback the callback object to call when an update is pending
	 * @param username the username of the client
	 * @return 0 if success, an error code otherwise
	 */
	public int receive_updates(ClientRMI_Interface callback, String username) {
		/*
		 * Receive updates from the server
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, register the client to receive updates
		 * 4. send to the newly registered client the user's followers and the details for the multicast
		 */

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_user_connected(username)) {
			// 2. If the user is not logged in, return an error message
			return -1;
		}

		// 3. If the user is logged in, register the client to receive updates
		this.connections_manager.register_callback_of_user(username, callback);

		// 4. send to the newly registered client the user's followers and the details for the multicast
		try {
			callback.send_followers(this.server_db.get_user_followers(username));
			callback.send_multicast_details(this.properties.get_multicast_address(),
					this.properties.get_multicast_port(), InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException | RemoteException | WinsomeException e) {
			System.err.println(e.getMessage());
		}

		return 0;
	}

	public void setMulticastAddress(String multicastAddress) {
		this.properties.set_multicast_address(multicastAddress);
	}

	public String getMulticast_address() {
		return properties.get_multicast_address();
	}

	public int getMulticast_port() {
		return properties.get_multicast_port();
	}

	public void interrupt_rewards_thread() {
		this.rewards_thread.interrupt();
	}

	public void reward_users() {
		server_db.reward_everyone(properties.get_reward_authors());
	}

	public void close() {
		if (!is_running) {
			return;
		}
		try {
			this.workers_thread_poll.shutdown();
			this.workers_thread_poll.awaitTermination(1, TimeUnit.MINUTES);
			// DEBUG
			System.out.println("All workers terminated");

			interrupt_rewards_thread();
			// DEBUG
			System.out.println("Rewards thread terminated");

			this.server_db.close();
			this.server_socket.close();

			UnicastRemoteObject.unexportObject(this.server_rmi, true);
			// DEBUG
			System.out.println("RMI unexported");

		} catch (IOException | InterruptedException e) {
			System.err.println(e.getMessage());
		}
		is_running = false;
	}
}
