package winsome_server;

import java.io.IOException;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {


	static class ServerAuthorization {
		private ServerAuthorization() {
		}
	}

	private final Server_properties properties;
	public ServerSocket tcp_server_socket;
	public ServerSocket udp_server_socket;
	private final ExecutorService workers_thread_poll;

	private Thread welcome_thread;

	private Server_DB server_db;

	private User_collection users;
	private Post_collection posts;

	private ServerAuthorization authorization() {
		return new ServerAuthorization();
	}

	// constructor
	public Server(String serverProperties_configFile, String clientProperties_configFile) {
		/*
		 * create a new server object
		 *
		 * 0. Set a Shutdown hook
		 * 1. Create a new worker thread poll
		 * 2. Create a new server properties object
		 * 3. Create a new server database object
		 * 3.1 Try to load the server database
		 * 3.2 If an error occurred, exit the program
		 * 4. Create a new tcp server socket
		 * 5. Create a new udp server socket
		 * 6. Create a new server RMI object and bind it to the registry
		 * 7. Start the welcome thread
		 *
		 */

		// 0. Set a Shutdown hook
		Runtime.getRuntime().addShutdownHook(new Server_shudown_hook(this));

		// 1. Create a new worker thread poll
		this.workers_thread_poll = Executors.newCachedThreadPool();

		// 2. Create a new server properties object
		this.properties = new Server_properties(serverProperties_configFile, clientProperties_configFile);
		// 2.1 Store the server address in the properties file
		try {
			this.properties.set_server_address(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		// 2.2 save the properties to the client properties file
		this.properties.write_properties();

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
			this.tcp_server_socket = new ServerSocket(this.properties.get_tcp_port());

			// 5. Create a new udp server socket
			this.udp_server_socket = new ServerSocket(this.properties.get_udp_port());

			// 6. Create a new server RMI object and bind it to the registry
			Server_RMI server_rmi = new Server_RMI(this);
			RMI_registration_int stub = (RMI_registration_int) UnicastRemoteObject.exportObject(server_rmi, 0);
			LocateRegistry.createRegistry(this.properties.get_registry_port());
			Registry registry = LocateRegistry.getRegistry(this.properties.get_registry_port());
			registry.rebind(this.properties.get_rmi_name(), stub);

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			System.err.println("Server exception: " + e);
			e.printStackTrace();
		}

		// 7. Start the welcome thread
		this.welcome_thread = new Server_welcome_thread(this);
		this.welcome_thread.start();
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
			server_db.get_users().add_user("user_" + i, "password_" + i, tags);
		}

		// 3. For each user, add 1-5 Post with random title and text.
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < (int) (Math.random() * 5); j++) {
				server_db.add_post("user_" + i, "user_" + i, "title_" + j + "_" + i, "text" + j);
			}
		}

		// 4. Save the server_db.
		server_db.save_DB();

		// 5. Print the server_db.
		System.out.println(server_db.get_users().toString());
	}

	public void add_client(Socket client_socket) {
		workers_thread_poll.submit(new Server_worker(this, client_socket));
	}

	public String get_properties_toString() {
		return this.properties.toString();
	}

	public Server_properties get_properties() {
		return this.properties;
	}




	public int register_user(String username, String password, String[] tags) {
		/*
		  register a new user

		  1. add the user to database and return the result

		 */

		return this.server_db.add_user(username, password, tags);
	}

	public int login_user(String username, String password) {
		/*
		  login a user

		  1. check if username exists
		  2. if yes, check if password is correct
		  3. set user as logged in
		  4. return 0 if success, -1 if username does not exist, -2 if password is incorrect
		 */

//		// 1. check if username exists
//		User user = server_db.get_user(username);
//
//		if (user == null) {
//			return -1;
//		}

		// 2. if yes, check if password is correct
		// TODO

		// 3. set user as logged in
		// TODO

		// 4. return 0 if success, -1 if username does not exist, -2 if password is incorrect
		return 0;
	}

	public int logout_user(String username) {
		/*
		  logout a user

		  1. check if username exists
		  2. if yes, check if user is logged in
		  3. if yes, logout user
		  4. return 0 if success, -1 if username does not exist, -2 if user is not logged in
		 */

//		// 1. check if username exists
//		User user = server_db.get_user(username);
//
//		if (user == null) {
//			return -1;
//		}

		// 2. if yes, check if user is logged in
		// TODO

		// 3. if yes, logout user
		// TODO

		return 0;
	}

}
