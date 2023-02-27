package server;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.json.JSONObject;

//Student name: Yuanhang Liu
//Student ID: 1200403

public class ThreadForClients extends Thread
{
	
	private final Socket clientSocket; // getting client Socket
	private final ThreadForServers server; // getting server instance for broadcasting
	private final ServerPaint serverPaint; // getting paint GUI for painting
	private DataInputStream input;
	private DataOutputStream output;
	private String username; // storing user name of the thread for unique identification
	
	public String getUserName()
	{
		return this.username;
	}
	
	public ThreadForClients(ThreadForServers serverSocket, Socket clientSocket, ServerPaint serverPaint)
	{
		this.server = serverSocket;
		this.clientSocket = clientSocket;
		this.serverPaint = serverPaint;
	}
	
	public void run()
	{
		try 
		{
			ClientHandler(clientSocket);
		} 
		catch (IOException e) 
		{
			System.out.println("Client socket error on: " + clientSocket + "\n");
		} 
		catch (InterruptedException e) 
		{
			System.out.println("Client socket interrputed on: " + clientSocket + "\n");
		}
	}
	
	private void ClientHandler(Socket clientSocket) throws IOException, InterruptedException
	{
		
		this.input = new DataInputStream(clientSocket.getInputStream());
		this.output = new DataOutputStream(clientSocket.getOutputStream());
		
		outerloop:
		while(true)
		{
			
			String msg_out;
			
			if(input.available() > 0)
			{
				JSONObject msg_json = new JSONObject(input.readUTF());
				String queryCMD = (String) msg_json.get("queryCMD");
				
				List<ThreadForClients> clientList = server.getClientList();
				
				// If the chat command is received, broadcast to all other users and update Server GUI
				if(queryCMD.equalsIgnoreCase("chat"))
				{
					for(ThreadForClients client: clientList)
					{
						client.getOutputStream();
						
						msg_out = msg_json.toString();
						client.sendMessages(msg_out);
					}
					serverPaint.setChatRoom((String) msg_json.get("queryString"));
				}
				
				// If the one user quits, then remove the user from the thread list, name list and the GUI
				if(queryCMD.equalsIgnoreCase("quit"))
				{
					server.getClientList().remove(this);
					server.getClientNames().remove(this.username);
					serverPaint.removeFromUserList(this.username);
					// message all users for the logged out user
					for(ThreadForClients client: server.getClientList())
					{
						client.getOutputStream();
						
						JSONObject messageUsers = new JSONObject();
						
						messageUsers.put("queryCMD","logout");
						messageUsers.put("queryString",this.username);
						msg_out = messageUsers.toString();
						
						client.sendMessages(msg_out);

					}	
					
					break outerloop;
				}	
				// Broadcast drawing command to every other users if drawing is done by any user
				if(queryCMD.equalsIgnoreCase("draw"))
				{
					for(ThreadForClients client: clientList)
					{
						client.getOutputStream();
						
						msg_out = msg_json.toString();
						client.sendMessages(msg_out);
					}
					
					paintInServerGUI(msg_json);
					
					String queryCMDType = (String) msg_json.get("queryCMDType");
					List<String> paintRecords = server.getPaintRecords();
					// Storing paint records in the server
					// Clean the record if clean command is sent from a user
					if(queryCMDType.equalsIgnoreCase("clean"))
					{
						server.setPaintRecords(new ArrayList<>());
						// clean the canvas
						serverPaint.getGraphics().setColor(Color.WHITE);
						serverPaint.getGraphics().fillRect(0, 0, 600, 420);
						serverPaint.getGraphics().setColor(serverPaint.getColor());
						serverPaint.getCanvas().repaint();
					}
					else // Storing paint records in the server
					{
						paintRecords.add(msg_json.toString());
						server.setPaintRecords(paintRecords);
					}
				}
				// Broadcast the active user list 
				if(queryCMD.equalsIgnoreCase("login"))
				{
					
					String queryString = (String) msg_json.get("queryString");
					
					// Server choice to accept the client or not
					int serverOpinion = JOptionPane.showConfirmDialog(null, queryString + " Would like to join your whiteboard, do you accept? ","Request to Join", JOptionPane.YES_NO_OPTION);
					//Disconnect user if the server did not accept 
					if(serverOpinion == 1||serverOpinion == JOptionPane.CLOSED_OPTION)
					{
						JSONObject messageUsers = new JSONObject();
						
						messageUsers.put("queryCMD","error");
						messageUsers.put("queryCMDType","refuse");
						try 
						{
							output.writeUTF(messageUsers.toString());
							output.flush();
							server.getClientList().remove(this);
							break outerloop;
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
					}
					
					List<String> clientNames = server.getClientNames();
					
					//Check if the user name is existing in the login user list, if yes, then send error message to user requesting a different name
					for(String name: clientNames)
					{
						if(name.equals(queryString))
						{
							JSONObject messageUsers = new JSONObject();
							
							messageUsers.put("queryCMD","error");
							messageUsers.put("queryCMDType","sameUserName");
							try 
							{
								output.writeUTF(messageUsers.toString());
								output.flush();
								server.getClientList().remove(this);
								break outerloop;
							} 
							catch (IOException e) 
							{
								e.printStackTrace();
							}
							
						}
					}
					
					// When user name passed the check, add it to the client list in the ThreadForServer class
					this.username = queryString;
					clientNames.add(queryString);
					serverPaint.setUserName(queryString);
					server.setClientNames(clientNames);
					
					//broadcast all user names in the list to for user name updates
					for(ThreadForClients client: clientList)
					{
						for(String names: server.getClientNames())
						{
							client.getOutputStream();
							
							JSONObject messageUsers = new JSONObject();
							
							messageUsers.put("queryCMD","login");
							messageUsers.put("queryString",names);
							msg_out = messageUsers.toString();
							client.sendMessages(msg_out);
						}
						
					}
					
					//Broadcast it to all clients 
					JSONObject message = new JSONObject();
					
					message.put("queryCMD","load");
					output.writeUTF(message.toString());
					output.flush();
					// Send current paint to newly logged in user
					ByteArrayOutputStream imageArray = new ByteArrayOutputStream();
					ImageIO.write(serverPaint.getBufferedImage(), "png", imageArray);
					byte[] fileBytes = imageArray.toByteArray();
					
					output.writeInt(fileBytes.length);
					output.write(fileBytes);
					
					
				}
			}
			
		}	
		
		clientSocket.close();
	}
	
	
	
	private void paintInServerGUI(JSONObject msg_json) 
	{
		String queryCMDType = (String) msg_json.get("queryCMDType");
		
		if(queryCMDType.equalsIgnoreCase("text"))
		{
			// Draw text messages on Canvas
			String queryColor = (String) msg_json.get("queryColor");
			String queryString = (String) msg_json.get("queryString");
			int queryX1 = (int) msg_json.get("queryX1");
			int queryY1 = (int) msg_json.get("queryY1");
			serverPaint.getGraphics().setColor(new Color(Integer.parseInt(queryColor)));
			serverPaint.getGraphics().drawString(queryString, queryX1, queryY1);
			serverPaint.getCanvas().repaint();
		}
		else if(queryCMDType.equalsIgnoreCase("line"))
		{
			// Draw Line on Canvas
			String queryColor = (String) msg_json.get("queryColor");
			int queryX1 = (int) msg_json.get("queryX1");
			int queryY1 = (int) msg_json.get("queryY1");
			int queryX2 = (int) msg_json.get("queryX2");
			int queryY2 = (int) msg_json.get("queryY2");
			Color c = new Color(Integer.parseInt(queryColor));
			serverPaint.drawLine(queryX1, queryY1,queryX2,queryY2,c,serverPaint.getGraphics());
			serverPaint.getCanvas().repaint();
		}
		else if(queryCMDType.equalsIgnoreCase("circle"))
		{
			// Draw Line on Canvas
			String queryColor = (String) msg_json.get("queryColor");
			int queryX1 = (int) msg_json.get("queryX1");
			int queryY1 = (int) msg_json.get("queryY1");
			int queryX2 = (int) msg_json.get("queryX2");
			int queryY2 = (int) msg_json.get("queryY2");
			Color c = new Color(Integer.parseInt(queryColor));
			serverPaint.drawCircle(queryX1, queryY1,queryX2,queryY2,c,serverPaint.getGraphics());
			serverPaint.getCanvas().repaint();
		}
		else if(queryCMDType.equalsIgnoreCase("oval"))
		{
			// Draw Line on Canvas
			String queryColor = (String) msg_json.get("queryColor");
			int queryX1 = (int) msg_json.get("queryX1");
			int queryY1 = (int) msg_json.get("queryY1");
			int queryX2 = (int) msg_json.get("queryX2");
			int queryY2 = (int) msg_json.get("queryY2");
			Color c = new Color(Integer.parseInt(queryColor));
			serverPaint.drawOval(queryX1, queryY1,queryX2,queryY2,c,serverPaint.getGraphics());
			serverPaint.getCanvas().repaint();
		}
		else if(queryCMDType.equalsIgnoreCase("rectangle"))
		{
			// Draw Line on Canvas
			String queryColor = (String) msg_json.get("queryColor");
			int queryX1 = (int) msg_json.get("queryX1");
			int queryY1 = (int) msg_json.get("queryY1");
			int queryX2 = (int) msg_json.get("queryX2");
			int queryY2 = (int) msg_json.get("queryY2");
			Color c = new Color(Integer.parseInt(queryColor));
			serverPaint.drawRectangle(queryX1, queryY1,queryX2,queryY2,c,serverPaint.getGraphics());
			serverPaint.getCanvas().repaint();
		}
		else if(queryCMDType.equalsIgnoreCase("clean"))
		{
			//clean the canvas
			String queryColor = (String) msg_json.get("queryColor");
			Color c = new Color(Integer.parseInt(queryColor));
			
			serverPaint.getGraphics().setColor(Color.WHITE);
			serverPaint.getGraphics().fillRect(0, 0, 600, 420);
			serverPaint.getGraphics().setColor(c);
			serverPaint.getCanvas().repaint();
		}
		
	}
	
	// Send message to other clients
	private void sendMessages(String msg)
	{
		try 
		{
			output.writeUTF(msg);
			output.flush();
		}
		catch(SocketException e1)
		{
			JOptionPane.showMessageDialog(null, this.username +" has is connected");
			server.getClientList().remove(this);
			server.getClientNames().remove(this.username);
			serverPaint.removeFromUserList(this.username);
			// message all users for the logged out user
			for(ThreadForClients client: server.getClientList())
			{
				client.getOutputStream();
				String msg_out;
				
				JSONObject messageUsers = new JSONObject();
				
				messageUsers.put("queryCMD","logout");
				messageUsers.put("queryString",this.username);
				msg_out = messageUsers.toString();
				
				client.sendMessages(msg_out);

			}	
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public DataOutputStream getOutputStream()
	{
		return output;
	}
	
}
