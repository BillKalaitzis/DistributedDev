import java.io.*;
import java.net.*;

public class ATMClient{

	private static final int PORT = 4444;
	private static final int ID = 1;
	private static final int WITHDRAW = 2;
	private static final int DEPOSIT = 3;
	private static final int UPDATE = 4;
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		
		InetAddress host = InetAddress.getLocalHost();
		Socket dataSocket = new Socket(host,PORT);
		System.out.println("Connected to server.");
		
		BufferedReader user = new BufferedReader(new InputStreamReader(System.in));
		InputStream is = dataSocket.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		OutputStream os = dataSocket.getOutputStream();
		PrintWriter out = new PrintWriter(os,true);
		String msg;
		String response;
		int choice;
		String processedData;
		ClientProtocol protocol = new ClientProtocol();
		
		while(true){
			do{
				showMenu();
				msg = user.readLine();
				choice = protocol.processMenuChoice(msg);
			}while(choice == -1);
			do{
				printSubMenu(choice);
				if(choice != 4 && choice != 5)
					msg = user.readLine();
				processedData = protocol.processData(msg,choice);
			}while(processedData == null);
			
			out.println(processedData);
			// The server response
			if((response = in.readLine()).equals("Goodbye!")){
				System.out.println("Server closed connection");
				break;
			}
			else{
				if(choice == ID){
					if (response.equals("OK"))
						protocol.completedID();
					else
						System.out.println("Wrong credentials provided!");
				}
				else if(choice == DEPOSIT){
					System.out.println("Transaction complete");
				}
				else if(choice == WITHDRAW ){
					if(response.equals("OK"))
						System.out.println("Transaction complete");
					else
						System.out.println("Transaction failed");
				}
				else if(choice == UPDATE){
					int balance = Integer.parseInt(response);
					System.out.println("Your balance is " + balance);
				}
			}
		}
		

	}

	private static void showMenu(){
		ClientProtocol protocol = new ClientProtocol();
		System.out.println();
		System.out.println("### Available Actions ###");
		if(protocol.getIDState() == 0)
			System.out.println("1. Client identification");
		if(protocol.getIDState() == 1){
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
}