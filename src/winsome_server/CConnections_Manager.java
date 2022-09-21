package winsome_server;


import java.util.concurrent.ConcurrentHashMap;

public class CConnections_Manager extends ConcurrentHashMap<String, CConnection> {
	/*
	 * This class is used to manage the client connections.
	 */

	public void add_connection(CConnection connection) {
		/*
		 * This method adds a new connection to the connections manager
		 */
		this.put(connection.get_ip_address(), connection);
	}
	public CConnection get_connection(String address) {
		/*
		 * This method returns the connection of the given username
		 */
		return this.get(address);
	}

}
