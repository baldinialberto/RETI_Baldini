package winsome_server;

import winsome_comunication.Win_message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collections;

public class Worker_task implements Runnable {
    // member variables
    private final Server server;
    private final Selector selector;
    private final SelectionKey selection_key;


    // constructor
    public Worker_task(Server server, Selector selector, SelectionKey selection_key) {
        this.server = server;
        this.selector = selector;
        this.selection_key = selection_key;
    }

    // methods
    @Override
    public void run() {
        /*
         * This method is called when a new thread is created or a thread is reused
         *
         * The method will read the message from the client and process it
         * After the message is processed, the method will put the answer back into the buffer of the key
         * and register the key to the selector as writable
         *
         * 1. read the message from the client
         * 2. process the message
         * 3. put the answer into the buffer
         * 4. register the key as writable
         */

        /* 1. read the message from the client
         * the first message from the client is an array of strings
         */
        SocketChannel socket_channel = (SocketChannel) selection_key.channel();

        try {
            Win_message message = Win_message.receive(socket_channel);

            // 2. process the message
            if (message.toString().equals("")) {
                // the client has closed the connection
                // close the connection
                socket_channel.close();
                selection_key.cancel();
                return;
            }
            if (message.toString().equals("exit")) {
                // the client has closed the connection
                // close the connection
                socket_channel.close();
                selection_key.cancel();
                return;
            }
            Win_message response = process_message(message.toString());

            // 3. put the answer into the buffer + // 4. register the key as writable
            socket_channel.register(selector, SelectionKey.OP_WRITE, response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Win_message process_message(String request) {
        // TODO
        Win_message response = new Win_message(Collections.singletonList("response"));
        response.addString("Login_success");
        return response;
    }
}
