package winsome_server;

import java.io.IOException;
import java.net.Socket;

public class Server_welcome_thread extends Thread {
    private final Server server;

    public Server_welcome_thread(Server server) {
        this.server = server;
    }

    private void print_new_client(String address, int port) {
        System.out.printf("connection accepted to client at : %s:%d\n", address, port);
    }

    @Override
    public void run() {
        while (this.isAlive()) {
            System.out.println("Server is listening for new clients...");
            try {
                Socket newConnection = server.tcp_server_socket.accept();
                print_new_client(newConnection.getInetAddress().getHostAddress(), newConnection.getPort());
                server.serve_new_client(newConnection);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
