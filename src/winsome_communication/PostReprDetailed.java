package winsome_communication;

import java.util.ArrayList;

public class PostReprDetailed extends PostReprSimple {
	// Member variables
	private int upvotes;
	private int downvotes;
	private ArrayList<CommentRepr> comments;

	// Constructors
	// Default Constructor
	public PostReprDetailed(String id, String author, String title, String text, int upvotes, int downvotes, ArrayList<CommentRepr> comments) {
		super(id, author, title, text);
		this.upvotes = upvotes;
		this.downvotes = downvotes;
		this.comments = comments;
	}

	// Empty Constructor
	public PostReprDetailed() {
		super();
		this.upvotes = 0;
		this.downvotes = 0;
		this.comments = new ArrayList<CommentRepr>();
	}

	// String Constructor
	public PostReprDetailed(String string) {
		deserialize(string);
	}

	// Getters
	public int getUpvotes() {
		return upvotes;
	}

	public int getDownvotes() {
		return downvotes;
	}

	public ArrayList<CommentRepr> getComments() {
		return comments;
	}

	// Setters

	public void setUpvotes(int upvotes) {
		this.upvotes = upvotes;
	}

	public void setDownvotes(int downvotes) {
		this.downvotes = downvotes;
	}

	public void setComments(ArrayList<CommentRepr> comments) {
		this.comments = comments;
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
		for (CommentRepr comment : comments) {
			sb.append("\n").append(comment.toString());
		}

		return sb.toString();
	}
}
