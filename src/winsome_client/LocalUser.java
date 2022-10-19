package winsome_client;

import java.util.ArrayList;

/**
 * This class is used to store the local information of a user.
 * The user is the user logged in on the client.
 *
 */
public class LocalUser {
	// Member variables
	private final String username;
	private final ArrayList<String> followers;

	// Constructors
	// Default constructor
	public LocalUser(String username) {
		/*
		 * This constructor is used when we want to create a new user.
		 *
		 * 1. Set the username of this user.
		 * 2. Create a new list of users that are following the user.
		 */

		// 1. Set the username of this user.
		this.username = username;

		// 2. Create a new list of users that are following the user.
		this.followers = new ArrayList<>();
	}

	// Methods
	// Getters
	public String get_username() {
		return this.username;
	}

	public ArrayList<String> get_followers() {
		return this.followers;
	}

	// Setters
	// None

	// Other methods
	public void add_follower(String username) {
		this.followers.add(username);
	}

	public void add_followers(ArrayList<String> usernames) {
		this.followers.addAll(usernames);
	}

	public void remove_follower(String username) {
		this.followers.remove(username);
	}

	// toString
	@Override
	public String toString() {
		return "LocalUser [username=" + username + ", followers=" + followers + "]";
	}

}
