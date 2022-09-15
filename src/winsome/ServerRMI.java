package winsome;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.List;
import java.util.Objects;

public class ServerRMI extends RemoteObject implements RMI_registration_int {
	private final Server server;

	public ServerRMI(Server server, Server.ServerAuthorization authorization) {
		Objects.requireNonNull(authorization);
		this.server = Objects.requireNonNull(server);
	}

	@Override
	public int registerUser(String username, String password, String[] tags) throws RemoteException {
		synchronized (server) {
			return server.register_user(username, password, tags);
		}
	}
}

