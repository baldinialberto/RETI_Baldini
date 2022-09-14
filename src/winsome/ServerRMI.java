package winsome;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.Objects;

public class ServerRMI extends RemoteServer {
	private Server server;
	public ServerRMI(Server server, Server.ServerAuthorization auth) {
		super();
		Objects.requireNonNull(auth);
		this.server = server;
	}
	public Integer ServerRMI_registerUser(String username, String password) throws RemoteException {
		return server.register_user(username, password);
	}

}
