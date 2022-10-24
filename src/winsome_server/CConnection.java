package winsome_server;


import winsome_communication.ClientRMI_Interface;

public class CConnection {
	// member variables
	private final String address;
	private final String username;
	private ClientRMI_Interface callback;

	// constructor
	public CConnection(String address, String username, ClientRMI_Interface callback) {
		this.address = address;
		this.username = username;
		this.callback = callback;
	}

	// partial constructor
	public CConnection(String address, String username) {
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

	public ClientRMI_Interface get_callback() {
		return this.callback;
	}

	// setters
	public void set_callback(ClientRMI_Interface callback) {
		this.callback = callback;
	}
}
