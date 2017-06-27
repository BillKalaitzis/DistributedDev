package com.atm.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

@Path("/atm")
public class ATMServer {
	
	/*We are having trouble with sqlite3 deployment on tomcat, so we will emulate a simple DB*/
	private double balance;
	private static String DBUSER_ID = "1";
	private static String DBUSER_NAME = "bill";

	public ATMServer() throws IOException {
		
		File f = new File("/tmp/db.txt");
		if(!f.exists()) {
			this.balance = 500;
			this.writeDB();
		}
		else 
			this.readDB();
		
		
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/identifyUser")
	public String identifyUser(@QueryParam("id")String id, @QueryParam("name")String name) throws SQLException {
		if (id.equals(DBUSER_ID) && name.equals(DBUSER_NAME))
			return "OK";
		return "ERROR";
		
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/withdraw")
	public String withdraw(@QueryParam("amount")double amount) throws SQLException, IOException {
		
		this.readDB();
		if(amount <= 420 && amount <= this.balance) {
			this.balance = this.balance - amount;
			this.writeDB();
			return "OK";
		}
		return "ERROR";
		
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/deposit")
	public String deposit(@QueryParam("amount")double amount) throws SQLException, IOException {
		this.readDB();
		this.balance += amount;
		this.writeDB();
		return "OK";
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/update")
	public String update() throws SQLException, IOException {
		this.readDB();
		return Double.toString(this.balance);
		
	}
	
	private void readDB() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/tmp/db.txt"));
		String text = br.readLine();
		this.balance = Double.parseDouble(text);
		br.close();
	}
	
	private void writeDB() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("/tmp/db.txt"));
		bw.write(Double.toString(this.balance));
		bw.close();
	}


}
