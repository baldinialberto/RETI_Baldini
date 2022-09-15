package winsome;

import java.util.List;

public interface RMI_registration_int extends java.rmi.Remote {
	Object serialVersionUID = new Object();
	int registerUser(String username, String password, String[] tags) throws java.rmi.RemoteException;
}
