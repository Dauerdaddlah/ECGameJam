package de.ec.dev.gameobject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Letter extends GameObject
{
	private final char letter;
	private final Player player;
	
	public Letter(char letter, Player player)
	{
		this(letter, player, 0, 0);
	}
	
	public Letter(char letter, Player player, double x, double y)
	{
		move(x, y);
		this.letter = letter;
		this.player = player;
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
	
	@Override
	public void update(long delta)
	{
		Vector2D direction = new Vector2D(player.getX() - getX(), player.getY() - getY());

		Vector2D normalized = direction.getNormalized();

		move(normalized.getX() * delta / 5d, normalized.getY() * delta / 5d);
	}
}
