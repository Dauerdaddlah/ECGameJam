package de.ec.dev.gameobject;

public class Vector2D
{
	private double x;
	
	private double y;
	
	public Vector2D()
	{
		this(0, 0);
	}
	
	public Vector2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2D getNormalized()
	{
		double length = Math.sqrt((x * x) + (y * y));

		return new Vector2D(x / length, y / length);
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public void setY(double y)
	{
		this.y = y;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getLength()
	{
		return Math.sqrt((x * x) + (y * y));
	}
}
