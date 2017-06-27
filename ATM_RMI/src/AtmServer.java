import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class AtmServer {

	private static final String HOST = "localhost";
	private static final int PORT = Registry.REGISTRY_PORT;

	
	public static void main(String[] args) throws Exception{
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS.SSS");
		java.util.Date date;
		
		if(System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		
		AtmImpl rObj = new AtmImpl();
		Registry reg = LocateRegistry.createRegistry(PORT);
		String rmiObjName = "Atm";
		Naming.rebind(rmiObjName, rObj);
		System.out.println("Remote object bounded.");
		
	}
	
}
