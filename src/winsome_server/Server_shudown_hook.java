package winsome_server;

public class Server_shudown_hook extends Thread {
	private Server server;

	public Server_shudown_hook(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		/*
		 * This method is used to save the database to the files when the server is shut down.
		 *
		 * 1. Save the database to the files.
		 * 2. Shut down Server_Rewards_Thread.
		 */

		// 1. Save the database to the files.
		server.save_DB();

		// 2. Shut down Server_Rewards_Thread.
		server.interupt_rewards_thread();
	}
}
