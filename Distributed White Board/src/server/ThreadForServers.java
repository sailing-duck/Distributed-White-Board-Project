package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class ThreadForServers extends Thread
{
	private final int port;
	private final String serverUserName;
	private final String ip;
	
	private ArrayList<ThreadForClients> clientList = new ArrayList<>();// Storing every thread connections to a list 
	private List<String> clientNames = new ArrayList<>();// Storing all clientNames
	private List<String> paintRecords = new ArrayList<>(); // Storing all the painting records in the server
	
	public ThreadForServers getServer()
	{
		return this;
	}
	
	public List<String> getPaintRecords()
	{
		return paintRecords;
	}
	
	public void setPaintRecords(List<String> paintRecords)
	{
		this.paintRecords = paintRecords;
	}
	
	public List<String> getClientNames()
	{
		return clientNames;
	}
	
	public void setClientNames(List<String> clientNames)
	{
		this.clientNames = clientNames;
	}
	
	public ThreadForServers(int port,String userName,String ip)
	{
		this.port = port;
		this.serverUserName = userName;
		this.ip = ip;
	}
	
	public List<ThreadForClients> getClientList()
	{
		return clientList;
	}
	
	public void setClientList(ArrayList<ThreadForClients> clientList)
	{
		this.clientList = clientList;
	}
	
	@Override
	public void run()
	{
		try
		{
			
			ServerSocket server = new ServerSocket(port,10,InetAddress.getByName(ip));
			ServerPaint serverGUI = new ServerPaint(this);
			serverGUI.run();
			serverGUI.setUserName(serverUserName);
			
			while(true)
			{
				if(server.isClosed())
				{
					break;
				}
				
				System.out.println("Begin to accept client connections...\n");
				Socket clientSocket = server.accept();
				
				ThreadForClients client = new ThreadForClients(this, clientSocket,serverGUI);
				clientList.add(client);
				synchronized (client) 
				{
					client.start();
				}
				
				
			}
			server.close();
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
		catch (BindException e)
		{
			JOptionPane.showMessageDialog(null, "The port is in use, please use an another one...");
		    System.exit(0);
		}
		catch (ConnectException e)
		{
			JOptionPane.showMessageDialog(null, "Fail to connect to the server, please check the host system configration...");
		    System.exit(0);
		}
		catch (SocketException e)
		{
			System.out.println("Server is down...");
		    System.exit(0);
		}
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "Please contact technician for help...");
		    System.exit(0);
		}
	}
}
