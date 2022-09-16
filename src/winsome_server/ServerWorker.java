package winsome_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ServerWorker implements Runnable {
    private final Server server;
    private final Socket socket;

    public ServerWorker(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        /*
         * server worker thread
         *
         * 1. receive client request
         * 2. print client request
         */

        // 1. receive client request
        String request = null;
        OutputStream out = null;
        BufferedReader in = null;
        String client_address = null;

        try {
            out = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            client_address = socket.getInetAddress().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        do {

            try {
                request = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 2. print client request
            System.out.println("worker` received " + request + " from " + client_address);

        } while (!request.equals("logout"));

        System.out.println("worker is closing connection to " + client_address);


        // TODO server.write_jsonBackup();
    }
}
