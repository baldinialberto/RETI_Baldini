package winsome_client;

import winsome_comunication.ClientRMI_Interface;

import java.rmi.server.RemoteObject;

public class ClientRMI_Imp extends RemoteObject implements ClientRMI_Interface {
	private final Client client;

	public ClientRMI_Imp(Client client) {
		this.client = client;
	}

	@Override
	public void send_follower_update(String username, boolean add) throws java.rmi.RemoteException {
		int res;
		synchronized (client) {
			if (add) {
				res = client.add_follower(username);
			} else {
				res = client.remove_follower(username);
			}
		}
	}

	@Override
	public void send_followers(String[] followers) throws java.rmi.RemoteException {
		// DEBUG
		// System.out.println("Client_RMI_Imp.send_followers: " + followers.length);

		int res;
		synchronized (client) {
			res = client.addAll_followers(followers);
		}
	}

	@Override
	public void send_multicast_details(String ip, int port, String network_name) throws java.rmi.RemoteException {
		// DEBUG
		// System.out.println("Client_RMI_Imp.send_multicast_details: " + ip + " " + port + " " + network_name);

		int res;
		res = client.set_multicast(ip, port, network_name);
		client.start_notification_thread();
	}
}