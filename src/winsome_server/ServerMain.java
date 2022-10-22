package winsome_server;

public class ServerMain {
	public static void main(String[] args) {
		// Check # args if == 2 then 1st is server_config_file and 2nd is client_config_file
		// else use default config files (server_config.txt and client_config.txt)

		Server s;

		if (args.length == 2) {
			// Create a new server object
			s = new Server(args[0], args[1]);
		} else if (args.length == 0) {
			// Create a new server object
			s = new Server("server_config.txt", "client_config.txt");
		} else {
			System.out.println("Usage: java winsome_server.ServerMain [server_config_file] [client_config_file]");
			return;
		}

		Runtime.getRuntime().addShutdownHook(new ServerSH(s));
		s.server_welcome_service();
		System.out.println("Server shutdown");
	}
}