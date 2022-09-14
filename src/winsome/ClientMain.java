package winsome;

public class ClientMain {
    public static void main(String[] args) {
        /*
         * client main
         *
         * 1. create client
         * 2. start command line interface
         */

        // 1. create client
        Client client = new Client("config.txt");

        // 2. start command line interface
        client.start_CLI();
    }

}
