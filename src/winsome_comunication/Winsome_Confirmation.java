package winsome_comunication;

public class Winsome_Confirmation implements Winsome_Serializable {
	/*
	 * This class is used to store a confirmation message
	 *
	 * available methods:
	 * 1. constructor
	 * 2. serialize()
	 * 3. deserialize()
	 */

	// member variables
	private String message;
	private boolean success;

	// constructor
	public Winsome_Confirmation(String message, boolean success) {
		this.message = message;
		this.success = success;
	}

	// methods
	// 1. serialize() serialize the object to a String
	@Override
	public String serialize() {
		return String.format("%s;%s", message, success);
	}

	// 2. deserialize() deserialize the object from a String
	public static Winsome_Confirmation deserialize(String serialized_object) {
		System.out.println("Winsome_Confirmation.deserialize()");
		String[] parts = serialized_object.split(";");
		return new Winsome_Confirmation(parts[0], Boolean.parseBoolean(parts[1]));
	}

	// getters
	public String get_message() {
		return message;
	}

	public boolean get_success() {
		return success;
	}
}
