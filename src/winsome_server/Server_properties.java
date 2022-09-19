package winsome_server;

import java.io.*;
import java.util.HashMap;

public class Server_properties {
    /*
     * This class is used to store the properties of the server
     *
     * the properties are stored in a properties file with the following format:
     * # this is a comment
     * property_name = property_value
     *
     * the properties are:
     * 1. SERVER=<server_address> (no default value)
     * 2. TCP_PORT=<tcp_port> (default: 8080)
     * 3. UDP_PORT=<udp_port> (default: 8070)
     * 4. REGISTRY_PORT=<registry_port> (default: 1099)
     * 5. USERS_DATABASE=<users_database_name> (default: users.json)
     * 6. POSTS_DATABASE=<posts_database_name> (default: posts.json)
     * 7. RMI_NAME=<rmi_name> (default: Server_registration_RMI)
     * 8. MULTICAST_ADDRESS=<multicast_address> (no default value)
     * 9. MULTICAST_PORT=<multicast_port> (default: 8000)
     * 10. TIME_OUT=<time_out> (default: 1000)
     *
     * the properties are stored in a HashMap<String, String>
     *
     * the properties are read from a file and written to 2 files:
     * 1. the server properties file
     * 2. the client properties file
     *
     * the client properties file is used from the client to connect to the server,
     * so it holds information about the server address and the ports (with the same flags).
     */

	// members
	private final HashMap<String, String> properties;
	private final String serverProperties_file;
	private final String clientProperties_file;

	// constructor
	public Server_properties(String serverProperties_file, String clientProperties_file) {
		this.serverProperties_file = serverProperties_file;
		this.clientProperties_file = clientProperties_file;
		this.properties = new HashMap<>();
		read_properties();
	}

	// getters
	public int get_tcp_port() {
		/*
		 * return the tcp port if it exists, otherwise return the default value
		 */
		if (this.properties.containsKey("TCP_PORT")) {
			return Integer.parseInt(this.properties.get("TCP_PORT"));
		} else {
			return 8080;
		}
	}
	public int get_udp_port() {
		/*
		 * return the udp port if it exists, otherwise return the default value
		 */
		if (this.properties.containsKey("UDP_PORT")) {
			return Integer.parseInt(this.properties.get("UDP_PORT"));
		} else {
			return 8070;
		}
	}
	public int get_registry_port() {
		/*
		 * return the registry port if it exists, otherwise return the default value
		 */
		if (this.properties.containsKey("REGISTRY_PORT")) {
			return Integer.parseInt(this.properties.get("REGISTRY_PORT"));
		} else {
			return 1099;
		}
	}
	public String get_users_database() {
		/*
		 * return the users database name if it exists, otherwise return the default value
		 */
		return this.properties.getOrDefault("USERS_DATABASE", "users.json");
	}
	public String get_posts_database() {
		/*
		 * return the posts database name if it exists, otherwise return the default value
		 */
		return this.properties.getOrDefault("POSTS_DATABASE", "posts.json");
	}
	public String get_rmi_name() {
		/*
		 * return the rmi name if it exists, otherwise return the default value
		 */
		return this.properties.getOrDefault("RMI_NAME", "Server_registration_RMI");
	}
	public String get_multicast_address() {
		/*
		 * return the multicast address if it exists, otherwise return null
		 */
		return this.properties.getOrDefault("MULTICAST_ADDRESS", null);
	}
	public int get_multicast_port() {
		/*
		 * return the multicast port if it exists, otherwise return the default value
		 */
		if (this.properties.containsKey("MULTICAST_PORT")) {
			return Integer.parseInt(this.properties.get("MULTICAST_PORT"));
		} else {
			return 8000;
		}
	}
	public int get_time_out() {
		/*
		 * return the time-out if it exists, otherwise return the default value
		 */
		if (this.properties.containsKey("TIME_OUT")) {
			return Integer.parseInt(this.properties.get("TIME_OUT"));
		} else {
			return 1000;
		}
	}

	// setters
	public void set_server_address(String server_address) {
		properties.put("SERVER", server_address);
	}
	public void set_multicast_address(String multicast_address) {
		properties.put("MULTICAST_ADDRESS", multicast_address);
	}

	// methods
	public void read_properties() {
		/*
		 * This method reads the properties from the server properties file
		 * and stores them in the HashMap<String, String> properties
		 *
		 * If the property is not found in the file, the default value is used
		 * If the property file is not found, a new file is created with the default values
		 *
		 * 1. Read the properties file
		 * 2. If the file is not found, create a new file with the default values
		 * 3. If the file is found, read the properties and store them in the HashMap<String, String> properties
		 */

		try {
			// 1. read the properties file
			FileReader fileReader = new FileReader(serverProperties_file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			// read the properties
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				// skip the comments
				if (line.startsWith("#")) {
					continue;
				}

				// split the line
				String[] line_split = line.split("=");

				// check if the line is valid
				if (line_split.length != 2) {
					continue;
				}

				// 3. store the property
				properties.put(line_split[0], line_split[1]);
			}

			// close the file
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			// 2. If the file is not found, create a new file with the default values
			create_properties_file();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void create_properties_file() {
		/*
		 * This method creates a new properties file with the default values
		 */

		// set the default values
		properties.put("TCP_PORT", "8080");
		properties.put("UDP_PORT", "8070");
		properties.put("REGISTRY_PORT", "1099");
		properties.put("USERS_DATABASE", "users.json");
		properties.put("POSTS_DATABASE", "posts.json");
		properties.put("RMI_NAME", "Server_registration_RMI");
		properties.put("MULTICAST_PORT", "8000");
		properties.put("TIME_OUT", "1000");

		// write the properties to the file
		write_properties();
	}

	public void write_properties() {
		/*
		 * This method writes the properties to the server properties file
		 * and to the client properties file
		 */

		// write the properties to the server properties file
		try {
			// create the file
			FileWriter fileWriter = new FileWriter(serverProperties_file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			// write the properties
			for (String key : properties.keySet()) {
				bufferedWriter.write(key + "=" + properties.get(key));
				bufferedWriter.newLine();
			}

			// close the file
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// write the properties to the client properties file
		try {
			// create the file
			FileWriter fileWriter = new FileWriter(clientProperties_file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			// write the properties
			for (String key : properties.keySet()) {
				// skip the properties that are not used by the client
				if (key.equals("USERS_BACKUP") ||
						key.equals("FILES_BACKUP") ||
						key.equals("TIME_OUT")) {
					continue;
				}

				bufferedWriter.write(key + "=" + properties.get(key));
				bufferedWriter.newLine();
			}

			// close the file
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
