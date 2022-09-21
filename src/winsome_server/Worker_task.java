package winsome_server;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

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

        // 2. process the message
        // TODO

        // 3. put the answer into the buffer
        // TODO

        // 4. register the key as writable
        // TODO
    }
}
