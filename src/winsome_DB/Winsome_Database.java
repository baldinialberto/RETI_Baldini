package winsome_DB;

import winsome_comunication.*;
import winsome_server.Winsome_DB_Interface;
import winsome_server.Winsome_Reward;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class represents the Database of the Winsome server.
 * It contains:
 * 1. A map of all the users in the database.
 * 2. A map of all the posts in the database.
 * 3. The id of the last post created.
 * 4. The file path of the users' database.
 * 5. The file path of the posts' database.
 * <p></p>
 * This class is a singleton.
 */
public class Winsome_Database implements Winsome_DB_Interface {
	// Member variables
	private Winsome_DB_Posts posts;
	private Winsome_DB_Users users;
	final private String posts_file_path;
	final private String users_file_path;
	private boolean posts_backup_valid = true;
	private boolean users_backup_valid = true;
	private boolean create_if_not_exist;
	private boolean initialized = false;
	private static Winsome_Database instance = null;

	private final Winsome_DB_Thread thread = new Winsome_DB_Thread(this);

	// Constructors

	// Private constructor
	private Winsome_Database(String posts_file_path, String users_file_path, boolean create_if_not_exist) {
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

		// 3. Set the create_if_not_exist flag
		this.create_if_not_exist = create_if_not_exist;

		// 4. Start the thread
		thread.start();
	}

	// Instance getter
	public static Winsome_Database getInstance(
			String posts_file_path, String users_file_path, boolean create_if_not_exist)
	{
		/*
		 * This method is used to get the instance of the database.
		 * If the database is not initialized, it will be initialized.
		 * If the database is already initialized, it will return the instance.
		 *
		 * 1. If the database is not initialized, initialize it.
		 * 2. Return the instance.
		 */

		// 1. If the database is not initialized, initialize it.
		if (instance == null)
			instance = new Winsome_Database(posts_file_path, users_file_path, create_if_not_exist);

		// 2. Return the instance.
		return instance;
	}

