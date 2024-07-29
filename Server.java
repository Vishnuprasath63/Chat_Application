// Java implementation of Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import java.io.*;
import java.util.*;
import java.net.*;

public class Server 
{

	static Vector<ClientHandler> ar = new Vector<>();//to handle no of clients and store as vector
	
	static int i = 0;

	public static void main(String[] args) throws IOException 
	{
		ServerSocket ss = new ServerSocket(8080);
		
		Socket s;
		
		while (true) 
		{
			s = ss.accept();

			System.out.println("New client request received : " + s);
			
			DataInputStream dis = new DataInputStream(s.getInputStream());//to get only data using .getInputStream()
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			System.out.println("Creating a new handler for this client...");

			ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);//match the client and server 

			Thread t = new Thread(mtch);//creating threaed
			
			System.out.println("Adding this client to active client list");

			ar.add(mtch);//adding each thread to vector as a object

			t.start();
			i++;

		}
	}
}

class ClientHandler implements Runnable 
{
	Scanner scn = new Scanner(System.in);
	private String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;
	boolean isloggedin;
	
	public ClientHandler(Socket s, String name,DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.name = name;
		this.s = s;
		this.isloggedin=true;
	}

	@Override
	public void run() {

		String received;
		while (true) 
		{
			try
			{
				received = dis.readUTF();//scan the content = readUTF
				
				System.out.println(received);
				
				if(received.equals("logout")){
					this.isloggedin=false;
					this.s.close();
					break;
				}
				//"-" is used as a token seperator between msg and recipient
				StringTokenizer st = new StringTokenizer(received, "-"); //stringTokenizer useed to tokeniz a each string 
				String MsgToSend = st.nextToken();
				String recipient = st.nextToken();

				for (ClientHandler mc : Server.ar) 
				{
					if (mc.name.equals(recipient) && mc.isloggedin==true) 
					{
						mc.dos.writeUTF(this.name+" : "+MsgToSend);
						break;
					}
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		try
		{
			this.dis.close();
			this.dos.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
