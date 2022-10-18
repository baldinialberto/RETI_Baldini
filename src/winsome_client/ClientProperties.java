package winsome_client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class ClientProperties {
	/*
	 * client properties
	 *
	 * this class stores all the properties needed by the client
	 * some of them are written by the server like the server addresses and ports
	 *
	 * the properties are stored in a HashMap<String, String>
	 *
	 * the properties:
	 * 1. SERVER=<server_address> (default: localhost)
	 * 2. TCP_PORT=<tcp_port> (default: 8080)
	 * 3. REGISTRY_PORT=<registry_port> (default: 1099)
	 * 4. RMI_NAME=<rmi_name> (default: Server_registration_RMI)
	 * 5. MULTICAST_ADDRESS=<multicast_address> (default: 224.0.1.1)
	 * 6. MULTICAST_PORT=<multicast_port> (default: 8000)
	 */

	// members
	private final HashMap<String, String> properties;

	// constructor
	public ClientProperties(String properties_file) {
		this.properties = new HashMap<>();
		this.read_properties(properties_file);
	}

	// getters
	public String get_server_address() {
		/*
		 * return the server address if it exists, otherwise return the default value
		 */
		return this.properties.getOrDefault("SERVER", "localhost");
	}

	public int get_tcp_port() {
		/*
		 * return the tcp port if it exists, otherwise return the default value
		 */
		return Integer.parseInt(this.properties.getOrDefault("TCP_PORT", "8080"));
	}

	public int get_registry_port() {
		/*
		 * return the registry port if it exists, otherwise return the default value
		 */
		return Integer.parseInt(this.properties.getOrDefault("REGISTRY_PORT", "1099"));
	}

	public String get_rmi_name() {
		/*
		 * return the rmi name if it exists, otherwise return the default value
		 */
		return this.properties.getOrDefault("RMI_NAME", "Server_registration_RMI");
	}

	public String get_multicast_address() {
		/*
		 * return the multicast address if it exists, otherwise return the default value
		 */
		return this.properties.getOrDefault("MULTICAST_ADDRESS", "224.0.1.1");
	}

	public int get_multicast_port() {
		/*
		 * return the multicast port if it exists, otherwise return the default value
		 */
		return Integer.parseInt(this.properties.getOrDefault("MULTICAST_PORT", "8000"));
	}

	// setters // no setters

	// methods
	public void read_properties(String configFile) {
		/*
		 * load the properties from the config file
		 *
		 * 1. try to load the properties from the config file
		 */

		// 1. try to read the properties from the config file
		try {
			FileReader fileReader = new FileReader(configFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] line_split = line.split("=");
				if (line_split.length == 2) {
					this.properties.put(line_split[0], line_split[1]);
				}
			}

			bufferedReader.close();
		} catch (Exception e) {
			System.out.println("Error while reading the config file");
			e.printStackTrace();
		}
	}
}
