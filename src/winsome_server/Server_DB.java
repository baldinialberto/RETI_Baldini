package winsome_server;

import winsome_comunication.Post_detailed;
import winsome_comunication.Post_simple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Server_DB {
	// Member variables
	Post_collection posts;
	User_collection users;
	final String posts_file_path;
	final String users_file_path;


	// public constants
	public enum DB_ERROR_CODE {
		SUCCESS(0),
		USR_NOT_FOUND(-1),
		USR_ALREADY_EXISTS(-2),

		USR_ALREADY_FOLLOWING(-3);

		private final int value;

		DB_ERROR_CODE(int value) {
			this.value = value;
		}

		public static Optional<DB_ERROR_CODE> valueOf(int value) {
			// DEBUG
			System.out.println("DB_ERROR_CODE: valueOf: " + value);

			for (DB_ERROR_CODE code : DB_ERROR_CODE.values()) {
				if (code.value == value) {
					return Optional.of(code);
				}
			}
			return Optional.empty();
		}

		public int getValue() {
			return value;
		}
		public static String getStringOf(DB_ERROR_CODE code) {
			switch (code) {
				case SUCCESS:
					return "NO_ERROR";
				case USR_NOT_FOUND:
					return "USERNAME NOT FOUND";
				case USR_ALREADY_EXISTS:
					return "USERNAME ALREADY EXISTS";
				case USR_ALREADY_FOLLOWING:
					return "USERNAME ALREADY FOLLOWING";
				default:
					return "UNKNOWN_ERROR";
			}
		}
		public static String getStringOf(int value) {
			Optional<DB_ERROR_CODE> code = DB_ERROR_CODE.valueOf(value);
			return code.map(DB_ERROR_CODE::getStringOf).orElse("UNKNOWN_ERROR");
		}
	}

	// Constructor
	public Server_DB(String posts_file_path, String users_file_path) {
		/*
		 * This constructor is used when we want to create a new server database.
		 *
		 *
		 * 1. Set the file path of the posts file.
		 * 2. Set the file path of the users file.
		 */

		// 1. Set the file path of the posts file.
		this.posts_file_path = posts_file_path;

		// 2. Set the file path of the users file.
		this.users_file_path = users_file_path;
	}

	// Methods
	public int load_DB() {
		/*
		 * This method is used to load the database from the files.
		 *
		 * 1. Load the posts from the posts file.
		 * 2. Load the users from the users file.
		 */

		// 1. Load the posts from the posts file.
		load_posts();

		// 2. Load the users from the users file.
		load_users();

		return 0;
	}

	public int save_DB() {
		/*
		 * This method is used to save the database to the files.
		 *
		 * 1. Save the posts to the posts file.
		 * 2. Save the users to the users file.
		 */

		// 1. Save the posts to the posts file.
		save_posts();

		// 2. Save the users to the users file.
		save_users();

		return 0;
	}

	public int add_post(String author, String username, String title, String text) {
		/*
		 * This method is used to add a post to the database.
		 *
		 * 1. Add the post to the posts.
		 * 2. Add the post to the user.
		 */

		// 1. Add the post to the posts.
		posts.add_post(author, title, text);

		// 2. Add the post to the user.
		return users.add_post(username, posts.getLast_post_id());
	}

	private void load_posts() {
		/*
		 * This method is used to load the posts from the posts file.
		 *
		 * 1. Try to load the posts from the posts file.
		 * 2. If the posts file does not exist, create a new posts file.
		 */

		// 1. Try to load the posts from the posts file.
		try {
			posts = Post_collection.JSON_read(posts_file_path);
		} catch (IOException e) {
			// if it's not a FileNotFoundException, print the stack trace
			if (!(e instanceof java.io.FileNotFoundException)) {
				e.printStackTrace();
			}

			// 2. If the posts file does not exist, create a new posts file.
			posts = new Post_collection();
			save_posts();
		}
	}

	private void load_users() {
		/*
		 * This method is used to load the users from the users file.
		 *
		 * 1. Try to load the users from the users file.
		 * 2. If the users file does not exist, create a new users file.
		 */

		// 1. Try to load the users from the users file.
		try {
			users = User_collection.JSON_read(users_file_path);
		} catch (IOException e) {
			// if it's not a FileNotFoundException, print the stack trace
			if (!(e instanceof java.io.FileNotFoundException)) {
				e.printStackTrace();
			}

			// 2. If the users file does not exist, create a new users file.
			users = User_collection.getInstance();
			save_users();
		}
	}

	private void save_posts() {
		/*
		 * This method is used to save the posts to the posts file.
		 *
		 * 1. Try to save the posts to the posts file.
		 */

		// 1. Try to save the posts to the posts file.
		try {
			posts.JSON_write(posts_file_path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void save_users() {
		/*
		 * This method is used to save the users to the users file.
		 *
		 * 1. Try to save the users to the users file.
		 */

		// 1. Try to save the users to the users file.
		try {
			users.JSON_write(users_file_path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public boolean user_check_password(String username, String password)
	{
		/*
		 * This method is used to check if a user exists.
		 *
		 * 1. Check if the user exists.
		 */

		// 1. Check if the user exists.
		if (users.user_exists(username))
		{
			return users.check_password(username, password);
		}
		else
		{
			return false;
		}
	}

	public int add_user(String username, String password, String[] tags) {
		/*
		 * This method is used to add a user to the database.
		 *
		 * 1. Add the user to the users.
		 */

		// 1. Add the user to the users.
		return users.add_user(username, password, tags);
	}

	public List<String> users_with_common_tags(String username) {
		/*
		 * This method is used to get a list of users with common tags.
		 *
		 * 1. Get the list of users with common tags.
		 */

		// 1. Get the list of users with common tags.
		return users.users_with_common_tags(username);
	}

	public int follow_username(String user, String username_to_follow) {
		/*
		 * This method is used to follow a user.
		 *
		 * 1. Follow the user.
		 */

		// 1. Follow the user.
		return users.follow_username(user, username_to_follow);
	}

	public int unfollow_username(String user, String username_to_unfollow) {
		/*
		 * This method is used to unfollow a user.
		 *
		 * 1. Unfollow the user.
		 */

		// 1. Unfollow the user.
		return users.unfollow_username(user, username_to_unfollow);
	}

	public ArrayList<Post_simple> get_blog(String user) {
		/*
		 * This method is used to get a blog.
		 *
		 * 1. Get the user's blog.
		 */

		if (users.user_exists(user))
		{
			// 1. Get the user's blog.
			return posts.get_postsimple_from_idlist(users.get_user_postids(user));
		}
		else
		{
			return null;
		}
	}

	public Post_detailed get_post_detailed(String post_id) {
		/*
		 * This method is used to get detailed information about a post.
		 *
		 * 1. Get the post.
		 */

		if (posts.post_exists(post_id))
		{
			// 1. Get the post.
			return posts.get_post_detailed(post_id);
		}
		else
		{
			return null;
		}
	}


	// Getters
	// None

	// Setters
	// None
}
