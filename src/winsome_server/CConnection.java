package winsome_server;


import winsome_comunication.Client_RMI_Interface;

public class CConnection {
	// member variables
	private final String address;
	private final String username;
	private final Client_RMI_Interface callback;

	// constructor
	public CConnection(String address, String username, Client_RMI_Interface callback) {
		this.address = address;
		this.username = username;
		this.callback = callback;
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
	// none
}
