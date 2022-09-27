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
		 * 2. Return 0.
		 * 3. If the user collection does not contain the user, return -1.
		 */

		// 1. If the user collection contains the user, add the post to the user.
		if (this.containsKey(username)) {
			this.get(username).add_post(post_id);
			// 2. Return 0.
			return 0;
		}

		// 3. If the user collection does not contain the user, return -1.
		return -1;
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

