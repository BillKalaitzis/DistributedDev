import java.rmi.*;
import java.rmi.server.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AtmImpl extends UnicastRemoteObject implements AtmInterface{
	
	private static final int ID = 1;
	private static final int WITHDRAW = 2;
	private static final int DEPOSIT = 3;
	private static final int UPDATE = 4;
	
	private static final String url = "jdbc:sqlite:db/atm.sqlite3";
	private static Connection con;
	private static Statement st;
	private int userID;
	private int transactionAmount = 0;
	private int action = 0;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS.SSS");
	
	public AtmImpl() throws RemoteException{
		try{
			con = DriverManager.getConnection(url);
			st = con.createStatement();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public String identifyUser(String msg) throws SQLException{
		action = ID;
		transactionAmount = 0;
		String[] str = msg.split(" ");
		String name = str[0];
		String ID = str[1];
		String q = "Select * from user where " + "ID = " + ID +" AND name = '" + name + "'" ;

		ResultSet rs = st.executeQuery(q);
		userID = Integer.parseInt(ID);
		int count = 0;
		while (rs.next()) {
			count ++;
		}
		logTransaction();
		if(count == 1)
			return "OK";
		else
			return "ERROR";

	}
	
	public String withdraw(String msg) throws SQLException{
		action = WITHDRAW;
		String q = "Update balance set balance = balance - " + msg.trim() + " where id = " + userID;
		ResultSet rs = st.executeQuery("Select balance from balance where ID = "+userID);
		int currBalance = Integer.parseInt(rs.getString(1));
		rs = st.executeQuery("Select sum(amount) from log where ID = " + userID + " and type = " + WITHDRAW + " and date >= DATE('now', 'weekday 0', '-7 days') ");					
		int totalWithdraws;
		try{
			totalWithdraws = Integer.parseInt(rs.getString(1));
		}catch (NumberFormatException e){
			totalWithdraws = 0;
		}
		
		int amountToWithdraw = Integer.parseInt(msg);
		if (currBalance >= amountToWithdraw && (totalWithdraws + amountToWithdraw ) <= 420){
			st.execute(q);
			transactionAmount = Integer.parseInt(msg.trim());
			logTransaction();
			return "OK";
		}
		else{
			transactionAmount = 0;
			logTransaction();
			return "ERROR";
		}

	}
	
	public void deposit (String msg) throws SQLException{
		action = DEPOSIT;
		String q = "Update balance set balance = balance + " + msg.trim() + " where id = " + userID;
		st.execute(q);
		transactionAmount = Integer.parseInt(msg.trim());
		logTransaction();
	}
	
	public String update() throws SQLException{
		action = UPDATE;
		transactionAmount = 0;
		String q = "Select balance from balance where ID = " + userID;
		ResultSet rs = st.executeQuery(q);
		String result = rs.getString(1);
		logTransaction();
		return result;
	}
	
	private void logTransaction() throws SQLException{
		java.util.Date date =  Calendar.getInstance().getTime(); 
		String fdate = formatter.format(date);
		String log = "Insert into log values (" + userID + ",'" + fdate + "'," + action + "," + transactionAmount + ")";
		st.execute(log);
	}
}
