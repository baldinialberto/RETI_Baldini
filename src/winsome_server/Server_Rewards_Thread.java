package winsome_server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is used to send reward notifications to the clients.
 * The thread updates the rewards and sends the notifications every 60 seconds.
 * The rewards are calculated based on the recent activity of the clients.
 * The activities are stored in a concurrent queue that is updated by the server.
 */
public class Server_Rewards_Thread extends Thread {
	// member variables
	private ConcurrentLinkedQueue<Rewarded_Action> activities = new ConcurrentLinkedQueue<>();
	private Server server;

	// constructor
	public Server_Rewards_Thread(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		/*
		 * This method is used to run the thread.
		 * The thread updates the rewards and sends the notifications every 60 seconds.
		 *
		 * 1. While the thread is running.
		 * 2. Save the current time.
		 * 3. Update the rewards.
		 * 4. Wait until 60 seconds have passed.
		 * 5. Send the notifications.
		 */

		// 1. While the thread is running.
		while (this.isAlive()) {
			// 2. Save the current time.
			long start_time = System.currentTimeMillis();

			// 3. Update the rewards.
			this.update_rewards();

			// 4. Wait until 60 seconds have passed.
			while (System.currentTimeMillis() - start_time < 60000) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// 5. Send the notifications.
			this.send_notifications();
		}
	}

	/**
	 * This method is used to send a DatagramPacket to the multicast group.
	 */
	private void send_notifications() {
		/*
		 * This method is used to send a DatagramPacket to the multicast group.
		 *
		 * 1. Create a DatagramPacket.
		 * 2. Send the DatagramPacket.
		 */
		try {
			// 1. Create a DatagramPacket.
			byte[] buffer = "The rewards have been updated.".getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
					InetAddress.getByName(this.server.getMulticast_address()), this.server.getMulticast_port());
			DatagramSocket socket = new DatagramSocket(this.server.getMulticast_port()+1);

			// 2. Send the DatagramPacket.
			socket.send(packet);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void update_rewards() {
	}

}
