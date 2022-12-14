package winsome_communication;

import java.rmi.Remote;

/**
 * This interface is used to define the methods that the server can call on the client.
 *
 * @public send_follower_update this method is used to send updates to the client regarding the followers
 * @public send_followers this method is used to send the followers of a user to the client
 * @public send_multicast_details this method is used to send multicast details to the client
 */
public interface ClientRMI_Interface extends Remote {
	/**
	 * send_follower_update is used to send updates to the client regarding the followers
	 * the method returns a boolean value to indicate if the update was successful or not
	 *
	 * @param username the username of the user that has been followed
	 * @param add      a boolean value to indicate if the user has been followed or unfollowed
	 * @throws java.rmi.RemoteException if the client is not connected to the server
	 */
	void send_follower_update(String username, boolean add) throws java.rmi.RemoteException;

	/**
	 * send_followers is used to send the followers of a user to the client
	 * the method returns a boolean value to indicate if the update was successful or not
	 *
	 * @param followers an array of usernames of the followers of the user
	 *                  the array is empty if the user has no followers
	 * @throws java.rmi.RemoteException if the method fails to send the followers
	 */
	void send_followers(String[] followers) throws java.rmi.RemoteException;

	/**
	 * send_multicast_details is used to send multicast details to the client
	 * the method returns a boolean value to indicate if the update was successful or not
	 *
	 * @param ip   the ip address of the multicast group
	 * @param port the port of the multicast group
	 * @throws java.rmi.RemoteException if the method fails to send the multicast details
	 */
	void send_multicast_details(String ip, int port, String network_name) throws java.rmi.RemoteException;
}
