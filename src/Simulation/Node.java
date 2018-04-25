package Simulation;
import Algorithms.Filters.*;
import Common.TwoDPoint;

public class Node extends TwoDPoint
{
	public Node(int id, double x, double y,  double range)
	{
		super(x, y);
		this.range = range;
	}
	
	public Node(int id)
	{
		this(id, 0.0, 0.0, 0.0);
	}
	
	public Node()
	{
		this(0, 0.0, 0.0, 0.0);
	}
	
	/*unique id*/
	protected int id;
	
	/*radio range of the node*/
	protected double range;		
}
