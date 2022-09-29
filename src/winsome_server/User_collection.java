package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class User_collection extends ConcurrentHashMap<String, User> implements JSON_Serializable {
	// Member variables
	private static User_collection instance;

	// Constructor
	private User_collection() {
		/*
		 * This constructor is used when we want to create a new user collection.
		 *
		 * 1. Create a new user collection.
		 */

		// 1. Create a new user collection.
		super();
	}

	// Methods
	public static User_collection getInstance() {
		/*
		 * This method is used to get the instance of the user collection.
		 *
		 * 1. If the instance of the user collection is null, create a new instance of the user collection.
		 * 2. Return the instance of the user collection.
		 */

		// 1. If the instance of the user collection is null, create a new instance of the user collection.
		if (instance == null) {
			instance = new User_collection();
		}

		// 2. Return the instance of the user collection.
		return instance;
	}


	public int add_user(String username, String password, String[] tags) {
		/*
		 * This method is used to add a user to the user collection.
		 *
		 * 1. If the user collection does not contain the user, add the user to the user collection.
		 * 2. Return 0.
		 * 3. If the user is not valid, return -1.
		 */

		// 1. If the user collection does not contain the user, add the user to the user collection.
		if (!this.containsKey(username) || tags.length != 0) {
			/* 1.1 Make sure that tags contains at least one tag and a maximum of 5 tags.
			 * if no tags are provided, return -1.
			 * if more than 5 tags are provided, add only the first 5 tags.
			 */
			if (tags.length > 5) {
				tags = Arrays.copyOfRange(tags, 0, 5);
			}

			this.put(username, new User(username, password, tags));
			// 2. Return 0.
			return 0;
		}

		// 3. If the user is not valid, return -1.
		return -1;
	}

	public int add_post(String username, String post_id)
	{
		/*
		 * This method is used to add a post to the user collection.
		 *
		 * 1. If the user collection contains the user, add the post to the user.
		 * 2. If the user already has the post int his blog, return POST_ALREADY_EXISTS.
		 * 3. If the user collection does not contain the user, return USR_NOT_FOUND.
		 */

		// 1. If the user collection contains the user, add the post to the user.
		if (this.containsKey(username)) {
			// 2. If the user already has the post int his blog, return POST_ALREADY_EXISTS.
			if (this.get(username).getPosts().contains(post_id)) {
				return Server_DB.DB_ERROR_CODE.POST_ALREADY_EXISTS.getValue();
			}
			this.get(username).add_post(post_id);
			return Server_DB.DB_ERROR_CODE.SUCCESS.getValue();
		}

		// 3. If the user collection does not contain the user, return -1.
		return Server_DB.DB_ERROR_CODE.USR_NOT_FOUND.getValue();
	}

	public List<String> users_with_common_tags(String username)
	{
		/*
		 * This method is used to get the usernames that have at least one tag in common with the user.
		 *
		 * 1. Get the user.
		 * 2. Get the tags of the user.
		 * 3. Create a list of usernames that have at least one tag in common with the user.
		 * 4. Return the list of usernames.
		 */

		// 1. Get the user.
		User user = this.get(username);
		if (user == null) {
			//DEBUG
			System.out.println("User not found");
			return new ArrayList<>();
		}

		// 2. Get the tags of the user.
		String[] tags = user.getTags();

		//DEBUG
		System.out.println("User tags: " + Arrays.toString(tags));

		// 3. Create a list of usernames that have at least one tag in common with the user.
		List<String> usernames = new ArrayList<>();
		for (User u : this.values()) {
			if (u.getUsername().equals(username)) {
				continue;
			}

			for (String tag : tags) {
				if (Arrays.asList(u.getTags()).contains(tag)) {
					usernames.add(u.getUsername());
					break;
				}
			}
		}

		// 4. Return the list of usernames.
		return usernames;
	}

	public int follow_username(String user, String username_to_follow)
	{
		/*
		 * This method is used to follow a user.
		 *
		 * 1. Check if the user is in the user collection.
		 * 2. Check if the user to follow is in the user collection.
		 * 3. Check if the user is already following the user to follow.
		 * 4. Add the user to follow to the user's following list.
		 * 5. Add the user to the user to follow's followers list.
		 * 6. Return 0.
		 */

		// DEBUG
		System.out.println("follow_username: " + user + " " + username_to_follow + " start");

		// 1. Check if the user is in the user collection.
		if (!this.containsKey(user)) {
			return Server_DB.DB_ERROR_CODE.USR_NOT_FOUND.getValue();
		}

		// 2. Check if the user to follow is in the user collection.
		if (!this.containsKey(username_to_follow)) {
			return Server_DB.DB_ERROR_CODE.USR_NOT_FOUND.getValue();
		}

		// 3. Check if the user is already following the user to follow.
		if (this.get(user).getFollowing().contains(username_to_follow)) {
			return Server_DB.DB_ERROR_CODE.USR_ALREADY_FOLLOWING.getValue();
		}

		// 4. Add the user to follow to the user's following list.
		this.get(user).add_following(username_to_follow);

		// 5. Add the user to the user to follow's followers list.
		this.get(username_to_follow).add_follower(user);

		// DEBUG
		System.out.println("follow_username: " + user + " " + username_to_follow + " end");

		// 6. Return 0.
		return Server_DB.DB_ERROR_CODE.SUCCESS.getValue();
	}

	public int unfollow_username(String user, String username_to_unfollow)
	{
		/*
		 * This method is used to unfollow a user.
		 *
		 * 1. Check if the user is in the user collection.
		 * 2. Check if the user to unfollow is in the user collection.
		 * 3. Check if the user is following the user to unfollow.
		 * 4. Remove the user to unfollow from the user's following list.
		 * 5. Remove the user from the user to unfollow's followers list.
		 * 6. Return 0.
		 */

		// 1. Check if the user is in the user collection.
		if (!this.containsKey(user)) {
			return -1;
		}

		// 2. Check if the user to unfollow is in the user collection.
		if (!this.containsKey(username_to_unfollow)) {
			return -1;
		}

		// 3. Check if the user is following the user to unfollow.
		if (!this.get(user).getFollowing().contains(username_to_unfollow)) {
			return -1;
		}

		// 4. Remove the user to unfollow from the user's following list.
		this.get(user).remove_following(username_to_unfollow);

		// 5. Remove the user from the user to unfollow's followers list.
		this.get(username_to_unfollow).remove_follower(user);

		// 6. Return 0.
		return 0;
	}
	public int delete_post(String user, String postId) {
		/*
		 * This method is used to remove a post from the user's blog.
		 *
		 * 1. Check if the user is in the user collection.
		 * 2. Check if the post is in the user's blog.
		 * 3. Remove the post from the user's blog.
		 */

		// 1. Check if the user is in the user collection.
		if (!this.containsKey(user)) {
			return Server_DB.DB_ERROR_CODE.USR_NOT_FOUND.getValue();
		}

		// 2. Check if the post is in the user's blog.
		if (!this.get(user).getPosts().contains(postId)) {
			return Server_DB.DB_ERROR_CODE.POST_NOT_FOUND.getValue();
		}

		// 3. Remove the post from the user's blog.
		this.get(user).getPosts().remove(postId);

		return Server_DB.DB_ERROR_CODE.SUCCESS.getValue();
	}

	public void remove_post_from_blogs(String postId) {
		/*
		 * This method is used to remove a post from all the user's blogs.
		 *
		 * 1. Iterate through all the users.
		 * 2. Remove the post from the user's blog.
		 */

		// 1. Iterate through all the users.
		for (User user : this.values()) {
			// 2. Remove the post from the user's blog.
			user.getPosts().remove(postId);
		}
	}
	public ArrayList<String> get_user_blog(String username)
	{
		/*
		 * This method is used to get the post ids of a user.
		 *
		 * 1. Get the user.
		 * 2. Get the post ids of the user.
		 * 3. Return the post ids.
		 */

		if (!this.containsKey(username)) {
			return null;
		}

		// 1. Get the user.
		User user = this.get(username);

		// 2. Get the post ids of the user.
		// 3. Return the post ids.
		return new ArrayList<>(user.getPosts());
	}

	public ArrayList<String> get_user_feed(String username)
	{
		/*
		 * This method is used to get the post ids of a user.
		 *
		 * 1. Get the user.
		 */

		if (!this.containsKey(username)) {
			return null;
		}

		// 1. Get the user.
		User user = this.get(username);

		// TODO continue here

		return null;
	}

	public boolean user_exists(String user) {
		return this.containsKey(user);
	}
	public boolean check_password(String username, String password) {
		if (this.containsKey(username)) {
			return this.get(username).getPassword().equals(password);
		}
		return false;
	}
	public ArrayList<String> get_users_following(String user) {
		if (this.containsKey(user)) {
			return new ArrayList<>(this.get(user).getFollowing());
		}
		return null;
	}
	public ArrayList<String> get_users_followers(String user) {
		if (this.containsKey(user)) {
			return new ArrayList<>(this.get(user).getFollowers());
		}
		return null;
	}
	public List<String> get_users_blogs(ArrayList<String> users) {
		List<String> blogs = new ArrayList<>();
		for (String user : users) {
			blogs.addAll(this.get(user).getPosts());
		}
		return blogs;
	}

	// Other methods
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	public static User_collection JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), User_collection.class);
	}
}

