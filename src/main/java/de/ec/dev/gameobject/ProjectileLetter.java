package de.ec.dev.gameobject;

import java.awt.Color;

public class ProjectileLetter extends Letter
{
	private Vector2D direction;
	
	public ProjectileLetter(Vector2D target, char letter)
	{
		super(letter, null);
		direction = target.getNormalized();
		
		move(400, 580);
	}
	
	public Vector2D getDirection()
	{
		return direction;
	}
	
	@Override
	protected Color getColor()
	{
		return Color.BLUE;
	}
	
	@Override
	public void update(long delta)
	{
		move(getDirection().getX() * delta / 5d, getDirection().getY() * delta / 5d);
	}
}