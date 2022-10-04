package winsome_server;


import winsome_comunication.Client_RMI_Interface;

public class Client_connection {
	// member variables
	private final String address;
	private final String username;
	private Client_RMI_Interface callback;

	// constructor
	public Client_connection(String address, String username, Client_RMI_Interface callback) {
		this.address = address;
		this.username = username;
		this.callback = callback;
	}

	// partial constructor
	public Client_connection(String address, String username) {
		this.address = address;
		this.username = username;
		this.callback = null;
	}

	// getters
	public String get_address() {
		return this.address;
	}

	public String get_username() {
		return this.username;
	}

	public Client_RMI_Interface get_callback() {
		return this.callback;
	}

	// setters
	public void set_callback(Client_RMI_Interface callback) {
		this.callback = callback;
	}
}