	// Private Methods
	private int load_DB() throws Winsome_DB_Exception.UsersDatabaseNotFound, Winsome_DB_Exception.PostsDatabaseNotFound {
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
	private void load_posts() throws Winsome_DB_Exception.PostsDatabaseNotFound {
		/*
		 * This method is used to load the posts from the posts file.
		 *
		 * 1. Try to load the posts from the posts file.
		 * 2. If the posts file does not exist, create a new posts file.
		 */

		// 1. Try to load the posts from the posts file.
		try {
			posts = Winsome_DB_Posts.JSON_read(posts_file_path);
		} catch (IOException e) {
			// if it's not a FileNotFoundException, print the stack trace
			if (!(e instanceof java.io.FileNotFoundException)) {
				e.printStackTrace();
			}

			if (create_if_not_exist) {
				// 2. If the posts file does not exist, create a new posts file.
				posts = new Winsome_DB_Posts();
				save_posts();
			} else {
				throw new Winsome_DB_Exception.PostsDatabaseNotFound();
			}
		}
	}
	private void load_users() throws Winsome_DB_Exception.UsersDatabaseNotFound {
		/*
		 * This method is used to load the users from the users file.
		 *
		 * 1. Try to load the users from the users file.
		 * 2. If the users file does not exist, create a new users file.
		 */

		// 1. Try to load the users from the users file.
		try {
			users = Winsome_DB_Users.JSON_read(users_file_path);
		} catch (IOException e) {
			// if it's not a FileNotFoundException, print the stack trace
			if (!(e instanceof java.io.FileNotFoundException)) {
				e.printStackTrace();
			}

			if (create_if_not_exist) {
				// 2. If the users file does not exist, create a new users file.
				users = new Winsome_DB_Users();
				save_users();
			} else {
				throw new Winsome_DB_Exception.UsersDatabaseNotFound();
			}
		}
	}
	void save_DB() throws Winsome_DB_Exception.DatabaseNotInitialized, Winsome_DB_Exception.DatabaseNotSaved {
		/*
		 * This method is used to save the database to the files.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. Save the posts to the posts file.
		 * 3. Save the users to the users file.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		// 2. Save the posts to the posts file.// 3. Save the users to the users file.
		if (save_posts() != 0 || save_users() != 0)
			throw new Winsome_DB_Exception.DatabaseNotSaved();
	}
	private int save_posts() {
		/*
		 * This method is used to save the posts to the posts file.
		 *
		 * 1. Try to save the posts to the posts file.
		 */

		if (posts_backup_valid) return 0;

		// 1. Try to save the posts to the posts file.
		try {
			posts.JSON_write(posts_file_path);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}

		return 0;
	}
	private int save_users() {
		/*
		 * This method is used to save the users to the users file.
		 *
		 * 1. Try to save the users to the users file.
		 */

		if (users_backup_valid) return 0;

		// 1. Try to save the users to the users file.
		try {
			users.JSON_write(users_file_path);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}

		return 0;
	}


	/**
	 * This method creates a new user in the database.
	 *
	 * @param username The username of the new user.
	 * @param password The password of the new user.
	 * @param tags     The tags (interests) of the new user.
	 * @throws Winsome_DB_Exception.UsernameAlreadyExists  if the username already exists in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public void create_user(String username, String password, String[] tags) throws Winsome_DB_Exception.UsernameAlreadyExists, Winsome_DB_Exception.DatabaseNotInitialized {
		/*
		 * This method creates a new user in the database.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username already exists in the database, throw an exception.
		 * 3. Create the new user.
		 * 4. Dirty the users backup.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		// 2. If the username already exists in the database, throw an exception.
		if (users.containsKey(username))
			throw new Winsome_DB_Exception.UsernameAlreadyExists();

		// 3. Create the new user.
		users.put(username, new User_DB(username, password, tags));

		// 4. Dirty the users backup.
		users_backup_valid = false;
	}

	/**
	 * This method checks if a user's credentials are correct.
	 *
	 * @param username The username of the user to check.
	 * @param password The password of the user to check.
	 * @return True if the credentials are correct, false otherwise.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public boolean check_credentials(String username, String password) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		/*
		 * This method checks if a user's credentials are correct.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. Check if the password is correct.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username))
			throw new Winsome_DB_Exception.UsernameNotFound();

		// 3. Check if the password is correct.
		return users.get(username).getPassword().equals(password);
	}

	/**
	 * This method makes a user follow another user.
	 *
	 * @param username           The username of the user that wants to follow.
	 * @param username_to_follow The username of the user to follow.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.UsernameAlreadyFollows if the user already follows the user to follow.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public void user_follows(String username, String username_to_follow) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.UsernameAlreadyFollows, Winsome_DB_Exception.DatabaseNotInitialized {
		/*
		 * This method makes a user follow another user.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. If the user already follows the user to follow, throw an exception.
		 * 4. Make the user follow the user to follow.
		 * 5. Add the user to the <username_to_follow> followers.
		 * 6. Dirty the users backup.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username))
			throw new Winsome_DB_Exception.UsernameNotFound();

		// 3. If the user already follows the user to follow, throw an exception.
		if (users.get(username).getFollowing().contains(username_to_follow))
			throw new Winsome_DB_Exception.UsernameAlreadyFollows(username, username_to_follow);

		// 4. Make the user follow the user to follow.
		users.get(username).getFollowing().add(username_to_follow);

		// 5. Add the user to the <username_to_follow> followers.
		users.get(username_to_follow).getFollowers().add(username);

		// 6. Dirty the users backup.
		users_backup_valid = false;
	}

	/**
	 * This method makes a user unfollow another user.
	 *
	 * @param username             The username of the user that wants to unfollow.
	 * @param username_to_unfollow The username of the user to unfollow.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.UsernameNotFollowing   if the user is not following the user to unfollow.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public void user_unfollows(String username, String username_to_unfollow) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.UsernameNotFollowing, Winsome_DB_Exception.DatabaseNotInitialized {
		/*
		 * This method makes a user unfollow another user.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. If the user is not following the user to unfollow, throw an exception.
		 * 4. Make the user unfollow the user to unfollow.
		 * 5. Remove the user from the <username_to_unfollow> followers.
		 * 6. Dirty the users backup.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username))
			throw new Winsome_DB_Exception.UsernameNotFound();

		// 3. If the user is not following the user to unfollow, throw an exception.
		if (!users.get(username).getFollowing().contains(username_to_unfollow))
			throw new Winsome_DB_Exception.UsernameNotFollowing(username, username_to_unfollow);

		// 4. Make the user unfollow the user to unfollow.
		users.get(username).getFollowing().remove(username_to_unfollow);

		// 5. Remove the user from the <username_to_unfollow> followers.
		users.get(username_to_unfollow).getFollowers().remove(username);

		// 6. Dirty the users backup.
		users_backup_valid = false;
	}

	/**
	 * This method gets a user's followers list.
	 *
	 * @param username The username of the user to get the followers.
	 * @return An array of usernames of the followers.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public String[] get_user_followers(String username) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		/*
		 * This method gets a user's followers list.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. Return the followers list.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username))
			throw new Winsome_DB_Exception.UsernameNotFound();

		// 3. Return the followers list.
		return users.get(username).getFollowers().toArray(new String[0]);
	}

	/**
	 * This method gets a user's following list.
	 *
	 * @param username The username of the user to get the following.
	 * @return An array of usernames of the following.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public String[] get_user_following(String username) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		/*
		 * This method gets a user's following list.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. Return the following list.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username))
			throw new Winsome_DB_Exception.UsernameNotFound();

		// 3. Return the following list.
		return users.get(username).getFollowing().toArray(new String[0]);
	}

	/**
	 * This method gets a user's wallet.
	 *
	 * @param username The username of the user to get the wallet.
	 * @return A Wallet_representation object with the wallet information.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public Wallet_representation get_user_wallet(String username) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		// TODO: Implement this method.
		return null;
	}

	/**
	 * This method gets a user's posts.
	 *
	 * @param username The username of the user to get the posts.
	 * @return An array of Post_representation objects with the posts' information.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public Post_representation_simple[] get_user_posts(String username) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		return new Post_representation_simple[0];
	}

	/**
	 * This method creates a new post in the database.
	 *
	 * @param author  The username of the author of the post.
	 * @param title   The title of the post.
	 * @param content The content of the post.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 * @throws Winsome_DB_Exception.PostInvalidTitle       if the title is invalid.
	 * @throws Winsome_DB_Exception.PostInvalidContent     if the content is invalid.
	 */
	@Override
	public void create_post(String author, String title, String content) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.DatabaseNotInitialized, Winsome_DB_Exception.PostInvalidTitle, Winsome_DB_Exception.PostInvalidContent {

	}

	/**
	 * This method removes a post from the database.
	 *
	 * @param username The username that wants to remove the post.
	 * @param post_id  The id of the post to remove.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.PostNotFound           if the post is not found in the database.
	 * @throws Winsome_DB_Exception.PostNotOwned           if the post is not owned by the user.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public void remove_post(String username, String post_id) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.PostNotFound, Winsome_DB_Exception.PostNotOwned, Winsome_DB_Exception.DatabaseNotInitialized {

	}

	/**
	 * This method gets a post's rates.
	 *
	 * @param post_id The id of the post to get the rates.
	 * @return An array of booleans (true if the rate is like, false if it is dislike).
	 * @throws Winsome_DB_Exception.PostNotFound           if the post is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public boolean[] get_post_rates(String post_id) throws Winsome_DB_Exception.PostNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		return new boolean[0];
	}

	/**
	 * This method gets a post's comments.
	 *
	 * @param post_id The id of the post to get the comments.
	 * @return An array of Comment_representation objects with the comments' information.
	 * @throws Winsome_DB_Exception.PostNotFound           if the post is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public Comment_representation[] get_post_comments(String post_id) throws Winsome_DB_Exception.PostNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		return new Comment_representation[0];
	}

	/**
	 * This method is used to like/dislike a post.
	 *
	 * @param username The username of the user who wants to rate the post.
	 * @param post_id  The id of the post to rate.
	 * @param rate     The rate of the user to the post.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.PostNotFound           if the post is not found in the database.
	 * @throws Winsome_DB_Exception.PostAlreadyRated       if the user has already rated the post.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public void rate_post(String username, String post_id, boolean rate) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.PostNotFound, Winsome_DB_Exception.PostAlreadyRated, Winsome_DB_Exception.DatabaseNotInitialized {

	}

	/**
	 * This method is used to comment a post.
	 *
	 * @param username The username of the user who wants to comment the post.
	 * @param post_id  The id of the post to comment.
	 * @param comment  The comment of the user to the post.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.PostNotFound           if the post is not found in the database.
	 * @throws Winsome_DB_Exception.PostCommentedByAuthor  if the user is the author of the post.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public void comment_on_post(String username, String post_id, String comment) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.PostNotFound, Winsome_DB_Exception.PostCommentedByAuthor, Winsome_DB_Exception.DatabaseNotInitialized {

	}

	/**
	 * This method is used to get a post's simple representation.
	 *
	 * @param post_id The id of the post to get the representation.
	 * @return A Post_representation_simple object with the post's information.
	 * @throws Winsome_DB_Exception.PostNotFound           if the post is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public Post_representation_simple get_post_simple(String post_id) throws Winsome_DB_Exception.PostNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		return null;
	}

	/**
	 * This method is used to get a post's full representation.
	 *
	 * @param post_id the id of the post to get the representation.
	 * @return A Post_representation_detailed object with the post's information.
	 * @throws Winsome_DB_Exception.PostNotFound           if the post is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public Post_representation_detailed get_post(String post_id) throws Winsome_DB_Exception.PostNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		return null;
	}

	/**
	 * This method is used to get the posts of a user.
	 *
	 * @param username The username of the user to get the posts.
	 * @return An array of Post_representation_simple objects with the posts' information.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public Post_representation_simple[] get_user_blog(String username) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		return new Post_representation_simple[0];
	}

	/**
	 * This method is used to get the posts of a user's feed.
	 *
	 * @param username The username of the user to get the feed.
	 * @return An array of Post_representation_simple objects with the posts' information.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public Post_representation_simple[] get_user_feed(String username) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		return new Post_representation_simple[0];
	}

	/**
	 * This method is used to get the usernames of users with similar interests with the given user.
	 *
	 * @param username The username of the user to get the similar users.
	 * @return An array of strings with the usernames of the similar users.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public String[] get_similar_users(String username) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		return new String[0];
	}

	/**
	 * This method is used to reward every user with a certain amount of coins.
	 */
	@Override
	public void reward_everyone() {

	}

	/**
	 * This method is used to close the database.
	 */
	@Override
	public void close() {

	}
}
