package winsome_server;

import winsome_comunication.WinsomeMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

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
         * 3.4. If a client is ready to write, write the response to it
         * 4. Close the server socket channel
         */

        // 1. Create the selector and the server socket channel
        Selector selector = null;
        ServerSocketChannel server_socket_channel = null;

        try {
            server_socket_channel = ServerSocketChannel.open();
            ServerSocket ss = server_socket_channel.socket();
            InetSocketAddress address = new InetSocketAddress(server.get_properties().get_tcp_port());
            ss.bind(address);
            server_socket_channel.configureBlocking(false);
            selector = Selector.open();
            // 2. Register the server socket channel to the selector
            server_socket_channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        // 3. Loop
        while (true) {
            // 3.1. Wait for a new connection or a new request
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            Set<SelectionKey> ready_keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = ready_keys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                // 3.2. If a new connection is accepted, put it in the selector
                if (key.isAcceptable()) {
                    try {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);

                        // Debug
                        System.out.println("new client accepted : " + client);
                        client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // 3.3. If a new request is received, add it to the queue of requests to serve
                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    server.add_client_to_serve(new Client_to_serve(client.socket().getInetAddress().toString(),
                            Client_to_serve.Operation.read));
                }

                // 3.4. If a client is ready to write, write the response to it
                if (key.isWritable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    server.add_client_to_serve(new Client_to_serve(client.socket().getInetAddress().toString(),
                            Client_to_serve.Operation.write));
                }
            }
        }



    }
}
