import java.util.ArrayList;
import java.util.HashMap;

public class ServerProtocol {
	
	private static final int ID = 1;
	private static final int WITHDRAW = 2;
	private static final int DEPOSIT = 3;
	private static final int UPDATE = 4;
	private static final int EXIT = 5;
	
	
	public Message processData(String s){
		Message msg = new Message();
		int action = getAction(s);
		ArrayList<String> data = getData(s,action);
		msg.setAction(action);
		msg.setData(data);
		
		return msg;
	}
	
	private int getAction(String s){
		int action;
		
		if(s.startsWith("I"))
			action=ID;
		else if(s.startsWith("A"))
			action = WITHDRAW;
		else if(s.startsWith("K"))
			action = DEPOSIT;
		else if(s.startsWith("Y"))
			action = UPDATE;
		else
			action = EXIT;
		return action;
	}
	
	private ArrayList<String> getData(String s, int action){
		ArrayList<String> data = new ArrayList<String>();
		
		if(action == ID){
			String[] str = s.split(" ");
			String name = str[1];
			String ID = str[2];
			data.add(name);
			data.add(ID);
			
		}
		else if(action == WITHDRAW || action == DEPOSIT) {
			String[] str = s.split(" ");
			String amount = str[1];
			data.add(amount);
		}
		
		return data;
		
	}

}
