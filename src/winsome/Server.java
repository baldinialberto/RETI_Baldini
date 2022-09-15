package winsome;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	class ServerAuthorization {
		private ServerAuthorization() {
		}

		;
	}

	;

	private final ServerProperties properties;
	public ServerSocket tcp_server_socket;
	public ServerSocket udp_server_socket;
	private final ExecutorService workers_thread_poll;

	private ArrayList<User_serializable> users;

	private Server_DB server_db;

	private ServerAuthorization authorization() {
		return new ServerAuthorization();
	}

	public Server(String serverProperties_configFile) {

		this.workers_thread_poll = Executors.newCachedThreadPool();
		this.properties = ServerProperties.readFile(serverProperties_configFile);
		server_db = new Server_DB();

		try {
			assert properties != null;

			//server_db.JSON_read(this.properties.getBackup_file());

			this.tcp_server_socket = new ServerSocket(properties.getTcp_port());
			this.udp_server_socket = new ServerSocket(properties.getUdp_port());

			String server_address = InetAddress.getLocalHost().getHostAddress();
			properties.setServer_address(authorization(), server_address);
			properties.dump_to_file(serverProperties_configFile);

			// add stub for RMI
			ServerRMI serverRMI = new ServerRMI(this, authorization());
			RMI_registration_int stub = (RMI_registration_int) UnicastRemoteObject.exportObject(serverRMI, 0);

			LocateRegistry.createRegistry(properties.getRegistry_port());
			Registry r = LocateRegistry.getRegistry(properties.getRegistry_port());

			r.rebind("ServerRMI", stub);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}


		// submit server welcome service
		Thread reception_thread = new ServerReception(this);
		reception_thread.start();
	}

	public void add_client(Socket client_socket) {
		workers_thread_poll.submit(new ServerWorker(this, client_socket));
	}

	public String get_properties_toString() {
		return this.properties.toString();
	}

	public ServerProperties get_properties() {
		return this.properties;
	}


	public void read_jsonBackup() {
		try {
			server_db.JSON_read(this.properties.getBackup_file());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void write_jsonBackup() {
		try {
			server_db.JSON_write(this.properties.getBackup_file());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int register_user(String username, String password, String[] tags) {
		/*
		  register a new user

		  1. check if username is already taken
		  2. if not, add user to the database
		  3. return 0 if success, -1 if username is already taken
		 */

		// 1. check if username is already taken
		if (server_db.get_user(username) != null) {
			return -1;
		}

		// 2. if not, add user to the database
		server_db.add_user(new User(username, password, tags));

		// 3. return 0 if success, -1 if username is already taken
		return 0;

	}

	public int login_user(String username, String password) {
		/*
		  login a user

		  1. check if username exists
		  2. if yes, check if password is correct
		  3. set user as logged in
		  4. return 0 if success, -1 if username does not exist, -2 if password is incorrect
		 */

		// 1. check if username exists
		User user = server_db.get_user(username);

		if (user == null) {
			return -1;
		}

		// 2. if yes, check if password is correct
		if (!user.check_psw(password, authorization())) {
			return -2;
		}

		// 3. set user as logged in
		user.set_login_status(true, authorization());

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

		// 1. check if username exists
		User user = server_db.get_user(username);

		if (user == null) {
			return -1;
		}

		// 2. if yes, check if user is logged in
		if (!user.get_login_status()) {
			return -2;
		}

		// 3. if yes, logout user
		user.set_login_status(false, authorization());

		return 0;
	}

}
