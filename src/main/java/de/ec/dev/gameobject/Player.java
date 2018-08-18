package de.ec.dev.gameobject;

import java.awt.Graphics2D;

public class Player extends GameObject
{
	private int lives;
	
	public Player()
	{
		lives = 5;
	}
	
	public void takeLife()
	{
		lives -= 1;
	}
	
	public int getLives()
	{
		return lives;
	}
	
	@Override
	public void draw(Graphics2D g2d)
	{
		g2d.fillRect((int) getX() - 10, (int) getY() - 10, 20, 20);
	}
}
