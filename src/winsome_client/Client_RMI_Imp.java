package winsome_client;

import winsome_comunication.Client_RMI_Interface;

import java.rmi.server.RemoteObject;

public class Client_RMI_Imp extends RemoteObject implements Client_RMI_Interface {
	private final Client client;

	public Client_RMI_Imp(Client client) {
		this.client = client;
	}

	@Override
	public int send_follower_update(String username, boolean add) throws java.rmi.RemoteException {
		int res;
		synchronized (client) {
			if (add) {
				res = client.add_follower(username);
			} else {
				res = client.remove_follower(username);
			}
		}
		return res;
	}

	@Override
	public int send_followers(String[] followers) throws java.rmi.RemoteException {
		int res;
		synchronized (client) {
			res = client.addAll_followers(followers);
		}
		return res;
	}

	@Override
	public int send_multicast_details(String ip, int port, String network_name) throws java.rmi.RemoteException {
		int res;
		synchronized (client) {
			res = client.set_multicast(ip, port, network_name);
		}
		return res;
	}
}
