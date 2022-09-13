package winsome;

public class ClientMain {
    public static void main(String[] args) {
        Client client = new Client("config.txt");
        client.start_CLI();
    }

}
