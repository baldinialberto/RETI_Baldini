package winsome_server;

public class Client_to_serve {
	// member variables
	public enum Operation{read, write};
	private final String client_address;
	private final Operation operation;

	// constructor
	public Client_to_serve(String client_address, Operation operation) {
		this.client_address = client_address;
		this.operation = operation;
	}

	// getters
	public String get_client_address() {
		return client_address;
	}

	public boolean is_ready_to_read() {
		return operation == Operation.read;
	}

	public boolean is_ready_to_write() {
		return operation == Operation.write;
	}
}
