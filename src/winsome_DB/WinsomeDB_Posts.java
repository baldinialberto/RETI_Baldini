package winsome_DB;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents all the posts in the database.
 * It contains:
 * 1. A map of all the posts in the database.
 * 2. The id of the last post created.
 * <p></p>
 * This class is available only to the Winsome_Database
 */
public class WinsomeDB_Posts implements JSON_Serializable {
	// Member variables
	private HashMap<String, PostDB> posts = new HashMap<>();
	private String last_post_id;

	// Constructors

	// Default constructor // Jackson constructor
	public WinsomeDB_Posts() {
	}

	// JSON Methods
	public static WinsomeDB_Posts JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), WinsomeDB_Posts.class);
	}
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	// Getters
	public String getLast_post_id() {
		return last_post_id;
	}
	public Map<String, PostDB> getPosts() {
		return posts;
	}

	// Setters
	public void setLast_post_id(String last_post_id) {
		this.last_post_id = last_post_id;
	}
	public void setPosts(HashMap<String, PostDB> posts) {
		this.posts = posts;
	}

    // Adders
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
		this.posts.put(post_id, new PostDB(post_id, author, title, text));

		// 3. Set the last post_id to the new post_id.
		last_post_id = post_id;
	}

	// Get post
	public PostDB get_post(String post_id) { return this.posts.get(post_id); }

	// Removers
	public void remove_post(String postId, String user_requested_removal) {
		/*
		 * This method is used to delete a post from the post collection.
		 *
		 * 1. Check if the post exists.
		 * 2. Check if the user is the author of the post.
		 * 3. Delete the post.
		 * 4. Return the status code.
		 */

		// 1. Check if the post exists.
		if (!this.posts.containsKey(postId)) {
			return;
		}

		// 2. Check if the user is the author of the post.
		if (!this.posts.get(postId).getAuthor().equals(user_requested_removal)) {
			return;
		}

		// 3. Delete the post.
		this.posts.remove(postId);

		// 4. Return the status code.
	}
}
