package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
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
		 * 3. If the user collection contains the user, return -1.
		 */

		// 1. If the user collection does not contain the user, add the user to the user collection.
		if (!this.containsKey(username)) {
			this.put(username, new User(username, password, tags));
			// 2. Return 0.
			return 0;
		}

		// 3. If the user collection contains the user, return -1.
		return -1;
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

