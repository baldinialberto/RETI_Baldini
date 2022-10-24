package winsome_communication;

import winsome_DB.RateDB;
import winsome_DB.WinsomeDB_Exception;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

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
 * 1. Winsome_Server_Sender(SocketChannel socket_channel) = create a new Winsome_Server_Sender.
 */
public class WinsomeServerSender {
	// Member variables
	private final SocketChannel socket_channel;

	/**
	 * Create a new Winsome_Server_Sender.
	 *
	 * @param socket_channel = the socket channel to communicate with the server.
	 */
	public WinsomeServerSender(SocketChannel socket_channel) {
		this.socket_channel = socket_channel;
	}

	/**
	 * Login the user.
	 *
	 * @param username = the username of the user.
	 * @param password = the password of the user.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public void login(String username, String password) throws WinsomeException {
		/*
		 * login request:
		 * 1. request type = LOGIN_REQUEST
		 * 2. username
		 * 3. password
		 *
		 * login response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage login_request = new WinMessage();
		login_request.addString(WinMessage.LOGIN_REQUEST);
		login_request.addString(username);
		login_request.addString(password);

		WinMessage login_response;
		try {
			// 2. Send login request to server
			login_request.send(socket_channel);

			// 3. Receive login response from server of unknown size
			login_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!login_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Login failed.
			throw new WinsomeDB_Exception.GenericException(
					login_response.getString(1));
		}

		// Login successful.
	}

	/**
	 * Logout the user.
	 *
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public void logout() throws WinsomeException {
		/*
		 * logout request:
		 * 1. request type = LOGOUT_REQUEST
		 *
		 * logout response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage logout_request = new WinMessage();
		logout_request.addString(WinMessage.LOGOUT_REQUEST);

		WinMessage logout_response;
		try {
			// 2. Send logout request to server
			logout_request.send(socket_channel);

			// 3. Receive logout response from server of unknown size
			logout_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!logout_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Logout failed.
			throw new WinsomeDB_Exception.GenericException(
					logout_response.getString(1));
		}

		// Logout successful.
	}

	/**
	 * List all the users with similar interests to the user.
	 *
	 * @return the list of users.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public String[] list_users() throws WinsomeException {
		/*
		 * list_users request:
		 * 1. request type = LIST_USERS_REQUEST
		 *
		 * list_users response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 * 3. username 1
		 * 4. username 2
		 * 5. username 3
		 * ...
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage list_users_request = new WinMessage();
		list_users_request.addString(WinMessage.LIST_USERS_REQUEST);

		WinMessage list_users_response;
		try {
			// 2. Send list_users request to server
			list_users_request.send(socket_channel);

			// 3. Receive list_users response from server of unknown size
			list_users_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!list_users_response.getString(0).equals(WinMessage.SUCCESS)) {
			// List_users failed.
			throw new WinsomeDB_Exception.GenericException(
					list_users_response.getString(1));
		}

		// List_users successful.
		UserRepr[] users = new UserRepr[list_users_response.size() - 1];
		for (int i = 1; i < list_users_response.size(); i++) {
			users[i - 1] = new UserRepr(list_users_response.getString(i));
		}

		return Arrays.stream(users).map(UserRepr::toString).toArray(String[]::new);
	}

	/**
	 * List all the users that the user is following.
	 *
	 * @return the list of users.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public String[] list_following() throws WinsomeException {
		/*
		 * list_following request:
		 * 1. request type = LIST_FOLLOWING_REQUEST
		 *
		 * list_following response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 * 3. username 1
		 * 4. username 2
		 * 5. username 3
		 * ...
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage list_following_request = new WinMessage();
		list_following_request.addString(WinMessage.LIST_FOLLOWING_REQUEST);

		WinMessage list_following_response;
		try {
			// 2. Send list_following request to server
			list_following_request.send(socket_channel);

			// 3. Receive list_following response from server of unknown size
			list_following_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!list_following_response.getString(0).equals(WinMessage.SUCCESS)) {
			// List_following failed.
			throw new WinsomeDB_Exception.GenericException(
					list_following_response.getString(1));
		}

		// List_following successful.
		return list_following_response.getStrings().subList(1, list_following_response.size()).toArray(new String[0]);
	}

	/**
	 * Follow the user.
	 *
	 * @param username = the username of the user to follow.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public void follow(String username) throws WinsomeException {
		/*
		 * follow request:
		 * 1. request type = FOLLOW_REQUEST
		 * 2. username
		 *
		 * follow response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage follow_request = new WinMessage();
		follow_request.addString(WinMessage.FOLLOW_REQUEST);
		follow_request.addString(username);

		WinMessage follow_response;
		try {
			// 2. Send follow request to server
			follow_request.send(socket_channel);

			// 3. Receive follow response from server of unknown size
			follow_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!follow_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Follow failed.
			throw new WinsomeDB_Exception.GenericException(
					follow_response.getString(1));
		}

		// Follow successful.
	}

	/**
	 * Unfollow the user.
	 *
	 * @param username = the username of the user to unfollow.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public void unfollow(String username) throws WinsomeException {
		/*
		 * unfollow request:
		 * 1. request type = UNFOLLOW_REQUEST
		 * 2. username
		 *
		 * unfollow response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage unfollow_request = new WinMessage();
		unfollow_request.addString(WinMessage.UNFOLLOW_REQUEST);
		unfollow_request.addString(username);

		WinMessage unfollow_response;
		try {
			// 2. Send unfollow request to server
			unfollow_request.send(socket_channel);

			// 3. Receive unfollow response from server of unknown size
			unfollow_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!unfollow_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Unfollow failed.
			throw new WinsomeDB_Exception.GenericException(
					unfollow_response.getString(1));
		}

		// Unfollow successful.
	}

	/**
	 * Get the blog of the user.
	 *
	 * @return the blog of the user.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public PostReprSimple[] blog() throws WinsomeException {
		/*
		 * blog request:
		 * 1. request type = BLOG_REQUEST
		 *
		 * blog response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 * 3. post 1
		 * 4. post 2
		 * 5. post 3
		 * ...
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage blog_request = new WinMessage();
		blog_request.addString(WinMessage.BLOG_REQUEST);

		WinMessage blog_response;
		try {
			// 2. Send blog request to server
			blog_request.send(socket_channel);

			// 3. Receive blog response from server of unknown size
			blog_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!blog_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Blog failed.
			throw new WinsomeDB_Exception.GenericException(
					blog_response.getString(1));
		}

		// Blog successful.
		return blog_response.getStrings().subList(1, blog_response.size()).stream()
				.map(PostReprSimple::new).toArray(PostReprSimple[]::new);
	}

	/**
	 * Get the feed of the user.
	 *
	 * @return the feed of the user.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public PostReprSimple[] feed() throws WinsomeException {
		/*
		 * feed request:
		 * 1. request type = FEED_REQUEST
		 *
		 * feed response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 * 3. post 1
		 * 4. post 2
		 * 5. post 3
		 * ...
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage feed_request = new WinMessage();
		feed_request.addString(WinMessage.SHOW_FEED_REQUEST);

		WinMessage feed_response;
		try {
			// 2. Send feed request to server
			feed_request.send(socket_channel);

			// 3. Receive feed response from server of unknown size
			feed_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!feed_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Feed failed.
			throw new WinsomeDB_Exception.GenericException(
					feed_response.getString(1));
		}

		// Feed successful.
		return feed_response.getStrings().subList(1, feed_response.size()).stream()
				.map(PostReprSimple::new).toArray(PostReprSimple[]::new);
	}

	/**
	 * Create a post.
	 *
	 * @param title   = the title of the post.
	 * @param content = the content of the post.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public void create_post(String title, String content) throws WinsomeException {
		/*
		 * create post request:
		 * 1. request type = CREATE_POST_REQUEST
		 * 2. title
		 * 3. content
		 *
		 * create post response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage create_post_request = new WinMessage();
		create_post_request.addString(WinMessage.POST_REQUEST);
		create_post_request.addString(title);
		create_post_request.addString(content);

		WinMessage create_post_response;
		try {
			// 2. Send create post request to server
			create_post_request.send(socket_channel);

			// 3. Receive create post response from server of unknown size
			create_post_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!create_post_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Create post failed.
			throw new WinsomeDB_Exception.GenericException(
					create_post_response.getString(1));
		}

		// Create post successful.
	}

	/**
	 * Delete a post.
	 *
	 * @param post_id = the id of the post.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public void delete_post(int post_id) throws WinsomeException {
		/*
		 * delete post request:
		 * 1. request type = DELETE_POST_REQUEST
		 * 2. post id
		 *
		 * delete post response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage delete_post_request = new WinMessage();
		delete_post_request.addString(WinMessage.DELETE_REQUEST);
		delete_post_request.addString(String.valueOf(post_id));

		WinMessage delete_post_response;
		try {
			// 2. Send delete post request to server
			delete_post_request.send(socket_channel);

			// 3. Receive delete post response from server of unknown size
			delete_post_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!delete_post_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Delete post failed.
			throw new WinsomeDB_Exception.GenericException(
					delete_post_response.getString(1));
		}

		// Delete post successful.
	}

	/**
	 * Show a post.
	 *
	 * @param post_id = the id of the post.
	 * @return the post.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public PostReprDetailed show_post(int post_id) throws WinsomeException {
		/*
		 * show post request:
		 * 1. request type = SHOW_POST_REQUEST
		 * 2. post id
		 *
		 * show post response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 * 3. post
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage show_post_request = new WinMessage();
		show_post_request.addString(WinMessage.SHOW_POST_REQUEST);
		show_post_request.addString(String.valueOf(post_id));

		WinMessage show_post_response;
		try {
			// 2. Send show post request to server
			show_post_request.send(socket_channel);

			// 3. Receive show post response from server of unknown size
			show_post_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!show_post_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Show post failed.
			throw new WinsomeDB_Exception.GenericException(
					show_post_response.getString(1));
		}

		// Show post successful.
		return new PostReprDetailed(show_post_response.getString(1));
	}

	/**
	 * Rewin a post.
	 *
	 * @param post_id = the id of the post.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public void rewin_post(int post_id) throws WinsomeException {
		/*
		 * rewin post request:
		 * 1. request type = REWIN_POST_REQUEST
		 * 2. post id
		 *
		 * rewin post response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage rewin_post_request = new WinMessage();
		rewin_post_request.addString(WinMessage.REWIN_REQUEST);
		rewin_post_request.addString(String.valueOf(post_id));

		WinMessage rewin_post_response;
		try {
			// 2. Send rewin post request to server
			rewin_post_request.send(socket_channel);

			// 3. Receive rewin post response from server of unknown size
			rewin_post_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!rewin_post_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Rewin post failed.
			throw new WinsomeDB_Exception.GenericException(
					rewin_post_response.getString(1));
		}

		// Rewin post successful.
	}

	/**
	 * Rate a post.
	 *
	 * @param post_id = the id of the post.
	 * @param rating  = the rating of the post.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public void rate_post(int post_id, boolean rating) throws WinsomeException {
		/*
		 * rate post request:
		 * 1. request type = RATE_POST_REQUEST
		 * 2. post id
		 * 3. rating
		 *
		 * rate post response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage rate_post_request = new WinMessage();
		rate_post_request.addString(WinMessage.RATE_REQUEST);
		rate_post_request.addString(String.valueOf(post_id));
		rate_post_request.addString(rating ? RateDB.UPVOTE : RateDB.DOWNVOTE);

		WinMessage rate_post_response;
		try {
			// 2. Send rate post request to server
			rate_post_request.send(socket_channel);

			// 3. Receive rate post response from server of unknown size
			rate_post_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!rate_post_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Rate post failed.
			throw new WinsomeDB_Exception.GenericException(
					rate_post_response.getString(1));
		}

		// Rate post successful.
	}

	/**
	 * Comment a post.
	 *
	 * @param post_id = the id of the post.
	 * @param comment = the comment of the post.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public void comment_post(int post_id, String comment) throws WinsomeException {
		/*
		 * comment post request:
		 * 1. request type = COMMENT_POST_REQUEST
		 * 2. post id
		 * 3. comment
		 *
		 * comment post response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage comment_post_request = new WinMessage();
		comment_post_request.addString(WinMessage.COMMENT_REQUEST);
		comment_post_request.addString(String.valueOf(post_id));
		comment_post_request.addString(comment);

		WinMessage comment_post_response;
		try {
			// 2. Send comment post request to server
			comment_post_request.send(socket_channel);

			// 3. Receive comment post response from server of unknown size
			comment_post_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!comment_post_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Comment post failed.
			throw new WinsomeDB_Exception.GenericException(
					comment_post_response.getString(1));
		}

		// Comment post successful.
	}

	/**
	 * Get the wallet of the user.
	 *
	 * @return the wallet of the user.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public WalletRepr wallet() throws WinsomeException {
		/*
		 * wallet request:
		 * 1. request type = WALLET_REQUEST
		 *
		 * wallet response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 * 3. wallet representation
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage wallet_request = new WinMessage();
		wallet_request.addString(WinMessage.WALLET_REQUEST);

		WinMessage wallet_response;
		try {
			// 2. Send wallet request to server
			wallet_request.send(socket_channel);

			// 3. Receive wallet response from server of unknown size
			wallet_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!wallet_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Wallet failed.
			throw new WinsomeDB_Exception.GenericException(
					wallet_response.getString(1));
		}

		// Wallet successful.
		return new WalletRepr(wallet_response.getString(1));
	}

	/**
	 * Get the wallet of the user in BTC.
	 *
	 * @return the wallet of the user in BTC.
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public double wallet_btc() throws WinsomeException {
		/*
		 * wallet btc request:
		 * 1. request type = WALLET_BTC_REQUEST
		 *
		 * wallet btc response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 * 3. wallet representation
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 * 3. Receive the response.
		 * 4. Check the response.
		 */

