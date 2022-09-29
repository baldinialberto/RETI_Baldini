package winsome_client;

import winsome_comunication.Client_RMI_Interface;

import java.rmi.server.RemoteObject;

public class Client_RMI_Imp extends RemoteObject implements Client_RMI_Interface {
	private final Client client;

	public Client_RMI_Imp(Client client) {
		this.client = client;
	}

	@Override
	public int send_follower_update(String username) {
		int res;
		synchronized (client) {
			res = client.send_follower_update(username);
		}
		return res;
	}

	@Override
	public int send_followers(String[] followers) {
		int res;
		synchronized (client) {
			res = client.send_followers(followers);
		}
		return res;
	}

	@Override
	public int send_multicast_details(String ip, int port) {
		int res;
		synchronized (client) {
			res = client.send_multicast_details(ip, port);
		}
		return res;
	}
}
