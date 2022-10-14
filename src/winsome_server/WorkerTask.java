package winsome_server;

import winsome_comunication.WinMessage;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class WorkerTask implements Runnable {
	// member variables
	private final Server server;
	private final Selector selector;
	private final SelectionKey selection_key;


	// constructor
	public WorkerTask(Server server, Selector selector, SelectionKey selection_key) {
		this.server = server;
		this.selector = selector;
		this.selection_key = selection_key;
	}

	// methods
	@Override
	public void run() {
		/*
		 * This method is called when a new thread is created or a thread is reused
		 *
		 * The method will read the message from the client and process it
		 * After the message is processed, the method will put the answer back into the buffer of the key
		 * and register the key to the selector as writable
		 *
		 * 1. read the message from the client
		 * 2. process the message
		 * 3. put the answer into the buffer
		 * 4. register the key as writable
		 */

		/* 1. read the message from the client
		 * the first message from the client is an array of strings
		 */
		SocketChannel socket_channel = (SocketChannel) selection_key.channel();

		try {
			WinMessage message = WinMessage.receive(socket_channel);

			// DEBUG
			System.out.println("Received message: " + message);

			// 2. process the message
			if (message.getString(0).equals(WinMessage.EXIT)) {
				// the client has closed the connection
				// close the connection
				socket_channel.close();
				selection_key.cancel();
				return;
			}
			WinMessage response = process_message(
					message.getStringsArray(),
					socket_channel.getRemoteAddress().toString()
			);

			// 3. put the answer into the buffer + // 4. register the key as writable
			socket_channel.register(selector, SelectionKey.OP_WRITE, response);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public WinMessage process_message(String[] request, String address) {
		/*
		 * This method will process the request and return the response
		 *
		 * 1. parse the request
		 * 2. process the request
		 * 3. return the response
		 */

		/* 1. parse the request (an array of strings)
		 * the first string is the type of the request
		 * the rest of the strings are the parameters of the request
		 * the type must be one of the following:
		 * 1. login
		 * 2. logout
		 * 3. list_users
		 * 4. follow
		 * 5. unfollow
		 * 6. post
		 * 7. blog
		 * 8. show_post
		 * 9. show_feed
		 * 10. delete_post
		 * 11. rewin_post
		 * 12. comment
		 * 13. rate
		 * 14. list_following
		 * 15. wallet
		 * 16. wallet_btc
		 *
		 * If the type is not one of the above, an error message will be returned
		 */
		WinMessage response = new WinMessage();
		String type = request[0];
		String[] parameters = new String[request.length - 1];
		System.arraycopy(request, 1, parameters, 0, request.length - 1);

		/* 2. process the request
		 * will be processed by the server that return a response
		 */

		// 2.1 login
		switch (type) {
			case WinMessage.LOGIN_REQUEST:
				// the request is a login request
				// the parameters are username and password
				// the response will be a string "success" or "error, reason"

				// parameters[0] is the username
				// parameters[1] is the password
				if (parameters.length != 2) {
					// the request is not valid
					response.addString(WinMessage.ERROR);
					response.addString("Invalid request");
					break;
				}

				response = this.server.login_request(parameters[0], parameters[1], address);
				break;
			// 2.2 logout
			case WinMessage.LOGOUT_REQUEST:
				// the request is a logout request
				// no parameters are provided by the client
				// the response will be a string "success" or "error, reason"
				response = this.server.logout_request(address);
				break;

			// 2.3 list_users
			case WinMessage.LIST_USERS_REQUEST:
				// the request is a list_users request
				// no parameters are provided by the client
				// the response will be a string "success" or "error, reason"
				response = this.server.list_users_request(address);
				break;
			// 2.4 follow
			case WinMessage.FOLLOW_REQUEST:
				// the request is a follow request
				// the parameter is the username of the user to follow
				// the response will be a string "success" or "error, reason"

				// parameters[0] is the username of the user to follow

				if (parameters.length != 1) {
					// the request is not valid
					response.addString(WinMessage.ERROR);
					response.addString("Invalid request");
					break;
				}

				response = this.server.follow_request(parameters[0], address);
				break;
			// 2.5 unfollow
			case WinMessage.UNFOLLOW_REQUEST:
				// the request is an unfollow request
				// the parameters is the username of the user to unfollow
				// the response will be a string "success" or "error, reason"

				// parameters[0] is the username of the user to unfollow

				if (parameters.length != 1) {
					// the request is not valid
					response.addString(WinMessage.ERROR);
					response.addString("Invalid request");
					break;
				}

				response = this.server.unfollow_request(parameters[0], address);
				break;
			// 2.6 post
			case WinMessage.POST_REQUEST:
				// the request is a create_post request
				// the parameters are the title and the content of the post
				// the response will be a string "success" or "error, reason"

				// parameters[0] is the title of the post
				// parameters[1] is the content of the post

				if (parameters.length != 2) {
					// the request is not valid
					response.addString(WinMessage.ERROR);
					response.addString("Invalid request");
					break;
				}

				response = this.server.create_post_request(address, parameters[0], parameters[1]);
				break;
			// 2.7 blog
			case WinMessage.BLOG_REQUEST:
				// the request is a blog request
				// the response will be a string "success" or "error, reason"

				response = this.server.blog_request(address);
				break;
			// 2.8 show_post
			case WinMessage.SHOW_POST_REQUEST:
				// the request is a show_post request
				// the parameter is the id of the post
				// the response will be a string "success" or "error, reason"

				// parameters[0] is the id of the post

				if (parameters.length != 1) {
					// the request is not valid
					response.addString(WinMessage.ERROR);
					response.addString("Invalid request");
					break;
				}

				response = this.server.show_post_request(address, parameters[0]);
				break;
			// 2.9 show_feed
			case WinMessage.SHOW_FEED_REQUEST:
				// the request is a show_feed request
				// the response will be a string "success" or "error, reason"

				response = this.server.show_feed_request(address);
				break;
			// 2.10 delete_post
			case WinMessage.DELETE_REQUEST:
				// the request is a delete_post request
				// the parameter is the id of the post
				// the response will be a string "success" or "error, reason"

				// parameters[0] is the id of the post

				if (parameters.length != 1) {
					// the request is not valid
					response.addString(WinMessage.ERROR);
					response.addString("Invalid request");
					break;
				}

				response = this.server.delete_post_request(address, parameters[0]);
				break;
			// 2.11 rewin_post
			case WinMessage.REWIN_REQUEST:
				// the request is a rewin_post request
				// the parameter is the id of the post
				// the response will be a string "success" or "error, reason"

				// parameters[0] is the id of the post

				if (parameters.length != 1) {
					// the request is not valid
					response.addString(WinMessage.ERROR);
					response.addString("Invalid request");
					break;
				}

				response = this.server.rewin_post_request(address, parameters[0]);
				break;
			// 2.12 comment
			case WinMessage.COMMENT_REQUEST:
				// the request is a comment request
				// the parameters are the id of the post and the content of the comment
				// the response will be a string "success" or "error, reason"

				// parameters[0] is the id of the post
				// parameters[1] is the content of the comment

				if (parameters.length != 2) {
					// the request is not valid
					response.addString(WinMessage.ERROR);
					response.addString("Invalid request");
					break;
				}

				response = this.server.comment_request(address, parameters[0], parameters[1]);
				break;
			// 2.13 rate
			case WinMessage.RATE_REQUEST:
				// the request is a show_comments request
				// the parameter is the id of the post
				// the response will be a string "success" or "error, reason"

				// parameters[0] is the id of the post
				// parameters[1] is the rating

				if (parameters.length != 2) {
					// the request is not valid
					response.addString(WinMessage.ERROR);
					response.addString("Invalid request");
					break;
				}

				response = this.server.rate_request(address, parameters[0], parameters[1]);
				break;
			// 2.14 list_following
			case WinMessage.LIST_FOLLOWING_REQUEST:
				// the request is a list_followings request
				// the response will be a string "success" or "error, reason"

				response = this.server.list_followings_request(address);
				break;
			// 2.15 wallet
			case WinMessage.WALLET_REQUEST:
				// the request is a wallet request
				// the response will be a string "success" or "error, reason"

				response = this.server.wallet_request(address);
				break;
			// 2.16 wallet_btc
			case WinMessage.WALLET_BTC_REQUEST:
				// the request is a wallet_btc request
				// the response will be a string "success" or "error, reason"

				response = this.server.wallet_btc_request(address);
				break;
			default:
				// the request is not valid
				// the response will be a string "error, reason"
				response.addString(WinMessage.ERROR);
				response.addString("Invalid request");
				break;
		}

		// 3. return the response
		return response;
	}
}
