package winsome_server;

import winsome_DB.Winsome_DB_Exception;
import winsome_comunication.Comment_representation;
import winsome_comunication.Post_representation_detailed;
import winsome_comunication.Post_representation_simple;
import winsome_comunication.Wallet_representation;

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
 * 3. Get a post's votes.
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
	void create_user(String username, String password, String[] tags)
			throws Winsome_DB_Exception.UsernameAlreadyExists;
	boolean check_credentials(String username, String password)
			throws Winsome_DB_Exception.UsernameNotFound;
	void user_follows(String username, String username_to_follow)
			throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.UsernameAlreadyFollows;
	void user_unfollows(String username, String username_to_unfollow)
			throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.UsernameNotFollowing;
	String[] get_user_followers(String username)
			throws Winsome_DB_Exception.UsernameNotFound;
	String[] get_user_following(String username)
			throws Winsome_DB_Exception.UsernameNotFound;
	Wallet_representation get_user_wallet(String username)
			throws Winsome_DB_Exception.UsernameNotFound;
	Post_representation_simple[] get_user_posts(String username)
			throws Winsome_DB_Exception.UsernameNotFound;

	// POSTS //
	void create_post(String author, String title, String content)
			throws Winsome_DB_Exception.UsernameNotFound,
			Winsome_DB_Exception.PostInvalidTitle, Winsome_DB_Exception.PostInvalidContent;
	void remove_post(String username, String post_id)
			throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.PostNotFound,
			Winsome_DB_Exception.PostNotOwned;
	boolean[] get_post_votes(String post_id)
			throws Winsome_DB_Exception.PostNotFound;
	Comment_representation[] get_post_comments(String post_id)
			throws Winsome_DB_Exception.PostNotFound;
	void vote_on_post(String username, String post_id, boolean vote)
			throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.PostNotFound,
			Winsome_DB_Exception.PostAlreadyVoted;
	void comment_on_post(String username, String post_id, String comment)
			throws Winsome_DB_Exception.UsernameNotFound, Winsome_DB_Exception.PostNotFound,
			Winsome_DB_Exception.PostCommentedByAuthor;
	Post_representation_simple get_post_simple(String post_id)
			throws Winsome_DB_Exception.PostNotFound;
	Post_representation_detailed get_post(String post_id)
			throws Winsome_DB_Exception.PostNotFound;

	// GENERAL //
	Post_representation_simple[] get_user_blog(String username)
			throws Winsome_DB_Exception.UsernameNotFound;
	Post_representation_simple[] get_user_feed(String username)
			throws Winsome_DB_Exception.UsernameNotFound;
	String[] get_similar_users(String username)
			throws Winsome_DB_Exception.UsernameNotFound;

	// GLOBAL //
	void reward_everyone();
}
