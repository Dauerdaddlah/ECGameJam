package de.ec.dev.gameobject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Deque;
import java.util.LinkedList;

import de.ec.GameDisplay;

public class HUD extends GameObject
{
	private final Player player;
	private final GameDisplay display;
	
	public HUD(Player player, GameDisplay display)
	{
		this.player = player;
		this.display = display;
	}
	
	@Override
	public void draw(Graphics2D g2d)
	{
		drawPoints(g2d);
		drawLives(g2d);
		drawWord(g2d);
	}
	
	private void drawPoints(Graphics2D g2d)
	{
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Sans-Serif", Font.BOLD, 18));
		g2d.drawString("Points: " + player.getPoints(), display.getDrawAreaWidth() - 100, 20);
	}

	private void drawLives(Graphics2D g2d)
	{
		g2d.setColor(player.getLives() > 2 ? Color.GREEN : Color.RED);
		g2d.setFont(new Font("Sans-Serif", Font.BOLD, 18));
		g2d.drawString("Lives: " + player.getLives(), display.getDrawAreaWidth() - 100, display.getDrawAreaHeight() - 10);
	}
	
	private void drawWord(Graphics2D g2d)
	{
		g2d.setFont(new Font("Sans-Serif", Font.BOLD, 30));
		g2d.setColor(Color.GRAY);
		g2d.drawString(player.getWord(), 10, display.getDrawAreaHeight() - 10);
		
		Deque<Color> colors = new LinkedList<>();
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		colors.add(Color.ORANGE);
		colors.add(Color.MAGENTA);
		colors.add(Color.RED);
		colors.add(Color.CYAN);
		colors.add(Color.PINK);
		
		String word = player.getWord();
		String curWord = player.getCurWord();
		
		for(int i = 0; i < word.length(); ++i)
		{
			Color c = colors.removeLast();
			colors.addFirst(c);
			g2d.setColor(c);
			if(curWord.length() >= word.length() - i)
			{
				g2d.drawString(player.getWord().substring(0, word.length() - i), 10, display.getDrawAreaHeight() - 10);
			}
		}
	}
}
