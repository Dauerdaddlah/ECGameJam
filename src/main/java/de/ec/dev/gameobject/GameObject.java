package de.ec.dev.gameobject;

import de.ec.Drawable;

public abstract class GameObject implements Drawable
{
	private Vector2D position;
	
	public GameObject()
	{
		position = new Vector2D();
	}
	
	public final void move(double x, double y)
	{
		position.setX(position.getX() + x);
		position.setY(position.getY() + y);
	}

	public double getX()
	{
		return position.getX();
	}
	
	public double getY()
	{
		return position.getY();
	}
}
