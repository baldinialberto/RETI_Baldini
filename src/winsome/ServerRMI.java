package winsome;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.Objects;

public class ServerRMI extends RemoteServer {
	public class IntRemote implements Remote {
		public int value;
		public IntRemote(int value) throws RemoteException
		{
			this.value = value;
		}
	}
	private Server server;
	public ServerRMI(Server server, Server.ServerAuthorization auth) throws RemoteException {
		super();
		Objects.requireNonNull(auth);
		this.server = server;
	}
	public IntRemote ServerRMI_registerUser(String username, String password) throws RemoteException {
		return new IntRemote(server.register_user(username, password));
	}

}

