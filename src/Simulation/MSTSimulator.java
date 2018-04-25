package Simulation;
import Algorithms.*;
import Algorithms.Filters.*;
import Common.*;

import java.util.*;
import java.awt.Point;
import java.io.*;


public class MSTSimulator 
{
	
	public MSTSimulator(double areaWidth, double areaHeight, int numSensor, int numTarget, double coverRange, double radioError, double maxVeclocity, int totalSteps, int numParticles)
	{
		this.areaWidth = areaWidth;
		this.areaHeight = areaHeight;
		this.numSensor = numSensor;
		this.numTarget = numTarget;
		this.coverRange = coverRange;
		this.radioError = radioError;
		this.maxVeclocity = maxVeclocity;
		this.totalSteps = totalSteps;
		this.numParticles = numParticles;
		
		sensors = new ArrayList<Sensor>();
		targets = new ArrayList<Target>();
		filters = new ArrayList<ParticleFilter>();
	}
	
	
	
	public void runSimulation ()
	{
		int itr = 0;
		
		while (itr++ <totalSteps)
		{
			/* move sensors  and targets*/
			move();
			
			/*collection sensing data*/
			ArrayList< ArrayList<TwoDPoint> > senseData = collectionSensingData();
			
			/* try to find targets' locations */
			/* update particle filters*/
			for(int i =0; i<targets.size(); i++)
			{
				
				
				//predict location
				filters.get(i).predict(1);
				
				//observe 
				ArrayList<TwoDPoint> data = senseData.get(i);
			
				ArrayList<TwoDPoint> data1 = new ArrayList<TwoDPoint> ();
				for (int j =0; j<sensors.size(); j++)
				{
					if (targets.get(0).distance( sensors.get(j)) >coverRange  )
					{
						data1.add(sensors.get(j));
					}
								
				}
			
				RangeFreeObservationModel oberver= (RangeFreeObservationModel) filters.get(i).getObserver();
				oberver.setListenedSensors(data); //feed in observed data
				oberver.setNotListenedSensors(data1);
				filters.get(i).correct(); // correct prediction accroding to observation 	
				
				
				
		
				double ratio = filters.get(i).effectiveCountRatio();
				//normalize weight
				filters.get(i).normalizeWeights();
				
				ratio = filters.get(i).effectiveCountRatio();

						
				//estimate location
				TwoDPoint rt = filters.get(i).estimateLocation(1.0);
				//compare the estimated location with ground true
				//print out the error
				System.out.println("Step "+String.valueOf(itr) + ": Absolute error of target "+ String.valueOf(i) +" : "+rt.distance(targets.get(i)));
				
				
				//re-sample 
				ratio = filters.get(i).effectiveCountRatio();
				filters.get(i).reSampling();
				//filters.get(i).normalizeWeights();
				//ratio = filters.get(i).effectiveCountRatio();

				
				int a= 0;
				a++;
					
			}
			
			
		}
		
		
		
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
			sensors.get(i).setPosition(random_waypoint(sensors.get(i)) );
		}
		
		for(int i=0; i<targets.size(); i++)
		{
			targets.get(i).setPosition(random_waypoint(targets.get(i)));
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
    public TwoDPoint random_waypoint( TwoDPoint point) 
    {
        
    	TwoDPoint dstPoint = new TwoDPoint( Utils.UniformDist(0.0, areaWidth), Utils.UniformDist(0.0, areaHeight) );
    	double dist = point.distance(dstPoint);  
    	
    	double dx = dstPoint.getX() - point.getX();
    	double dy = dstPoint.getY() - point.getY();
    	/*the real speed of the node, we just randomly pick a number smaller than the max possible speed*/
    	double v = maxVeclocity * Math.random();
    	double x =  Math.min( Math.max( 0.0, point.getX() + (v/dist) *dx ), areaWidth );
    	double y =  Math.min( Math.max( 0.0,  point.getY() +(v/dist) *dy ), areaHeight );
    
    	return new TwoDPoint(x, y);    	
		/*
    	if( v< dist ) // scale the distance according to speed v 
    	{
    		point.setX(point.getX() + (v/dist) *dx);
			point.setY(point.getY() +(v/dist) *dy);
    		
    	}
    	else
    	{
    		point.setPosition(dstPoint);
    		
    	}
    	*/
     
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
	
	private int totalSteps;
	
	private int numParticles;
	
	
	
}
