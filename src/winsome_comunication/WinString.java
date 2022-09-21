package winsome_comunication;

public class WinString implements WinsomeSerializable {
	// member variables
	String message;

	// constructor
	public WinString(String message) {
		this.message = message;
	}
	public WinString() {
		this.message = "";
	}

	@Override
	public byte[] serialize() {
		return message.getBytes();
	}

	@Override
	public void deserialize(byte[] data) {
		message = new String(data);
	}

	@Override
	public MessageType get_type() {
		return MessageType.STRING;
	}

	@Override
	public int get_length() {
		return message.getBytes().length;
	}

}
