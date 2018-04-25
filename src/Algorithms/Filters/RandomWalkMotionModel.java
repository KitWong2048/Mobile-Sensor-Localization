package Algorithms.Filters;

import Common.TwoDPoint;
import Common.Utils;

public class RandomWalkMotionModel extends IMotionModel
{	

	public RandomWalkMotionModel(double maxSpeed, double areaWidth, double areaHeight)
	{
		this.maxSpeed = maxSpeed;
		this.areaWidth = areaWidth;
		this.areaHeight = areaHeight;
	}
	
	@Override
	public void drift (Particle p, double interval)
	{
		/* update the location of particle p according to motion model*/
		/* randomly move p to another location inside some circle*/
		
		TwoDPoint point = Utils.UniformDistOnDisk(maxSpeed*interval, p.getX(), p.getY());
		double x = Math.min(Math.max(0.0, point.getX()), areaWidth);
		double y = Math.min(Math.max(0.0, point.getY()), areaHeight);
		
		p.setX(x);
		p.setY(y);
		
	}
	
	private double maxSpeed;
	private double areaWidth;
	private double areaHeight;

}
