import java.rmi.*;
import java.sql.SQLException;

public interface AtmInterface extends Remote{
	
	String identifyUser(String msg) throws RemoteException, SQLException;
	String withdraw (String msg) throws RemoteException, SQLException;
	void deposit(String msg) throws RemoteException, SQLException;
	String update() throws RemoteException, SQLException;

}
