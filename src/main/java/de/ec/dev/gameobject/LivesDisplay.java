package de.ec.dev.gameobject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class LivesDisplay extends GameObject
{
	private final Player player;
	
	public LivesDisplay(Player player)
	{
		this.player = player;
	}
	
	@Override
	public void draw(Graphics2D g2d)
	{
		double width = g2d.getClip().getBounds2D().getWidth();
		double height = g2d.getClip().getBounds2D().getHeight();
		
		g2d.setColor(player.getLives() > 2 ? Color.GREEN : Color.RED);
		g2d.setFont(new Font("Sans-Serif", Font.BOLD, 18));
		g2d.drawString("Lives: " + player.getLives(), (int) (width - 100), (int) (height - 10));
	}
}
