package winsome_DB;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_comunication.UserRepr;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This class represents a user in the database.
 * It contains:
 * 1. The username of the user.
 * 2. The password of the user.
 * 3. The Interests of the user.
 * 4. The Posts of the user.
 * 5. The Followers of the user.
 * 6. The usernames that the user is Following.
 * 6. The Wallet of the user.
 * <p></p>
 * This class is available only to the Winsome_Database
 */
public class UserDB implements JSON_Serializable {
	// Member variables
	private String username;
	private String password;
	private String[] tags;
	private WalletDB wallet;
	private HashSet<String> posts;
	private HashSet<String> following;
	private HashSet<String> followers;

	// Constructors

	// Default constructor
	public UserDB(String username, String password, String[] tags) {
		/*
		 * This constructor is used when we want to create a new user.
		 *
		 * 1. Set the username of this user.
		 * 2. Set the password of this user.
		 * 3. Set the tags of this user.
		 * 4. Create a new wallet for this user.
		 * 5. Create a new list of posts for this user.
		 * 6. Create a new list of following for this user.
		 * 7. Create a new list of followers for this user.
		 */

		// 1. Set the username of this user.
		this.username = username;

		// 2. Set the password of this user.
		this.password = password;

		// 3. Set the tags of this user.
		this.tags = tags;

		// 4. Create a new wallet for this user.
		this.wallet = new WalletDB(username);

		// 5. Create a new list of posts for this user.
		this.posts = new HashSet<>();

		// 6. Create a new list of following for this user.
		this.following = new HashSet<>();

		// 7. Create a new list of followers for this user.
		this.followers = new HashSet<>();
	}

	// Jackson constructor
	public UserDB() {
		/*
		 * This constructor is used by Jackson when it reads a JSON file.
		 */
	}

	// JSON Methods
	public static UserDB JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), UserDB.class);
	}
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	// Getters
	public String getUsername() {
		return this.username;
	}
	public String getPassword() { return this.password; }
	public String[] getTags() { return this.tags; }
	public WalletDB getWallet() {
		return this.wallet;
	}
	public HashSet<String> getPosts() {
		return this.posts;
	}
	public HashSet<String> getFollowing() {
		return this.following;
	}
	public HashSet<String> getFollowers() {
		return this.followers;
	}

	// Setters
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public void setWallet(WalletDB wallet) {
		this.wallet = wallet;
	}
	public void setPosts(HashSet<String> posts) {
		this.posts = posts;
	}
	public void setFollowing(HashSet<String> following) {
		this.following = following;
	}
	public void setFollowers(HashSet<String> followers) {
		this.followers = followers;
	}

	// Adders
	public void add_follower(String follower) {
		this.followers.add(follower);
	}
	public void add_following(String following) {
		this.following.add(following);
	}
	void add_post(String postID) {
		this.posts.add(postID);
	}

	// Removers
	public void remove_follower(String follower) {
		this.followers.remove(follower);
	}
	public void remove_following(String following) {
		this.following.remove(following);
	}
	public void remove_post(String postID) {
		this.posts.remove(postID);
	}

	// Representation
	public UserRepr representation() {
		return new UserRepr(this.username, this.tags);
	}

	@Override
	public String toString() {
		return "User{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				", tags=" + Arrays.toString(tags) +
				", wallet=" + wallet +
				", posts=" + posts +
				'}';
	}
}
