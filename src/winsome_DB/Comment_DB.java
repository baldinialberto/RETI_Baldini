package winsome_DB;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_comunication.CommentRepr;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * This class represents a comment in the database.
 * It contains:
 * 1. The content of the comment.
 * 2. The author of the comment. (from User_interaction)
 * 3. The time the comment was created. (from User_interaction)
 * <p></p>
 * This class is available only to the Winsome_Database
 */
public class Comment_DB extends User_interaction {
	// Member variables
	private String comment;

	// Constructors

	// Default constructor
	public Comment_DB(String username, String comment) {
		/*
		 * This constructor is used when we want to create a new comment.
		 *
		 * 1. Set the username of this comment.
		 * 2. Set the comment of this comment.
		 * 3. Set the time created of this comment.
		 *
		 */

		// 1. Set the username of this comment.
		this.comment = comment;

		// 2. Set the comment of this comment.
		this.author = username;

		// 3. Set the time created of this comment.
		this.time_created = new Timestamp(System.currentTimeMillis());
	}

	// Jackson constructor
	public Comment_DB() {
	}


	// JSON Methods
	public static Comment_DB JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), Comment_DB.class);
	}
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	// Getters
	public String getComment() {
		return this.comment;
	}

	// Setters
	public void setComment(String comment) {
		this.comment = comment;
	}

	// Representation
	public CommentRepr representation() {
		/*
		 * This method is used to get a Comment_simple object from this Comment object.
		 *
		 * 1. Create a new Comment_simple object.
		 */

		// 1. Create a new Comment_simple object.
		return new CommentRepr(this.author, this.comment,
				new SimpleDateFormat("yyyy-MM-dd HH:mm").format(this.time_created));
	}
}
