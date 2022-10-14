package winsome_client;

public class ClientSH extends Thread {
	private final Client client;

	public ClientSH(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		if (client.is_on()) client.exit();
	}
}
