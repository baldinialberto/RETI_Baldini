package winsome;


import java.rmi.Remote;

public interface RMI_registration_int extends Remote {
	Object serialVersionUID = new Object();
	int registerUser(String username, String password, String[] tags) throws java.rmi.RemoteException;
}
