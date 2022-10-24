package winsome_communication;

/**
 * This class represents a post in the Winsome system.
 * The post is a representation of a post in the database.
 * The post is serializable and can be sent over the network.
 * The post is also deserializable and can be created from a string.
 */
public class PostReprSimple extends WinSerializable {
	// Member variables
	protected String title;
	protected String content;
	protected String author;
	protected String id;

	// Constructor
	public PostReprSimple(String title, String content, String author, String id) {
		this.title = title;
		this.content = content;
		this.author = author;
		this.id = id;
	}

	// Empty constructor
	public PostReprSimple() {
	}

	/**
	 * Deserialization constructor
	 * This constructor is used to create a post representation from a string.
	 * The string is a serialized post representation.
	 * @param string The serialized post representation.
	 */
	public PostReprSimple(String string) {
		deserialize(string);
	}

	// Getters
	public String getTitle() {
		return title;
	}
	public String getContent() {
		return content;
	}
	public String getAuthor() {
		return author;
	}
	public String getId() {
		return id;
	}

	// Setters
	public void setTitle(String title) {
		this.title = title;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public void setId(String id) {
		this.id = id;
	}


	// Methods
	@Override
	public String toString() {
		/*
		 * This method is used to get a pretty string representation of the post.
		 *
		 * the string representation is in the following format:
		 * <id> | <author> | <title>
		 */

		return String.format("%-6s | %-10s | %-20s", this.id, this.author, this.title);
	}
}
