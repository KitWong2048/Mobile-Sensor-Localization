package Common;

import java.util.*;
import java.math.*;
//import org.jscience.mathematics.vector.*;
//import org.jscience.mathematics.number.*;

public class Utils 
{
	
	/* generate exponential distributed random variable*/
	public static double exponetialDist( double mean )
	{
		Random r = new Random();
		double y = 0 ;
		while ( y==0 )
		{
			
			double A = r.nextDouble();
			y = (-mean) * Math.log(1-A);
			
		}
		
		return y;
		
	}

	
	
	/* generate a random number in  [lower, upper]*/

	public static double UniformDist(double lower, double upper)
	{
		Random random= new Random();	
		return lower+ (upper-lower)* random.nextDouble();
	
	}
	
	
	/* generate random point on a disk (on/insdie a circle) */
	public static TwoDPoint UniformDistOnDisk (double radius, double originX, double orginY)
	{
		Random random= new Random();	
		double dSquare = random.nextDouble()* (radius*radius);
		double d = Math.sqrt(dSquare);
		double angle = random.nextDouble() * 2* Math.PI;
		
		TwoDPoint point = new TwoDPoint(originX+d*Math.cos(angle), orginY+ d*Math.sin(angle) );
		
		return point;
	}
	
	

	/* generate gaussian random number */
	public static double normalDist (double mean, double sigma)
	{
		Random random = new Random();
		return random.nextGaussian()*sigma + mean; 
		
	}
	


}
