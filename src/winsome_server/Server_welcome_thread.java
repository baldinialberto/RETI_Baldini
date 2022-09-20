package winsome_server;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

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
//            System.out.println("Server is listening for new clients...");
//            try {
//                Socket newConnection = server.tcp_server_socket.accept();
//                print_new_client(newConnection.getInetAddress().getHostAddress(), newConnection.getPort());
//                server.serve_new_client(newConnection);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }

            /*
             * Welcome_thread main loop
             * The welcome thread is used to accept new connections and to serve new requests from clients
             * To do so it uses a Multiplexed ServerSocketChannel and a Selector
             * When a new connection is accepted, the welcome thread puts it in the selector
             * When a client sends a request, the welcome thread puts it in a queue of requests to serve
             * The queue is then served by the worker threads
             *
             * 1. Create the selector and the server socket channel
             * 2. Register the server socket channel to the selector
             * 3. Loop
             * 3.1. Wait for a new connection or a new request
             * 3.2. If a new connection is accepted, put it in the selector
             * 3.3. If a new request is received, add it to the queue of requests to serve
             * 4. Close the server socket channel
             */

            // 1. Create the selector and the server socket channel
            Selector selector = null;
            ServerSocketChannel server_socket_channel = null;

            // 2. Register the server socket channel to the selector
            
        }
    }
}
