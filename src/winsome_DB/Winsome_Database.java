package winsome_DB;

import winsome_comunication.*;
import winsome_server.Winsome_DB_Interface;
import winsome_server.Winsome_Reward;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
	private final boolean create_if_not_exist;
	private boolean initialized = false;
	private static Winsome_Database instance = null;
	private final Winsome_DB_Thread thread = new Winsome_DB_Thread(this);
	private final Lock users_R_lock;
	private final Lock users_W_lock;
	private final Lock posts_R_lock;
	private final Lock posts_W_lock;

	// Constructors

	// Private constructor
	private Winsome_Database(String posts_file_path, String users_file_path, boolean create_if_not_exist) {
		/*
		 * This constructor is used when we want to create a new server database.
		 *
		 *
		 * 1. Set the file path of the posts file.
		 * 2. Set the file path of the users file.
		 * 3. Set the create_if_not_exist flag.
		 * 4. Create the locks.
		 */

		// 1. Set the file path of the posts file.
		this.posts_file_path = posts_file_path;

		// 2. Set the file path of the users file.
		this.users_file_path = users_file_path;

		// 3. Set the create_if_not_exist flag
		this.create_if_not_exist = create_if_not_exist;

		// 4. Create the locks.
		ReadWriteLock users_RW_lock = new ReentrantReadWriteLock(true);
		ReadWriteLock posts_RW_lock = new ReentrantReadWriteLock(true);
		users_R_lock = users_RW_lock.readLock();
		users_W_lock = users_RW_lock.writeLock();
		posts_R_lock = posts_RW_lock.readLock();
		posts_W_lock = posts_RW_lock.writeLock();
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
	public void load_DB() throws Winsome_DB_Exception.UsersDatabaseNotFound, Winsome_DB_Exception.PostsDatabaseNotFound {
		/*
		 * This method is used to load the database from the files.
		 *
		 * 1. Load the posts from the posts file.
		 * 2. Load the users from the users file.
		 * 3. Set the initialized flag to true.
		 * 4. Start the database thread.
		 */

		// 1. Load the posts from the posts file.
		load_posts();

		// 2. Load the users from the users file.
		load_users();

		// 3. Set the initialized flag to true.
		initialized = true;

		// 4. Start the thread.
		thread.start();
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

		// DEBUG
		// Print that the database is being saved.
		// In green color.
		System.out.println("\033[32mSaving the database...\033[0m");

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
		posts_R_lock.lock();
		try {
			posts.JSON_write(posts_file_path);
		} catch (IOException e) {
			e.printStackTrace();
			posts_R_lock.unlock();
			return -1;
		}

		posts_R_lock.unlock();
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
		users_R_lock.lock();
		try {
			users.JSON_write(users_file_path);
		} catch (IOException e) {
			e.printStackTrace();
			users_R_lock.unlock();
			return -1;
		}

		users_R_lock.unlock();
		return 0;
	}

	private void lock_both_R() {
		users_R_lock.lock();
		posts_R_lock.lock();
	}

	private void unlock_both_R() {
		posts_R_lock.unlock();
		users_R_lock.unlock();
	}

	private void lock_both_W() {
		users_W_lock.lock();
		posts_W_lock.lock();
	}

	private void unlock_both_W() {
		posts_W_lock.unlock();
		users_W_lock.unlock();
	}

	// Public Methods

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

		users_W_lock.lock();
		// 2. If the username already exists in the database, throw an exception.
		if (users.containsKey(username)) {
			users_W_lock.unlock();
			throw new Winsome_DB_Exception.UsernameAlreadyExists();
		}

		// 3. Create the new user.
		users.put(username, new User_DB(username, password, tags));

		// 4. Dirty the users backup.
		users_backup_valid = false;

		users_W_lock.unlock();
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

		users_R_lock.lock();
		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			users_R_lock.unlock();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. Check if the password is correct.
		boolean res = users.get(username).getPassword().equals(password);
		users_R_lock.unlock();

		return res;
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

		users_W_lock.lock();
		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			users_W_lock.unlock();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. If the user already follows the user to follow, throw an exception.
		if (users.get(username).getFollowing().contains(username_to_follow)) {
			users_W_lock.unlock();
			throw new Winsome_DB_Exception.UsernameAlreadyFollows(username, username_to_follow);
		}

		// 4. Make the user follow the user to follow.
		users.get(username).getFollowing().add(username_to_follow);

		// 5. Add the user to the <username_to_follow> followers.
		users.get(username_to_follow).getFollowers().add(username);

		// 6. Dirty the users backup.
		users_backup_valid = false;

		users_W_lock.unlock();
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

		users_W_lock.lock();
		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			users_W_lock.unlock();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. If the user is not following the user to unfollow, throw an exception.
		if (!users.get(username).getFollowing().contains(username_to_unfollow)) {
			users_W_lock.unlock();
			throw new Winsome_DB_Exception.UsernameNotFollowing(username, username_to_unfollow);
		}

		// 4. Make the user unfollow the user to unfollow.
		users.get(username).getFollowing().remove(username_to_unfollow);

		// 5. Remove the user from the <username_to_unfollow> followers.
		users.get(username_to_unfollow).getFollowers().remove(username);

		// 6. Dirty the users backup.
		users_backup_valid = false;

		users_W_lock.unlock();
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

		users_R_lock.lock();
		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			users_R_lock.unlock();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. Return the followers list.
		String[] res = users.get(username).getFollowers().toArray(new String[0]);
		users_R_lock.unlock();

		return res;
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

		users_R_lock.lock();
		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			users_R_lock.unlock();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. Return the following list.
		String[] res = users.get(username).getFollowing().toArray(new String[0]);
		users_R_lock.unlock();

		return res;
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
		/*
		 * This method gets a user's wallet.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. Return the wallet.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		users_R_lock.lock();
		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			users_R_lock.unlock();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}
		// 3. Return the wallet.
		Wallet_representation res = users.get(username).getWallet().representation();
		users_R_lock.unlock();

		return res;
	}

	/**
	 * This method gets a user's posts.
	 *
	 * @param username The username of the user to get the posts.
	 * @return An array of Post_representation objects with the posts' information.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override @Deprecated
	public Post_representation_simple[] get_user_posts(String username) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.DatabaseNotInitialized {
		return get_user_feed(username);
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
		/*
		 * This method creates a new post in the database.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. If the title is invalid, throw an exception.
		 * 4. If the content is invalid, throw an exception.
		 * 5. Create a new post and add it to the database.
		 * 6. Add the post to the author's posts.
		 * 7. Dirty the posts backup.
		 * 8. Dirty the users backup.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		// 2. If the username is not found in the database, throw an exception.
		users_R_lock.lock();
		if (!users.containsKey(author)) {
			users_R_lock.unlock();
			throw new Winsome_DB_Exception.UsernameNotFound(author);
		}
		users_R_lock.unlock();

		// 3. If the title is invalid, throw an exception.
		if (title == null || title.length() == 0 || title.length() > Post_DB.TITLE_MAX_LENGTH)
			throw new Winsome_DB_Exception.PostInvalidTitle(title);

		// 4. If the content is invalid, throw an exception.
		if (content == null || content.length() == 0 || content.length() > Post_DB.CONTENT_MAX_LENGTH)
			throw new Winsome_DB_Exception.PostInvalidContent(content);

		users_W_lock.lock();
		posts_W_lock.lock();
		// 5. Create a new post and add it to the database.
		posts.add_post(author, title, content);

		// 6. Add the post to the author's posts.
		users.get(author).getPosts().add(posts.getLast_post_id());

		// 8. Dirty the posts backup.
		posts_backup_valid = false;

		// 9. Dirty the users backup.
		users_backup_valid = false;
		posts_W_lock.unlock();
		users_W_lock.unlock();
	}

	/**
	 * This method removes a post from the database.
	 * If the post is rewined, it will be removed only on the user's blog.
	 * If the post is owned by the user, it will be removed from the database (any rewined link will be removed).
	 *
	 * @param username The username that wants to remove the post.
	 * @param post_id  The id of the post to remove.
	 * @throws Winsome_DB_Exception.UsernameNotFound       if the username is not found in the database.
	 * @throws Winsome_DB_Exception.PostNotFound           if the post is not found in the database.
	 * @throws Winsome_DB_Exception.PostNotInBlog          if the post is not int the user's blog.
	 * @throws Winsome_DB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	@Override
	public void remove_post(String username, String post_id) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.PostNotFound, Winsome_DB_Exception.PostNotInBlog, Winsome_DB_Exception.DatabaseNotInitialized {
		/*
		 * This method removes a post from the database.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. If the post is not found in the database, throw an exception.
		 * 4. If the post is not in the user's blog, throw an exception.
		 * 5. If the user is not the author of the post remove it from the user's blog and return.
		 * 6. Remove the post from the database.
		 * 7. Remove the post from the user's posts.
		 * 8. Dirty the posts backup.
		 * 9. Dirty the users backup.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		lock_both_R();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			users_R_lock.unlock();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. If the post is not found in the database, throw an exception.
		if (!posts.getPosts().containsKey(post_id)) {
			posts_R_lock.unlock();
			throw new Winsome_DB_Exception.PostNotFound(post_id);
		}

		unlock_both_R();

		lock_both_W();

		// 4. If the post is not in the user's blog, throw an exception.
		if (!users.get(username).getPosts().contains(post_id)) {
			unlock_both_W();
			throw new Winsome_DB_Exception.PostNotInBlog(username, post_id);
		}

		// 5. If the user is not the author of the post remove it from the user's blog and return.
		if (!posts.getPosts().get(post_id).getAuthor().equals(username)) {
			users.get(username).getPosts().remove(post_id);
			unlock_both_W();
			return;
		}

		// 6. Remove the post from the database.
		posts.getPosts().remove(post_id);

		// 7. Remove the post from the user's posts.
		users.get(username).getPosts().remove(post_id);

		// 8. Dirty the posts backup.
		posts_backup_valid = false;

		// 9. Dirty the users backup.
		users_backup_valid = false;

		unlock_both_W();
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
		/*
		 * This method gets a post's rates.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the post is not found in the database, throw an exception.
		 * 3. Get the post's rates.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		posts_R_lock.lock();

		// 2. If the post is not found in the database, throw an exception.
		if (!posts.getPosts().containsKey(post_id)) {
			posts_R_lock.unlock();
			throw new Winsome_DB_Exception.PostNotFound(post_id);
		}

		// 3. Get the post's rates.
		ArrayList<RateDB> rates = posts.getPosts().get(post_id).getRates();

		posts_R_lock.unlock();

		boolean[] res = new boolean[rates.size()];
		for (int i = 0; i < rates.size(); i++)
			res[i] = rates.get(i).getRate();

		return res;
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
		/*
		 * This method gets a post's comments.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the post is not found in the database, throw an exception.
		 * 3. Get the post's comments.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		posts_R_lock.lock();

		// 2. If the post is not found in the database, throw an exception.
		if (!posts.getPosts().containsKey(post_id)) {
			posts_R_lock.unlock();
			throw new Winsome_DB_Exception.PostNotFound(post_id);
		}

		// 3. Get the post's comments.
		ArrayList<Comment_DB> comments = posts.getPosts().get(post_id).getComments();

		posts_R_lock.unlock();

		Comment_representation[] res = new Comment_representation[comments.size()];
		for (int i = 0; i < comments.size(); i++)
			res[i] = comments.get(i).representation();

		return res;
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
		/*
		 * This method is used to like/dislike a post.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. If the post is not found in the database, throw an exception.
		 * 4. If the user has already rated the post, throw an exception.
		 * 5. Add the rate to the post.
		 * 6. Dirty the posts backup.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		lock_both_R();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			unlock_both_R();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. If the post is not found in the database, throw an exception.
		if (!posts.getPosts().containsKey(post_id)) {
			unlock_both_R();
			throw new Winsome_DB_Exception.PostNotFound(post_id);
		}

		// 4. If the user has already rated the post, throw an exception.
		ArrayList<RateDB> rates = posts.getPosts().get(post_id).getRates();
		for (RateDB r : rates) {
			if (r.getAuthor().equals(username)) {
				unlock_both_R();
				throw new Winsome_DB_Exception.PostAlreadyRated(username, post_id);
			}
		}

		unlock_both_R();

		lock_both_W();

		// 5. Add the rate to the post.
		posts.getPosts().get(post_id).addVote(new RateDB(username, rate));

		// 6. Dirty the posts backup.
		posts_backup_valid = false;

		unlock_both_W();
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
		/*
		 * This method is used to comment a post.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. If the post is not found in the database, throw an exception.
		 * 4. If the user is the author of the post, throw an exception.
		 * 5. Add the comment to the post.
		 * 6. Dirty the posts backup.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		lock_both_R();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			unlock_both_R();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. If the post is not found in the database, throw an exception.
		if (!posts.getPosts().containsKey(post_id)) {
			unlock_both_R();
			throw new Winsome_DB_Exception.PostNotFound(post_id);
		}

		// 4. If the user is the author of the post, throw an exception.
		if (posts.getPosts().get(post_id).getAuthor().equals(username)) {
			unlock_both_R();
			throw new Winsome_DB_Exception.PostCommentedByAuthor(username, post_id);
		}

		unlock_both_R();

		posts_W_lock.lock();

		// 5. Add the comment to the post.
		posts.getPosts().get(post_id).addComment(new Comment_DB(username, comment));

		// 6. Dirty the posts backup.
		posts_backup_valid = false;

		posts_W_lock.unlock();
	}

	/**
	 * This method is used to rewin a post (post an already existing post in the user's blog).
	 * The post remain the same, but the post is added in the user's blog.
	 *
	 * @param username The username of the user who wants to rewin the post.
	 * @param postId   The id of the post to rewin.
	 */
	@Override
	public void rewin_post(String username, String postId) throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.PostNotFound, Winsome_DB_Exception.PostAlreadyRewined, Winsome_DB_Exception.DatabaseNotInitialized {
		/*
		 * This method is used to rewin a post (post an already existing post in the user's blog).
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. If the post is not found in the database, throw an exception.
		 * 4. If the post is already posted by the user, throw an exception.
		 * 5. Add the post to the user's blog.
		 * 6. Dirty the posts backup.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		lock_both_R();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			unlock_both_R();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. If the post is not found in the database, throw an exception.
		if (!posts.getPosts().containsKey(postId)) {
			unlock_both_R();
			throw new Winsome_DB_Exception.PostNotFound(postId);
		}

		// 4. If the post is already posted by the user, throw an exception.
		if (users.get(username).getPosts().contains(postId)) {
			unlock_both_R();
			throw new Winsome_DB_Exception.PostAlreadyRewined(username, postId);
		}

		unlock_both_R();

		lock_both_W();

		// 5. Add the post to the user's blog.
		users.get(username).getPosts().add(postId);

		// 6. Dirty the users backup.
		users_backup_valid = false;

		unlock_both_W();
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
		/*
		 * This method is used to get a post's simple representation.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the post is not found in the database, throw an exception.
		 * 3. Return the post's simple representation.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		posts_R_lock.lock();

		// 2. If the post is not found in the database, throw an exception.
		if (!posts.getPosts().containsKey(post_id)) {
			posts_R_lock.unlock();
			throw new Winsome_DB_Exception.PostNotFound(post_id);
		}

		// 3. Return the post's simple representation.
		Post_representation_simple ret = posts.getPosts().get(post_id).representation_simple();

		posts_R_lock.unlock();

		return ret;
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
		/*
		 * This method is used to get a post's full representation.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the post is not found in the database, throw an exception.
		 * 3. Return the post's full representation.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		posts_R_lock.lock();

		// 2. If the post is not found in the database, throw an exception.
		if (!posts.getPosts().containsKey(post_id)) {
			posts_R_lock.unlock();
			throw new Winsome_DB_Exception.PostNotFound(post_id);
		}

		// 3. Return the post's full representation.
		Post_representation_detailed ret = posts.getPosts().get(post_id).representation_detailed();

		posts_R_lock.unlock();

		return ret;
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
		/*
		 * This method is used to get the posts of a user.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. Get the posts of the user.
		 * 4. return a representation of the posts of the user.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		lock_both_R();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			unlock_both_R();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. Get the posts of the user.
		String[] posts_ids = users.get(username).getPosts().toArray(new String[0]);

		// 4. return a representation of the posts of the user.
		Post_representation_simple[] ret = new Post_representation_simple[posts_ids.length];
		for (int i = 0; i < posts_ids.length; i++)
			ret[i] = posts.getPosts().get(posts_ids[i]).representation_simple();

		unlock_both_R();

		return ret;
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
		/*
		 * This method is used to get the posts of a user's feed.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. Get the users' following list.
		 * 4. Get the posts of the users' following list.
		 * 5. Return a representation of the posts of the users' following list.
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		lock_both_R();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			unlock_both_R();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. Get the users' following list.
		String[] following = users.get(username).getFollowing().toArray(new String[0]);

		TreeSet<Post_DB> posts_list = new TreeSet<>();

		// 4. Get the posts of the users' following list.
		for (String user : following) {
			String[] posts_ids = users.get(user).getPosts().toArray(new String[0]);
			for (String post_id : posts_ids)
				posts_list.add(posts.getPosts().get(post_id));
		}

		// 5. Return a representation of the posts of the users' following list.
		Post_representation_simple[] ret = new Post_representation_simple[posts_list.size()];

		int i = 0;
		for (Post_DB post : posts_list)
			ret[i++] = post.representation_simple();

		unlock_both_R();

		return ret;
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
		/*
		 * This method is used to get the usernames of users with similar interests with the given user.
		 *
		 * 1. If the database is not initialized, throw an exception.
		 * 2. If the username is not found in the database, throw an exception.
		 * 3. Get the users' interests.
		 * 4. Get every username that has at least one interest in common with the user. (The user is not included in the list.)
		 */

		// 1. If the database is not initialized, throw an exception.
		if (!initialized)
			throw new Winsome_DB_Exception.DatabaseNotInitialized();

		users_R_lock.lock();

		// 2. If the username is not found in the database, throw an exception.
		if (!users.containsKey(username)) {
			users_R_lock.unlock();
			throw new Winsome_DB_Exception.UsernameNotFound(username);
		}

		// 3. Get the users' interests.
		String[] interests = users.get(username).getTags();

		// 4. Get every username that has at least one interest in common with the user. (The user is not included in the list.)
		TreeSet<String> similar_users = new TreeSet<>();

		for (String user : users.keySet()) {
			if (user.equals(username))
				continue;

			String[] user_interests = users.get(user).getTags();

			if (Arrays.stream(user_interests).anyMatch(t -> Arrays.asList(interests).contains(t)))
				similar_users.add(user);
		}

		users_R_lock.unlock();

		return similar_users.toArray(new String[0]);
	}

	/**
	 * This method is used to reward every user with a certain amount of coins.
	 */
	@Override
	public void reward_everyone() {
		/*
		 * This method is used to reward every user with a certain amount of coins.
		 *
		 * 1. If the database is not initialized, return.
		 * 2. Go through every post and calculate every reward.
		 * 2.1. The reward returned by the post is a list of usernames and the amount of coins to reward them.
		 * 3. Divide the author's between the author and the users that rewined the post (those
		 *   that rewined the post are rewarded with a percentage of the author's reward).
		 * 4. Reward the users.
		 */

		// 1. If the database is not initialized, return.
		if (!initialized)
			return;

		boolean dirty = false;

		users_W_lock.lock();
		posts_R_lock.lock();

		// 2. Go through every post and calculate every reward.
		for (Post_DB post : posts.getPosts().values()) {
			// 2.1. The reward returned by the post is a list of usernames and the amount of coins to reward them.
			List<Winsome_Reward> rewards = post.calculate_rewards();
			if (rewards == null)
				continue;

			dirty = true;

			// 3. Divide the author's between the author and the users that rewined the post (those
			//    that rewined the post are rewarded with a percentage of the author's reward).
			List<String> users_rewined = new ArrayList<>();
			for (User_DB u : users.values())
				if (!u.getUsername().equals(post.getAuthor()) && u.getPosts().contains(post.getId()))
					users_rewined.add(u.getUsername());

			double reward_author = rewards.get(0).value;
			double reward_rewined = users_rewined.size() > 0 ? reward_author * 0.1 : 0; // 10% of the author's reward then goes to the rewined users.
			reward_author -= reward_rewined;

			// 4. Reward the users.

			// Reward the author.
			users.get(rewards.get(0).username).getWallet().add_transaction(new Transaction_DB(reward_author));
			// DEBUG
			System.out.println("Rewarding " + rewards.get(0).username + " with " + reward_author + " coins.");

			// Reward the rewined users.
			for (String user : users_rewined) {
				users.get(user).getWallet().add_transaction(new Transaction_DB(reward_rewined / users_rewined.size()));
				// DEBUG
				System.out.println("Rewarding " + user + " with " + reward_rewined / users_rewined.size() + " coins.");
			}

			// Reward the other users (curators).
			for (Winsome_Reward reward : rewards.subList(1, rewards.size())) {
				users.get(reward.username).getWallet().add_transaction(new Transaction_DB(reward.value));
				// DEBUG
				System.out.println("Rewarding " + reward.username + " with " + reward.value + " coins.");
			}

		}

		if (dirty) {
			posts_backup_valid = false;
			users_backup_valid = false;
		}

		posts_R_lock.unlock();
		users_W_lock.unlock();
	}

	/**
	 * This method is used to close the database.
	 */
	@Override
	public void close() {
		/*
		 * This method is used to close the database.
		 *
		 * 1. If the database is not initialized, return.
		 * 2. Interrupt the thread that saves the database.
		 * 3. Save the database.
		 * 4. Set the database as not initialized.
		 */


		// 1. If the database is not initialized, return.
		if (!initialized) {
			return;
		}

		// 2. Interrupt the thread that saves the database.
		thread.mustStop();

		// 3. Save the database.
		try {
			save_DB();
		} catch (Winsome_DB_Exception.DatabaseNotInitialized | Winsome_DB_Exception.DatabaseNotSaved e) {
			System.err.println(e.getMessage());
		}

		// 4. Set the database as not initialized.
		lock_both_W();
		initialized = false;
		unlock_both_W();
	}
}
