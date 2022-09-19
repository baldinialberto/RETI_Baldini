package winsome_comunication;

import java.io.*;
import java.net.Socket;

public class WinsomeMessage {
	/*
	 * This class is used to store a message and to send it sever-client or client-server
	 *
	 * The message is stored in a String
	 * the rest of the bytes are used to store the message.
	 * The message is sent using TCP
	 *
	 * available methods:
	 * 1. constructor
	 * 2. get_message()
	 * 3. set_message()
	 * 4. send_message()
	 * 5. receive_message()
	 * 6. get_message_length()
	 */

	// member variables
	private String message;
	private int message_length;


	// constructor
	public WinsomeMessage(Winsome_Serializable message) {
		this.message = message.serialize();
		this.message_length = this.message.length();
	}

	// getters
	public String get_message() {
		return message;
	}

	// setters
	public void set_message(String message) {
		this.message = message;
	}

	// methods
	public void send_message(Socket socket) throws IOException {
		/*
		 * This method is used to send a message to a socket
		 *
		 * 1. send the message length
		 * 2. send the message
		 */

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		// 1. send the message length
		out.write(message_length);

		// 2. send the message
		out.write(message);
	}

	public static <T extends Winsome_Serializable> T receive_message(Socket socket) throws IOException {
		/*
		 * This method is used to receive a message from a socket
		 *
		 * 1. receive the message length
		 * 2. receive the message
		 * 3. return the Object
		 */

		// 1. receive the message length
		int message_length = socket.getInputStream().read();
		// 2. receive the message
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] message = new char[message_length];
		int bytes_readen = br.read(message, 0, message_length);

		if (bytes_readen != message_length) {
			System.out.println("Error: bytes_readen != message_length : " + bytes_readen + " != " + message_length);
		}

		// 3. return the Object
		return T.deserialize(new String(message));
	}

	public int get_message_length() {
		return message_length;
	}
}


