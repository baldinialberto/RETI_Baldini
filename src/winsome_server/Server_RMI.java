package winsome_server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.Objects;

public class Server_RMI extends RemoteObject implements RMI_registration_int {
	private final Server server;

	public Server_RMI(Server server) {
		this.server = Objects.requireNonNull(server);
	}

	@Override
	public int registerUser(String username, String password, String[] tags) throws RemoteException {
		int res;
		synchronized (server) {
			res =  server.register_user(username, password, tags);
		}
		return res;
	}
}

