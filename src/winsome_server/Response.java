package winsome_server;

import winsome_comunication.WinsomeMessage;

public class Response {
	// member variables
	private final WinsomeMessage message;
	private final String client_address;

	// constructor
	public Response(WinsomeMessage message, String client_address) {
		this.message = message;
		this.client_address = client_address;
	}

	// getters
	public WinsomeMessage get_message() {
		return message;
	}

	public String get_client_address() {
		return client_address;
	}
}
