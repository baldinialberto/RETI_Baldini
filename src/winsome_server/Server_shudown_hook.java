package winsome_server;

public class Server_shudown_hook extends Thread {
	private final Server server;

	public Server_shudown_hook(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		/*
		 * This method is used to save the database to the files when the server is shut down.
		 *
		 * 1. Close the server
		 */

		// 1. Close the server
		server.close();
	}
}
