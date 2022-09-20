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

	// constructor
	public WinsomeMessage(Winsome_Serializable message) {
		this.message = message.serialize();
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
		 * 1. send the message
		 */

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		// 1. send the message
		out.write(message);
		out.write('\n');
		out.flush();
		out.close();
	}

	public static <T extends Winsome_Serializable> T receive_message(Socket socket) throws IOException {
		/*
		 * This method is used to receive a message from a socket
		 *
		 * 1. receive the message
		 * 2. return the Object
		 */

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String message = null;

		// 1. receive the message
		message = in.readLine();
		in.close();

		// 2. return the Object
		return T.deserialize(message);
	}
}


