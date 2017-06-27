import java.util.ArrayList;

public class DBProtocol {

	private static final int ID = 1;
	private static final int WITHDRAW = 2;
	private static final int DEPOSIT = 3;
	private static final int UPDATE = 4;
	private int userID;
	
	public String getQuery(int action, ArrayList<String> data){
		
		String q = null;;
		if(action == ID){
			q = "Select * from user where ID = " + data.get(1) + " AND name = '" + data.get(0) + "'" ;
			userID = Integer.parseInt(data.get(1));
		}
		else if (action == DEPOSIT)
			q = "Update balance set balance = balance + " + data.get(0) + " where id = " + userID;
		else if (action == UPDATE)
			q = "Select balance from balance where ID = " + userID;
		else if (action == WITHDRAW)
			q = "Update balance set balance = balance - " + data.get(0) + " where id = " + userID;
		return q;
	}
	
}
