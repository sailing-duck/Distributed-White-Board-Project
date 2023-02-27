package client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Canvas;
import java.awt.Color;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.Math;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;
import java.awt.Panel;
import org.json.*;
import javax.swing.JList;
import javax.swing.JLabel;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class ClientPaint
{
	BufferedImage image = new BufferedImage(600, 420, BufferedImage.TYPE_INT_RGB);
	private JFrame frame;
	private MyCanvas canvas;
	private Color color;
	private Graphics g = image.getGraphics();
	private String buttonType;
	private int x1,y1,x2,y2;
	private JButton oval;
	private JButton rectangle;
	private JButton text = new JButton("Text");
	private JButton clean = new JButton("Clean");
	private JButton quit = new JButton("QUIT");
	private JButton chatSend = new JButton("Send");
	private Panel black = new Panel();
	private Panel purple = new Panel();
	private Panel brown = new Panel();
	private Panel lightBrown = new Panel();
	private Panel gray = new Panel();
	private Panel gold = new Panel();
	private Panel orange = new Panel();
	private Panel yellow = new Panel();
	private Panel green = new Panel();
	private Panel lightGreen = new Panel();
	private Panel blue = new Panel();
	private Panel lightBlue = new Panel();
	private Panel red = new Panel();
	private Panel pink = new Panel();
	private Panel cyan = new Panel();
	private Panel magenta = new Panel();
	private JLabel selfName = DefaultComponentFactory.getInstance().createTitle("");
	private String inputText;
	private JList<String> userList = new JList();
	private DefaultListModel<String> model = new DefaultListModel<>();
	private List<String> userNames = new ArrayList<>();
	
	private static Socket clientSocket;
	private static DataInputStream in;
	private static DataOutputStream out;
	private JTextField chatInput;
	private String ownName;
	private JTextArea chatRoom = new JTextArea();
	
	public String getOwnName()
	{
		return ownName;
	}
	
	public JFrame getFrame()
	{
		return frame;
	}
	
	public Graphics getGraphics()
	{
		return g;
	}
	
	public Canvas getCanvas()
	{
		return canvas;
	}
	
	public void setUserName(String user)
	{
		
		if(this.userNames.isEmpty())
		{
			selfName.setText("Welcome: " + user);
			this.ownName = user;// Store own name for chat
			this.userNames.add(user);
		}
		else 
		{
			int isExist = 0;
			
			for(String users : this.userNames)
			{
				if(users.equals(user))
				{
					isExist ++;
				}
				
			}
			
			if(isExist == 0)
			{
				model.addElement(user);
				this.userNames.add(user);
			}
		}
		
	}
	
	public void removeFromUserList(String logoutUser) 
	{
		model.removeElement(logoutUser);
		userNames.remove(logoutUser);
	}
	
	public void run() 
	{
		try 
		{
			
			frame.setVisible(true);
			System.out.println(clientSocket);
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
		

	public ClientPaint(Socket clientsocket) 
	{
		initialize();
		this.clientSocket = clientsocket;
		
	}

	private void initialize() 
	{
		frame = new JFrame();
		frame.getContentPane().setForeground(new Color(0, 0, 0));
		frame.setBounds(100, 100, 949, 515);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		
		canvas = new MyCanvas();
		canvas.setBackground(new Color(255, 255, 255));
		canvas.setForeground(new Color(0, 0, 0));
		canvas.setBounds(107, 49, 600, 420);
		canvas.setImage(image);
		frame.getContentPane().add(canvas);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, 600, 420);
		g.setColor(Color.BLACK);
		
		userList.setModel(model);
		
		JButton line = new JButton("Line");
		line.setFont(new Font("Tahoma", Font.PLAIN, 10));
		line.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				buttonType = "line";
			}
		});
		line.setBounds(10, 49, 90, 32);
		frame.getContentPane().add(line);
		
		JButton circle = new JButton("Circle");
		circle.setFont(new Font("Tahoma", Font.PLAIN, 10));
		circle.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				buttonType = "circle";
			}
		});
		circle.setBounds(10, 92, 90, 32);
		frame.getContentPane().add(circle);
		
		oval = new JButton("Oval");
		oval.setFont(new Font("Tahoma", Font.PLAIN, 10));
		oval.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				buttonType = "oval";
			}
		});
		oval.setBounds(10, 135, 90, 32);
		frame.getContentPane().add(oval);
		
		rectangle = new JButton("Rectangle");
		rectangle.setFont(new Font("Tahoma", Font.PLAIN, 10));
		rectangle.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				buttonType = "rectangle";
			}
		});
		
		text.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				buttonType = "text";
			}
		});
		
		rectangle.setBounds(10, 178, 90, 32);
		frame.getContentPane().add(rectangle);
		clean.setFont(new Font("Tahoma", Font.PLAIN, 10));
		
		clean.setBounds(10, 264, 90, 32);
		frame.getContentPane().add(clean);
		
		black.setBackground(new Color(0, 0, 0));
		black.setBounds(6, 313, 20, 20);
		frame.getContentPane().add(black);
		
		purple.setBackground(new Color(138, 43, 226));
		purple.setBounds(31, 313, 20, 20);
		frame.getContentPane().add(purple);
		
		brown.setBackground(new Color(102, 0, 0));
		brown.setBounds(56, 313, 20, 20);
		frame.getContentPane().add(brown);
		
		lightBrown.setBackground(new Color(153, 102, 0));
		lightBrown.setBounds(81, 313, 20, 20);
		frame.getContentPane().add(lightBrown);
		
		gray.setBackground(new Color(153, 153, 153));
		gray.setBounds(6, 339, 20, 20);
		frame.getContentPane().add(gray);
		
		gold.setBackground(new Color(255, 204, 0));
		gold.setBounds(31, 339, 20, 20);
		frame.getContentPane().add(gold);
		
		orange.setBackground(new Color(255, 102, 0));
		orange.setBounds(56, 339, 20, 20);
		frame.getContentPane().add(orange);
		
		yellow.setBackground(new Color(255, 255, 0));
		yellow.setBounds(81, 339, 20, 20);
		frame.getContentPane().add(yellow);
		
		green.setBackground(new Color(51, 153, 0));
		green.setBounds(6, 365, 20, 20);
		frame.getContentPane().add(green);
		
		lightGreen.setBackground(new Color(51, 255, 0));
		lightGreen.setBounds(31, 365, 20, 20);
		frame.getContentPane().add(lightGreen);
		
		blue.setBackground(new Color(0, 51, 255));
		blue.setBounds(56, 365, 20, 20);
		frame.getContentPane().add(blue);
		
		lightBlue.setBackground(new Color(30, 144, 255));
		lightBlue.setBounds(81, 365, 20, 20);
		frame.getContentPane().add(lightBlue);
		
		red.setBackground(new Color(255, 51, 51));
		red.setBounds(6, 391, 20, 20);
		frame.getContentPane().add(red);
		
		pink.setBackground(new Color(255, 102, 102));
		pink.setBounds(31, 391, 20, 20);
		frame.getContentPane().add(pink);
		
		cyan.setBackground(new Color(0, 255, 255));
		cyan.setBounds(56, 391, 20, 20);
		frame.getContentPane().add(cyan);
		
		magenta.setBackground(new Color(255, 0, 255));
		magenta.setBounds(81, 391, 20, 20);
		frame.getContentPane().add(magenta);
		
		text.setFont(new Font("Tahoma", Font.PLAIN, 10));
		text.setBounds(10, 221, 90, 32);
		
		frame.getContentPane().add(text);
		
		
		userList.setBounds(713, 80, 211, 118);
		frame.getContentPane().add(userList);
		selfName.setFont(new Font("Tahoma", Font.BOLD, 11));
		selfName.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		selfName.setBounds(713, 49, 211, 20);
		frame.getContentPane().add(selfName);

		quit.setFont(new Font("Tahoma", Font.PLAIN, 10));
		quit.setBounds(11, 437, 90, 32);
		frame.getContentPane().add(quit);
		
		JLabel lblNewLabel = new JLabel("CHAT ROOM");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(778, 206, 83, 16);
		frame.getContentPane().add(lblNewLabel);
		
		chatSend.setBounds(855, 440, 70, 23);
		frame.getContentPane().add(chatSend);
		
		chatInput = new JTextField();
		chatInput.setBounds(713, 412, 215, 23);
		frame.getContentPane().add(chatInput);
		chatInput.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(715, 225, 212, 176);
		frame.getContentPane().add(scrollPane);
		scrollPane.setViewportView(chatRoom);
		
		color = Color.black;
		buttonType = "line";
		
		
		/*
		 * Color selections 
		 */
		black.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = new Color(0,0,0);
			}
		});
		
		purple.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = new Color(102,0,153);
			}
		});
		
		brown.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = new Color(102,51,0);
			}
		});
		
		lightBrown.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = new Color(153,102,0);
			}
		});
		
		gray.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = new Color(153,153,153);
			}
		});
		
		gold.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = new Color(255,204,51);
			}
		});
		
		orange.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = new Color(255,102,0);
			}
		});
		
		
		yellow.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = Color.YELLOW;
			}
		});
		
		green.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color =  new Color(0,102,0);
			}
		});
		
		lightGreen.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = new Color(102,255,102);
			}
		});
		
		blue.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = new Color(0,0,204);
			}
		});
		
		lightBlue.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = new Color(51,204,255);
			}
		});
		
		red.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = Color.RED;
			}
		});
		
		pink.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = Color.PINK;
			}
		});
		
		cyan.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = Color.CYAN;
			}
		});
		
		magenta.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				color = Color.MAGENTA;
			}
		});
		
		canvas.addMouseListener(new MouseAdapter() 
		{
		      public void mousePressed(MouseEvent e) 
		      {
		    	  // Original point where mouse is clicked 
		    	  x1 = e.getX();
		    	  y1 = e.getY();
		    	  g.setColor(color);
		    	  // Add text to the canvas where user has entered
		    	  if(buttonType.equalsIgnoreCase("text"))
		    	  {
		    		  inputText= JOptionPane.showInputDialog(null,"Input text here");
		    		  if(inputText!=null)
		    		  {
		    			  JSONObject message = new JSONObject();
		    			  
			    		  g.setColor(color);
			    		  g.drawString(inputText,x1,y1);
			    		  canvas.repaint();
			    		  
			    		  message.put("queryCMD","draw");
			    		  message.put("queryCMDType","text");
			    		  message.put("queryColor",Integer.toString(color.getRGB()));
			    		  message.put("queryString",inputText);
			    		  message.put("queryX1",x1);
			    		  message.put("queryY1",y1);
			    		  
			    		  sendMessage(out, message);
		    		  }
		    	  }
		      }   
		      
		      // perform drawing when mouse is released 
		      public void mouseReleased(MouseEvent e) 
		      {
		    	  x2 = e.getX();
		    	  y2 = e.getY();
		    	  if(buttonType.equalsIgnoreCase("line"))
		    	  {
		    		  JSONObject message = new JSONObject();
		    		  
		    		  // Draw lines
		    		  drawLine(x1, y1, x2, y2, color, g);
		    		  
		    		  // Send messages to the server
		    		  message.put("queryCMD","draw");
		    		  message.put("queryCMDType","line");
		    		  message.put("queryColor",Integer.toString(color.getRGB()));
		    		  message.put("queryX1",x1);
		    		  message.put("queryY1",y1);
		    		  message.put("queryX2",x2);
		    		  message.put("queryY2",y2);
		    		  
		    		  sendMessage(out, message);
		    	  }
		    	  else if(buttonType.equalsIgnoreCase("circle"))
		    	  {
		    		  JSONObject message = new JSONObject();
		    		  
		    		  // Draw Circle
		    		  drawCircle(x1, y1, x2, y2, color, g);
		    		  
		    		  // Send messages to the server
		    		  message.put("queryCMD","draw");
		    		  message.put("queryCMDType","circle");
		    		  message.put("queryColor",Integer.toString(color.getRGB()));
		    		  message.put("queryX1",x1);
		    		  message.put("queryY1",y1);
		    		  message.put("queryX2",x2);
		    		  message.put("queryY2",y2);
		    		  
		    		  sendMessage(out, message);
		    	  }
		    	  else if(buttonType.equalsIgnoreCase("oval"))
		    	  {
		    		  
		    		  JSONObject message = new JSONObject();
		    		  
		    		  // Draw Oval
		    		  drawOval(x1, y1, x2, y2, color, g);
		    		  
		    		  // Send messages to the server
		    		  message.put("queryCMD","draw");
		    		  message.put("queryCMDType","oval");
		    		  message.put("queryColor",Integer.toString(color.getRGB()));
		    		  message.put("queryX1",x1);
		    		  message.put("queryY1",y1);
		    		  message.put("queryX2",x2);
		    		  message.put("queryY2",y2);
		    		  
		    		  sendMessage(out, message);
		    	  }
		    	  else if(buttonType.equalsIgnoreCase("rectangle"))
		    	  {
		    		  JSONObject message = new JSONObject();
		    		  
		    		  // Draw Rectangle
		    		  drawRectangle(x1, y1, x2, y2, color, g);
		    		  
		    		  // Send messages to the server
		    		  message.put("queryCMD","draw");
		    		  message.put("queryCMDType","rectangle");
		    		  message.put("queryColor",Integer.toString(color.getRGB()));
		    		  message.put("queryX1",x1);
		    		  message.put("queryY1",y1);
		    		  message.put("queryX2",x2);
		    		  message.put("queryY2",y2);
		    		  
		    		  sendMessage(out, message);
		    	  }
		    	  canvas.repaint();
		      }
		});
		
		// When Clean button is clicked, clean the canvas
		clean.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				JSONObject message = new JSONObject();
				
				// clean the canvas
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, 600, 420);
				g.setColor(color);
				canvas.repaint();
				
				// Send messages to the server
	    		message.put("queryCMD","draw");
	    		message.put("queryCMDType","clean");
	    		message.put("queryColor",Integer.toString(color.getRGB()));
	    		
	    		sendMessage(out, message);
			}
		});
		
		// Send quit message and close the program
		quit.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				
				JSONObject message = new JSONObject();
				message.put("queryCMD","quit");
				sendMessage(out, message);
				//Quit the program
				System.exit(0);				
			}
		});
		
		// Send chat message to everyone
		chatSend.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if(!chatInput.getText().isEmpty())
				{
					JSONObject message = new JSONObject();
					message.put("queryCMD","chat");
					message.put("queryString",ownName+" : "+chatInput.getText());
					sendMessage(out, message);
					chatInput.setText("");
				}
			}
		});
	}
	
	// set Chat messages in the chat room
	public void setChatRoom(String chat) 
	{
		chatRoom.append(chat+"\n");
	}
	
	// Flush the message to the server
	private void sendMessage(DataOutputStream out,JSONObject message)
	{
		try 
		{
			out.writeUTF(message.toString());
			out.flush();
		}
		catch(SocketException e)
		{
			JOptionPane.showMessageDialog(null,"Server is down, may not be able to connect again for a while...");
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,"Please contact technician for help...");
		}
	}
	
	public void drawLine(int x1, int y1, int x2, int y2, Color c, Graphics g)
	{
		g.setColor(c);
		g.drawLine(x1, y1, x2, y2);
	}
	
	public void drawCircle(int x1, int y1, int x2, int y2, Color c, Graphics g)
	{
		int r = 0;
		int d = 0;
		
		r = Math.abs(x1-x2);
		d = r * 2;
		g.setColor(c);
		g.drawOval(x1-r, y1-r, d, d);
	}
	
	public void drawOval(int x1, int y1, int x2, int y2, Color c, Graphics g)
	{
		int w = 0;
		int h = 0;
		
		w = Math.abs(x1-x2);
		h = Math.abs(y1-y2);
		g.setColor(c);
		
		g.drawOval(x1,y1,w,h);
	}
	
	public void drawRectangle(int x1, int y1, int x2, int y2, Color c, Graphics g)
	{
		int w = 0;
		int h = 0;
		
		w = Math.abs(x1-x2);
		h = Math.abs(y1-y2);
		g.setColor(c);
		g.drawRect(x1,y1,w,h);
	}
}
