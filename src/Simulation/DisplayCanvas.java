package Simulation;

import java.util.*;

import java.awt.*;
import javax.swing.JPanel;

import Common.*;


public class DisplayCanvas extends JPanel
{
	public DisplayCanvas(double coverRange, int canvasWidth, double simulationAreaWidth) 
	{
		sensorPoints = null;
		targetPoint = null;
		estimationPoint = null;
		particlePoints = null;
	        //shapesList = new ArrayList<Shape>();
		this.simulationAreaWidth = simulationAreaWidth;
		scaledSensingRange = (int) Math.round( coverRange* canvasWidth/simulationAreaWidth );
	}
	
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		/* fill the canvas with white */
		Graphics2D g2 = (Graphics2D) g;
	    Rectangle bounds = new Rectangle(0, 0,getWidth(), getHeight() );
	    g2.setPaint( Color.black );
	    g2.fill( bounds );

	    //drawNode(350, 350, 10, Color.BLUE, g2);
	    
	    
	    /*drawing*/
	    //draw sensors
	    if(sensorPoints!=null)
	    {
	    	for(int i=0; i<sensorPoints.size(); i++)
	    	{
	    		int x = (int) Math.round(  sensorPoints.get(i).getX() );
	    		int y = (int) Math.round( sensorPoints.get(i).getY() );
	    		//drawAndFillRoundSqure(x, y, squareSize, Color.YELLOW, g2);
	    		drawAndFillSquare(x, y, squareSize, Color.YELLOW, g2);
	    	}
	    }
	    //draw target
	    if (targetPoint!=null)
	    {
	    	int x = (int) Math.round( targetPoint.getX() );
	    	int y = (int)Math.round( targetPoint.getY() );
	    	//drawAndFillRoundSqure(x, y, squareSize, Color.RED, g2);
	    	drawAndFillSquare(x, y, squareSize, Color.GREEN, g2);
	    	drawCircle(x, y, scaledSensingRange, Color.GREEN, g2);
	    
	    }
	    
	    //draw estimation
	    if (estimationPoint!= null)
	    {
	    	int x = (int) Math.round( estimationPoint.getX() );
	    	int y = (int) Math.round( estimationPoint.getY() );
	    	drawCircle(x, y, scaledSensingRange, Color.RED, g2);
	    }	
	    
	    
	    //draw particles
	    /*
	    if(particlePoints!=null)
	    {
	    	for(int i =0; i<particlePoints.size(); i++)
	    	{
	    		int x=(int) Math.round( particlePoints.get(i).getX() );
	    		int y=(int) Math.round( particlePoints.get(i).getY() );
	    		drawNode( x, y, pointSize, Color.BLUE, g2);
	    	
	    	}    
	    }
	    */

	    //drawSquare(200, 200, 200, Color.CYAN, g2);
	    //drawCircle(300, 300, 30, Color.red,  g2);
	    //drawAndFillRoundSqure(120, 120, 20, Color.gray, g2);
	    //drawNode(50, 50, 5, Color.DARK_GRAY, g2);
	}
	
	
	private void drawAndFillRoundSqure(int centerX, int centerY, int side, Color c, Graphics g)
	{
		int topX = centerX-side/2;
		int topY = centerY-side/2;
		g.setColor(c);
		g.drawRoundRect(topX, topY, side, side, side/2, side/2);
		g.fillRoundRect(topX, topY, side, side, side/2, side/2);
	}
	
	private void drawlRoundSqure(int centerX, int centerY, int side, Color c, Graphics g)
	{
		int topX = centerX-side/2;
		int topY = centerY-side/2;
		g.setColor(c);
		g.drawRoundRect(topX, topY, side, side, side/2, side/2);
	}
	

	private void drawAndFillSquare(int centerX, int centerY, int side, Color c, Graphics g)
	{
		int topX = centerX-side/2;
		int topY = centerY-side/2;
		g.setColor(c);
		g.drawRect(topX, topY, side, side);
		g.fillRect(topX, topY, side, side);
	}
	
	private void drawSquare(int centerX, int centerY, int side, Color c, Graphics g)
	{
		int topX = centerX-side/2;
		int topY = centerY-side/2;
		g.setColor(c);
		g.drawRect(topX, topY, side, side);
	}
	
	private void drawCircle(int centerX, int centerY, int side, Color c, Graphics g)
	{
		int topX = centerX-side/2;
		int topY = centerY-side/2;
		g.setColor(c);
		g.drawOval(topX, topY, side, side);
	}
	
	private void drawNode(int centerX, int centerY, int radius, Color c, Graphics g)
	{
		int topX = centerX-radius;
		int topY = centerY-radius;
	    g.setColor(c);
	    g.fillOval(topX, topY, radius, radius);
	    g.drawOval(topX, topY, radius, radius);
	}


	
	public void setSensorPoints (ArrayList<TwoDPoint> sensorPoints)
	{
		this.sensorPoints = sensorPoints;
	}
	
	public ArrayList<TwoDPoint> getSensorPoints ()
	{
		return this.sensorPoints;
	}
	
	
	public void setTargetPoint(TwoDPoint targetPoint)
	{
		this.targetPoint = targetPoint;
	}
	
	public TwoDPoint getTargetPoint()
	{
		return this.targetPoint;
	}
	
	
	public void setEstimationPoint(TwoDPoint estimationPoint)
	{
		this.estimationPoint = estimationPoint;
	}
	
	public TwoDPoint getEstimationPoint()
	{
		return this.estimationPoint;
	}
	
	
	public void setParticlePoints(ArrayList<TwoDPoint> particlePoints)
	{
		this.particlePoints = particlePoints;
	}
	
	public ArrayList<TwoDPoint> getParticlePoints()
	{
		return this.particlePoints;
	}
	
	
	
	private ArrayList<TwoDPoint> sensorPoints;
	private TwoDPoint targetPoint;
	private TwoDPoint estimationPoint;
	private ArrayList<TwoDPoint> particlePoints;
	
	

	private final int pointSize = 4;
	private final int squareSize = 15;
	//private final int circleSize = 60;

	private int scaledSensingRange;
	private double simulationAreaWidth;
}
