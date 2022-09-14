package winsome;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.Objects;

public class ServerRMI extends RemoteObject {
	private final Server server;

	public static class IntRemote implements Remote {
		private int value;

		public IntRemote(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}

	public ServerRMI(Server server, Server.ServerAuthorization auth) throws RemoteException {
		super();
		Objects.requireNonNull(auth);
		this.server = server;
	}
	public IntRemote ServerRMI_registerUser(String username, String password) throws RemoteException {
		return new IntRemote(server.register_user(username, password));
	}

}

