package winsome_client;

import java.util.ArrayList;

/**
 * This class is used to store the local information of a user.
 * The user is the user logged in on the client.
 *
 * @public
 * @get_username() - Get the username of the user.
 * @get_following() - Get the list of users that the user is following.
 * @get_followers() - Get the list of users that are following the user.
 * @get_balance() - Get the balance of the user.
 * @add_following(String username) - Add a user to the list of users that the user is following.
 * @add_followings(List<String> usernames) - Add a list of users to the list of users that the user is following.
 * @add_follower(String username) - Add a user to the list of users that are following the user.
 * @add_followers(List<String> usernames) - Add a list of users to the list of users that are following the user.
 * @remove_following(String username) - Remove a user from the list of users that the user is following.
 * @remove_follower(String username) - Remove a user from the list of users that are following the user.
 * @update_balance(double amount) - Update the balance of the user.
 */
public class LocalUser {
	// Member variables
	private String username;
	private final ArrayList<String> following;
	private final ArrayList<String> followers;
	private float balance;

	// Constructors
	// Default constructor
	public LocalUser(String username) {
		/*
		 * This constructor is used when we want to create a new user.
		 *
		 * 1. Set the username of this user.
		 * 2. Create a new list of users that the user is following.
		 * 3. Create a new list of users that are following the user.
		 */

		// 1. Set the username of this user.
		this.username = username;

		// 2. Create a new list of users that the user is following.
		this.following = new ArrayList<>();

		// 3. Create a new list of users that are following the user.
		this.followers = new ArrayList<>();
	}

	// Methods
	// Getters
	public String get_username() {
		return this.username;
	}

	public ArrayList<String> get_following() {
		return this.following;
	}

	public ArrayList<String> get_followers() {
		return this.followers;
	}

	public float get_balance() {
		return this.balance;
	}

	// Setters
	// None

	// Other methods
	public void add_following(String username) {
		this.following.add(username);
	}

	public void add_followings(ArrayList<String> usernames) {
		this.following.addAll(usernames);
	}

	public void add_follower(String username) {
		this.followers.add(username);
	}

	public void add_followers(ArrayList<String> usernames) {
		this.followers.addAll(usernames);
	}

	public void remove_following(String username) {
		this.following.remove(username);
	}

	public void remove_follower(String username) {
		this.followers.remove(username);
	}

	public void update_balance(float amount) {
		this.balance += amount;
	}

	// toString
	@Override
	public String toString() {
		return "LocalUser [username=" + username + ", following=" + following + ", followers=" + followers + ", balance="
				+ balance + "]";
	}

}
