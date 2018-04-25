package Algorithms.Filters;
import java.util.*;
import Common.*;

public class RangeFreeObservationModel extends IObservationModel
{
	
	public RangeFreeObservationModel(double coverRange)
	{
		this.coverRange = coverRange;
		
	}
	
	@Override
	public void measure( Particle p)
	{
		/* update the weight of particle p according to measurement  */
		boolean feasible =true;
		for (int i=0; i<listenedSensors.size(); i++)
		{
			if (p.distance( listenedSensors.get(i)) > coverRange )
			{
				feasible = false;
				
			}
		}
		
		
		for (int i=0; i<notListenedSensors.size(); i++ )
		{
			if(p.distance( notListenedSensors.get(i)) <coverRange)
			{
				feasible = false;
			}
			
		}
		
		if (feasible)
		{
			p.setWeight(1.0);
		}
		else
		{
			p.setWeight(0.0);
		}
	}
	
	
	public ArrayList<TwoDPoint> getListenedSensors() {
		return listenedSensors;
	}

	public void setListenedSensors(ArrayList<TwoDPoint> listenedSensors) {
		this.listenedSensors = listenedSensors;
	}

	public void setNotListenedSensors(ArrayList<TwoDPoint> notListenedSensors) {
		this.notListenedSensors = notListenedSensors;
	}

	
	
	private ArrayList<TwoDPoint> listenedSensors;	
	private ArrayList<TwoDPoint> notListenedSensors;
	private double coverRange;

}
