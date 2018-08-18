package de.ec;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import de.ec.dev.gameobject.Vector2D;

public class InputListener
{
	private final GameDisplay display;
	
	/** if true, inputs will be accepted, which are created by pressing and holding a key */
	private boolean acceptConsecutivePresses;
	
	private Set<String> keysPressed;
	
	private Deque<String> keyInputs;
	
	private Point mousePosition;
	
	public InputListener(GameDisplay display)
	{
		this.display = display;
		keysPressed = new HashSet<>();
		keyInputs = new LinkedBlockingDeque<>();
		
		this.display.canvas.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
			}
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				String letter = String.valueOf(e.getKeyChar()).toUpperCase();
				
				keysPressed.remove(letter);
			}
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				String letter = String.valueOf(e.getKeyChar()).toUpperCase();
				
				if(keysPressed.add(letter) || acceptConsecutivePresses)
				{
					synchronized (keyInputs)
					{
						keyInputs.addLast(letter);
					}
				}
			}
		});
		
		this.display.canvas.addMouseMotionListener(new MouseMotionListener()
		{
			
			@Override
			public void mouseMoved(MouseEvent e)
			{
				mousePosition = e.getPoint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				mousePosition = e.getPoint();
			}
		});
	}
	
	public void setAcceptConsecutivePresses(boolean acceptConsecutivePresses)
	{
		this.acceptConsecutivePresses = acceptConsecutivePresses;
	}
	
	public boolean isAcceptConsecutivePresses()
	{
		return acceptConsecutivePresses;
	}
	
	public boolean hasKeyEvents()
	{
		return !keyInputs.isEmpty();
	}
	
	public String nextKeyEvent()
	{
		return keyInputs.removeFirst();
	}
	
	public Vector2D getMousePosition()
	{
		// to avoid any further synchronization
		Point mousePosition = this.mousePosition;
		return new Vector2D(mousePosition.getX(), mousePosition.getY());
	}
}
