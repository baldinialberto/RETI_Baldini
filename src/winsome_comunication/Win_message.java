package winsome_comunication;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Win_message {
	public final static String ERROR = "ERROR";
	public final static String SUCCESS = "SUCCESS";
	private final List<String> message = new ArrayList<>();
	byte[] bytes;
	boolean serialized = false;

	public Win_message(List<String> message) {
		this.message.addAll(message);
	}
	public Win_message(String[] message) {
		this.message.addAll(Arrays.asList(message));
	}
	public Win_message() {
	}

	public byte[] serialize() {
		if (serialized) {
			return bytes;
		}
		StringBuilder message = new StringBuilder();
		for (String string : this.message) {
			message.append(string).append(";");
		}
		bytes = message.toString().getBytes();
		serialized = true;
		return bytes;
	}

	public void deserialize(byte[] data) {
		this.message.clear();
		String[] message = new String(data).split(";");
		this.message.addAll(Arrays.asList(message));
	}

	private int get_length() {
		return serialize().length;
	}

	// list methods
	public void addString(String message) {
		this.message.add(message);
		serialized = false;
	}
	public void addStrings(String[] message) {
		this.message.addAll(Arrays.asList(message));
		serialized = false;
	}
	public void addStrings(List<String> message) {
		this.message.addAll(message);
		serialized = false;
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

	// send - receive methods
	public void send(SocketChannel socket_channel) throws IOException {
		/*
		 * This method sends the message to the given socket channel
		 * The method behaves differently depending on the blocking mode of the socket channel
		 *
		 * 1. send the length of the message
		 * 2. send the message
		 * 3. clear the buffer
		 */

		boolean blocking = socket_channel.isBlocking();
		ByteBuffer length_buffer = ByteBuffer.allocate(Integer.BYTES);
		length_buffer.putInt(get_length());
		length_buffer.flip();
		ByteBuffer message_buffer = ByteBuffer.allocate(get_length());
		message_buffer.put(serialize());
		message_buffer.flip();

		// 1. send the length of the message
		if (blocking) {
			socket_channel.write(length_buffer);
		} else {
			while (length_buffer.hasRemaining()) {
				socket_channel.write(length_buffer);
			}
		}

		// 2. send the message
		if (blocking) {
			socket_channel.write(message_buffer);
		} else {
			while (message_buffer.hasRemaining()) {
				socket_channel.write(message_buffer);
			}
		}

		// 3. clear the buffer
		length_buffer.clear();
		message_buffer.clear();
	}

	public static Win_message receive(SocketChannel socket_channel) throws IOException {
		/*
		 * This method receives a message from the given socket channel
		 * The method behaves differently depending on the blocking mode of the socket channel
		 *
		 * 1. receive the length of the message
		 * 2. receive the message
		 */

		boolean blocking = socket_channel.isBlocking();
		int length = 0;
		ByteBuffer length_buffer = ByteBuffer.allocate(Integer.BYTES);
		ByteBuffer message_buffer;

		// 1. receive the length of the message
		if (blocking) {
			socket_channel.read(length_buffer);
		} else {
			while (length_buffer.hasRemaining()) {
				socket_channel.read(length_buffer);
			}
		}

		length_buffer.flip();
		length = length_buffer.getInt();
		message_buffer = ByteBuffer.allocate(length);

		// 2. receive the message
		if (blocking) {
			socket_channel.read(message_buffer);
		} else {
			while (message_buffer.hasRemaining()) {
				socket_channel.read(message_buffer);
			}
		}

		message_buffer.flip();
		byte[] data = new byte[length];
		message_buffer.get(data);

		Win_message message = new Win_message();
		message.deserialize(data);

		return message;
	}

	@Override
	public String toString() {
		return message.toString();
	}
}
