package winsome;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerReception extends Thread {
    private final Server server;

    public ServerReception(Server server) {
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
                server.add_client(newConnection);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
