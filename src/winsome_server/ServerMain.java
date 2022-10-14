package winsome_server;

public class ServerMain {
	public static void main(String[] args) {
		Server s = new Server("server_config.txt", "client_config.txt");

		Runtime.getRuntime().addShutdownHook(new ServerSH(s));

		s.server_welcome_service();
	}
}