package winsome;

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

    }
}
