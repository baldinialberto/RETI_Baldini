package winsome_server;

import java.nio.channels.SocketChannel;

public class CConnection {
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
	private final SocketChannel socket_channel;

	private String username;

	// constructor
	public CConnection(SocketChannel socket_channel) {
		this.socket_channel = socket_channel;
	}

	// getters
	public SocketChannel get_socket_channel() {
		return this.socket_channel;
	}
	public String get_username() {
		return this.username;
	}
	public String get_ip_address() {
		return this.socket_channel.socket().getInetAddress().getHostAddress();
	}
	public int get_port() {
		return this.socket_channel.socket().getPort();
	}

	// setters
	public void set_username(String username) {
		this.username = username;
	}

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

}
