package de.ec.dev.gameobject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import de.ec.Drawable;
import de.ec.GameDisplay;

public class DevGame implements Drawable
{
	private static final char[] LETTERS = {'A', 'W', 'E', 'S', 'O', 'M', 'E'};
	
	public static void main(String[] args)
	{
		new DevGame();
	}

	private Random random;
	private GameDisplay display;
	private final Drawable gameDrawable;
	private boolean gameRunning;
	private Set<GameObject> gameObjects;
	private Set<GameObject> removeObjects;
	private Player player;
	
	private long lastA;
	private long lastW;
	private long lastE;
	private long lastS;
	private long lastO;
	private long lastM;
	
	private boolean a;
	private boolean w;
	private boolean e;
	private boolean s;
	private boolean o;
	private boolean m;

	public DevGame()
	{
		random = new Random();
		gameObjects = new HashSet<>();
		removeObjects = new HashSet<>();
		player = new Player();
		player.move(400, 580);
		gameObjects.add(player);
		display = new GameDisplay(800, 600);
		gameDrawable = this;
		
		lastA = 0;
		lastW = 0;
		lastE = 0;
		lastS = 0;
		lastO = 0;
		lastM = 0;
		
		a = false;
		w = false;
		e = false;
		s = false;
		o = false;
		m = false;

		gameRunning = true;

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher()
		{
			
			@Override
			public boolean dispatchKeyEvent(KeyEvent ev)
			{
				if(ev.getID() != KeyEvent.KEY_PRESSED)
				{
					return false;
				}
				
				if(ev.getKeyCode() == KeyEvent.VK_A && (System.currentTimeMillis() - lastA > 1000))
				{
					a = true;
					lastA = System.currentTimeMillis();
				}
				
				if(ev.getKeyCode() == KeyEvent.VK_W && (System.currentTimeMillis() - lastW > 1000))
				{
					w = true;
					lastW = System.currentTimeMillis();
				}
				
				if(ev.getKeyCode() == KeyEvent.VK_E && (System.currentTimeMillis() - lastE > 1000))
				{
					e = true;
					lastE = System.currentTimeMillis();
				}
				
				if(ev.getKeyCode() == KeyEvent.VK_S && (System.currentTimeMillis() - lastS > 1000))
				{
					s = true;
					lastS = System.currentTimeMillis();
				}
				
				if(ev.getKeyCode() == KeyEvent.VK_O && (System.currentTimeMillis() - lastO > 1000))
				{
					o = true;
					lastO = System.currentTimeMillis();
				}
				
				if(ev.getKeyCode() == KeyEvent.VK_M && (System.currentTimeMillis() - lastM > 1000))
				{
					m = true;
					lastM = System.currentTimeMillis();
				}

				return false;
			}
		});

