package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Post_collection extends ConcurrentHashMap<String, Post> implements JSON_Serializable {
	// Member variables
	private static Post_collection instance;


	private String last_post_id;

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

	public void add_post(String author, String title, String text) {
		/*
		 * This method is used to add a post to the post collection.
		 *
		 * 1. Create a new post_id that is the last post_id plus one.
		 * 2. Add the post to the post collection.
		 * 3. Set the last post_id to the new post_id.
		 */

		// 1. Create a new post_id that is the last post_id plus one.
		String post_id = last_post_id == null ? "0" : Integer.toString(Integer.parseInt(last_post_id) + 1);

		// 2. Add the post to the post collection.
		this.put(post_id, new Post(post_id, author, title, text));

		// 3. Set the last post_id to the new post_id.
		last_post_id = post_id;
	}

	// Getters
	public String getLast_post_id() {
		return last_post_id;
	}

	// Setters
	public void setLast_post_id(String last_post_id) {
		this.last_post_id = last_post_id;
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
