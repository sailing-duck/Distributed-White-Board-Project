package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.BindException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.json.JSONObject;

//Student name: Yuanhang Liu
//Student ID: 1200403

public class ClientMain
{
	private static String ip; // server ip address in string format
	private static int port; // port number to connect 
	private static Socket clientSocket;
	private static DataInputStream in;
	private static DataOutputStream out;
	private static String userName; // the user name for join in the whiteboard
	
	public static void main(String[] args)
	{
		try 
		{
			ip = args[0].toString();
			port = Integer.parseInt(args[1]);
			userName = args[2].toString();
			
			clientSocket = new Socket(ip,port);
			ClientPaint client = new ClientPaint(clientSocket);
			client.run();
			
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			
			JSONObject loginJSON = new JSONObject();
			loginJSON.put("queryCMD","login");
			loginJSON.put("queryString",userName);
			
			//message server the username for login
			sendMessage(out, loginJSON);
			// Display self in the GUI
			client.setUserName((String) loginJSON.get("queryString"));
			
			// Send quit message to the server if client window is closed 
			client.getFrame().addWindowListener(new java.awt.event.WindowAdapter()
			{
				public void windowClosing(java.awt.event.WindowEvent windowEvent)
				{
					sendQuitMessage();
				}
			});
			
			while(clientSocket.isConnected())
			{
				// Receive messages from the server
				if(in.available() > 0)
				{
					
					JSONObject msg_json = new JSONObject(in.readUTF());
					String queryCMD = (String) msg_json.get("queryCMD");
					
					// If the command is load, the prepare to receive image files
					if(queryCMD.equalsIgnoreCase("load"))
					{
						// receive image and paint on the canvas
						byte[] imageBytes = new byte[in.readInt()];
						in.readFully(imageBytes,0,imageBytes.length);
						
						InputStream imageArray = new ByteArrayInputStream(imageBytes);
						client.getGraphics().drawImage(ImageIO.read(imageArray), 0, 0, client.getCanvas());
						client.getCanvas().repaint();
						
					}
					
					// Getting chat messages and display in the CHAT ROOM
					if(queryCMD.equalsIgnoreCase("chat"))
					{
						String queryString = (String) msg_json.get("queryString");
						client.setChatRoom(queryString);
					}
					
					// receive quit message from the server
					if(queryCMD.equalsIgnoreCase("quit"))
					{
						JOptionPane.showMessageDialog(null, "Server has quit");
						break;
					}
					
					// getting logged in user names from server
					if(queryCMD.equalsIgnoreCase("login"))
					{
						String queryString = (String) msg_json.get("queryString");
						client.setUserName(queryString);
						
					}
					// getting logged in user names from server
					if(queryCMD.equalsIgnoreCase("logout"))
					{
						String queryString = (String) msg_json.get("queryString");
						client.removeFromUserList(queryString);
						//if the user is own, then quit the program
						if(queryString.equalsIgnoreCase(client.getOwnName()))
						{
							JOptionPane.showMessageDialog(null, "You have been kicked out by the manager...");
							break;
						}
					}
					
					// receive error messages when login name is the same
					if(queryCMD.equalsIgnoreCase("error"))
					{
						String queryCMDType = (String) msg_json.get("queryCMDType");
						if(queryCMDType.equalsIgnoreCase("sameUserName"))
						{
							JOptionPane.showMessageDialog(null, "Please login with a different user name, your one has been in use...");
							break;
						}
						if(queryCMDType.equalsIgnoreCase("refuse"))
						{
							JOptionPane.showMessageDialog(null, "Your joining request has been refused...");
							break;
						}
						
					}
					
					// If the message is drawing, then perform draw actions on canvas
					if(queryCMD.equalsIgnoreCase("draw"))
					{
						String queryCMDType = (String) msg_json.get("queryCMDType");
						
						if(queryCMDType.equalsIgnoreCase("text"))
						{
							// Draw text messages on Canvas
							String queryColor = (String) msg_json.get("queryColor");
							String queryString = (String) msg_json.get("queryString");
							int queryX1 = (int) msg_json.get("queryX1");
							int queryY1 = (int) msg_json.get("queryY1");
							client.getGraphics().setColor(new Color(Integer.parseInt(queryColor)));
							client.getGraphics().drawString(queryString, queryX1, queryY1);
							client.getCanvas().repaint();
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
							client.drawLine(queryX1, queryY1,queryX2,queryY2,c,client.getGraphics());
							client.getCanvas().repaint();
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
							client.drawCircle(queryX1, queryY1,queryX2,queryY2,c,client.getGraphics());
							client.getCanvas().repaint();
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
							client.drawOval(queryX1, queryY1,queryX2,queryY2,c,client.getGraphics());
							client.getCanvas().repaint();
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
							client.drawRectangle(queryX1, queryY1,queryX2,queryY2,c,client.getGraphics());
							client.getCanvas().repaint();
						}
						else if(queryCMDType.equalsIgnoreCase("clean"))
						{
							//clean the canvas
							String queryColor = (String) msg_json.get("queryColor");
							Color c = new Color(Integer.parseInt(queryColor));
							
							client.getGraphics().setColor(Color.WHITE);
							client.getGraphics().fillRect(0, 0, 600, 420);
							client.getGraphics().setColor(c);
							client.getCanvas().repaint();
						}
					}
				}
			}
			
			clientSocket.close();
			System.exit(0);
		} 
		catch (java.lang.ArrayIndexOutOfBoundsException e)
		{
			JOptionPane.showMessageDialog(null, "Your input is not vaild, please use below format: java -jar JoinWhiteBoard.jar <serverIPAddress> <serverPort> username");
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
		
		catch (UnknownHostException e)
		{
			JOptionPane.showMessageDialog(null,"Unknow host, please check host name and try again...");
			System.exit(0);
		}
		catch (ConnectException e) 
		{
			JOptionPane.showMessageDialog(null,"Server is down, please try again later...");
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
	
	private static void sendQuitMessage()
	{
		try 
		{
			JSONObject quitJSON = new JSONObject();
			quitJSON.put("queryCMD","quit");
			
			out.writeUTF(quitJSON.toString());
			out.flush();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,"Please contact technician for help...");
		}
	}
	
	private static void sendMessage(DataOutputStream out,JSONObject message)
	{
		try 
		{
			out.writeUTF(message.toString());
			out.flush();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,"Please contact technician for help...");
		}
	}
	
}
