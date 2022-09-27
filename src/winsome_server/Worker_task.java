package winsome_server;

import winsome_comunication.Win_message;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

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
            if (message.getString(0).equals(Win_message.EXIT)) {
                // the client has closed the connection
                // close the connection
                socket_channel.close();
                selection_key.cancel();
                return;
            }
            Win_message response = process_message(
                    message.getStringsArray(),
                    socket_channel.getRemoteAddress().toString()
            );

            // 3. put the answer into the buffer + // 4. register the key as writable
            socket_channel.register(selector, SelectionKey.OP_WRITE, response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Win_message process_message(String[] request, String address) {
        /*
         * This method will process the request and return the response
         *
         * 1. parse the request
         * 2. process the request
         * 3. return the response
         */

        /* 1. parse the request (an array of strings)
         * the first string is the type of the request
         * the rest of the strings are the parameters of the request
         * the type must be one of the following:
         * 1. login
         * 2. logout
         * 3. list_users
         * 4. follow
         * 5. unfollow
         * 6. ...
         *
         * If the type is not one of the above, an error message will be returned
         */
        Win_message response = new Win_message();
        String type = request[0];
        String[] parameters = new String[request.length - 1];
        System.arraycopy(request, 1, parameters, 0, request.length - 1);

        /* 2. process the request
         * the request will be processed by the server
         * the server will return a response
         */

        // 2.1 login
        if (type.equals("login")) {
            // the request is a login request
            // the parameters are username and password
            // the response will be a string "success" or "error, reason"
            String username = parameters[0];
            String password = parameters[1];
            response = this.server.login_request(username, password, address);
        }
        // 2.2 logout
        else if (type.equals("logout")) {
            // the request is a logout request
            // no parameters are provided by the client
            // the response will be a string "success" or "error, reason"
            response = this.server.logout_request(address);
        }
        // 2.3 list_users
        else if (type.equals("list_users")) {
            // the request is a list_users request
            // no parameters are provided by the client
            // the response will be a string "success" or "error, reason"
            response = this.server.list_users_request(address);
        }
        // 2.4 follow
        else if (type.equals("follow")) {
            // the request is a follow request
            // the parameter is the username of the user to follow
            // the response will be a string "success" or "error, reason"
            String username = parameters[0];
            response = this.server.follow_request(username, address);
        }
        // 2.5 unfollow
        else if (type.equals("unfollow")) {
            // the request is an unfollow request
            // the parameters is the username of the user to unfollow
            // the response will be a string "success" or "error, reason"
            String username = parameters[0];
            response = this.server.unfollow_request(username, address);
        }

        // 3. return the response
        return response;
    }
}
