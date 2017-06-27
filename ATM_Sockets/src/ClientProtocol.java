
public class ClientProtocol {
	
	private static final int ERROR = -1;
	private static final int ID = 1;
	private static final int WITHDRAW = 2;
	private static final int DEPOSIT = 3;
	private static final int UPDATE = 4;
	private static final int EXIT = 5;
	
	private static int HAS_BEEN_IDENTIFIED = 0;
	
	public int processMenuChoice(String s){
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
	
	public String processData(String s, int n){
		
		String procData = null;
		
		if(n == ID){
			if(s.trim().matches("[a-zA-Z]* [0-9]*")){
				procData = "I " + s.trim();
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
			procData = "A " + s.trim(); 
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
			procData = "K " + s.trim(); 
		}

		else if(n == UPDATE)
			procData = "Y";	
		else
			procData = "E";
		
		return procData;
				
	}
	
	public void completedID(){
		System.out.println("The user has been identified!");
		HAS_BEEN_IDENTIFIED = 1;
	}
	
	public int getIDState(){
		return HAS_BEEN_IDENTIFIED;
	}

}
