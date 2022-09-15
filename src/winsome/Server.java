package winsome;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class                                                                                                                                                 Server {
    class ServerAuthorization {
        private ServerAuthorization() {
        };
    };

    private final ServerProperties properties;
    public ServerSocket tcp_server_socket;
    public ServerSocket udp_server_socket;
    private final ExecutorService workers_thread_poll;

    private ArrayList<User_serializable> users;

    private ServerAuthorization authorization() {
        return new ServerAuthorization();
    }

    public Server(String serverProperties_configFile) {
        this.properties = ServerProperties.readFile(serverProperties_configFile);
        this.workers_thread_poll = Executors.newCachedThreadPool();

        try {
            assert properties != null;
            this.tcp_server_socket = new ServerSocket(properties.getTcp_port());
            this.udp_server_socket = new ServerSocket(properties.getUdp_port());

            String server_address = InetAddress.getLocalHost().getHostAddress();
            properties.setServer_address(authorization(), server_address);
            properties.dump_to_file(serverProperties_configFile);

            // add stub for RMI
            ServerRMI serverRMI = new ServerRMI(this, authorization());
            RMI_registration_int stub = (RMI_registration_int) UnicastRemoteObject.exportObject(serverRMI, 0);

            LocateRegistry.createRegistry(properties.getRegistry_port());
            Registry r = LocateRegistry.getRegistry(properties.getRegistry_port());

            r.rebind("ServerRMI", stub);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }



        // submit server welcome service
        Thread reception_thread = new ServerReception(this);
        reception_thread.start();
    }

    public void add_client(Socket client_socket) {
        workers_thread_poll.submit(new ServerWorker(this, client_socket));
    }

    public String get_properties_toString() {
        return this.properties.toString();
    }

    public ServerProperties get_properties() {
        return this.properties;
    }


    public void read_jsonBackup(String filename) {
    }

    public void write_jsonBackup(String filename) {
    }

    public int register_user(String username, String password, String[] tags) {
        return 0;
    }

}
