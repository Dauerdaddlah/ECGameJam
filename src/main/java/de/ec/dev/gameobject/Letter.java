package de.ec.dev.gameobject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Letter extends GameObject
{
	private char letter;
	
	public Letter(char letter)
	{
		this(letter, 0, 0);
	}
	
	public Letter(char letter, double x, double y)
	{
		move(x, y);
		this.letter = letter;
	}
	
	protected Color getColor()
	{
		return Color.RED;
	}
	
	public char getLetter()
	{
		return letter;
	}
	
	@Override
	public void draw(Graphics2D g2d)
	{
		g2d.setColor(getColor());
		g2d.setFont(new Font("Sans-Serif", Font.BOLD, 30));
		g2d.drawString("" + letter, Math.round(getX()), Math.round(getY()));
	}
}
