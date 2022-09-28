package winsome_comunication;

import java.util.ArrayList;

public class Post_detailed extends Post_simple {
	// Member variables
	private int upvotes;
	private int downvotes;
	private ArrayList<Comment_simple> comments;

	// Constructors
	// Default Constructor
	public Post_detailed(String id, String author, String title, String text, int upvotes, int downvotes, ArrayList<Comment_simple> comments) {
		super(id, author, title, text);
		this.upvotes = upvotes;
		this.downvotes = downvotes;
		this.comments = comments;
	}
	// String Constructor
	public Post_detailed(String string) {
		deserialize(string);
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

		for (Comment_simple comment : this.comments) {
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
			comments.add(new Comment_simple(parts[i]));
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
		sb.append("Upvotes: ").append(upvotes).append(", Downvotes: ").append(downvotes).append("\n");
		sb.append("Comments: \n");
		for (Comment_simple comment : comments) {
			sb.append(comment.toString()).append("\n");
		}

		return sb.toString();
	}
}
