package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Post_collection extends ConcurrentHashMap<Post_ID, Post> implements JSON_Serializable {
	// Member variables
	private static Post_collection instance;

	// Constructor
	private Post_collection() {
		/*
		 * This constructor is used when we want to create a new post collection.
		 *
		 * 1. Create a new post collection.
		 */

		// 1. Create a new post collection.
		super();
	}

	// Methods
	public static Post_collection getInstance() {
		/*
		 * This method is used to get the instance of the post collection.
		 *
		 * 1. If the instance of the post collection is null, create a new instance of the post collection.
		 * 2. Return the instance of the post collection.
		 */

		// 1. If the instance of the post collection is null, create a new instance of the post collection.
		if (instance == null) {
			instance = new Post_collection();
		}

		// 2. Return the instance of the post collection.
		return instance;
	}


	// Other methods
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	public static Post_collection JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), Post_collection.class);
	}
}
