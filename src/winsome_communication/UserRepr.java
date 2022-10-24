package winsome_communication;

public class UserRepr extends WinSerializable {
	private String username;
	private String[] tags;

	// Constructors
	// Default constructor
	public UserRepr(String username, String[] tags) {
		this.username = username;
		this.tags = tags;
	}

	// Empty Constructor
	public UserRepr()
	{
		this.username = "nan";
		this.tags = new String[0];
	}

	// Deserialization Constructor
	public UserRepr(String user_serialized)
	{
		deserialize(user_serialized);
	}

	// Getters
	public String getUsername() {
		return this.username;
	}

	public String[] getTags() {
		return this.tags;
	}

	// Setters
	public void setUsername(String username) {
		this.username = username;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	@Override
	public String toString()
	{
		/*
		 * Return a pretty String representation of the user
		 * <username> | <tag1>, <tag2>, ..., <tagn>
		 * the username field occupies 10 characters max
		 */

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-10s", this.username)).append(" | ");
		for (String s : this.tags)
			sb.append(s).append(", ");
		sb.delete(sb.length() - 2, sb.length());

		return sb.toString();
	}
}
