package client;

import java.awt.*;

//Student name: Yuanhang Liu
//Student ID: 1200403

public class MyCanvas extends Canvas{
    
	private static final long serialVersionUID = 1L;
	private Image image = null;
	
	// Set bufferedimage 
	public void setImage(Image image) 
	{
		this.image = image;
	}
	
	// Override paint method to perform paint functions
	@Override
	public void paint(Graphics g) 
	{
		g.drawImage(image, 0, 0, null);
	}
	
	// Override update method to perform paint functions
	@Override
	public void update(Graphics g) 
	{
		paint(g);
	}
	
}