package winsome_comunication;

public class UserRepr implements WinSerializable {
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


	@Override
	public String serialize() {
		/*
		 * This method is used to serialize the User
		 *
		 * The user is serialized in the following way:
		 * <username>|<tag1>|<tag2>|...|<tagn>
		 */

		StringBuilder sb = new StringBuilder(username);
		for (String s : tags)
			sb.append("|").append(s);

		return sb.toString();
	}

	@Override
	public void deserialize(String string) {
		/*
		 * This method is used to deserialize a string
		 *
		 * The string is deserialized in the following way:
		 * <username>|<tag1>|<tag2>|...|<tagn>
		 */

		String[] split = string.split("\\|");
		this.username = split[0];
		this.tags = new String[split.length - 1];
		System.arraycopy(split, 1, this.tags, 0, split.length - 1);
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
