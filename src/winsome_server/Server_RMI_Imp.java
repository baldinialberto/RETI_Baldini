package winsome_server;

import winsome_comunication.Client_RMI_Interface;
import winsome_comunication.Server_RMI_Interface;
import winsome_comunication.Winsome_Exception;

import java.rmi.server.RemoteObject;

public class Server_RMI_Imp extends RemoteObject implements Server_RMI_Interface {
	private final Server server;

	public Server_RMI_Imp(Server server) {
		this.server = server;
	}

	@Override
	public String register_user(String username, String password, String[] tags) throws java.rmi.RemoteException {
		try {
			server.register_request(username, password, tags);
		} catch (Winsome_Exception e) {
			return e.niceMessage();
		}
		return "User registered successfully";
	}

	@Override
	public int receive_updates(Client_RMI_Interface callback, String username) throws java.rmi.RemoteException {
		int res;
		res = server.receive_updates(callback, username);
		return res;
	}
}

