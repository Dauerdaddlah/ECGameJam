package de.ec.dev.gameobject;

import java.awt.Graphics2D;

public class Player extends GameObject
{
	private int lives;
	private final String word;
	
	private String curWord;
	private int points;
	
	public Player(int lives, String word)
	{
		this.lives = lives;
		this.word = word;
		
		curWord = "";
	}
	
	public void takeLife()
	{
		lives -= 1;
	}
	
	public int getLives()
	{
		return lives;
	}
	
	public String getWord()
	{
		return word;
	}
	
	@Override
	public void draw(Graphics2D g2d)
	{
		g2d.fillRect((int) getX() - 10, (int) getY() - 10, 20, 20);
	}

	public void letterMet(char letter)
	{
		if(word.startsWith(curWord + letter))
		{
			curWord += letter;
			if(curWord.equals(word))
			{
				points += word.length();
				curWord = "";
			}
		}
	}
	
	public String getCurWord()
	{
		return curWord;
	}
	
	public int getPoints()
	{
		return points;
	}
}
