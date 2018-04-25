package Simulation;

import java.awt.EventQueue;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;

import Algorithms.Filters.IMotionModel;
import Algorithms.Filters.IObservationModel;
import Algorithms.Filters.ParticleFilter;
import Algorithms.Filters.RandomWalkMotionModel;
import Algorithms.Filters.RangeFreeObservationModel;
import Common.TwoDPoint;
import Common.Utils;

public class SimulationThread extends Thread
{
	
	public SimulationThread(DisplayCanvas displayCanvas, double areaWidth, double areaHeight, int numSensor, int numTarget, double coverRange, double radioError, double maxVeclocity, int numParticles)
	{
		this.displayCanvas = displayCanvas;
		
		this.areaWidth = areaWidth;
		this.areaHeight = areaHeight;
		this.numSensor = numSensor;
		this.numTarget = numTarget;
		this.coverRange = coverRange;
		this.radioError = radioError;
		this.maxVeclocity = maxVeclocity;
		this.numParticles = numParticles;
		
		sensors = new ArrayList<Sensor>();
		targets = new ArrayList<Target>();
		filters = new ArrayList<ParticleFilter>();
		estimation = null; //estimation by particle filtering
		
		displayWidth= displayCanvas.getWidth();
		displayHeight = displayCanvas.getHeight();
		toRun = true;
		

	}
	
