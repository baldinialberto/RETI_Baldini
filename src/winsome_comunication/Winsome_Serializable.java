package winsome_comunication;

public interface Winsome_Serializable {
	/*
	 * This interface is used to serialize and deserialize objects
	 * that are meant to be sent over the network
	 *
	 * available methods:
	 * 1. serialize() serialize the object to a String
	 * 2. deserialize() deserialize the object from a String
	 */

	// methods
	// 1. serialize() serialize the object to a String
	String serialize();

	// 2. deserialize() deserialize the object from a String
	static <T extends Winsome_Serializable> T deserialize(String serialized_object) {
		System.out.println("Winsome_Serializable.deserialize() is not implemented");
		return null;
	}
}
