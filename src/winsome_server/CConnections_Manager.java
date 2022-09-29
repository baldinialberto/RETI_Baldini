package winsome_server;


import winsome_comunication.Client_RMI_Interface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CConections_Manager class
 * This class is used to manage the client connections.
 */
public class CConnections_Manager extends ConcurrentHashMap<String, CConnection> {
	// constants
	public static final int CONNECTIONS_MANAGER_OK = 0;
	public static final int CONNECTIONS_MANAGER_ERROR = -1;
	public static final int CONNECTIONS_MANAGER_ALREADY_EXISTS = -2;
	public static final int CONNECTIONS_MANAGER_DOES_NOT_EXIST = -3;

	/**
	 * add_connection
	 * this method is used to add a new connection to the connections manager
	 * the method returns an integer value to indicate if the connection was added successfully or not
	 * if not successful, the method returns the error code
	 *
	 * @param connection the connection to be added
	 * @return 0 if the connection was added successfully, an error code otherwise
	 */
	public int add_connection(CConnection connection)
	{
		/*
		 * add the connection to the connections manager
		 *
		 * 1. check the parameters
		 * 2. check if the connection already exists:
		 *  loop through the connections manager and check if the username or the address already exists
		 * 3. if the connection does not exist, add it to the connections manager
		 * 4. return the result
		 */

		// 1. check the parameters
		if (connection == null)
			return CONNECTIONS_MANAGER_ERROR;

		// 2. check if the connection already exists:
		// loop through the connections manager and check if the username or the address already exists
		for (CConnection c : this.values())
		{
			if (c.get_username().equals(connection.get_username()))
				return CONNECTIONS_MANAGER_ALREADY_EXISTS;
			if (c.get_address().equals(connection.get_address()))
				return CONNECTIONS_MANAGER_ALREADY_EXISTS;
		}

		// 3. if the connection does not exist, add it to the connections manager
		this.put(connection.get_address(), connection);

		// 4. return the result
		return CONNECTIONS_MANAGER_OK;
	}

	/**
	 * remove_connection
	 * this method is used to remove a connection from the connections manager
	 * the method returns an integer value to indicate if the connection was removed successfully or not
	 * if not successful, the method returns the error code
	 *
	 * @param address the connection to be removed
	 * @return 0 if the connection was removed successfully, an error code otherwise
	 */
	public int remove_connection(String address)
	{
		/*
		 * remove the connection from the connections manager
		 *
		 * 1. check the parameters
		 * 2. check if the connection exists:
		 * 3. if the connection exists, remove it from the connections manager
		 * 4. return the result
		 */

		// 1. check the parameters
		if (address == null)
			return CONNECTIONS_MANAGER_ERROR;

		// 2. check if the connection exists:
		// loop through the connections manager and check if the address exists
		if (!this.containsKey(address))
			return CONNECTIONS_MANAGER_DOES_NOT_EXIST;

		// 3. if the connection exists, remove it from the connections manager
		this.remove(address);

		// 4. return the result
		return CONNECTIONS_MANAGER_OK;
	}

	/**
	 * get_callback
	 * this method is used to get the callback object of a connection
	 *
	 * @param address the address of the connection
	 * @return the callback object of the connection
	 */
	public Client_RMI_Interface get_callback(String address)
	{
		/*
		 * get the callback object of a connection
		 *
		 * 1. check the parameters
		 * 2. check if the connection exists:
		 * 3. if the connection exists, return the callback object
		 */

		// 1. check the parameters
		if (address == null)
			return null;

		// 2. check if the connection exists:
		// loop through the connections manager and check if the address exists
		if (!this.containsKey(address))
			return null;

		// 3. if the connection exists, return the callback object
		return this.get(address).get_callback();
	}

	/**
	 * get_address
	 * this method is used to get multiple callback objects of connections
	 *
	 * @param addresses the addresses of the connections
	 * @return the callback objects of the connections, null if not found
	 */
	public List<Client_RMI_Interface> get_callbacks(List<String> addresses) {
		/*
		 * get the callback objects of connections
		 *
		 * 1. check the parameters
		 * 2. loop through the addresses
		 * 3. get the callback object of the connection
		 * 4. if not null, add the callback object to the list
		 * 5. return the list
		 */

		// 1. check the parameters
		if (addresses == null)
			return null;

		List<Client_RMI_Interface> callbacks = new ArrayList<>();

		// 2. loop through the addresses
		for (String address : addresses)
		{
			// 3. get the callback object of the connection
			Client_RMI_Interface callback = this.get_callback(address);

			// 4. if not null, add the callback object to the list
			if (callback != null)
				callbacks.add(callback);
		}

		// 5. return the list
		return callbacks.isEmpty() ? null : callbacks;
	}

	/**
	 * get_username
	 * this method is used to get the username of a connection
	 *
	 * @param address the address of the connection
	 * @return the username of the connection
	 */
	public String get_username(String address)
	{
		/*
		 * get the username of a connection
		 *
		 * 1. check the parameters
		 * 2. check if the connection exists:
		 * 3. if the connection exists, return the username
		 */

		// 1. check the parameters
		if (address == null)
			return null;

		// 2. check if the connection exists:
		// loop through the connections manager and check if the address exists
		if (!this.containsKey(address))
			return null;

		// 3. if the connection exists, return the username
		return this.get(address).get_username();
	}
}