	@Override
	public void run()
	{
		init();
		while (toRun)
		{
			try {
				this.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			move();
			

			/*collection sensing data*/
			ArrayList< ArrayList<TwoDPoint> > senseData = collectionSensingData();
			
			
			/* try to find targets' locations, assuming there is only one target for the time being */
			/* update particle filters*/

			//predict location
			filters.get(0).predict(1);
			//show sensors,  targets and particles in the GUI
			//updateUI();
				
					
			//observe 
			ArrayList<TwoDPoint> data = senseData.get(0);
			
			ArrayList<TwoDPoint> data1 = new ArrayList<TwoDPoint> ();
			for (int j =0; j<sensors.size(); j++)
			{
				if (targets.get(0).distance( sensors.get(j)) >coverRange  )
				{
					data1.add(sensors.get(j));
				}
							
			}
			//if (data.size()!=0)
			//{
			RangeFreeObservationModel observer= (RangeFreeObservationModel) filters.get(0).getObserver();
			observer.setListenedSensors(data); //feed in observed data
			observer.setNotListenedSensors(data1);
					
				
			filters.get(0).correct(); // correct prediction accroding to observation 	
			//}
				
			//normalize weight
			filters.get(0).normalizeWeights();
				
			
			//estimate location
			estimation = filters.get(0).estimateLocation(1.0);
				//compare the estimated location with ground true
				//print out the error
				//System.out.println("Step "+String.valueOf(itr) + ": Absolute error of target "+ String.valueOf(i) +" : "+rt.distance(targets.get(i)));
					
			//re-sample 
			filters.get(0).reSampling();
				
			//show the new estimation
			updateUI();
		}
		
	}
	

	/*update canvas in a thread-safe manner */
	public void updateUI()
	{
		ArrayList<TwoDPoint> sensorPoints = new ArrayList<TwoDPoint> () ;
		TwoDPoint targetPoint;
		TwoDPoint estimationPoint;
		ArrayList<TwoDPoint> particlePoints = new ArrayList<TwoDPoint>();
		
		/*fill in sensor points*/
		for(int i=0; i<sensors.size(); i++)
		{
			TwoDPoint point = new TwoDPoint (sensors.get(i).getX()*displayWidth/areaWidth, sensors.get(i).getY()*displayHeight/areaHeight  );
			sensorPoints.add(point);
		}	
		/*fill in target points, assuming there is only 1 target for the time being*/
		targetPoint =new TwoDPoint (targets.get(0).getX()*displayWidth/areaWidth, targets.get(0).getY()*displayHeight/areaHeight );
		/*fill in estimation*/
		if (estimation!=null)
		{			
			estimationPoint = new TwoDPoint ( estimation.getX()*displayWidth/areaWidth  ,  estimation.getY()*displayHeight/areaHeight );
		}
		else
		{
			estimationPoint =null;
		}
		/*fill in particle points*/
		for(int i =0; i<filters.get(0).getParticles().size(); i++)
		{
			double x = filters.get(0).getParticles().get(i).getX() * displayWidth/areaWidth;
			double y = filters.get(0).getParticles().get(i).getY() * displayHeight/areaHeight;
			TwoDPoint point = new TwoDPoint(x , y);
			particlePoints.add(point);
		}
			
		
		
		EventQueue.invokeLater
		(
			new Runnable() 
			{ 
				public void run() 
				{ 
					/*fill in data*/
					displayCanvas.setSensorPoints(sensorPoints);
					displayCanvas.setTargetPoint(targetPoint);
					displayCanvas.setEstimationPoint(estimationPoint);
					displayCanvas.setParticlePoints(particlePoints);
					/*re-paint the canvas*/
					displayCanvas.repaint();
				} 
			}
		); 
		
		
	}
	
	
	

	
	/*initialization*/
	public void init()
	{
		//create sensors
		for (int i =0; i<numSensor; i++)
		{
			Sensor sensor = new Sensor(i, Utils.UniformDist(0.0, areaWidth),  Utils.UniformDist(0.0, areaHeight),  coverRange);
			sensors.add(sensor);
		}
		
		//create targets
		for (int i =0; i<numTarget; i++)
		{
			Target target = new Target(i, Utils.UniformDist(0.0, areaWidth),  Utils.UniformDist(0.0, areaHeight),  coverRange);
			targets.add(target);
			
			/*for each target, create a particle filter*/
			IMotionModel predictor = new RandomWalkMotionModel(maxVeclocity, areaWidth, areaHeight);
			IObservationModel observer = new RangeFreeObservationModel(coverRange);
			ParticleFilter filter = new ParticleFilter(areaWidth, areaHeight, observer, predictor);
			filters.add(filter);
		}
		
		
		
		//initiate the particle filters
		for (int i = 0; i<numTarget; i++)
		{
			filters.get(i).CreateInitialParticles(numParticles);
			
		}
	}
	
	
	
	public void move()
	{
		/*
		for(int i=0; i<sensors.size(); i++)
		{
			sensors.get(i).setPosition(random_waypoint(sensors.get(i), i, true));
		}
		
		for(int i=0; i<targets.size(); i++)
		{
			targets.get(i).setPosition(random_waypoint(targets.get(i), i, false));
		}
		*/
		
		
		for(int i=0; i<sensors.size(); i++)
		{
			
			TwoDPoint point = Utils.UniformDistOnDisk(maxVeclocity*1, sensors.get(i).getX(), sensors.get(i).getY());
			double x = Math.min(Math.max(0.0, point.getX()), areaWidth);
			double y = Math.min(Math.max(0.0, point.getY()), areaHeight);
			sensors.get(i).setX(x);
			sensors.get(i).setY(y);
		}
		
		
		for(int i=0; i<targets.size(); i++)
		{
			
			TwoDPoint point = Utils.UniformDistOnDisk(maxVeclocity*1, targets.get(i).getX(), targets.get(i).getY());
			double x = Math.min(Math.max(0.0, point.getX()), areaWidth);
			double y = Math.min(Math.max(0.0, point.getY()), areaHeight);
			targets.get(i).setX(x);
			targets.get(i).setY(y);
		}
		
		

		
		
	}
	
	public ArrayList< ArrayList<TwoDPoint> > collectionSensingData()
	{
		ArrayList< ArrayList<TwoDPoint> > senseData = new ArrayList< ArrayList<TwoDPoint>>(); 
		for (int i = 0; i<targets.size(); i++)
		{
			ArrayList<TwoDPoint> listenedSensors = new ArrayList<TwoDPoint>();
			
			for (int j =0; j<sensors.size(); j++)
			{
				if (targets.get(i).distance( sensors.get(j)) <=coverRange  )
				{
					listenedSensors.add(sensors.get(j));
				}
				
			}
		
			senseData.add(listenedSensors);
		}
		
		return senseData;
	}
	
	
	
	/** random waypoint mobility model */
    public TwoDPoint random_waypoint( TwoDPoint point, int i, boolean isSensor) 
    {
        
    	TwoDPoint dstPoint = new TwoDPoint( Utils.UniformDist(0.0, areaWidth), Utils.UniformDist(0.0, areaHeight) );
    	
    	double dist = point.distance(dstPoint);  
    	
    	double dx = dstPoint.getX() - point.getX();
    	double dy = dstPoint.getY() - point.getY();
    	/*the real speed of the node, we just randomly pick a number smaller than the max possible speed*/
    	double v = Math.pow(maxVeclocity,2) * Math.random();
    	v = Math.sqrt(v);
//    	double x =  Math.min( Math.max( 0.0, point.getX() + (v/dist) *dx ), areaWidth );
//    	double y =  Math.min( Math.max( 0.0,  point.getY() +(v/dist) *dy ), areaHeight );
    	double x =  point.getX() + (v/dist) *dx;
    	double y =  point.getY() +(v/dist) *dy;
    
    	if (isSensor && i == 0) {
    		System.out.println(dstPoint);
	    	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	System.out.println("dx = " + dx);
	    	System.out.println("dy = " + dy);
    	}
		
//    	if( v> dist ) // scale the distance according to speed v 
//    	{
//    		x =point.getX() +(v/dist) *dx;
//			y =point.getY() +(v/dist) *dy;
//    		
//    	}

    	
		return new TwoDPoint(x, y);   
    }
    
	
	public void terminate()
	{
		this.toRun = false;
	}
	
	

	
	public ArrayList<Sensor> getSensors() {
		return sensors;
	}



	public void setSensors(ArrayList<Sensor> sensors) {
		this.sensors = sensors;
	}




	public ArrayList<Target> getTargets() {
		return targets;
	}



	public void setTargets(ArrayList<Target> targets) {
		this.targets = targets;
	}




	private double radioError;
	
	private double coverRange;
	
	private double maxVeclocity;
	
	private ArrayList<Sensor> sensors;
	
	private ArrayList<Target> targets;
	
	/*for the time being, use one filter for each target*/
	private ArrayList<ParticleFilter> filters;
	
	private double areaWidth;
	
	private double areaHeight;
	
	private int numSensor;
	
	private int numTarget;
		
	private int numParticles;
	
	private DisplayCanvas displayCanvas;
	
	private boolean toRun;
	
	private int displayWidth;
	
	private int displayHeight;
	
	
	private TwoDPoint estimation;
}
