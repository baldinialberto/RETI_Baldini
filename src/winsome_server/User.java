package winsome_server;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User implements JSON_Serializable {
	// Member variables
	private String username;
	private String password;
	private String[] tags;
	private Wallet wallet;
	private ArrayList<String> posts;

	private ArrayList<String> following;

	private ArrayList<String> followers;

	// Constructors

	// Default constructor
	public User(String username, String password, String[] tags) {
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
		this.wallet = new Wallet(username);

		// 5. Create a new list of posts for this user.
		this.posts = new ArrayList<>();

		// 6. Create a new list of following for this user.
		this.following = new ArrayList<>();

		// 7. Create a new list of followers for this user.
		this.followers = new ArrayList<>();
	}

	// Jackson constructor
	public User() {
		/*
		 * This constructor is used by Jackson when it reads a JSON file.
		 */
	}

	// Methods

	void add_post(String postID) {
		this.posts.add(postID);
	}

	// Getters

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String[] getTags() {
		return this.tags;
	}

	public Wallet getWallet() {
		return this.wallet;
	}

	public ArrayList<String> getPosts() {
		return this.posts;
	}

	public ArrayList<String> getFollowing() {
		return this.following;
	}

	public ArrayList<String> getFollowers() {
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

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	public void setPosts(ArrayList<String> posts) {
		this.posts = posts;
	}

	public void setFollowing(ArrayList<String> following) {
		this.following = following;
	}

	public void setFollowers(ArrayList<String> followers) {
		this.followers = followers;
	}

	// Other methods
	public void add_follower(String follower)
	{
		this.followers.add(follower);
	}

	public void add_following(String following)
	{
		this.following.add(following);
	}

	public void remove_follower(String follower)
	{
		this.followers.remove(follower);
	}

	public void remove_following(String following)
	{
		this.following.remove(following);
	}

	@Override
	public String toString()
	{
		return "User{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				", tags=" + Arrays.toString(tags) +
				", wallet=" + wallet +
				", posts=" + posts +
				'}';
	}

	// Other methods
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	public static User JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), User.class);
	}
}
