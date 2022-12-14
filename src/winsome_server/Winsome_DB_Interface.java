package winsome_server;

import winsome_DB.WinsomeDB_Exception;
import winsome_communication.CommentRepr;
import winsome_communication.PostReprDetailed;
import winsome_communication.PostReprSimple;
import winsome_communication.WalletRepr;

/**
 * This interface is used by the Winsome Server to communicate with the Database.
 * <p></p>
 * Available methods:
 * // USERS //
 * 1.  Create a new user.
 * 2. Check a user's credentials.
 * 3. User follows another user.
 * 4. User unfollows another user.
 * 5. Get a user's followers.
 * 6. Get a user's following.
 * 7. Get a user's wallet.
 * 8. Get a user's posts.
 * 9. ...
 * <p></p>
 * // POSTS //
 * 1. Create a new post.
 * 2. Remove a post.
 * 3. Get a post's rates.
 * 4. Get a post's comments.
 * 5. Vote on a post.
 * 6. Comment on a post.
 * 7. Get a simple representation of a post.
 * 8. Get a detailed representation of a post.
 * 9. ...
 * <p></p>
 * // GENERAL //
 * 1. Get a user's blog
 * 2. Get a user's feed
 * 3. Get a list of usernames with similar interests with a given user.
 * 4. ...
 * <p></p>
 * // GLOBAL //
 * 1. Reward every user with a given amount of winsome.
 * 2. ...
 * <p></p>
 * Each method could throw a Winsome_DB_Exception of some kind, check the documentation for each method.
 * <p></p>
 * @author Winsome
 * @version 1.0
 * @since 1.0
 */
public interface Winsome_DB_Interface {
	// USERS //

	/**
	 * This method creates a new user in the database.
	 * @param username The username of the new user.
	 * @param password The password of the new user.
	 * @param tags The tags (interests) of the new user.
	 * @throws WinsomeDB_Exception.UsernameAlreadyExists if the username already exists in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	void create_user(String username, String password, String[] tags)
			throws WinsomeDB_Exception.UsernameAlreadyExists, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method checks if a user's credentials are correct.
	 * @param username The username of the user to check.
	 * @param password The password of the user to check.
	 * @return True if the credentials are correct, false otherwise.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	boolean check_credentials(String username, String password)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method makes a user follow another user.
	 * @param username The username of the user that wants to follow.
	 * @param username_to_follow The username of the user to follow.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.UsernameAlreadyFollows if the user already follows the user to follow.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	void user_follows(String username, String username_to_follow)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.UsernameAlreadyFollows,
			WinsomeDB_Exception.DatabaseNotInitialized, WinsomeDB_Exception.UsernameFollowItself;

	/**
	 * This method makes a user unfollow another user.
	 * @param username The username of the user that wants to unfollow.
	 * @param username_to_unfollow The username of the user to unfollow.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.UsernameNotFollowing if the user is not following the user to unfollow.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	void user_unfollows(String username, String username_to_unfollow)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.UsernameNotFollowing,
			WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method gets a user's followers list.
	 * @param username The username of the user to get the followers.
	 * @return An array of usernames of the followers.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	String[] get_user_followers(String username)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method gets a user's following list.
	 * @param username The username of the user to get the following.
	 * @return An array of usernames of the following.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	String[] get_user_following(String username)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method gets a user's wallet.
	 * @param username The username of the user to get the wallet.
	 * @return A Wallet_representation object with the wallet information.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	WalletRepr get_user_wallet(String username)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method gets a user's posts.
	 * @param username The username of the user to get the posts.
	 * @return An array of Post_representation objects with the posts' information.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	PostReprSimple[] get_user_posts(String username)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	// POSTS //

	/**
	 * This method creates a new post in the database.
	 * @param author The username of the author of the post.
	 * @param title The title of the post.
	 * @param content The content of the post.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 * @throws WinsomeDB_Exception.PostInvalidTitle if the title is invalid.
	 * @throws WinsomeDB_Exception.PostInvalidContent if the content is invalid.
	 */
	void create_post(String author, String title, String content)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.DatabaseNotInitialized,
			WinsomeDB_Exception.PostInvalidTitle, WinsomeDB_Exception.PostInvalidContent;

