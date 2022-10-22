package winsome_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is used to send reward notifications to the clients.
 * The thread updates the rewards and sends the notifications every 60 seconds.
 * The rewards are calculated based on the recent activity of the clients.
 * The activities are stored in a concurrent queue that is updated by the server.
 */
public class Server_Rewards_Thread extends Thread {
	private final byte[] buffer = new byte[1024];
	// member variables
	private final Server server;
	private final DatagramSocket socket;
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();

	private final int minutes_to_reward;


	// constructor
	public Server_Rewards_Thread(Server server, int minutes_to_reward, DatagramSocket socket) {
		this.setDaemon(true);
		this.server = server;
		this.minutes_to_reward = minutes_to_reward;
		this.socket = socket;

		// print out the multicast address
		System.out.println("Multicast address: " + server.getMulticast_address());
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

		lock.lock();

		// 1. While the thread is running.
		while (!this.isInterrupted()) {
			// 2. Save the current time.
			long start_time = System.currentTimeMillis();

			// 3. Update the rewards.
			this.update_rewards();
			System.out.println("Rewards updated.");

			// 4. Wait til <minutes_to_reward> minutes have passed.
			try {
				long time_to_wait = (long) minutes_to_reward * 60 * 1000 - (System.currentTimeMillis() - start_time);

				// DEBUG
				System.out.println("Time to wait: " + time_to_wait + " ms, seconds: " + time_to_wait / 1000);

				if (time_to_wait > 0)
					condition.await(time_to_wait, java.util.concurrent.TimeUnit.MILLISECONDS);
			} catch (InterruptedException | IllegalMonitorStateException ignored) {
			}

			// 5. Send the notifications.
			this.send_notifications();
			System.out.println("Notifications sent.");
		}

		lock.unlock();

		//DEBUG
		System.out.println("Rewards thread stopped.");
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
			// 1.1 fill the buffer with "REWARDS UPDATED" message
			String message = "REWARDS UPDATED";
			System.arraycopy(message.getBytes(), 0, buffer, 0, message.length());
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
					InetAddress.getByName(this.server.getMulticast_address()),
					this.server.getMulticast_port());

			// 2. Send the DatagramPacket.
			socket.send(packet);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void update_rewards() {
		server.reward_users();
	}

}
