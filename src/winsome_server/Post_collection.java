package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_comunication.Post_detailed;
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

	public int delete_post(String postId, String user) {
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
			return Server_DB.DB_ERROR_CODE.POST_NOT_FOUND.getValue();
		}

		// 2. Check if the user is the author of the post.
		if (!this.posts.get(postId).getAuthor().equals(user)) {
			return Server_DB.DB_ERROR_CODE.POST_NOT_AUTHORIZED.getValue();
		}

		// 3. Delete the post.
		this.posts.remove(postId);

		// 4. Return the status code.
		return 200;
	}
	public boolean is_author(String user, String postId) {
		/*
		 * This method is used to check if the user is the author of the post.
		 */

		return this.posts.get(postId).getAuthor().equals(user);
	}
	public int comment_post(String postId, String user, String comment) {
		/*
		 * This method is used to comment on a post.
		 *
		 * 1. Check if the post exists (if not return POST_NOT_FOUND).
		 * 2. Add the comment to the post.
		 */

		// 1. Check if the post exists (if not return POST_NOT_FOUND).
		if (!this.posts.containsKey(postId)) {
			return Server_DB.DB_ERROR_CODE.POST_NOT_FOUND.getValue();
		}

		// 2. Add the comment to the post.
		this.posts.get(postId).getComments().add(new Comment(user, comment));

		return Server_DB.DB_ERROR_CODE.SUCCESS.getValue();
	}
	public int rate_post(String postId, String user, String rate) {
		/*
		* This method is used to rate a post.
		*
		* 1. Check if the post exists (if not return POST_NOT_FOUND).
		* 2. Check if the user has already rated the post (if so return POST_ALREADY_RATED).
		* 3. If the rate is neither "+1" (like) nor "-1" (dislike) return POST_INVALID_RATE.
		* 4. Add the rating to the post.
		*/

		// 1. Check if the post exists (if not return POST_NOT_FOUND).
		if (!this.posts.containsKey(postId)) {
			return Server_DB.DB_ERROR_CODE.POST_NOT_FOUND.getValue();
		}

		// 2. Check if the user has already rated the post (if so return POST_ALREADY_RATED).
		if (this.posts.get(postId).has_vote_from(user)) {
			return Server_DB.DB_ERROR_CODE.POST_ALREADY_RATED.getValue();
		}

		// 3. If the rate is neither "+1" (like) nor "-1" (dislike) return POST_INVALID_RATE.
		if (!rate.equals("+1") && !rate.equals("-1")) {
			return Server_DB.DB_ERROR_CODE.POST_INVALID_RATING.getValue();
		}

		// 4. Add the rating to the post.
		this.posts.get(postId).getVotes().add(
				new Vote(user, rate.equals("+1") ? Vote.VoteType.UPVOTE : Vote.VoteType.DOWNVOTE));

		return Server_DB.DB_ERROR_CODE.SUCCESS.getValue();
	}
	public ArrayList<Post_simple> get_postsimple_from_idlist(List<String> ids)
	{
		/*
		 * This method is used to get a list of posts from the post collection.
		 *
		 * 1. Create a new list of posts.
		 * 2. For each post_id in the list of post_ids:
		 * 3. Get the post from the post collection.
		 * 4. Add the post to the list of posts.
		 * 5. Sort the list of posts.
		 * 6. Populate the list of post_simples;
		 * 7. Return the list of posts_simples.
		 */

		// 1. Create a new list of posts.
		ArrayList<Post> posts = new ArrayList<>();
		ArrayList<Post_simple> post_simples = new ArrayList<>();

		// 2. For each post_id in the list of post_ids:
		for (String id : ids) {
			// 3. Get the post from the post collection.
			Post post = this.posts.get(id);
			// 4. Add the post to the list of posts.
			posts.add(post);
		}

		// 5. Sort the list of posts.
		posts.sort(Post::compareTo);

		// 6. Populate the list of post_simples;
		for (Post post : posts) {
			post_simples.add(post.to_post_simple());
		}

		// 7. Return the list of post_simples.
		return post_simples.isEmpty() ? null : post_simples;

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

	public boolean post_exists(String post_id) {
		return posts.containsKey(post_id);
	}
	public Post_detailed get_post_detailed(String post_id) {
		/*
		 * This method is used to get detailed information about a post in the post collection.
		 *
		 * 1. Get the post from the post collection.
		 * 2. Return the Post_detail object derived from the post.
		 */

		// 1. Get the post from the post collection.
		Post post = this.posts.get(post_id);
		if (post == null) {
			return null;
		}

		// 2. Return the Post_detail object derived from the post.
		return post.to_post_detailed();
	}
	public Post get_post(String post_id) {
		/*
		 * This method is used to get a post from the post collection.
		 *
		 * 1. Get the post from the post collection.
		 * 2. Return the post.
		 */

		// 1. Get the post from the post collection.
		// 2. Return the post.
		return this.posts.get(post_id);
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