	/**
	 * This method removes a post from the database.
	 * @param username The username that wants to remove the post.
	 * @param post_id The id of the post to remove.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.PostNotFound if the post is not found in the database.
	 * @throws WinsomeDB_Exception.PostNotInBlog if the post is not owned by the user.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	void remove_post(String username, String post_id)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.PostNotFound,
			WinsomeDB_Exception.PostNotInBlog, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method gets a post's rates.
	 * @param post_id The id of the post to get the rates.
	 * @return An array of booleans (true if the rate is like, false if it is dislike).
	 * @throws WinsomeDB_Exception.PostNotFound if the post is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	boolean[] get_post_rates(String post_id)
			throws WinsomeDB_Exception.PostNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method gets a post's comments.
	 * @param post_id The id of the post to get the comments.
	 * @return An array of Comment_representation objects with the comments' information.
	 * @throws WinsomeDB_Exception.PostNotFound if the post is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	CommentRepr[] get_post_comments(String post_id)
			throws WinsomeDB_Exception.PostNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method is used to like/dislike a post.
	 * @param username The username of the user who wants to rate the post.
	 * @param post_id The id of the post to rate.
	 * @param rate The rate of the user to the post.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.PostNotFound if the post is not found in the database.
	 * @throws WinsomeDB_Exception.PostAlreadyRated if the user has already rated the post.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	void rate_post(String username, String post_id, boolean rate)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.PostNotFound,
			WinsomeDB_Exception.PostAlreadyRated, WinsomeDB_Exception.DatabaseNotInitialized, WinsomeDB_Exception.PostRatedByAuthor;

	/**
	 * This method is used to comment a post.
	 * @param username The username of the user who wants to comment the post.
	 * @param post_id The id of the post to comment.
	 * @param comment The comment of the user to the post.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.PostNotFound if the post is not found in the database.
	 * @throws WinsomeDB_Exception.PostCommentedByAuthor if the user is the author of the post.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	void comment_on_post(String username, String post_id, String comment)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.PostNotFound,
			WinsomeDB_Exception.PostCommentedByAuthor, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method is used to rewin a post (post an already existing post in the user's blog).
	 * The post remain the same, but the post is added in the user's blog.
	 * @param username The username of the user who wants to rewin the post.
	 * @param postId The id of the post to rewin.
	 */
	void rewin_post(String username, String postId)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.PostNotFound,
			WinsomeDB_Exception.PostAlreadyRewined, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method is used to get a post's simple representation.
	 * @param post_id The id of the post to get the representation.
	 * @return A PostReprSimple object with the post's information.
	 * @throws WinsomeDB_Exception.PostNotFound if the post is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	PostReprSimple get_post_simple(String post_id)
			throws WinsomeDB_Exception.PostNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method is used to get a post's full representation.
	 * @param post_id the id of the post to get the representation.
	 * @return A Post_representation_detailed object with the post's information.
	 * @throws WinsomeDB_Exception.PostNotFound if the post is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	PostReprDetailed get_post(String post_id)
			throws WinsomeDB_Exception.PostNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	// GENERAL //

	/**
	 * This method is used to get the posts of a user.
	 * @param username The username of the user to get the posts.
	 * @return An array of PostReprSimple objects with the posts' information.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	PostReprSimple[] get_user_blog(String username)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method is used to get the posts of a user's feed.
	 * @param username The username of the user to get the feed.
	 * @return An array of PostReprSimple objects with the posts' information.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	PostReprSimple[] get_user_feed(String username)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	/**
	 * This method is used to get the usernames of users with similar interests with the given user.
	 * @param username The username of the user to get the similar users.
	 * @return An array of strings with the usernames of the similar users.
	 * @throws WinsomeDB_Exception.UsernameNotFound if the username is not found in the database.
	 * @throws WinsomeDB_Exception.DatabaseNotInitialized if the database is not initialized.
	 */
	String[] get_similar_users(String username)
			throws WinsomeDB_Exception.UsernameNotFound, WinsomeDB_Exception.DatabaseNotInitialized;

	// GLOBAL //

	/**
	 * This method is used to reward every user with a certain amount of coins.
	 * @param author_reward the percentage of coins to reward the author of the post.
	 */
	void reward_everyone(double author_reward);

	/**
	 * This method is used to close the database.
	 */
	void close();


}
