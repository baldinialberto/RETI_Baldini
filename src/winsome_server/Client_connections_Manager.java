package winsome_server;


import winsome_comunication.Client_RMI_Interface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CConections_Manager class
 * This class is used to manage the client connections.
 */
public class Client_connections_Manager extends ConcurrentHashMap<String, Client_connection> {
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
	public int add_connection(Client_connection connection) {
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
		for (Client_connection c : this.values()) {
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
	public int remove_connection(String address) {
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
	public Client_RMI_Interface get_callback(String address) {
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
		for (String address : addresses) {
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
	public String get_username(String address) {
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

	/**
	 * is_user_connected
	 * this method is used to check if a user is connected
	 *
	 * @param username the username of the user
	 * @return true if the user is connected, false otherwise
	 */
	public boolean is_user_connected(String username) {
		/*
		 * check if a user is connected
		 *
		 * 1. check the parameters
		 * 2. loop through the connections manager
		 * 3. check if the username exists
		 */

		// 1. check the parameters
		if (username == null)
			return false;

		// 2. loop through the connections manager
		for (Client_connection c : this.values()) {
			// 3. check if the username exists
			if (c.get_username().equals(username))
				return true;
		}

		return false;
	}

	/**
	 * is_address_connected
	 * this method is used to check if a client is connected with a specific address
	 *
	 * @param address the address of the client
	 * @return true if the client is connected, false otherwise
	 */
	public boolean is_address_connected(String address) {
		/*
		 * check if a client is connected with a specific address
		 *
		 * 1. check the parameters
		 * 2. return the result
		 */

		// 1. check the parameters
		if (address == null)
			return false;

		// 2. return the result
		return this.containsKey(address);
	}

	/**
	 * register_callback_of_user
	 * this method is used to register the callback of a user
	 *
	 * @param username the username of the user
	 * @param callback the callback of the user
	 * @return true if the callback was registered successfully, false otherwise
	 */
	public boolean register_callback_of_user(String username, Client_RMI_Interface callback) {
		/*
		 * register the callback of a user
		 *
		 * 1. check the parameters
		 * 2. loop through the connections manager
		 * 3. if the username exists, register the callback
		 * 4. return the result
		 */

		// 1. check the parameters
		if (username == null || callback == null)
			return false;

		// 2. loop through the connections manager
		for (Client_connection c : this.values()) {
			// 3. if the username exists, register the callback
			if (c.get_username().equals(username)) {
				c.set_callback(callback);
				return true;
			}
		}

		return false;
	}
}
