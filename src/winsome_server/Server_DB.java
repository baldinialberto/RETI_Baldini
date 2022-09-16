package winsome_server;

import java.io.IOException;

public class Server_DB {
	// Member variables
	Post_collection posts;
	User_collection users;
	final String posts_file_path;
	final String users_file_path;

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
			posts = Post_collection.getInstance();
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


	// Getters
	public Post_collection get_posts() {
		return posts;
	}

	public User_collection get_users() {
		return users;
	}

	// Setters
	// None
}
