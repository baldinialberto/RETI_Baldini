package winsome_comunication;

public class CommentRepr extends WinSerializable {
	private String author;
	private String text;
	private String time_created;

	// Constructor
	public CommentRepr(String author, String text, String time_created) {
		this.author = author;
		this.text = text;
		this.time_created = time_created;
	}

	// Empty constructor
	public CommentRepr() {
	}

	// String constructor
	public CommentRepr(String string) {
		deserialize(string);
	}

	// Getters
	public String getAuthor() {
		return author;
	}
	public String getText() {
		return text;
	}
	public String getTime_created() {
		return time_created;
	}

	// Setters
	public void setAuthor(String author) {
		this.author = author;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setTime_created(String time_created) {
		this.time_created = time_created;
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
