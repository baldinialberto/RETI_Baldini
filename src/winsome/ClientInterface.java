package winsome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientInterface {

	private Client client;

	public ClientInterface(Client client) {
		this.client = client;
	}

	public void exec()
	{
		while (client.is_on())
		{
			try {
				listen();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void printErrorCommand() {
		System.out.println("Command not recognized");
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
		String command = tokens[0];
		List<String> args = new ArrayList<>(Arrays.asList(tokens));
		args.remove(0);

		switch (command) {
			case "register":
				/*
				 * register <username> <password> <tag> <tag> ... case
				 * client.register(String username, String password, List<String> tags)
				 *
				 * prepare the arguments and call the client.register method
				 */
				List<String> tags = new ArrayList<>(args);
				tags.remove(0);
				tags.remove(0);
				try {
					client.register(args.get(0), args.get(1), tags);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "login":
				/*
				 * login <username> <password> case
				 * client.login(String username, String password)
				 *
				 * prepare the arguments and call the client.login method
				 */
				try {
					client.login(args.get(0), args.get(1));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "logout":
				/*
				 * logout case client.logout()
				 *
				 * call the client.logout method
				 */
				try {
					client.logout();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "list":
				if (args.size() == 1 && args.get(0).equals("users")) {
					/*
					 * list users case client.listUsers()
					 *
					 * call the client.listUsers method
					 */
					try {
						client.listUsers();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				} else if (args.size() == 1 && args.get(0).equals("following")) {
					/*
					 * list following case client.listFollowing()
					 *
					 * call the client.listFollowing method
					 */
					try {
						client.listFollowing();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				} else if (args.size() == 1 && args.get(0).equals("followers")) {
					/*
					 * list followers case client.listFollowers()
					 *
					 * call the client.listFollowers method
					 */
					try {
						client.listFollowers();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				} else {
					/*
					 * list <something else> case printErrorCommand()
					 *
					 * call the printErrorCommand method
					 */
					printErrorCommand();
				}
				break;
			case "follow":
				/*
				 * follow <username> case client.follow(String username)
				 *
				 * prepare the arguments and call the client.followUser method
				 */
				try {
					client.followUser(args.get(0));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "unfollow":
				/*
				 * unfollow <username> case client.unfollowUser(String username)
				 *
				 * prepare the arguments and call the client.unfollowUser method
				 */
				try {
					client.unfollowUser(args.get(0));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "blog":
				/*
				 * blog case client.viewBlog()
				 *
				 * call the client.viewBlog method
				 */
				try {
					client.viewBlog();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "post":
				/*
				 * post <title> <content> case client.createPost(String title, String content)
				 *
				 * prepare the arguments and call the client.createPost method
				 */
				try {
					String title = args.get(0);
					args.remove(0);
					String content = String.join(" ", args);
					Post newPost = client.create_post(title, content);
					client.createPost(newPost.title_obj(), newPost.content_obj());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "show":
				if (args.size() == 1 && args.get(0).equals("feed")) {
					/*
					 * show feed case client.showFeed()
					 *
					 * call the client.showFeed method
					 */
					client.showFeed();
				} else if (args.size() == 2 && args.get(0).equals("post")) {
					/*
					 * show post <id> case client.showPost(String id)
					 *
					 * prepare the arguments and call the client.showPost method
					 */
					try {
						client.showPost(args.get(1));
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				} else {
					/*
					 * show <something else> case printErrorCommand()
					 *
					 * call the printErrorCommand method
					 */
					printErrorCommand();
				}
				break;
			case "delete":
				/*
				 * delete <id> case client.delete(String id)
				 *
				 * prepare the arguments and call the client.deletePost method
				 */
				try {
					client.deletePost(args.get(0));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "rewin":
				/*
				 * rewin <id> case client.rewin(String id)
				 *
				 * prepare the arguments and call the client.rewinPost method
				 */
				try {
					client.rewinPost(args.get(0));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "rate":
				/*
				 * rate <id> <rating> case client.rate(String id, int rating)
				 *
				 * prepare the arguments and call the client.ratePost method
				 */
				// check if the rating is an integer 1 or -1
				if (args.get(1).equals("1") || args.get(1).equals("-1")) {
					try {
						client.ratePost(args.get(0), Integer.parseInt(args.get(1)) == 1);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				} else {
					System.out.println("Rating must be 1 or -1");
				}
				break;
			case "comment":
				/*
				 * comment <id> <content> case client.addComment(String id, String content)
				 *
				 * prepare the arguments and call the client.addComment method
				 */
				try {
					String id = args.get(0);
					args.remove(0);
					Comment comment = client.create_comment(String.join(" ", args));

					client.addComment(id, comment);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "wallet":
				if (args.size() == 0) {
					/*
					 * wallet case client.getWallet()
					 *
					 * call the client.getWallet method
					 */
					client.getWallet();
				} else if (args.size() == 1 && args.get(0).equals("btc")) {
					/*
					 * wallet btc case client.getWalletInBitcoin()
					 *
					 * call the client.getWalletInBitcoin method
					 */
					client.getWalletInBitcoin();
				} else {
					printErrorCommand();
				}
				break;
			case "exit":
				/*
				 * exit case client.exit()
				 *
				 * call the client.exit method
				 */
				client.logout();
				client.exit();
				break;
			case "help":
				/*
				 * help case printHelp()
				 *
				 * call the printHelp method
				 */
				printHelp();
				break;
			default:
				printErrorCommand();
				break;
		}
	}

	private void printHelp() {
		/*
		 * print the help message
		 *
		 * the help message should be the following:
		 *
		 * register <username> <password> - register a new user
		 * login <username> <password> - login as an existing user
		 * logout - logout the current user
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

}
