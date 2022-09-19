package winsome_server;


import java.util.concurrent.ConcurrentHashMap;

public class Client_connections_Manager extends ConcurrentHashMap<String, Client_connection> {
	/*
	 * This class is used to manage the client connections.
	 */

	public void add_client(String username, String ip_address, int port) {
		/*
		 * This method is used to add a new client to the HashMap.
		 *
		 * 1. Create a new Client_connection object.
		 * 2. Add the Client_connection object to the HashMap.
		 */
		// 1. Create a new Client_connection object.
		Client_connection client = new Client_connection(username, ip_address, port);

		// 2. Add the Client_connection object to the HashMap.
		this.put(username, client);
	}
	public void remove_client(String username) {
		/*
		 * This method is used to remove a client from the HashMap.
		 *
		 * 1. Remove the client from the HashMap.
		 */
		// 1. Remove the client from the HashMap.
		this.remove(username);
	}
}
