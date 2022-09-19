package winsome_server;

import winsome_comunication.WinsomeMessage;
import winsome_comunication.Winsome_Confirmation;

import java.io.*;
import java.net.Socket;

public class Server_worker implements Runnable {
    // member variables
    private final Server server;
    private final Socket socket;

    // constructor
    public Server_worker(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    // methods
    @Override
    public void run() {
        /*
         * server worker thread
         *
         * 1. Initialize Reader and Writer
         * 2. Read the message from the client
         * 3. Parse the message and execute the command server side
         * 3.1 If the message is a logout message, close the connection
         * 4. Send the result back to the client
         */

        String request = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        String client_address = null;

        try {
            // 1. Initialize Reader and Writer
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            client_address = socket.getInetAddress().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        do {
            try {
                // 2. Read the message from the client
                request = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Debug
            System.out.println("worker` received " + request + " from " + client_address);

            if (request == null) break;

            // 3. Parse the message and execute the command server side
            String[] request_split = request.split(" ");
            String command = request_split[0];
            String[] args = new String[request_split.length - 1];
            System.arraycopy(request_split, 1, args, 0, request_split.length - 1);

            // 3.1 If the message is a logout message, close the connection
            if (command.equals("logout")) {
                continue;
            }

            // 4. Send the result back to the client
            try {
                redirect_request(command, args, out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } while (!request.equals("logout"));

        System.out.println("worker is closing connection to " + client_address);
    }

    private void redirect_request(String command, String[] args, BufferedWriter out) throws IOException
    {
        /*
         * This method is used to redirect a request to the correct method
         *
         * map the command to the correct method
         * <command, method>
         *
         * login, login_user
         * logout, logout_user
         */

        WinsomeMessage message = null;

        switch (command) {
            case "login":
                message = server.login_user(args[0], args[1]);
                Winsome_Confirmation confirmation = Winsome_Confirmation.deserialize(message.get_message());
                message.send_message(socket);
                if (confirmation.get_success()) {
                    server.add_client(args[0], socket);
                }
                break;
            case "logout":
                server.logout_user(args[0]);
                server.remove_client(args[0]);
                break;
            default:
                System.out.println("command not found");
                break;
        }
    }
}
