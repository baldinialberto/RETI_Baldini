package winsome;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    class ServerAuthorization {
        private ServerAuthorization() {
        };
    };

    private ServerProperties properties;
    public ServerSocket tcp_server_socket;
    public ServerSocket udp_server_socket;
    private String server_address;
    private ExecutorService tpe;

    private ArrayList<User_serializable> users;

    private ServerAuthorization authorization() {
        return new ServerAuthorization();
    }

    public Server(String serverProperties_configFile) {
        this.properties = ServerProperties.readFile(serverProperties_configFile);
        this.tpe = Executors.newCachedThreadPool();

        try {
            this.tcp_server_socket = new ServerSocket(properties.getTcp_port());
            this.udp_server_socket = new ServerSocket(properties.getUdp_port());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // update server address
        try {
            server_address = InetAddress.getLocalHost().getHostAddress();
            properties.setServer_address(authorization(), server_address);
            properties.dump_to_file(serverProperties_configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // submit server welcome service
        tpe.submit(new ServerReception(this));
    }

    public void add_client(Socket client_socket) {
        tpe.submit(new ServerWorker(this, client_socket));
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

}