		new Thread(this::gameLoop).start();
	}

	private void gameLoop()
	{
		long lastTime = 0;
		lastTime = getDeltaMs(lastTime);

		try
		{
			display.setVisible(true);

			while (gameRunning)
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
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				if (display.isCloseRequested())
				{
					gameRunning = false;
				}
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}

		display.destroy();
		System.exit(0);
	}

	private void checkInput()
	{
		if(a)
		{
			a = false;
			
			Point target = display.scanMouse();
			
			gameObjects.add(new ProjectileLetter(new Vector2D(target.getX() - player.getX(),
					target.getY() - player.getY()), 'A'));
		}

		if(w)
		{
			w = false;
			
			Point target = display.scanMouse();
			
			gameObjects.add(new ProjectileLetter(new Vector2D(target.getX() - player.getX(),
					target.getY() - player.getY()), 'W'));
		}

		if(e)
		{
			e = false;
			
			Point target = display.scanMouse();
			
			gameObjects.add(new ProjectileLetter(new Vector2D(target.getX() - player.getX(),
					target.getY() - player.getY()), 'E'));
		}

		if(s)
		{
			s = false;
			
			Point target = display.scanMouse();
			
			gameObjects.add(new ProjectileLetter(new Vector2D(target.getX() - player.getX(),
					target.getY() - player.getY()), 'S'));
		}

		if(o)
		{
			o = false;
			
			Point target = display.scanMouse();
			
			gameObjects.add(new ProjectileLetter(new Vector2D(target.getX() - player.getX(),
					target.getY() - player.getY()), 'O'));
		}

		if(m)
		{
			m = false;
			
			Point target = display.scanMouse();
			
			gameObjects.add(new ProjectileLetter(new Vector2D(target.getX() - player.getX(),
					target.getY() - player.getY()), 'M'));
		}
	}

	private void update(long delta)
	{
		gameObjects.forEach(obj ->
		{
			if(obj instanceof ProjectileLetter)
			{
				moveProjectile((ProjectileLetter) obj, delta);
			}
			else if (obj instanceof Letter)
			{
				moveLetter((Letter) obj, delta);
			}
		});

		removeObjects.forEach(obj ->
		{
			gameObjects.remove(obj);
		});

		removeObjects.clear();

		if (gameObjects.stream()
				.filter(o -> o instanceof Letter && !(o instanceof ProjectileLetter))
				.collect(Collectors.toList()).size() < 1)
		{
			
			float r = random.nextFloat();
			
			char c = LETTERS[random.nextInt(LETTERS.length - 1)];
			
			if(r < 0.33f)
			{
				gameObjects.add(new Letter(c, 0, display.getDrawAreaHeight() * random.nextFloat()));
			}
			else if(r < 0.66f)
			{
				gameObjects.add(new Letter(c, display.getDrawAreaWidth() * random.nextFloat(), 0));
			}
			else
			{
				gameObjects.add(new Letter(c, display.getDrawAreaWidth(),  display.getDrawAreaHeight() * random.nextFloat()));
			}
		}
	}

	private void moveProjectile(ProjectileLetter projectile, long delta)
	{
		projectile.move(projectile.getDirection().getX() * delta / 5d, projectile.getDirection().getY() * delta / 5d);
		
		for(GameObject obj : gameObjects)
		{
			if(!(obj instanceof Letter) || obj instanceof ProjectileLetter)
			{
				continue;
			}
			
			if(Math.abs(projectile.getX() - obj.getX()) < 20
					&& Math.abs(projectile.getY() - obj.getY()) < 20
					&& ((Letter) obj).getLetter() == projectile.getLetter())
			{
				removeObjects.addAll(Arrays.asList(projectile, obj));
			}
		}
//		
		if(projectile.getX() < 0 || projectile.getX() > display.getDrawAreaWidth()
				|| projectile.getY() < 0 || projectile.getY() > display.getDrawAreaHeight())
		{
			removeObjects.add(projectile);
		}
	}

	private void moveLetter(Letter letter, long delta)
	{
		Vector2D direction = new Vector2D(player.getX() - letter.getX(), player.getY() - letter.getY());

		if (direction.getLength() < 1)
		{
			if (player.getLives() == 1)
			{
				endGame();
			}

			player.takeLife();

			removeObjects.add(letter);
		}

		Vector2D normalized = direction.getNormalized();

		letter.move(normalized.getX() * delta / 5d, normalized.getY() * delta / 5d);
	}

	private void endGame()
	{
		JOptionPane.showMessageDialog(null, "You lose", "Game ends", JOptionPane.INFORMATION_MESSAGE);

		System.exit(0);
	}

	private void doLogic()
	{
		// TODO Auto-generated method stub

	}

	private int getDeltaMs(long lastTime)
	{
		long now = System.nanoTime() / 1000000;
		return (int) (now - lastTime);
	}

	@Override
	public void draw(Graphics2D g2d)
	{
		double width = g2d.getClip().getBounds2D().getWidth();
		double height = g2d.getClip().getBounds2D().getHeight();

		g2d.clearRect(0, 0, (int) width, (int) height);

		gameObjects.forEach(g -> g.draw(g2d));

		g2d.setColor(player.getLives() > 2 ? Color.GREEN : Color.RED);
		g2d.setFont(new Font("Sans-Serif", Font.BOLD, 18));
		g2d.drawString("Lives: " + player.getLives(), (int) (width - 100), (int) (height - 10));
	}
}
