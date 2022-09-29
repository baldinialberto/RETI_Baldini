package winsome_client;

public class Client_shutdown_hook extends Thread {
	private final Client client;

	public Client_shutdown_hook(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		if (client.is_on()) client.exit();
	}
}
