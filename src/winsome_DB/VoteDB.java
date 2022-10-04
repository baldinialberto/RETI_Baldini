package winsome_DB;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * This class represents a vote in the database.
 * It contains:
 * 1. The value of the vote.
 * 2. The time the vote was created. (from User_interaction)
 * 3. The author of the vote. (from User_interaction)
 * <p></p>
 * This class is available only to the Winsome_Database
 */
public class VoteDB extends User_interaction {
	// Member variables
	private boolean vote; // true = upvote, false = downvote

	// Constructors
	
	// Default constructor
	public VoteDB(String username, boolean vote) {
		/*
		 * This constructor is used when we want to create a new vote.
		 * 1. Set the username of this vote.
		 * 2. Set the vote of this vote.
		 * 3. Set the time created of this vote.
		 */

		// 1. Set the username of this vote.
		this.author = username;

		// 2. Set the vote of this vote.
		this.vote = vote;

		// 3. Set the time created of this vote.
		this.time_created = new Timestamp(System.currentTimeMillis());
	}

	// Jackson constructor
	public VoteDB() {
	}
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}
	
	
	// JSON Methods
	public static VoteDB JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), VoteDB.class);
	}

	// Getters
	public boolean getVote() {
		return this.vote;
	}
	
	// Setters
	public void setVote(boolean vote) {
		this.vote = vote;
	}
	
	
	@Override
	public String toString() {
		return "Vote{" +
				"vote=" + (vote ? "like" : "dislike") +
				", author='" + author + '\'' +
				", time_created=" + time_created +
				'}';
	}
}
