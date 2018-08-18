package de.ec.dev.gameobject;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import de.ec.Drawable;
import de.ec.GameDisplay;
import de.ec.InputListener;

public class DevGame implements Drawable
{
	public static void main(String[] args)
	{
		new DevGame();
	}
	
	public static final String WORD = "awesome".toUpperCase();
	
	private boolean acceptAllLetters = true;

	private Random random;
	private GameDisplay display;
	private final Drawable gameDrawable;
	private boolean gameRunning;
	private Player player;
	private InputListener il;
	
	private Set<GameObject> gameObjects;
	private Set<GameObject> removeObjects;
	
	private List<Drawable> drawables;
	private List<Updateable> updateables;
	private List<Logic> logics;
	
	public DevGame()
	{
		random = new Random();
		gameObjects = new HashSet<>();
		removeObjects = new HashSet<>();
		drawables = new ArrayList<>();
		updateables = new ArrayList<>();
		logics = new ArrayList<>();
		
		player = new Player(5);
		player.move(400, 580);
		addGameObject(player);
		addGameObject(new LivesDisplay(player));
		display = new GameDisplay(800, 600);
		
		logics.add(new CollisionLogic());
		logics.add(new EndGameLogic());
		logics.add(new LetterAttackLogic(3));
		
		gameDrawable = this;
		gameRunning = true;

		il = display.getInputListener();
		il.setAcceptConsecutivePresses(false);

		new Thread(this::gameLoop).start();
	}

	private void addGameObject(GameObject obj)
	{
		gameObjects.add(obj);
		drawables.add(obj);
		updateables.add(obj);
	}
	
	private void removeGameObject(GameObject obj)
	{
		removeObjects.add(obj);
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
		while(il.hasKeyEvents())
		{
			String letter = il.nextKeyEvent();
			
			if(letter.length() != 1)
			{
				continue;
			}
			
			if(!acceptAllLetters && !WORD.contains(letter))
			{
				continue;
			}
			
			Vector2D target = il.getMousePosition();
			
			addGameObject(
				new ProjectileLetter(
					new Vector2D(
						target.getX() - player.getX(),
						target.getY() - player.getY()),
					letter.charAt(0)));
		}
	}

	private void update(long delta)
	{
		updateables.forEach(g -> g.update(delta));
	}

	private void doLogic()
	{
		logics.forEach(Logic::doLogic);
		
		removeObjects.forEach(gameObjects::remove);
		removeObjects.forEach(drawables::remove);
		removeObjects.forEach(updateables::remove);
		removeObjects.clear();
	}

	private void endGame()
	{
		JOptionPane.showMessageDialog(null, "You lose", "Game ends", JOptionPane.INFORMATION_MESSAGE);
	
		System.exit(0);
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

		drawables.forEach(g -> g.draw(g2d));
	}
	
	private class CollisionLogic implements Logic
	{
		@Override
		public void doLogic()
		{
			Set<ProjectileLetter> projectileLetters = gameObjects.stream()
					.filter(ProjectileLetter.class::isInstance)
					.map(ProjectileLetter.class::cast)
					.collect(Collectors.toSet());
			Set<Letter> letters = gameObjects.stream()
					.filter(Letter.class::isInstance)
					.filter(l -> !projectileLetters.contains(l))
					.map(Letter.class::cast)
					.collect(Collectors.toSet());
			
			for(Letter l : letters)
			{
				for(ProjectileLetter p : projectileLetters)
				{
					if(l.collides(p)
						&& l.getLetter() == p.getLetter())
					{
						removeGameObject(p);
						removeGameObject(l);
					}
				}
			}
			
			for(Letter l : letters)
			{
				if(l.collides(player))
				{
					player.takeLife();
					removeGameObject(l);
				}
			}
			
			for(ProjectileLetter p : projectileLetters)
			{
				if(p.getX() < 0
					|| p.getX() > display.getDrawAreaWidth()
					|| p.getY() < 0
					|| p.getY() > display.getDrawAreaHeight())
				{
					removeGameObject(p);
				}
			}
		}
	}
	
	private class EndGameLogic implements Logic
	{
		@Override
		public void doLogic()
		{
			if (player.getLives() < 1)
			{
				endGame();
			}
		}
	}
	
	private class LetterAttackLogic implements Logic
	{
		private int attackers;
		
		public LetterAttackLogic(int attackers)
		{
			this.attackers = attackers;
		}
		
		@Override
		public void doLogic()
		{
			if(gameObjects.stream()
				.filter(Letter.class::isInstance)
				.filter(o -> !(o instanceof ProjectileLetter))
				.count() < attackers)
			{
				float r = random.nextFloat();
				
				char c = WORD.charAt(random.nextInt(WORD.length() - 1));
				
				if(r < 0.33f)
				{
					addGameObject(new Letter(c, player, 0, display.getDrawAreaHeight() * random.nextFloat()));
				}
				else if(r < 0.66f)
				{
					addGameObject(new Letter(c, player, display.getDrawAreaWidth() * random.nextFloat(), 0));
				}
				else
				{
					addGameObject(new Letter(c, player, display.getDrawAreaWidth(),  display.getDrawAreaHeight() * random.nextFloat()));
				}
			}
		}
	}
}
