import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.sql.SQLException;

public class AtmClient {
	
	// When prompted for credentials enter: Bill 1
	
	private static final int ERROR = -1;
	private static final int ID = 1;
	private static final int WITHDRAW = 2;
	private static final int DEPOSIT = 3;
	private static final int UPDATE = 4;
	private static final int EXIT = 5;
	private static int HAS_BEEN_IDENTIFIED = 0;
	
	private static final String HOST = "localhost";
	private static int PORT = Registry.REGISTRY_PORT;
	
	public static void main(String[] args) throws NotBoundException, IOException, SQLException{
		if(System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		
		Registry registry = LocateRegistry.getRegistry(HOST,PORT);
		String rmiObjName = "Atm";
		AtmInterface atm = (AtmInterface) registry.lookup(rmiObjName);
		
		BufferedReader user = new BufferedReader(new InputStreamReader(System.in));
		String msg;
		int choice ;
		String ret;
		while(true){
			do{
				showMenu();
				msg = user.readLine();
				choice = processMenuChoice(msg);
			}while(choice == -1);
			do{
				printSubMenu(choice);
				if(choice != 4 && choice != 5)
					msg = user.readLine();
				ret = processData(msg,choice);
			}while(ret == null);
			
			if(choice == ID){
				String id_res = atm.identifyUser(msg);
				if(id_res.equals("OK")){
					completedID();
					System.out.println("Success");
				}
				else
					System.out.println("Error");
			}
			else if(choice == DEPOSIT){
				atm.deposit(msg);
				System.out.println("Transaction Complete");
			}
			else if(choice == WITHDRAW){
				String withdraw_res = atm.withdraw(msg);
				if(withdraw_res.equals("OK"))
					System.out.println("Transaction complete");
				else
					System.out.println("Transaction failed");
			}
			
			else if(choice == UPDATE){
				String update_res = atm.update();
				System.out.println("Your balance is " + update_res);
			}
			
			else{
				System.out.println("Terminating the session... Bye!");
				break;
			}
			
			
		}
	}
	
	
	private static void showMenu(){
		System.out.println();
		System.out.println("### Available Actions ###");
		if(getIDState() == 0)
			System.out.println("1. Client identification");
		if(getIDState() == 1){
			System.out.println("2. Withdraw");
			System.out.println("3. Deposit");
			System.out.println("4. Balance update");
		}
		System.out.println("5. Exit");
		System.out.print("Enter the desired action > ");
		
	}
	
	private static void printSubMenu(int n){
		if(n == 1){
			System.out.print("Enter your name and ID > ");
		}
		else if(n == 2){
			System.out.print("Enter the amount to withdraw > ");
		}
		else if(n == 3){
			System.out.print("Enter the amount to deposit > ");
		}
		
	}
	
	public static int processMenuChoice(String s){
		int n;
		try{
			n = Integer.parseInt(s.trim());
		}catch (NumberFormatException e){
			System.out.println("You must enter an Integer.");
			return ERROR;
		}
		
		if(n > ID && n < EXIT && HAS_BEEN_IDENTIFIED == 0){
			System.out.println("The user must be identified before any other actions are taken.");
			return ERROR;
		}
		
		if(HAS_BEEN_IDENTIFIED == 1 && n == 1){
			System.out.println("User has already been identified");
			return ERROR;
		}
		
		if(n >= ID  && n <= EXIT)
			return n;
		
		return ERROR; 
	}
	
	public static String processData(String s, int n){
		
		String procData = null;
		
		if(n == ID){
			if(s.trim().matches("[a-zA-Z]* [0-9]*")){
				procData = s.trim();
				//HAS_BEEN_IDENTIFIED = 1;
			}
			else
				System.out.println("Wrong input format, try again");
		}
		else if(n == WITHDRAW){
			int i;
			try{
				i = Integer.parseInt(s.trim());
			}catch (NumberFormatException e){
				System.out.println("You must enter a positive Integer up to 420");
				return null;
			}
			if(i <= 0 || i > 420){
				System.out.println("You must enter a positive Integer up to 420");
				return null;
			}
			procData = s.trim(); 
		}
		
		else if(n == DEPOSIT){
			int i;
			try{
				i = Integer.parseInt(s.trim());
			}catch (NumberFormatException e){
				System.out.println("You must enter a positive Integer");
				return null;
			}
			if(i <= 0){
				System.out.println("You must enter a positive Integer");
				return null;
			}
			procData = s.trim(); 
		}

		else if(n == UPDATE)
			procData = "Y";	
		else
			procData = "E";
		
		return procData;
				
	}
	
	public static void completedID(){
		System.out.println("The user has been identified!");
		HAS_BEEN_IDENTIFIED = 1;
	}
	
	public static int getIDState(){
		return HAS_BEEN_IDENTIFIED;
	}
}
