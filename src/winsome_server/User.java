package winsome_server;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class User implements JSON_Serializable {
	// Member variables
	private String username;
	private String password;
	private String[] tags;
	private Wallet wallet;
	private List<Post_ID> posts;

	// Constructor
	public User(String username, String password, String[] tags) {
		/*
		 * This constructor is used when we want to create a new user.
		 *
		 * 1. Set the username of this user.
		 * 2. Set the password of this user.
		 * 3. Set the tags of this user.
		 * 4. Create a new wallet for this user.
		 * 5. Create a new list of posts for this user.
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
	}

	// Methods

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

	public List<Post_ID> getPosts() {
		return this.posts;
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

	public void setPosts(List<Post_ID> posts) {
		this.posts = posts;
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
