package winsome_comunication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WinStringArray implements WinsomeSerializable{
	private final List<String> message = new ArrayList<String>();

	public WinStringArray(List<String> message) {
		this.message.addAll(message);
	}
	public WinStringArray(String[] message) {
		this.message.addAll(Arrays.asList(message));
	}
	public WinStringArray() {
	}

	@Override
	public byte[] serialize() {
		StringBuilder message = new StringBuilder();
		for (String string : this.message) {
			message.append(string).append(";");
		}
		return message.toString().getBytes();
	}

	@Override
	public void deserialize(byte[] data) {
		this.message.clear();
		String[] message = new String(data).split(";");
		this.message.addAll(Arrays.asList(message));
	}

	@Override
	public MessageType get_type() {
		return MessageType.STRING_ARRAY;
	}

	@Override
	public int get_length() {
		return serialize().length;
	}

	// list methods
	public void addString(String message) {
		this.message.add(message);
	}
	public void addStrings(String[] message) {
		this.message.addAll(Arrays.asList(message));
	}
	public void addStrings(List<String> message) {
		this.message.addAll(message);
	}
	public List<String> getStrings() {
		return message;
	}
	public String[] getStringsArray() {
		return message.toArray(new String[0]);
	}
	public String getString(int index) {
		return message.get(index);
	}
	public int size() {
		return message.size();
	}
	public int indexOf(String message) {
		return this.message.indexOf(message);
	}
}
