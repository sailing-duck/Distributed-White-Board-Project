package server;

import java.net.BindException;
import java.net.ConnectException;

import javax.swing.JOptionPane;

//Student name: Yuanhang Liu
//Student ID: 1200403

public class ServerMain {
	
	private static int port; // server starting port
	private static String ip; // server ip 
	private static String userName;// the user name of the server
	
	public static void main(String[] args) 
	{	
		try 
		{
			ip = args[0].toString();
			port = Integer.parseInt(args[1]);
			userName = args[2].toString();
			
			ThreadForServers server = new ThreadForServers(port,userName,ip);
			
			synchronized(server)
			{
				server.start();
			}
		} 
		catch (java.lang.ArrayIndexOutOfBoundsException e)
		{
			JOptionPane.showMessageDialog(null, "Your input is not vaild, please use below format: java -jar CreateWhiteBoard.jar <serverIPAddress> <serverPort> username");
		    System.exit(0);
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null, "Port number needs to be a interger...");
		    System.exit(0);
		}
		catch (IllegalArgumentException e)
		{
			JOptionPane.showMessageDialog(null, "The port number needs to be smaller than 65536...");
		    System.exit(0);
		}
		catch (Exception e) 
		{
			JOptionPane.showMessageDialog(null, "Please contact technician for help...");
		    System.exit(0);
		}
		
		
		
	}
}
