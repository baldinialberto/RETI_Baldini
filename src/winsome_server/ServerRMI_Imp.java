package winsome_server;

import winsome_comunication.ClientRMI_Interface;
import winsome_comunication.ServerRMI_Interface;
import winsome_comunication.WinsomeException;

import java.rmi.server.RemoteObject;

public class ServerRMI_Imp extends RemoteObject implements ServerRMI_Interface {
	private final Server server;

	public ServerRMI_Imp(Server server) {
		this.server = server;
	}

	@Override
	public String register_user(String username, String password, String[] tags) throws java.rmi.RemoteException {
		try {
			server.register_request(username, password, tags);
		} catch (WinsomeException e) {
			return e.niceMessage();
		}
		return "User registered successfully";
	}

	@Override
	public int receive_updates(ClientRMI_Interface callback, String username) throws java.rmi.RemoteException {
		int res;
		res = server.receive_updates(callback, username);
		return res;
	}
}

