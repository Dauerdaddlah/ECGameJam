package de.ec;

import java.awt.Graphics2D;

public class Game implements Drawable
{
	public static void main(String[] args)
	{
		new Game();
	}
	
	private GameDisplay display;
	private final Drawable gameDrawable;
	private boolean gameRunning;
	
	public Game()
	{
		display = new GameDisplay(800, 600);
		gameDrawable = this;
		
		gameRunning = true;
		
		new Thread(this::gameLoop).start();
	}
	
	private void gameLoop()
	{
		long lastTime = 0;
		lastTime = getDeltaMs(lastTime);
		
		try
		{
			display.setVisible(true);
			
			while(gameRunning)
			{
				long delta = getDeltaMs(lastTime);
				
				lastTime += delta;
				
				checkInput();
				
				update(delta);
				
				doLogic();
				
				display.startRendering(gameDrawable);
				
				try
				{
					Thread.sleep(2);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				
				if(display.isCloseRequested())
				{
					gameRunning = false;
				}
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		
		display.destroy();
		System.exit(0);
	}
	
	private void checkInput()
	{
		// TODO Auto-generated method stub
		
	}

	private void update(long delta)
	{
		// TODO Auto-generated method stub
		
	}

	private void doLogic()
	{
		// TODO Auto-generated method stub
		
	}

	private int getDeltaMs(long lastTime)
	{
		long now = System.nanoTime() / 1000000;
		return (int)(now - lastTime);
	}
	
	@Override
	public void draw(Graphics2D g2d)
	{
		g2d.drawString("Our game for the Extra Credits Design Game Jam 2018", 100, 100);
	}
}
