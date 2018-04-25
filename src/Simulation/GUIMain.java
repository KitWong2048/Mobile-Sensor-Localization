package Simulation;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUIMain extends JFrame
{

	
	public GUIMain() 
	{
	    readConfigurationFile();
		
		/*setup UI*/
	    initComponents();
	    
	    simulationWorker = null;
	    
	    /*setup action listener*/
	    setActionListeners();
	}
	
	private void readConfigurationFile()
	{
		Properties prop = new Properties();
		InputStream input = null;

		try 
		{
			input = new FileInputStream("Simulation.prop");
			prop.load(input);
						
		} catch (IOException ex) 
		{
			ex.printStackTrace();
		}

		areaWidth = Double.parseDouble( prop.getProperty("areaWidth"));
		areaHeight = Double.parseDouble( prop.getProperty("areaHeight"));
		numSensor =Integer.parseInt(prop.getProperty("numSensor"));
		numTarget = Integer.parseInt(prop.getProperty("numTarget"));
		coverRange = Double.parseDouble( prop.getProperty("coverRange"));
		radioError=Double.parseDouble( prop.getProperty("radioError"));
		maxVeclocity=Double.parseDouble( prop.getProperty("maxVeclocity"));
		numParticles =  Integer.parseInt(prop.getProperty("sampleNum"));
		
		
	}
	
	private void initComponents() 
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
	    //this.getContentPane().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
	    //drawingPanel = new JPanel();
	    //drawingPanel.setSize(new Dimension(800, 400 ));
	    //this.getContentPane().add(drawingPanel);
	    
	    drawingPanel = new DisplayCanvas(coverRange,canvasWidth, areaWidth);
	    drawingPanel.setSize(new Dimension(canvasWidth, canvasHeight ));
	    
	    buttonPanel = new JPanel();
	    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	    buttonPanel.setSize(new Dimension(canvasWidth, 150 ));
	    //buttonPanel.setLayout( new GridLayout(1,2));
	    

	    reStartButton = new JButton("Restart");
	    buttonPanel.add(reStartButton);    
	    buttonPanel.add(Box.createRigidArea(new Dimension(200,0)));
	    stopButton = new JButton("Stop");
	    buttonPanel.add(stopButton);
	    buttonPanel.add(Box.createRigidArea(new Dimension(200,0)));
	    pauseButton = new JButton("Pause");
	    buttonPanel.add(pauseButton);
	    
	    statisticPanel = new JPanel();
	    //statisticPanel.setSize(drawingPanel.getWidth(), 150);
	    statisticPanel.setLayout(new BoxLayout(statisticPanel, BoxLayout.X_AXIS));
	    statisticPanel.setSize(new Dimension(canvasWidth, 150 ));
	    
	    this.add(drawingPanel);
	    this.add(buttonPanel);
	    this.add(statisticPanel);
	    
	    //Set size
	    this.setSize(new Dimension(canvasWidth, canvasHeight+150 ));
		
	}

	private void setActionListeners()
	{
		/*handle start event*/
		reStartButton.addActionListener
		(
				new ActionListener() 
				{ 
					public void actionPerformed (ActionEvent event) 
					{ 
						if(simulationWorker!=null)
						{
							simulationWorker.terminate();
						}
						simulationWorker = new SimulationThread(drawingPanel, areaWidth,  areaHeight,  numSensor,  numTarget,  coverRange,  radioError,  maxVeclocity, numParticles);
						simulationWorker.start(); 
					} 
				}
		); 
		
		
		/*handle stop event*/	
		stopButton.addActionListener
		(
				new ActionListener() 
				{ 
					public void actionPerformed (ActionEvent event) 
					{ 
						if(simulationWorker!=null)
						{
							simulationWorker.terminate();
						}
					} 
				}
		); 
		
		
		/*handle pause event*/
		pauseButton.addActionListener
		(
				new ActionListener() 
				{ 
					public void actionPerformed (ActionEvent event) 
					{ 
						if(simulationWorker!=null)
						{
							if(pauseButton.getText().compareTo("Pause")==0)
							{
								simulationWorker.suspend();
								pauseButton.setText("Resume");
							}
							else
							{
								simulationWorker.resume();
								pauseButton.setText("Pause");
							}
						}
					} 
				}
		); 
		
		
	}
    
    public void drawContent()
    {
    	drawingPanel.repaint();
    }
    
    private JPanel buttonPanel;
    
    private DisplayCanvas drawingPanel;
    
    private JPanel statisticPanel;
        
    private JButton reStartButton;
    
    private JButton stopButton;
    
    private JButton pauseButton;
    
    
    
    private final int canvasWidth = 700;
    
    private final int canvasHeight = 700;
    
    private SimulationThread simulationWorker;
    
    /* simulation parameters */
    private double areaWidth;
	private double areaHeight;
	private int numSensor;
	private int numTarget;
	private double coverRange;
	private double radioError;
	private double maxVeclocity;
	private int numParticles ;
	
    
    
    
  
    
    
    
    
    
    
    /* the main function, where to put this ?? */
    public static void main(String[] args) 
    {
    	GUIMain gui = new GUIMain();
    	gui.setVisible(true);
    	//gui.drawContent();
    	//gui.drawingPanel.repaint();
    }
    
    
    
    
    
   
    
    

}