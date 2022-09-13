package winsome;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ServerProperties {
    static final String server_address_keyword = "SERVER";
    static final String multicast_address_keyword = "MULTICAST";
    static final String registry_address_keyword = "REGHOST";
    static final String udp_port_keyword = "UDPPORT";
    static final String tcp_port_keyword = "TCPPORT";
    static final String multicast_port_keyword = "MCASTPORT";
    static final String registry_port_keyword = "REGPORT";
    static final String socket_timeout_millis_keyword = "TIMEOUT";

    private String server_address;
    private String multicast_address;
    private String registry_address;
    private final int udp_port;
    private final int tcp_port;
    private final int multicast_port;
    private final int registry_port;
    private final long socket_timeout_millis;

    public void dump_to_file(String filePath) {
        StringBuilder sb = new StringBuilder();

        // add server address
        sb.append("# Indirizzo del server\n" + server_address_keyword + "=" + server_address + "\n");
        // add server multicast address
        sb.append("# Indirizzo di multicast\n" + multicast_address_keyword + "=" + multicast_address + "\n");
        // add registry address
        sb.append("# Host su cui si trova il registry\n" + registry_address_keyword + "=" + registry_address + "\n");
        // add udp port
        sb.append("# Porta UDP del servert\n" + udp_port_keyword + "=" + udp_port + "\n");
        // add tcp port
        sb.append("# Porta TCP del server\n" + tcp_port_keyword + "=" + tcp_port + "\n");
        // add multicast port
        sb.append("# Porta di multicast\n" + multicast_port_keyword + "=" + multicast_port + "\n");
        // add registry port
        sb.append("# Porta del registry RMI\n" + registry_port_keyword + "=" + registry_port + "\n");
        // add socket timeout
        sb.append("# Timeout della socket\n" + socket_timeout_millis_keyword + "=" + socket_timeout_millis + "\n");

        // dump string to file
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] strToBytes = sb.toString().getBytes();
            outputStream.write(strToBytes);
            outputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static ServerProperties readFile(String filePath) {
        class ServerProperties_temp {
            private String server_address = "";
            private String multicast_address = "";
            private String registry_address = "";
            private int udp_port = 0;
            private int tcp_port = 0;
            private int multicast_port = 0;
            private int registry_port = 0;
            private long socket_timeout_millis = 0;

            public boolean server_address_isValid() {
                return !server_address.equals("");
            }

            public boolean multicast_address_isValid() {
                return !multicast_address.equals("");
            }

            public boolean registry_address_isValid() {
                return !registry_address.equals("");
            }

            public boolean udp_port_isValid() {
                return udp_port > 0;
            }

            public boolean tcp_port_isValid() {
                return tcp_port > 0;
            }

            public boolean multicast_port_isValid() {
                return multicast_port > 0;
            }

            public boolean registry_port_isValid() {
                return registry_port > 0;
            }

            public boolean socket_timeout_millis_isValid() {
                return socket_timeout_millis > 0;
            }

            public void readLine(String line) {
                if (line.toCharArray()[0] == '#')
                    return;
                String[] tokens = line.split("=");
                switch (tokens[0]) {
                    case ServerProperties.server_address_keyword:
                        server_address = new String(tokens[1].getBytes(StandardCharsets.UTF_8));
                        break;
                    case ServerProperties.multicast_address_keyword:
                        multicast_address = new String(tokens[1].getBytes(StandardCharsets.UTF_8));
                        break;
                    case ServerProperties.registry_address_keyword:
                        registry_address = new String(tokens[1].getBytes(StandardCharsets.UTF_8));
                        break;
                    case ServerProperties.udp_port_keyword:
                        try {
                            udp_port = Integer.parseInt(tokens[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ServerProperties.tcp_port_keyword:
                        try {
                            tcp_port = Integer.parseInt(tokens[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ServerProperties.multicast_port_keyword:
                        try {
                            multicast_port = Integer.parseInt(tokens[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ServerProperties.registry_port_keyword:
                        try {
                            registry_port = Integer.parseInt(tokens[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ServerProperties.socket_timeout_millis_keyword:
                        try {
                            socket_timeout_millis = Long.parseLong(tokens[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        ErrorHandler.printError(Thread.currentThread().getStackTrace()[0].getMethodName(),
                                String.format("keyword not recognized [%s]", tokens[0]));
                        break;
                }

            }

            public boolean isValid() {
                return server_address_isValid() &&
                        multicast_address_isValid() &&
                        registry_address_isValid() &&
                        udp_port_isValid() &&
                        tcp_port_isValid() &&
                        multicast_port_isValid() &&
                        registry_port_isValid() &&
                        socket_timeout_millis_isValid();
            }

            public String getServer_address() {
                return server_address_isValid() ? server_address : "localhost";
            }

            public String getMulticast_address() {
                return multicast_address_isValid() ? multicast_address : "multicast";
            }

            public String getRegistry_address() {
                return registry_address_isValid() ? registry_address : "registry";
            }

            public int getUdp_port() {
                return udp_port_isValid() ? udp_port : 8070;
            }

            public int getTcp_port() {
                return tcp_port_isValid() ? tcp_port : 8080;
            }

            public int getMulticast_port() {
                return multicast_port_isValid() ? multicast_port : 8090;
            }

            public int getRegistry_port() {
                return registry_port_isValid() ? registry_port : 8100;
            }

            public long getSocket_timeout_millis() {
                return socket_timeout_millis_isValid() ? socket_timeout_millis : 1000;
            }
        }
        File file = new File(filePath);
        if (!file.exists()) {
            ErrorHandler.printError(Thread.currentThread().getStackTrace()[1].getMethodName(),
                    String.format("file '%s' not found", filePath));
            return null;
        }
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            ServerProperties_temp temp = new ServerProperties_temp();
            stream.forEach(temp::readLine);
            return new ServerProperties(temp.getServer_address(), temp.getMulticast_address(),
                    temp.getRegistry_address(), temp.getUdp_port(), temp.getTcp_port(),
                    temp.getMulticast_port(), temp.getRegistry_port(), temp.getSocket_timeout_millis());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ServerProperties(String server_address, String multicast_address, String registry_address,
            int udp_port, int tcp_port, int multicast_port, int registry_port,
            long socket_timeout_millis) {
        this.server_address = server_address;
        this.multicast_address = multicast_address;
        this.registry_address = registry_address;
        this.udp_port = udp_port;
        this.tcp_port = tcp_port;
        this.multicast_port = multicast_port;
        this.registry_port = registry_port;
        this.socket_timeout_millis = socket_timeout_millis;
    }

    public void setServer_address(Server.ServerAuthorization a, String newServerAdrress) {
        if (a == null) {
            System.err.println(new WinsomeExceptions.UnauthorizedAction().toString());
            return;
        }
        if (!newServerAdrress.equals(""))
            server_address = newServerAdrress;

    }

    public void setMulticast_address(Server.ServerAuthorization a, String newMulticastAddress) {
        if (a == null) {
            System.err.println(new WinsomeExceptions.UnauthorizedAction().toString());
            return;
        }
        if (!newMulticastAddress.equals(""))
            multicast_address = newMulticastAddress;
    }

    public void setRegistry_address(Server.ServerAuthorization a, String newRegistryAddress) {
        if (a == null) {
            System.err.println(new WinsomeExceptions.UnauthorizedAction().toString());
            return;
        }
        if (!newRegistryAddress.equals(""))
            registry_address = newRegistryAddress;
    }

    public String getServer_address() {
        return server_address;
    }

    public String getMulticast_address() {
        return multicast_address;
    }

    public String getRegistry_address() {
        return registry_address;
    }

    public int getUdp_port() {
        return udp_port;
    }

    public int getTcp_port() {
        return tcp_port;
    }

    public int getMulticast_port() {
        return multicast_port;
    }

    public int getRegistry_port() {
        return registry_port;
    }

    public long getSocket_timeout_millis() {
        return socket_timeout_millis;
    }

    @Override
    public String toString() {
        return String.format("--Server_Properties--\n" +
                "%s = %s\n" + "%s = %s\n" + "%s = %s\n" + "%s = %s\n" +
                "%s = %s\n" + "%s = %s\n" + "%s = %s\n" + "%s = %s\n",
                ServerProperties.server_address_keyword, this.server_address,
                ServerProperties.multicast_address_keyword, this.multicast_address,
                ServerProperties.registry_address_keyword, this.registry_address,
                ServerProperties.udp_port_keyword, this.udp_port,
                ServerProperties.tcp_port_keyword, this.tcp_port,
                ServerProperties.multicast_port_keyword, this.multicast_port,
                ServerProperties.registry_port_keyword, this.registry_port,
                ServerProperties.socket_timeout_millis_keyword, this.socket_timeout_millis);
    }
}
