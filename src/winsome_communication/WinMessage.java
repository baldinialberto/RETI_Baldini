package winsome_communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WinMessage {
	// class constants
	public static final String LOGIN_REQUEST = "LOGIN";
	public static final String LOGOUT_REQUEST = "LOGOUT";
	public static final String LIST_USERS_REQUEST = "LIST_USERS";
	public static final String LIST_FOLLOWING_REQUEST = "LIST_FOLLOWING";
	public static final String FOLLOW_REQUEST = "FOLLOW";
	public static final String UNFOLLOW_REQUEST = "UNFOLLOW";
	public static final String BLOG_REQUEST = "BLOG";
	public static final String POST_REQUEST = "POST";
	public static final String SHOW_FEED_REQUEST = "SHOW_FEED";
	public static final String SHOW_POST_REQUEST = "SHOW_POST";
	public static final String DELETE_REQUEST = "DELETE";
	public static final String RATE_REQUEST = "RATE";
	public static final String REWIN_REQUEST = "REWIN";
	public static final String COMMENT_REQUEST = "COMMENT";
	public static final String WALLET_REQUEST = "WALLET";
	public static final String WALLET_BTC_REQUEST = "WALLET_BTC";
	public final static String ERROR = "ERROR";
	public final static String SUCCESS = "SUCCESS";
	public final static String EXIT = "EXIT";

	// member variables
	private ArrayList<String> message = new ArrayList<>();
	private byte[] bytes;
	private Boolean serialized = false;

	public WinMessage(List<String> message) {
		this.message.addAll(message);
	}

	public WinMessage(String[] message) {
		this.message.addAll(Arrays.asList(message));
	}

	public WinMessage() {
	}

	public static WinMessage receive(SocketChannel socket_channel) throws IOException {
		/*
		 * This method receives a message from the given socket channel
		 * The method behaves differently depending on the blocking mode of the socket channel
		 *
		 * 1. receive the length of the message
		 * 2. receive the message
		 */

		boolean blocking = socket_channel.isBlocking();
		int length;
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

		WinMessage message = new WinMessage();
		message.deserialize(data);

		return message;
	}

	public byte[] serialize(){
		/*
		 * This method serializes the message using Jackson
		 */
		if (serialized) {
			return bytes;
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			bytes = mapper.writeValueAsBytes(this.message);
			serialized = true;
		} catch (IOException e)
		{
			System.err.println(e.getMessage());
		}
		return bytes;
	}

	public void deserialize(byte[] data){
		/*
		 * This method deserializes the message using Jackson
		 */
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.readerForUpdating(this.message).readValue(data);
		} catch (IOException e)
		{
			System.err.println(e.getMessage());
		}
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

	@Override
	public String toString() {
		return message.toString();
	}
}
