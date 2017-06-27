import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sun.glass.ui.Pixels.Format;

public class ATMServer {
	
	private static final int PORT = 4444;
	
	
	public static void main(String[] args) throws IOException{
		
		ServerSocket connectionSocket = new ServerSocket(PORT);
		System.out.println("Server started.");
		
		while(true){
			Socket dataSocket = connectionSocket.accept();
			CommunicateWithClient rcv = new CommunicateWithClient(dataSocket);
			rcv.start();
		}
	}

}

class CommunicateWithClient extends Thread{
	
	private static final int ID = 1;
	private static final int WITHDRAW = 2;
	private static final int DEPOSIT = 3;
	private static final int UPDATE = 4;
	
	private Socket dataSocket;
	private static final String url = "jdbc:sqlite:db/atm.sqlite3";
	private Connection con;
	private static Statement st;
	private int userID;
	
	public CommunicateWithClient (Socket soc){
		dataSocket = soc;		
	}
	
	public void run(){
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS.SSS");
		java.util.Date date;
		
		try{
			con = DriverManager.getConnection(url);
			st = con.createStatement();
			
			System.out.println("Received request from " + dataSocket.getInetAddress());
			InputStream is = dataSocket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			OutputStream os = dataSocket.getOutputStream();
			PrintWriter out = new PrintWriter(os,true);
			ServerProtocol server_proto= new ServerProtocol();
			DBProtocol db_proto = new DBProtocol();
			
			String msg = in.readLine();
			int amount = 0;
			while(!msg.equals("E")){
				Message message = server_proto.processData(msg);
				int action = message.getAction();
				String q = db_proto.getQuery(action, message.getData());
				if(action == ID){
					userID = Integer.parseInt(message.getData().get(1));
					ResultSet rs = st.executeQuery(q);
					int count = 0;
					while (rs.next()) {
						count ++;
					}
					if(count == 1)
						out.println("OK");
					else
						out.println("ERROR");
				}
				else if(action == DEPOSIT){
					st.execute(q);
					out.println("OK");
					amount = Integer.parseInt(message.getData().get(0));
				}
				else if(action == UPDATE){
					ResultSet rs = st.executeQuery(q);
						out.println(rs.getString(1));
				}
				else if(action == WITHDRAW){
					ResultSet rs = st.executeQuery("Select balance from balance where ID = "+userID);
					int currBalance = Integer.parseInt(rs.getString(1));
					rs = st.executeQuery("Select sum(amount) from log where ID = " + userID + " and type = " + WITHDRAW + " and date >= DATE('now', 'weekday 0', '-7 days') ");					
					int totalWithdraws; 
					try{
						totalWithdraws = Integer.parseInt(rs.getString(1));
					}catch (NumberFormatException e){
						totalWithdraws = 0;
					}
					
					int amountToWithdraw = Integer.parseInt(message.getData().get(0));
					if (currBalance >= amountToWithdraw && (totalWithdraws + amountToWithdraw ) <= 420){
						st.execute(q);
						out.println("OK");
						amount = Integer.parseInt(message.getData().get(0));
					}
					else{
						amount = 0;
						out.println("ERROR");
					}
					
				}
				
				date =  Calendar.getInstance().getTime(); 
				String fdate = formatter.format(date);
				String log = "Insert into log values (" + userID + ",'" + fdate + "'," + action + "," + amount + ")";
				st.execute(log);
				msg = in.readLine();
			}
			
			out.println("Goodbye!");
			System.out.println("Closing connection to client");
			dataSocket.close();
			
		}catch (Exception e){
			e.printStackTrace();
		}
	
	}
	
	
	
}
