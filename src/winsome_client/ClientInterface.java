package winsome_client;

import winsome_server.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ClientInterface {

	private final Client client;
	HashMap<String, Method> commands;

	public ClientInterface(Client client) {
		this.client = client;
		populate_commands();
	}

	public void exec() {
		while (client.is_on()) {
			try {
				this.listen();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void populate_commands() {
		commands = new HashMap<>();
		Method[] methods = this.getClass().getDeclaredMethods();
		for (Method m : methods) {
			// the method is inserted only if "_command" is present in the name
			if (m.getName().contains("_command")) {
				commands.put(m.getName().split("_")[0], m);
			}
		}
	}

	private void printErrorCommand() {
		System.out.println("Command not recognized");
	}

	private void register_command(List<String> args) {
		/*
		 * register <username> <password> <tag> <tag> ... <tag>
		 *
		 * 1. check if there are at least 3 arguments, no more than 7
		 * 2. call the register method of the client
		 * 3. print the result
		 */

		// 1. check if there are at least 3 arguments, no more than 7
		if (args.size() < 3 || args.size() > 7) {
			System.out.println("register command : Wrong number of arguments : " +
					"usage : register <username> <password> <tag> <tag> ... <tag>");
			return;
		}

		// 2. call the register method of the client
		String username = args.get(0);
		String password = args.get(1);
		List<String> tags = args.subList(2, args.size());

		try
		{
			client.register(username, password, tags);
			// 3. print the result
			System.out.println("register command : User " + username + " registered");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

	}

	private void login_command(List<String> args) {
		/*
		 * login <username> <password>
		 *
		 * 1. check if there are exactly 2 arguments
		 * 2. call the login method of the client
		 * 3. print the result
		 */

		// 1. check if there are exactly 2 arguments
		if (args.size() != 2) {
			System.out.println("login command : Wrong number of arguments : " +
					"usage : login <username> <password>");
			return;
		}

		// 2. call the login method of the client
		String username = args.get(0);
		String password = args.get(1);

		try
		{
			if (client.login(username, password) == 0)
			{
				// 3. print the result
				System.out.println("login command : User " + username + " logged in");
			}
			else
			{
				System.out.println("login command : User " + username + " not logged in");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void logout_command(List<String> args) {
		/*
		 * logout
		 *
		 * 1. check if there are no arguments
		 * 2. call the logout method of the client
		 * 3. print the result
		 */

		// 1. check if there are no arguments
		if (args.size() != 0) {
			System.out.println("logout command : Wrong number of arguments : " +
					"usage : logout");
			return;
		}

		// 2. call the logout method of the client
		try
		{
			String username = client.get_username();
			if (client.logout() == 0)
			{
				// 3. print the result
				System.out.println("logout command : User " + username + " logged out");
			}
			else
			{
				System.out.println("logout command : User " + username + " not logged out");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void list_users_command(List<String> args) {
		/*
		 * list_users
		 *
		 * 1. check if there are no arguments
		 * 2. call the list_users method of the client
		 * 3. print the result
		 */

		// 1. check if there are no arguments
		if (args.size() != 0) {
			System.out.println("list_users command : Wrong number of arguments : " +
					"usage : list_users");
			return;
		}

		// 2. call the list_users method of the client
		try
		{
			List<String> users = client.listUsers();
			// 3. print the result (if there are users)
			if (users.size() > 0) {
				System.out.println("list_users command : Users :");
				for (String user : users) {
					System.out.println(user);
				}
			}
			else {
				System.out.println("list_users command : No users");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void list_followers_command(List<String> args) {
		/*
		 * list_followers
		 *
		 * 1. check if there are no arguments
		 * 2. call the list_followers method of the client
		 * 3. print the result
		 */

		// 1. check if there are no arguments
		if (args.size() != 0) {
			System.out.println("list_followers command : Wrong number of arguments : " +
					"usage : list_followers");
			return;
		}

		// 2. call the list_followers method of the client
		try
		{
			List<String> followers = client.listFollowers();
			// 3. print the result (if there are followers)
			if (followers.size() > 0) {
				System.out.println("list_followers command : Followers :");
				for (String follower : followers) {
					System.out.println(follower);
				}
			}
			else {
				System.out.println("list_followers command : No followers");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void list_following_command(List<String> args) {
		/*
		 * list_following
		 *
		 * 1. check if there are no arguments
		 * 2. call the list_following method of the client
		 * 3. print the result
		 */

		// 1. check if there are no arguments
		if (args.size() != 0) {
			System.out.println("list_following command : Wrong number of arguments : " +
					"usage : list_following");
			return;
		}

		// 2. call the list_following method of the client
		try
		{
			List<String> following = client.listFollowing();
			// 3. print the result (if there are users followed)
			if (following.size() > 0) {
				System.out.println("list_following command : Following :");
				for (String user : following) {
					System.out.println(user);
				}
			}
			else {
				System.out.println("list_following command : Not following anyone");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void follow_command(List<String> args) {
		/*
		 * follow <username>
		 *
		 * 1. check if there are exactly 1 argument
		 * 2. call the follow method of the client
		 * 3. print the result
		 */

		// 1. check if there are exactly 1 argument
		if (args.size() != 1) {
			System.out.println("follow command : Wrong number of arguments : " +
					"usage : follow <username>");
			return;
		}

		// 2. call the follow method of the client
		String username = args.get(0);

		try
		{
			client.followUser(username);
			// 3. print the result
			System.out.println("follow command : User " + username + " followed");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void unfollow_command(List<String> args) {
		/*
		 * unfollow <username>
		 *
		 * 1. check if there are exactly 1 argument
		 * 2. call the unfollow method of the client
		 * 3. print the result
		 */

		// 1. check if there are exactly 1 argument
		if (args.size() != 1) {
			System.out.println("unfollow command : Wrong number of arguments : " +
					"usage : unfollow <username>");
			return;
		}

		// 2. call the unfollow method of the client
		String username = args.get(0);

		try
		{
			client.unfollowUser(username);
			// 3. print the result
			System.out.println("unfollow command : User " + username + " unfollowed");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void blog_command(List<String> args) {
		/*
		 * blog
		 *
		 * 1. check if there are no arguments
		 * 2. call the blog method of the client
		 * 3. print the result
		 */

		// 1. check if there are no arguments
		if (args.size() != 0) {
			System.out.println("blog command : Wrong number of arguments : " +
					"usage : blog");
			return;
		}

		try
		{
			// 2. call the blog method of the client
			List<Post> post = client.viewBlog();
			// 3. print the result
			if (post.size() > 0) {
				System.out.println("blog command : Posts :");
				for (Post p : post) {
					System.out.println(p);
				}
			}
			else {
				System.out.println("blog command : No posts");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void post_command(List<String> args) {
		/*
		 * post <title> <content>
		 *
		 * 1. check if there are exactly 2 arguments
		 * 2. call the post method of the client
		 * 3. print the result
		 */

		// 1. check if there are exactly 2 arguments
		if (args.size() != 2) {
			System.out.println("post command : Wrong number of arguments : " +
					"usage : post <title> <content>");
			return;
		}

		// 2. call the post method of the client
		String title = args.get(0);
		String content = args.get(1);
		try
		{
			Post post = client.create_post(title, content);
			// TODO client.createPost(post.title_obj(), post.content_obj());
			// 3. print the result
			System.out.println("post command : Post created");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void show_feed_command(List<String> args) {
		/*
		 * show_feed
		 *
		 * 1. check if there are no arguments
		 * 2. call the show_feed method of the client
		 * 3. print the result
		 */

		// 1. check if there are no arguments
		if (args.size() != 0) {
			System.out.println("show_feed command : Wrong number of arguments : " +
					"usage : show_feed");
			return;
		}

		try
		{
			// 2. call the show_feed method of the client
			List<Post> post = client.showFeed();
			// 3. print the result
			if (post.size() > 0) {
				System.out.println("show_feed command : Posts :");
				for (Post p : post) {
					System.out.println(p);
				}
			}
			else {
				System.out.println("show_feed command : No posts");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void show_post_command(List<String> args) {
		/*
		 * show_post <post_id>
		 *
		 * 1. check if there are exactly 1 argument
		 * 2. call the show_post method of the client
		 * 3. print the result
		 */

		// 1. check if there are exactly 1 argument
		if (args.size() != 1) {
			System.out.println("show_post command : Wrong number of arguments : " +
					"usage : show_post <post_id>");
			return;
		}

		// 2. call the show_post method of the client
		String post_id = args.get(0);
		try
		{
			Post post = client.showPost(post_id);
			// 3. print the result
			System.out.println("show_post command : Post :");
			System.out.println(post);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void delete_command(List<String> args) {
		/*
		 * delete <post_id>
		 *
		 * 1. check if there are exactly 1 argument
		 * 2. call the delete method of the client
		 * 3. print the result
		 */

		// 1. check if there are exactly 1 argument
		if (args.size() != 1) {
			System.out.println("delete command : Wrong number of arguments : " +
					"usage : delete <post_id>");
			return;
		}

		// 2. call the delete method of the client
		String post_id = args.get(0);
		try
		{
			client.deletePost(post_id);
			// 3. print the result
			System.out.println("delete command : Post deleted");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void rewin_command(List<String> args) {
		/*
		 * rewin <post_id>
		 *
		 * 1. check if there are exactly 1 argument
		 * 2. call the rewin method of the client
		 * 3. print the result
		 */

		// 1. check if there are exactly 1 argument
		if (args.size() != 1) {
			System.out.println("rewin command : Wrong number of arguments : " +
					"usage : rewin <post_id>");
			return;
		}

		// 2. call the rewin method of the client
		String post_id = args.get(0);
		try
		{
			client.rewinPost(post_id);
			// 3. print the result
			System.out.println("rewin command : Post rewin");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void rate_command(List<String> args) {
		/*
		 * rate <post_id> <rating>
		 *
		 * 1. check if there are exactly 2 arguments
		 * 2. call the rate method of the client
		 * 3. print the result
		 */

		// 1. check if there are exactly 2 arguments
		if (args.size() != 2) {
			System.out.println("rate command : Wrong number of arguments : " +
					"usage : rate <post_id> <rating>");
			return;
		}

		// 2. call the rate method of the client
		String post_id = args.get(0);
		String rating = args.get(1);
		// 2.1 Check if the rating is a number either 1 or -1
		if (!rating.equals("1") && !rating.equals("-1")) {
			System.out.println("rate command : Wrong rating : " +
					"usage : rate <post_id> <rating>");
			return;
		}
		try
		{
			client.ratePost(post_id, rating.equals("1"));
			// 3. print the result
			System.out.println("rate command : Post rated");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void comment_command(List<String> args) {
		/*
		 * comment <post_id> <comment>
		 *
		 * 1. check if there are at least 2 arguments
		 * 2. call the comment method of the client
		 * 3. print the result
		 */

		// 1. check if there are at least 2 arguments
		if (args.size() < 2) {
			System.out.println("comment command : Wrong number of arguments : " +
					"usage : comment <post_id> <comment>");
			return;
		}

		// 2. call the comment method of the client
		String post_id = args.get(0);
		StringBuilder comment = new StringBuilder(args.get(1));
		for (int i = 2; i < args.size(); i++) {
			comment.append(" ").append(args.get(i));
		}
		try
		{
			client.addComment(post_id, client.create_comment(comment.toString()));
			// 3. print the result
			System.out.println("comment command : Post commented");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void wallet_command(List<String> args) {
		/*
		 * wallet
		 *
		 * 1. call the wallet method of the client
		 * 2. print the result
		 */

		// 1. call the wallet method of the client
		try
		{
			Wallet wallet = client.getWallet();
			// 2. print the result
			System.out.println("wallet command : Wallet :");
			System.out.println(wallet);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void wallet_btc_command(List<String> args) {
		/*
		 * wallet_btc
		 *
		 * 1. call the wallet_btc method of the client
		 * 2. print the result
		 */

		// 1. call the wallet_btc method of the client
		try
		{
			float wallet = client.getWalletInBitcoin();
			// 2. print the result
			System.out.println("wallet_btc command : Wallet :" + wallet + " BTC");
			System.out.println(wallet);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void help_command(List<String> args) {
		/*
		 * print the help message
		 *
		 * the help message should be the following:
		 *
		 * register <username> <password> - register a new user
		 * login <username> <password> - login as an existing user
		 * logout - logout the current user
		 * list users - list all the registered users that share at least one tag with the current user
		 * list followers - list all the followers of the current user
		 * list following - list all the users that the current user is following
		 * follow <username> - follow the user with the given username
		 * unfollow <username> - unfollow the user with the given username
		 * blog - show the blog of the current user
		 * post <title> <content> - create a new post
		 * show feed - show the feed of the logged user
		 * show post <id> - show the post with the given id
		 * delete <id> - delete the post with the given id
		 * rewin <id> - rewin the post with the given id
		 * rate <id> <rating> - rate the post with the given id
		 * comment <id> <content> - comment the post with the given id
		 * wallet - show the wallet of the logged user
		 * wallet btc - show the wallet of the logged user in bitcoin
		 * exit - logout the logged user
		 */
		String help =
				"register <username> <password> - register a new user\n" +
						"login <username> <password> - login as an existing user\n" +
						"logout - logout the current user\n" +
						"list users - list all the registered users that share at least one tag with the current user\n" +
						"list followers - list all the followers of the current user\n" +
						"list following - list all the users that the current user is following\n" +
						"follow <username> - follow the user with the given username\n" +
						"unfollow <username> - unfollow the user with the given username\n" +
						"blog - show the blog of the current user\n" +
						"post <title> <content> - create a new post\n" +
						"show feed - show the feed of the logged user\n" +
						"show post <id> - show the post with the given id\n" +
						"delete <id> - delete the post with the given id\n" +
						"rewin <id> - rewin the post with the given id\n" +
						"rate <id> <rating> - rate the post with the given id\n" +
						"comment <id> <content> - comment the post with the given id\n" +
						"wallet - show the wallet of the logged user\n" +
						"wallet btc - show the wallet of the logged user in bitcoin\n" +
						"exit - logout the logged user\n";

		System.out.println(help);
	}

	private void exit_command(List<String> args) {
		client.exit();
	}

	public void listen() throws IOException {
		/*
		 * This method is used to listen to the user input and execute the
		 * corresponding command
		 *
		 * register <username> <password> <tag> <tag> ... this command is used to register a new user
		 *    to the system. The # of tags is limited to 5 but less than 5 is allowed.
		 * login <username> <password> this command is used to login to the system.
		 * logout this command is used to logout from the system.
		 * list users this command is used to list all the users in the system that have at least
		 *    one tag in common with the current user.
		 * list following this command is used to list all the users that the current user is following.
		 * list followers this command is used to list all the users that are following the current user.
		 * follow <username> this command is used to follow a user.
		 * unfollow <username> this command is used to unfollow a user.
		 * blog this command is used to show the blog of the current user.
		 * post <title> <content> this command is used to post a new blog entry.
		 * show feed this command is used to show the feed of the current user.
		 * show post <id> this command is used to show a specific post and its statistics.
		 * delete <id> this command is used to delete a specific post of the current user.
		 * rewin <id> this command is used to repost a specific post in the feed of the current user.
		 * rate <id> <vote> this command is used to rate a specific post in the feed of the current user,
		 *    the vote can be 1 or -1 (like / dislike).
		 * comment <id> <comment> this command is used to comment a specific post in the feed of the current user.
		 * wallet this command is used to show the wallet of the current user.
		 * wallet btc this command is used to show the wallet of the current user in BTC.
		 * exit this command is used to exit the system.
		 * help this command is used to show the list of commands.
		 *
		 * Every other command will result in an error message.
		 */

		// promt the user to enter a command
		System.out.print("Enter a command: ");

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line = reader.readLine();
		String[] tokens = line.split(" ");

		// if the first token is "list" or "show" then the command is composed of 2 tokens
		// otherwise the command is composed of 1 token

		if (tokens[0].equals("list") || tokens[0].equals("show")) {
			if (tokens.length != 2) {
				System.out.println("Invalid command");
				return;
			}
		} else {
			if (tokens.length != 1) {
				System.out.println("Invalid command");
				return;
			}
		}

		boolean composed = tokens[0].equals("list") || tokens[0].equals("show");
		String command = composed ? tokens[0] + "_" + tokens[1] : tokens[0];

		// check if the command is valid (i.e. it is in the commands map)
		if (commands.containsKey(command)) {
			try {
				// execute the command with the arguments (not including the command itself)
				commands.get(command).invoke(this, new ArrayList<>(
						Arrays.asList(tokens).subList(composed ? 2 : 1, tokens.length)));
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage() + " " + e.getCause());
				e.printStackTrace();
			}
		} else {
			printErrorCommand();
		}
	}
}
