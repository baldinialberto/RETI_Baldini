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
public class RateDB extends User_interaction {
	// Member variables
	private boolean rate; // true = upvote, false = downvote

	public final static String UPVOTE = "+1";
	public final static String DOWNVOTE = "-1";

	// Constructors
	
	// Default constructor
	public RateDB(String username, boolean rate) {
		/*
		 * This constructor is used when we want to create a new vote.
		 * 1. Set the username of this vote.
		 * 2. Set the vote of this vote.
		 * 3. Set the time created of this vote.
		 */

		// 1. Set the username of this vote.
		this.author = username;

		// 2. Set the vote of this vote.
		this.rate = rate;

		// 3. Set the time created of this vote.
		this.time_created = new Timestamp(System.currentTimeMillis());
	}

	// Jackson constructor
	public RateDB() {
	}
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}
	
	
	// JSON Methods
	public static RateDB JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), RateDB.class);
	}

	// Getters
	public boolean getRate() {
		return this.rate;
	}
	
	// Setters
	public void setRate(boolean rate) {
		this.rate = rate;
	}
	
	
	@Override
	public String toString() {
		return "Vote{" +
				"vote=" + (rate ? "like" : "dislike") +
				", author='" + author + '\'' +
				", time_created=" + time_created +
				'}';
	}
}
