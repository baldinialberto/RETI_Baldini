package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_comunication.Post_simple;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Post_collection implements JSON_Serializable {
	// Member variables
	private ConcurrentHashMap<String, Post> posts = new ConcurrentHashMap<>();
	private String last_post_id;

	// Constructor
	public Post_collection() {
	}

	// Methods
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
		this.posts.put(post_id, new Post(post_id, author, title, text));

		// 3. Set the last post_id to the new post_id.
		last_post_id = post_id;
	}

	// Getters
	public String getLast_post_id() {
		return last_post_id;
	}

	public Post getPost(String post_id) {
		return posts.get(post_id);
	}

	public ConcurrentHashMap<String, Post> getPosts() {
		return posts;
	}


	// Setters
	public void setLast_post_id(String last_post_id) {
		this.last_post_id = last_post_id;
	}

	public void setPosts(ConcurrentHashMap<String, Post> posts) {
		this.posts = posts;
	}

	// Other methods
	public ArrayList<Post_simple> get_postsimple_from_idlist(List<String> ids)
	{
		/*
		 * This method is used to get a list of posts from the post collection.
		 *
		 * 1. Create a new list of posts.
		 * 2. For each post_id in the list of post_ids:
		 * 3. Get the post from the post collection.
		 * 4. Add the post to the list of posts.
		 * 5. Return the list of posts.
		 */

		// 1. Create a new list of posts.
		ArrayList<Post_simple> posts = new ArrayList<>();

		// 2. For each post_id in the list of post_ids:
		for (String id : ids) {
			// 3. Get the post from the post collection.
			Post post = this.posts.get(id);

			// 4. Add the post to the list of posts.
			posts.add(post.get_post_simple());
		}

		// 5. Return the list of posts.
		return posts;

	}

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
