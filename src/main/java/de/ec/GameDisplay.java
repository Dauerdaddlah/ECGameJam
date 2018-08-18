package de.ec;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;

public class GameDisplay
{
	/** frame the game is displayed on */
	Frame frame;

	/** Canvas inside the frame to display the game */
	Canvas canvas;

	/** {@link GraphicsEnvironment} for getting the {@link GraphicsConfiguration} */
	private GraphicsEnvironment ge;
	/** {@link GraphicsConfiguration} for creating {@link #backbuffer} */
	private GraphicsConfiguration gc;

	/** The {@link BufferStrategy} to display the game in double-buffer-mode */
	private BufferStrategy bs;

	/**
	 * the image on which will be drawn and which will afterwards be drawn on
	 * the screen
	 */
	private VolatileImage backbuffer;

	/** Whether the user wants to close the display */
	private boolean closeRequested;

	/** Whether the display is currently drawing */
	private boolean rendering;
	
	private InputListener inputListener;

	/**
	 * Create a gamedisplay, which drawing area is limited by the given size
	 * 
	 * @param width
	 *            The width for the drawing area
	 * @param height
	 *            The height for the drawing area
	 */
	public GameDisplay(int width, int height)
	{
		this(width, height, "GameDisplay");
	}

	public GameDisplay(int width, int height, String title)
	{
		super();

		canvas = new Canvas();
		closeRequested = false;
		rendering = false;
		inputListener = new InputListener(this);

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		if(width > d.width || width < 0)
		{
			width = d.width;
		}

		if(height > d.height || height < 0)
		{
			height = d.height;
		}

		canvas.setPreferredSize(new Dimension(width, height));

		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

		frame = new Frame(title);

		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				closeRequested = true;
			}
		});

		frame.addWindowFocusListener(new WindowFocusListener()
		{

			@Override
			public void windowLostFocus(WindowEvent e)
			{

			}

			@Override
			public void windowGainedFocus(WindowEvent e)
			{
				canvas.requestFocusInWindow();
			}
		});

		frame.setLayout(new BorderLayout());
		frame.add(canvas, BorderLayout.CENTER);
		frame.pack();
		frame.setIgnoreRepaint(true);
		// frame.setResizable(false);

		frame.setLocation((int)((d.getWidth() - width) / 2),
			(int)((d.getHeight() - height) / 2));

		canvas.createBufferStrategy(2);
		bs = canvas.getBufferStrategy();

		canvas.requestFocus();

		setDrawAreaSize(width, height);
	}

	public void startRendering(Drawable d)
	{
		if(!isVisible())
		{
			// TODO exception
			return;
		}

		if(rendering)
		{
			return;
		}

		rendering = true;
		Graphics2D g2d = backbuffer.createGraphics();
		g2d.clipRect(0, 0, backbuffer.getWidth(), backbuffer.getHeight());

		d.draw(g2d);

		g2d.dispose();

		Graphics g = bs.getDrawGraphics();
		g.drawImage(backbuffer, 0, 0, canvas.getWidth(), canvas.getHeight(),
			null);
		g.dispose();

		bs.show();

		rendering = false;
	}

	public boolean isRendering()
	{
		return rendering;
	}

	public void setVisible(boolean visible)
	{
		if(rendering)
		{
			// TODO exception
			return;
		}
		frame.setVisible(visible);
	}

	public boolean isVisible()
	{
		return frame.isVisible();
	}

	public void setTitle(String title)
	{
		frame.setTitle(title);
	}

	public String getTitle()
	{
		return frame.getTitle();
	}

	public boolean isCloseRequested()
	{
		return closeRequested;
	}

	public void setDrawAreaSize(int width, int height)
	{
		if(isRendering())
		{
			// TODO exception
			return;
		}

		backbuffer = gc.createCompatibleVolatileImage(width, height,
			Transparency.TRANSLUCENT);
	}

	public int getDrawAreaWidth()
	{
		return backbuffer.getWidth();
	}

	public int getDrawAreaHeight()
	{
		return backbuffer.getHeight();
	}

	public void setFrameResizable(boolean resizable)
	{
		frame.setResizable(true);
	}

	public void destroy()
	{
		frame.dispose();
	}
	
	public InputListener getInputListener()
	{
		return inputListener;
	}
}
