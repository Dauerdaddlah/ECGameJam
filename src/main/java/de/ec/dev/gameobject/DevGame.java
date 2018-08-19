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
		
		player = new Player(5, WORD);
		player.move(400, 580);
		display = new GameDisplay(800, 600);
		addGameObject(player);
		addGameObject(new HUD(player, display));
		
		logics.add(new CollisionLogic());
		logics.add(new EndGameLogic());
		logics.add(new LetterAttackLogic(1));
		
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
			
			if(!acceptAllLetters && !player.getWord().contains(letter))
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
		g2d.clearRect(0, 0, display.getDrawAreaWidth(), display.getDrawAreaHeight());

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
						
						player.letterMet(l.getLetter());
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
			String projectiles = gameObjects.stream()
						.filter(Letter.class::isInstance)
						.filter(o -> !(o instanceof ProjectileLetter))
						.map(Letter.class::cast)
						.map(Letter::getLetter)
						.map(Object::toString)
						.collect(Collectors.joining());
			
			if(projectiles.length() < attackers)
			{
				float r = random.nextFloat();
				
				// get the word, in the order, in which the letters would be needed to come
				// i. e. if the word is awesome and we already have awe then the word will be changed to someawe
				String word = player.getWord();
				word = word.substring(player.getCurWord().length());
				word += player.getCurWord();
				
				// the letterPool is the String from which we choose the next letter by random
				// the pool will contain the least-needed letter just once, the pre-least neede twice, etc
				// i. e. if the word is someawe, the pool will be set to (order irrelevant)
				// sssssssoooooommmmmeeeeaaawwe
				String letterPool = "";
				for(int i = 0; i < word.length(); ++i)
				{
					for(int j = word.length(); j > i; --j)
					{
						letterPool += word.charAt(i);
					}
				}
				
				char c = letterPool.charAt(random.nextInt(letterPool.length() - 1));
				
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
