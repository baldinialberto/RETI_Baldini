package winsome_comunication;

import java.rmi.Remote;

/**
 * This interface is used to define the methods that the client can call on the server.
 *
 * @author Winsome
 * @version 1.0
 * @public register_user this method is used to register a new user through an RMI call
 * @public receive_updates this method is used to receive updates from the server through an RMI call
 * @since 1.0
 */
public interface Server_RMI_Interface extends Remote {
	/**
	 * register_user is used to register a new user through an RMI call
	 * the method returns a boolean value to indicate if the registration was successful or not
	 *
	 * @param username the username of the user to be registered
	 * @param password the password of the user to be registered
	 * @param tags     an array of tags that the user is interested in
	 * @return a boolean value to indicate if the registration was successful or not
	 * @throws java.rmi.RemoteException
	 * @author Winsome
	 * @version 1.0
	 * @since 1.0
	 */
	int register_user(String username, String password, String[] tags) throws java.rmi.RemoteException;

	/**
	 * receive_updates is used to receive updates from the server through an RMI call
	 * the method returns a boolean value to indicate if the registration was successful or not
	 *
	 * @param callback the callback object that will be used to send updates to the client
	 *                 the callback object must implement the ClientInterface interface
	 * @return a boolean value to indicate if the registration was successful or not
	 * @throws java.rmi.RemoteException
	 * @author Winsome
	 * @version 1.0
	 * @since 1.0
	 */
	int receive_updates(Client_RMI_Interface callback, String username) throws java.rmi.RemoteException;
}
