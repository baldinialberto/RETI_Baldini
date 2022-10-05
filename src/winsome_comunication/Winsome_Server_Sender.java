package winsome_comunication;

import java.nio.channels.SocketChannel;

/**
 * This class is used by the client to communicate to the server.
 * <p></p>
 * Available methods:
 * 1. login(String username, String password) = login the user.
 * 2. logout() = logout the user.
 * 3. list_users() = list all the users with similar interests to the user.
 * 4. list_following() = list all the users that the user is following.
 * 5. follow(String username) = follow the user.
 * 6. unfollow(String username) = unfollow the user.
 * 7. blog() = get the blog of the user.
 * 8. feed() = get the feed of the user.
 * 9. create_post(String title, String content) = create a post.
 * 10. delete_post(int post_id) = delete a post.
 * 11. show_post(int post_id) = show a post.
 * 12. rewin_post(int post_id) = rewin a post.
 * 13. rate_post(int post_id, boolean rating) = rate a post.
 * 14. comment_post(int post_id, String comment) = comment a post.
 * 15. wallet() = get the wallet of the user.
 * 16. wallet_btc() = get the wallet of the user in BTC.
 * <p></p>
 * The methods return a Winsome_Exception if something goes wrong.
 * <p></p>
 * Constructor:
 * 1. Winsome_Server_Sender(SocketChannel socket_channel,
 *  Server_RMI_Interface server_rmi) = create a new Winsome_Server_Sender.
 */
public class Winsome_Server_Sender {
	// Member variables
	private final SocketChannel socket_channel;
	private final Server_RMI_Interface server_rmi;

	/**
	 * Create a new Winsome_Server_Sender.
	 * @param socket_channel = the socket channel to communicate with the server.
	 * @param server_rmi = the server RMI interface.
	 */
	public Winsome_Server_Sender(SocketChannel socket_channel, Server_RMI_Interface server_rmi) {
		this.socket_channel = socket_channel;
		this.server_rmi = server_rmi;
	}

	/**
	 * Login the user.
	 * @param username = the username of the user.
	 * @param password = the password of the user.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public void login(String username, String password) throws Winsome_Exception {
		// TODO : implement
	}

	/**
	 * Logout the user.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public void logout() throws Winsome_Exception {
		// TODO : implement
	}

	/**
	 * List all the users with similar interests to the user.
	 * @return the list of users.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public String[] list_users() throws Winsome_Exception {
		// TODO : implement
		return null;
	}

	/**
	 * List all the users that the user is following.
	 * @return the list of users.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public String[] list_following() throws Winsome_Exception {
		// TODO : implement
		return null;
	}

	/**
	 * Follow the user.
	 * @param username = the username of the user to follow.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public void follow(String username) throws Winsome_Exception {
		// TODO : implement
	}

	/**
	 * Unfollow the user.
	 * @param username = the username of the user to unfollow.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public void unfollow(String username) throws Winsome_Exception {
		// TODO : implement
	}

	/**
	 * Get the blog of the user.
	 * @return the blog of the user.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public Post_representation_simple[] blog() throws Winsome_Exception {
		// TODO : implement
		return null;
	}

	/**
	 * Get the feed of the user.
	 * @return the feed of the user.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public Post_representation_simple[] feed() throws Winsome_Exception {
		// TODO : implement
		return null;
	}

	/**
	 * Create a post.
	 * @param title = the title of the post.
	 * @param content = the content of the post.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public void create_post(String title, String content) throws Winsome_Exception {
		// TODO : implement
	}

	/**
	 * Delete a post.
	 * @param post_id = the id of the post.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public void delete_post(int post_id) throws Winsome_Exception {
		// TODO : implement
	}

	/**
	 * Show a post.
	 * @param post_id = the id of the post.
	 * @return the post.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public Post_representation_detailed show_post(int post_id) throws Winsome_Exception {
		// TODO : implement
		return null;
	}

	/**
	 * Rewin a post.
	 * @param post_id = the id of the post.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public void rewin_post(int post_id) throws Winsome_Exception {
		// TODO : implement
	}

	/**
	 * Rate a post.
	 * @param post_id = the id of the post.
	 * @param rating = the rating of the post.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public void rate_post(int post_id, boolean rating) throws Winsome_Exception {
		// TODO : implement
	}

	/**
	 * Comment a post.
	 * @param post_id = the id of the post.
	 * @param comment = the comment of the post.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public void comment_post(int post_id, String comment) throws Winsome_Exception {
		// TODO : implement
	}

	/**
	 * Get the wallet of the user.
	 * @return the wallet of the user.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public Wallet_representation wallet() throws Winsome_Exception {
		// TODO : implement
		return null;
	}
	
	/**
	 * Get the wallet of the user in BTC.
	 * @return the wallet of the user in BTC.
	 * @throws Winsome_Exception if something goes wrong (check the message for details).
	 */
	public double wallet_btc() throws Winsome_Exception {
		// TODO : implement
		return .0;
	}
}
