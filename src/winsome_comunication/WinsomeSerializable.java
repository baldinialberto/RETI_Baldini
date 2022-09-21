package winsome_comunication;

/*
 * This interface is used to define the methods that a serializable class must implement in order to be sent through the network
 * The class must implement the method serialize() and deserialize()
 * The class must also implement the method get_type() to tell the server what type of message it is
 * The class must also implement the method get_length() to tell the server how long the message is
 */
public interface WinsomeSerializable {
	enum MessageType {
		STRING,
		STRING_ARRAY,
	}

	byte[] serialize();
	void deserialize(byte[] data);
	MessageType get_type();
	int get_length();
}
