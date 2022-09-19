package winsome_server;

public class Client_connection {
	/*
	 * This class is used to store the information of a client connection
	 * the server is connected to a client through a socket
	 * the main connection is a TCP connection
	 * the server also uses UDP to broadcast a message to the clients
	 * When the TCP connection is established, the server will wait for a message from the client
	 * telling the server the client's RMI details
	 *
	 * available methods:
	 * 1. update_wallet(float amount)
	 * 2. new_follower(String username)
	 * 3. follower_left(String username)
	 * 4. close_connection()
	 */

	// member variables
	private String username;
	private String ip_address;
	private int port;

	// constructor
	public Client_connection(String username, String ip_address, int port) {
		this.username = username;
		this.ip_address = ip_address;
		this.port = port;
	}

	// getters
	public String getUsername() {
		return username;
	}
	public String getIp_address() {
		return ip_address;
	}
	public int getPort() {
		return port;
	}

	// setters // not used

	// methods
	public void update_wallet(float amount) {
		// TODO
	}
	public void new_follower(String username) {
		// TODO
	}
	public void follower_left(String username) {
		// TODO
	}
	public void close_connection() {
		// TODO
	}
}
