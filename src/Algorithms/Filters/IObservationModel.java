package Algorithms.Filters;

import java.util.ArrayList;

public class IObservationModel 
{
	
	public ArrayList<Particle> measure (ArrayList<Particle> particles)
	{
		for (Particle p :  particles)
		{
			measure(p);
		}
			
		return particles;
	}
	
	public void measure( Particle p)
	{
		/* to be Overrided */
		/* update the weight of particle p according to measurement  */
	}
	
	
	
}
