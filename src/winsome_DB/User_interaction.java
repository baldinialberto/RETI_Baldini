package winsome_DB;

import java.sql.Timestamp;

/**
 * This class represents a user interaction.
 * Available implementations are: Comment, Post, and Vote.
 *
 * @public getUsername() returns the username of this user interaction.
 * @public getTimeCreated() returns the time created of this user interaction.
 * @reserved setUsername() sets the username of this user interaction.
 * @reserved setTimeCreated() sets the time created of this user interaction.
 */
public abstract class User_interaction implements JSON_Serializable, Comparable<User_interaction> {
	protected String author;
	protected Timestamp time_created;

	// Getters
	public String getAuthor() {
		return this.author;
	}

	public Timestamp getTime_created() {
		return this.time_created;
	}

	// Setters
	public void setAuthor(String author) {
		this.author = author;
	}

	public void setTime_created(Timestamp time_created) {
		this.time_created = time_created;
	}

	@Override
	public int compareTo(User_interaction other) {
		/*
		 * Compare this user interaction to another user interaction.
		 * the comparison is based on the time created.
		 * the newer user interaction is considered smaller.
		 */
		return -this.time_created.compareTo(other.time_created);
	}
}
