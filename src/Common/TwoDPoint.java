package Common;

public class TwoDPoint 
{
	public TwoDPoint()
	{
		this(0.0, 0.0);
	}
	
	
	public TwoDPoint( double x, double y)
	{
		this.setX(x);
		this.setY(y);
	}
	
	/* calculate the distance from another point */
	public double distance(TwoDPoint p)
	{
		return Math.sqrt( Math.pow((x-p.getX()),2) + Math.pow((y-p.getY()),2) );
	}
	
	
	public void setPosition(TwoDPoint p)
	{
		x = p.x;
		y = p.y;
	}
	
	public double getX() {
		return x;
	}


	public void setX(double x) {
		this.x = x;
	}



	public double getY() {
		return y;
	}


	public void setY(double y) {
		this.y = y;
	}



	/**
	 * X position relative to the field
	 */
	protected double x;
	
	/**
	 * Y position relative to the field
	 */
	protected double y;
	
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
