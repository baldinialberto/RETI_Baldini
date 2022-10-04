package winsome_server;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	// Server Properties
	private final Server_properties properties;
	// Worker thread-pool
	private final ExecutorService workers_thread_poll;
	// Rewards thread
	private final Server_Rewards_Thread rewards_thread;

	// Server Database
	private final Server_DB server_db;
	// Server socket and selector
	private ServerSocketChannel server_socket;
	private Selector selector;
	// Connection Manager
	private final CConnections_Manager connections_manager = new CConnections_Manager();

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
		this.properties = new Server_properties(serverProperties_configFile, clientProperties_configFile);
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
		this.server_db = new Server_DB(this.properties.get_posts_database(), this.properties.get_users_database());

		// 3.1 Try to load the server database
		if (this.server_db.load_DB() != 0) {
			// 3.2 If an error occurred, exit the program
			System.out.println("Error: failed to load the server database");
			System.exit(1);
		}


		try {
			// 4. Create a new tcp server socket
			this.server_socket = ServerSocketChannel.open();
			this.server_socket.socket().bind(new InetSocketAddress(this.properties.get_tcp_port()));
			this.server_socket.configureBlocking(false);
			this.selector = Selector.open();
			this.server_socket.register(this.selector, SelectionKey.OP_ACCEPT);

			// 5. Create a new udp server socket
			// TODO: create a new udp server socket

			// 6. Create a new server RMI object and bind it to the registry
			Server_RMI_Imp server_rmi = new Server_RMI_Imp(this);
			Server_RMI_Interface stub = (Server_RMI_Interface) UnicastRemoteObject.exportObject(server_rmi, 0);
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
		this.rewards_thread = new Server_Rewards_Thread(this);
		this.rewards_thread.start();
	}

	public void server_welcome_service()
	{
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

						workers_thread_poll.submit(new Worker_task(this, selector, key));

						continue;
					}

					// 1.4. If a client is ready to write, write the response to it
					if (key.isWritable()) {
						SocketChannel client = (SocketChannel) key.channel();

						// Debug
						System.out.println("client : " + client + " is ready to write");

						Win_message win_message = (Win_message) key.attachment();
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

		// 2. Close the server socket channel
		try {
			this.server_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save_DB() {
		/*
		 * save the server database
		 *
		 * 1. Try to save the server database
		 * 2. If an error occurred, exit the program
		 *
		 */

		// 1. Try to save the server database
		if (this.server_db.save_DB() != 0) {
			// 2. If an error occurred, exit the program
			System.out.println("Error: failed to save the server database");
			System.exit(1);
		}
	}
	private void test_func()
	{
		/*
		 * This method is used to test the server.
		 *
		 * 1. Create a new server_db and load it.
		 * 2. Add 10 different users with their password and 1-5 tags.
		 * 3. For each user, add 1-5 Post with random title and text.
		 * 4. Save the server_db.
		 */

		// 1. Create a new server_db and load it.
		Server_DB server_db = new Server_DB("posts.json", "users.json");
		server_db.load_DB();

		// 2. Add 10 different users with their password and 1-5 tags.
		for (int i = 0; i < 10; i++) {
			String[] tags = new String[(int) (Math.random() * 5)];
			for (int j = 0; j < tags.length; j++) {
				tags[j] = "tag" + j;
			}
			server_db.add_user("user_" + i, "password_" + i, tags);
		}

		// 3. For each user, add 1-5 Post with random title and text.
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < (int) (Math.random() * 5); j++) {
				server_db.add_post("user_" + i, "user_" + i, "title_" + j + "_" + i, "text" + j);
			}
		}

		// 4. Save the server_db.
		server_db.save_DB();
	}


	// Client Interactions
	public int register_request(String username, String password, String[] tags) {
		/*
		  register a new user

		  1. add the user to database and return the result

		 */

		return this.server_db.add_user(username, password, tags);
	}

	public Win_message login_request(String username, String password, String address)
	{
		/*
		 * Login a user
		 *
		 * 1. Check if the user is already logged in
		 * 2. If the user/client is already logged in, return an error message
		 * 3. If the user is not logged in, check if the username and password are correct
		 * 4. If the username and password are correct, add the user to the logged in users
		 * 5. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is already logged in
		if (this.connections_manager.is_user_connected(username)) {
			// 2. If the user is already logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User already logged in");
			return result;
		} else if (this.connections_manager.is_address_connected(address)) {
			// 2. If the client is already logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("Client already logged in with another user");
			return result;
		}

		// 3. If the user is not logged in, check if the username and password are correct
		if (this.server_db.user_check_password(username, password)) {
			// 4. If the username and password are correct, add the user to the logged in users
			if (this.connections_manager.add_connection(new CConnection(address, username)) != 0)
			{
				// DEBUG
				System.out.println("Error: failed to add the connection to the connections manager");
				
				// 5. Return the result
				result.addString(Win_message.ERROR);
				result.addString("Error: failed to add the user to the logged in users");
				return result;
			}

			// DEBUG
			System.out.println("User " + username + " logged in from " + address);

			// 5. Return the result
			result.addString(Win_message.SUCCESS);
		} else {
			// 5. Return the result
			result.addString(Win_message.ERROR);
			result.addString("Wrong username or password");
		}
		return result;
	}

	public Win_message logout_request(String address)
	{
		/*
		 * Logout a user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, remove the user from the logged in users
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// DEBUG
		System.out.println("User " + this.connections_manager.get_username(address) + " logged out from " + address);

		// 3. If the user is logged in, remove the user from the logged in users
		this.connections_manager.remove_connection(address);

		// 4. Return the result
		result.addString(Win_message.SUCCESS);
		return result;
	}

	public Win_message list_users_request(String address)
	{
		/*
		 * List all the users
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, return the list of users
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, return the list of users
		result.addString(Win_message.SUCCESS);
		result.addStrings(this.server_db.users_with_common_tags(this.connections_manager.get_username(address)));
		return result;
	}

	public Win_message list_followings_request(String address)
	{
		/*
		 * list all the users that the user is following
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, return the list of users
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, return the list of users
		result.addString(Win_message.SUCCESS);
		result.addStrings(this.server_db.user_followings(this.connections_manager.get_username(address)));
		return result;
	}

	public Win_message follow_request(String username, String address)
	{
		/*
		 * Follow a user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to follow the user
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to follow the user
		int res = this.server_db.follow_username(this.connections_manager.get_username(address), username);
		if (res == Server_DB.DB_ERROR_CODE.SUCCESS.ordinal()) {
			// 4. Return the result
			result.addString(Win_message.SUCCESS);
		} else {
			// 4. Return the result
			result.addString(Win_message.ERROR);
			result.addString(Server_DB.DB_ERROR_CODE.getStringOf(res));
		}

		return result;
	}

	public Win_message unfollow_request(String username, String address)
	{
		/*
		 * Unfollow a user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to unfollow the user
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to unfollow the user
		if (this.server_db.unfollow_username(this.connections_manager.get_username(address), username) == 0) {
			// 4. Return the result
			result.addString(Win_message.SUCCESS);
		} else {
			// 4. Return the result
			result.addString(Win_message.ERROR);
			result.addString("User not found");
		}

		return result;
	}

	public Win_message create_post_request(String address, String title, String content)
	{
		/*
		 * Create a post
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to create the post
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to create the post
		if (this.server_db.add_post(this.connections_manager.get_username(address),
				this.connections_manager.get_username(address), title, content) == 0) {
			// 4. Return the result
			result.addString(Win_message.SUCCESS);
		} else {
			// 4. Return the result
			result.addString(Win_message.ERROR);
			result.addString("Error creating post");
		}

		return result;
	}

	public Win_message blog_request(String address) {
		/*
		 * Get the blog of the user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to get the blog
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to get the blog
		ArrayList<Post_simple> posts = this.server_db.get_blog(this.connections_manager.get_username(address));
		if (posts == null) {
			// 4. Return the result
			result.addString(Win_message.ERROR);
			result.addString("Error getting blog");
			return result;
		}

		// 4. Return the result
		result.addString(Win_message.SUCCESS);
		for (Post_simple post : posts) {
			result.addString(post.serialize());
		}

		return result;
	}

	public Win_message show_post_request(String address, String post_id) {
		/*
		 * Get the blog of the user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to get the blog
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to get the blog
		Post_detailed post = this.server_db.get_post_detailed(post_id);
		if (post == null) {
			// 4. Return the result
			result.addString(Win_message.ERROR);
			result.addString("Error getting post");
			return result;
		}

		// 4. Return the result
		result.addString(Win_message.SUCCESS);
		result.addString(post.serialize());

		return result;
	}

	public Win_message show_feed_request(String address) {
		/*
		 * Get the feed of the user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to get the feed
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to get the feed
		ArrayList<Post_simple> posts = this.server_db.get_feed(this.connections_manager.get_username(address));
		if (posts == null) {
			// 4. Return the result
			result.addString(Win_message.ERROR);
			result.addString("Error getting feed");
			return result;
		}

		// 4. Return the result
		result.addString(Win_message.SUCCESS);
		for (Post_simple post : posts) {
			result.addString(post.serialize());
		}

		return result;
	}

	public Win_message delete_post_request(String address, String postId) {
		/*
		 * Delete the post identified by postId
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to delete the post
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to delete the post
		if (this.server_db.delete_post(connections_manager.get_username(address), postId) == 0) {
			// 4. Return the result
			result.addString(Win_message.SUCCESS);
		} else {
			// 4. Return the result
			result.addString(Win_message.ERROR);
			result.addString("Error deleting post");
		}

		return result;
	}

	public Win_message rewin_post_request(String address, String postId) {
		/*
		 * Rewin the post identified by postId
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to rewin the post
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to rewin the post
		if (this.server_db.rewin_post(connections_manager.get_username(address), postId) == 0) {
			// 4. Return the result
			result.addString(Win_message.SUCCESS);
		} else {
			// 4. Return the result
			result.addString(Win_message.ERROR);
			result.addString("Error rewinning post");
		}

		return result;
	}

	public Win_message comment_request(String address, String postId, String comment) {
		/*
		 * Comment on the post identified by postId
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to comment on the post
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to comment on the post
		if (this.server_db.comment_post(connections_manager.get_username(address), postId, comment) == 0) {
			// 4. Return the result
			result.addString(Win_message.SUCCESS);
		} else {
			// 4. Return the result
			result.addString(Win_message.ERROR);
			result.addString("Error commenting on post");
		}

		return result;
	}

	public Win_message rate_request(String address, String postId, String rate) {
		/*
		 * Rate the post identified by postId
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to rate the post
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to rate the post
		if (this.server_db.rate_post(connections_manager.get_username(address), postId, rate) == 0) {
			// 4. Return the result
			result.addString(Win_message.SUCCESS);
		} else {
			// 4. Return the result
			result.addString(Win_message.ERROR);
			result.addString("Error rating post");
		}

		return result;
	}

	public Win_message wallet_request(String address)
	{
		/*
		 * Get the wallet of the user
		 *
		 * 1. Check if the user is logged in
		 * 2. If the user is not logged in, return an error message
		 * 3. If the user is logged in, ask the database to get the wallet
		 * 4. Return the result
		 */

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address))
		{
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to get the wallet
		Wallet_simple wallet = this.server_db.get_wallet(connections_manager.get_username(address));

		// 4. Return the result
		if (wallet != null)
		{
			result.addString(Win_message.SUCCESS);
			result.addString(wallet.serialize());
		}
		else
		{
			result.addString(Win_message.ERROR);
			result.addString("Error getting wallet");
		}

		return result;
	}

	public Win_message wallet_btc_request(String address)
	{
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

		Win_message result = new Win_message();

		// 1. Check if the user is logged in
		if (!this.connections_manager.is_address_connected(address)) {
			// 2. If the user is not logged in, return an error message
			result.addString(Win_message.ERROR);
			result.addString("User not logged in with this address");
			return result;
		}

		// 3. If the user is logged in, ask the database to get the user's wallet balance
		double balance = this.server_db.get_wallet_balance(connections_manager.get_username(address));

		// 4. Get the BTC currency with the random.org website
		try {
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

			// DEBUG
			// System.out.println("BTC currency: " + response);

			double btc;
			try {
				btc = Double.parseDouble(response.toString());
			} catch (NumberFormatException e) {
				// 5. Return the wallet balance in BTC currency
				result.addString(Win_message.ERROR);
				result.addString("Error getting BTC currency");
				return result;
			}

			// 5. Return the wallet balance in BTC currency
			result.addString(Win_message.SUCCESS);
			result.addString(String.valueOf(balance * (btc / 1000)));

		} catch (IOException e) {
			System.out.println("Error getting BTC currency : " + e.getMessage());
			result.addString(Win_message.ERROR);
			result.addString("Error getting BTC currency");
		}

		return result;
	}

	/**
	 * receive_updates
	 * this method register the client to receive updates about the followers
	 *
	 * @param callback the callback object to call when an update is pending
	 * @param username the username of the client
	 * @return 0 if success, an error code otherwise
	 */
	public int receive_updates(Client_RMI_Interface callback, String username) {
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
			callback.send_followers(this.server_db.user_followers(username).toArray(new String[0]));
			callback.send_multicast_details(this.properties.get_multicast_address(),
					this.properties.get_multicast_port(), "wlan1");
		} catch (RemoteException e) {
			throw new RuntimeException(e);
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

	public void interupt_rewards_thread() {
		this.rewards_thread.interrupt();
	}

	public void reward_users() {
		server_db.reward_users();
	}
}
