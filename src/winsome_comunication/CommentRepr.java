package winsome_comunication;

public class CommentRepr implements WinSerializable {
	private String author;
	private String text;
	private String time_created;

	// Constructor
	public CommentRepr(String author, String text, String time_created) {
		this.author = author;
		this.text = text;
		this.time_created = time_created;
	}

	// String constructor
	public CommentRepr(String string) {
		deserialize(string);
	}

	// Getters
	public String getAuthor() {
		return author;
	}

	// Setters
	public void setAuthor(String author) {
		this.author = author;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTime_created() {
		return time_created;
	}

	public void setTime_created(String time_created) {
		this.time_created = time_created;
	}

	@Override
	public String serialize() {
		/*
		 * Serialize the object to a string
		 *
		 * the comment is formatted as follows:
		 * author||text||time_created
		 *
		 * 1. Serialize the object
		 * 2. Return the serialized object
		 */

		// 1. Serialize the object
		// 2. Return the serialized object
		return author + "||" + text + "||" + time_created;
	}

	@Override
	public void deserialize(String string) {
		/*
		 * Deserialize the object from a string
		 *
		 * the comment is formatted as follows:
		 * author||text||time_created
		 *
		 * 1. Deserialize the object
		 */

		// 1. Deserialize the object
		String[] parts = string.split("\\|\\|");
		author = parts[0];
		text = parts[1];
		time_created = parts[2];
	}

	@Override
	public String toString() {
		/*
		 * Return a pretty string representation of the object
		 *
		 * the comment is returned as follows:
		 * time | author replied: text
		 *
		 */
		return time_created + " | " + author + " replied: " + text;
	}
}