		// 1. Create the request.
		WinMessage wallet_btc_request = new WinMessage();
		wallet_btc_request.addString(WinMessage.WALLET_BTC_REQUEST);

		WinMessage wallet_btc_response;
		try {
			// 2. Send wallet btc request to server
			wallet_btc_request.send(socket_channel);

			// 3. Receive wallet btc response from server of unknown size
			wallet_btc_response = WinMessage.receive(socket_channel);
		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// 4. Check the response.
		if (!wallet_btc_response.getString(0).equals(WinMessage.SUCCESS)) {
			// Wallet btc failed.
			throw new WinsomeDB_Exception.GenericException(
					wallet_btc_response.getString(1));
		}

		// Wallet btc successful.
		return Double.parseDouble(wallet_btc_response.getString(1));
	}

	/**
	 * Disconnect from the server.
	 *
	 * @throws WinsomeException if something goes wrong (check the message for details).
	 */
	public void disconnect() throws WinsomeException {
		/*
		 * disconnect request:
		 * 1. request type = DISCONNECT_REQUEST
		 *
		 * disconnect response:
		 * 1. SUCCESS / ERROR
		 * 2. message (if error)
		 *
		 * 1. Create the request.
		 * 2. Send the request.
		 */

		// 1. Create the request.
		WinMessage disconnect_request = new WinMessage();
		disconnect_request.addString(WinMessage.EXIT);

		try {
			// 2. Send disconnect request to server
			disconnect_request.send(socket_channel);

		} catch (IOException e) {
			throw new WinsomeDB_Exception.GenericException(
					"Problems with the TCP connection, unable to send/receive packages.");
		}

		// Disconnect successful.
	}
}
