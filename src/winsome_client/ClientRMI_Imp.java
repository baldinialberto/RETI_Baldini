package winsome_client;

import winsome_communication.ClientRMI_Interface;

import java.rmi.server.RemoteObject;

public class ClientRMI_Imp extends RemoteObject implements ClientRMI_Interface {
	private final Client client;

	public ClientRMI_Imp(Client client) {
		this.client = client;
	}

	@Override
	public void send_follower_update(String username, boolean add) throws java.rmi.RemoteException {
		synchronized (client) {
			if (add) {
				client.add_follower(username);
			} else {
				client.remove_follower(username);
			}
		}
	}

	@Override
	public void send_followers(String[] followers) throws java.rmi.RemoteException {
		synchronized (client) {
			client.addAll_followers(followers);
		}
	}

	@Override
	public void send_multicast_details(String ip, int port, String network_name) throws java.rmi.RemoteException {
		client.set_multicast(ip, port, network_name);
		client.start_notification_thread();
	}
}
