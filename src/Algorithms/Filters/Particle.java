package Algorithms.Filters;

import Common.TwoDPoint;

public class Particle extends TwoDPoint implements Comparable<Particle> 
{
	
	
	public Particle(double x, double y, double w) 
	{
		setX(x);
		setY(y);
		setWeight(w);
	}
	
	
	public Particle(double x, double y)
	{
		this(x, y, 0.0);	
	}
	
	
	public Particle()
	{
		this(0.0, 0.0, 0.0);
	}
	
	@Override
	public Particle clone() 
	{
		Particle clone = new Particle(this.x, this.y, this.weight);
		return clone;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + weight + ")";
	}
	

	/**
	 * Comparator for sorting operation
	 */
	@Override
	public int compareTo(Particle p) 
	{
		
		if (this.weight > p.weight)
			return -1;
		else if (this.weight < p.weight)
			return 1;
		else
			return 0;
		 		
		//return (int) (p.weight-this.weight);
	}
	
	
	
	public double getWeight()
	{
		return weight;
	}
	
	public void setWeight(double w)
	{
		this.weight = w;
		
	}
	



	/**
	 * Particle Weight
	 */
	private double weight;

	
	
	
}
