package winsome_comunication;

import java.util.ArrayList;
import java.util.Arrays;

public class Post_representation_detailed extends Post_representation_simple {
	// Member variables
	private int upvotes;
	private int downvotes;
	private ArrayList<Comment_representation> comments;

	// Constructors
	// Default Constructor
	public Post_representation_detailed(String id, String author, String title, String text, int upvotes, int downvotes, ArrayList<Comment_representation> comments) {
		super(id, author, title, text);
		this.upvotes = upvotes;
		this.downvotes = downvotes;
		this.comments = comments;
	}

	// String Constructor
	public Post_representation_detailed(String string) {
		deserialize(string);
	}

	private static String newLine_n_chars(String string, int n) {
		/*
		 * Add a new line every n characters
		 * n is the maximum number of characters per line
		 * The function tries to not break words
		 *
		 * 1. Break the string into words
		 * 2. Add a new line before reaching the maximum number of characters
		 * 3. Reassemble the string
		 * 4. Return the string
		 */

		// 1. Break the string into words
		String[] words = string.split(" ");

		// 2. Add a new line before reaching the maximum number of characters
		StringBuilder sb = new StringBuilder();
		int current_length = 0;
		for (int i = 0; i < words.length; i++) {
			if (current_length == 0 && words[i].length() > n) {
				// The word is longer than the maximum number of characters
				// Break the word into smaller words
				String[] sub_words = words[i].split("(?<=\\G.{" + n + "})");
				sb.append(sub_words[0]);
				for (int j = 1; j < sub_words.length; j++) {
					sb.append("...\n").append(sub_words[j]);
					current_length = sub_words[j].length() + 1;
				}
				sb.append(" ");
				current_length++;
			} else if (current_length + words[i].length() > n) {
				// The word is too long for the current line
				// Add a new line
				sb.append("\n");
				current_length = 0;
				i--;
			} else {
				// The word fits in the current line
				sb.append(words[i]).append(" ");
				current_length += words[i].length() + 1;
			}
		}

		// 3. Reassemble the string
		// 4. Return the string
		return sb.toString();
	}

	@Override
	public String serialize() {
		/*
		 * Serialize the object to a string
		 *
		 * the post is formatted as follows:
		 * <Post_simple_serialization>|-|upvotes|-|downvotes|-|comments
		 *
		 * 1. Serialize the object
		 * 2. Return the serialized object
		 */

		// 1. Serialize the object
		StringBuilder serialized = new StringBuilder(super.serialize());
		serialized.append("|-|");
		serialized.append(this.upvotes);
		serialized.append("|-|");

		serialized.append(this.downvotes);
		serialized.append("|-|");

		serialized.append(this.comments.size());
		serialized.append("|-|");

		for (Comment_representation comment : this.comments) {
			serialized.append(comment.serialize());
			serialized.append("|-|");
		}
		// remove the last "|-|"
		serialized.delete(serialized.length() - 3, serialized.length());

		// 2. Return the serialized object
		return serialized.toString();
	}

	@Override
	public void deserialize(String string) {
		/*
		 * Deserialize the object from a string
		 *
		 * the post is formatted as follows:
		 * <Post_simple_serialization>|-|upvotes|-|downvotes|-|ncomments|-|comments
		 *
		 * 1. Deserialize the object
		 */

		// 1. Deserialize the object
		String[] parts = string.split("\\|-\\|");
		super.deserialize(string);
		upvotes = Integer.parseInt(parts[4]);
		downvotes = Integer.parseInt(parts[5]);
		int ncomments = Integer.parseInt(parts[6]);
		comments = new ArrayList<>();
		for (int i = 7; i < parts.length; i++) {
			comments.add(new Comment_representation(parts[i]));
		}
	}

	@Override
	public String toString() {
		/*
		 * Return a string representation of the object
		 *
		 * The post is represented as follows:
		 * <Post_simple_toString>\n
		 * Upvotes: <upvotes>, Downvotes: <downvotes>\n
		 * Comments: \n<comments> (each comment is separated by a new line)
		 *
		 * 1. Return the string representation
		 */

		// 1. Return the string representation
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append("\n");
		// Append the content, insert a new line after maximum 50 characters
		sb.append(newLine_n_chars(this.content, 50)).append("\n");

		sb.append("Likes: ").append(upvotes).append(", Dislikes: ").append(downvotes).append("\n");
		sb.append("Comments: ").append(comments.size()>0?"":"none");
		for (Comment_representation comment : comments) {
			sb.append("\n").append(comment.toString());
		}

		return sb.toString();
	}
}
