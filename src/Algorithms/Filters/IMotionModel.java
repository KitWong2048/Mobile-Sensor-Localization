package Algorithms.Filters;
import java.util.*;


public class IMotionModel 
{
	
	public ArrayList<Particle> Predict (ArrayList<Particle> particles, double interval)
	{
		for (Particle p :  particles)
		{
			drift(p, interval);
		}
			
		return particles;
	}
	
	
	public void drift (Particle p, double interval)
	{
		/* to be Overrided */
		/* update the location of particle p according to motion model*/
	}

}
