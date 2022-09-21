package winsome_server;

public class Server_main {
    public static void main(String[] args) {
        Server s = new Server("server_config.txt", "client_config.txt");

        Runtime.getRuntime().addShutdownHook(new Server_shudown_hook(s));

        s.server_welcome_service();
    }
}