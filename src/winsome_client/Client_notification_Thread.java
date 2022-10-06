package winsome_client;

import java.net.*;

/**
 * This class is used to receive notifications from the server.
 * Listens to the multicast group and prints the notifications.
 */
public class Client_notification_Thread extends Thread {
	private final Client client;
	private MulticastSocket multicast_socket;

	public Client_notification_Thread(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		NetworkInterface network_interface = null;
		InetSocketAddress group = null;
		try {
			// 1. Create a multicast socket.
			this.multicast_socket = new MulticastSocket(this.client.getMulticast_port());

			group = new InetSocketAddress(this.client.getMulticast_address(), this.client.getMulticast_port());
			network_interface = NetworkInterface.getByName(this.client.getNetwork_interface());

			// 2. Join the multicast group.
			this.multicast_socket.joinGroup(group, network_interface);

			this.multicast_socket.setSoTimeout(1000);

			// 3. Receive the message.
			while (!this.isInterrupted()) {
				// 3.1. Receive the message.
				byte[] buffer = new byte[1024];
				try {
					this.multicast_socket.receive(new DatagramPacket(buffer, buffer.length));
				} catch (SocketTimeoutException ignored) {
					continue;
				}

				// 3.2. notify client that are new rewards
				client.new_rewards_available();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 4. Close the socket.
		if (network_interface != null)
			try {
				this.multicast_socket.leaveGroup(group, network_interface);
			} catch (Exception e) {
				e.printStackTrace();
			}
		this.multicast_socket.close();
	}
}
