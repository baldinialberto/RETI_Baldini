package winsome_comunication;

/**
 * This class represents a post in the Winsome system.
 * The post is a representation of a post in the database.
 * The post is serializable and can be sent over the network.
 * The post is also deserializable and can be created from a string.
 */
public class Post_representation_simple implements Win_Serializable {
	// Member variables
	private String title;
	private String content;
	private String author;
	private String id;

	// Constructor
	public Post_representation_simple(String title, String content, String author, String id) {
		this.title = title;
		this.content = content;
		this.author = author;
		this.id = id;
	}

	// Empty constructor
	public Post_representation_simple() {
	}

	// Deserialization constructor
	public Post_representation_simple(String string) {
		deserialize(string);
	}

	// Getters
	public String getTitle() {
		return title;
	}

	// Setters
	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getId() {
		return id;
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
		 * <id> | <title> by <author> : <content>
		 */

		return String.format("%s | %s by %s : %s", id, title, author, content);
	}

	@Override
	public String serialize() {
		/*
		 * This method is used to serialize the post.
		 *
		 * the serialized post is in the following format:
		 * title|-|content|-|author|-|id
		 *
		 *
		 * 1. Serialize the post.
		 * 2. Return the serialized post.
		 */

		// 1. Serialize the post.
		// 2. Return the serialized post.
		return title + "|-|" + content + "|-|" + author + "|-|" + id;
	}

	@Override
	public void deserialize(String string) {
		/*
		 * This method is used to deserialize the post.
		 *
		 * the serialized post is in the following format:
		 * title|-|content|-|author|-|id
		 *
		 * 1. Split the string into an array of strings.
		 * 2. Set the member variables.
		 */

		// 1. Split the string into an array of strings.
		String[] split = string.split("\\|-\\|");

		// 2. Set the member variables.
		title = split[0];
		content = split[1];
		author = split[2];
		id = split[3];
	}
}
