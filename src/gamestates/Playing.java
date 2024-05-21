package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PausedOverlay;
import utilz.LoadSave;
import static utilz.constants.Environment.*;

public class Playing extends State implements Statemethods {
	
	private Player player;
	private LevelManager levelManager;
	private EnemyManager enemyManager;
	private PausedOverlay pauseOverlay;
	private GameOverOverlay gameOverOverlay;
	private LevelCompletedOverlay levelCompletedOverlay;
	private boolean paused = false;
	
	private int xlvlOffset;
	private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
	private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
	private int maxlvlOffsetX;
	
	private BufferedImage backgroundImg, konoha, cloud;
	private int[] cloudPos;
	private Random rand = new Random();
	
	private boolean gameOver;
	private boolean lvlCompleted;
	private boolean playerDying = false;
	
	public Playing(Game game) {
		super(game);
		initClasses();
		
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMAGE);
		konoha = LoadSave.GetSpriteAtlas(LoadSave.KONOHA_HOUSES);
		cloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
		
		cloudPos = new int[8];
		for(int i = 0; i < cloudPos.length; i++) {
			cloudPos[i] = rand.nextInt((int) (100 * Game.SCALE));
		}
		
		calclvlOffset();
		loadStartLevel();
	}
	
	public void loadNextlevel() {
		resetAll();
		levelManager.loadNextLevel();
		player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
	}
	
	private void loadStartLevel() {
		enemyManager.loadEnemies(levelManager.getCurrentLevel());
		
	}

	private void calclvlOffset() {
		maxlvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
		
	}

	private void initClasses() {
		levelManager = new LevelManager(game);
		enemyManager = new EnemyManager(this);
		player = new Player(200, 200, (int) (100 *Game.SCALE), (int) (100 * Game.SCALE), this);
		player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
		pauseOverlay = new PausedOverlay(this);
		gameOverOverlay = new GameOverOverlay(this);
		levelCompletedOverlay = new LevelCompletedOverlay(this);
	}

	@Override
	public void update() {
		
		if(paused) {
			pauseOverlay.update();
		}else if(lvlCompleted) {
			levelCompletedOverlay.update();
		}else if(gameOver){
			gameOverOverlay.update();
		}else if(playerDying) {
			player.update();
		}else if(!gameOver) {
			levelManager.update();
			player.update();
			enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
			CheckCloseToBorder();
		}
	}

	private void CheckCloseToBorder() {
		int playerX = (int) player.getHitbox().x;
		int diff = playerX - xlvlOffset;

		if (diff > rightBorder)
			xlvlOffset += diff - rightBorder;
		else if (diff < leftBorder)
			xlvlOffset += diff - leftBorder;

		if (xlvlOffset > maxlvlOffsetX)
			xlvlOffset = maxlvlOffsetX;
		else if (xlvlOffset < 0)
			xlvlOffset = 0;

	}


	@Override
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0,Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		
		drawBackground(g);
		
		levelManager.draw(g, xlvlOffset);
		player.render(g, xlvlOffset);
		enemyManager.draw(g, xlvlOffset);

		if (paused) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
			pauseOverlay.draw(g);
		}else if(gameOver) {
			gameOverOverlay.draw(g);
		}else if (lvlCompleted)
			levelCompletedOverlay.draw(g);
	}
	
	private void drawBackground(Graphics g) {
		for(int i = 0; i < 2; i++) {
			g.drawImage(konoha, 0 + i * KONOHA_WIDTH - (int) (xlvlOffset * 0.3), (int) (110 * Game.SCALE), KONOHA_WIDTH, KONOHA_HEIGHT, null);
		}
		
		for(int i = 0; i < cloudPos.length; i++) {
			g.drawImage(cloud, CLOUD_WIDTH * 3 * i - (int) (xlvlOffset * 0.7), cloudPos[i], CLOUD_WIDTH * 2 , CLOUD_HEIGHT * 2,  null);
		}
	}
	
	public void resetAll() {
		//reset player, enemy, everything
		gameOver = false;
		paused = false;
		lvlCompleted = false;
		playerDying = false;
		player.resetAll();
		enemyManager.resetAllEnemies();
	}
	
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	
	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		enemyManager.checkEnemyHit(attackBox);
	}

	public void mouseDragged(MouseEvent e) {
		if(!gameOver) {
			if(paused) {
				pauseOverlay.mouseDragged(e);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(!gameOver) 
			if (e.getButton() == MouseEvent.BUTTON1)
				player.setAttacking(true);
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pauseOverlay.mousePressed(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mousePressed(e);
		}else {
			gameOverOverlay.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pauseOverlay.mouseReleased(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mouseReleased(e);
		}else {
			gameOverOverlay.mouseReleased(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pauseOverlay.mouseMoved(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mouseMoved(e);
		}else {
			gameOverOverlay.mouseMoved(e);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(gameOver)
			gameOverOverlay.KeyPressed(e);
		else {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				player.setLeft(true);
				break;
			case KeyEvent.VK_D:
				player.setRight(true);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(true);
				break;
			case KeyEvent.VK_ESCAPE:
				paused = !paused;
				break;
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(!gameOver) {
			switch (e.getKeyCode()) {

			case KeyEvent.VK_A:
				player.setLeft(false);

			case KeyEvent.VK_D:
				player.setRight(false);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(false);
				break;
			}
		}	
	}
	
	public void setLevelCompleted(boolean levelCompleted) {
		this.lvlCompleted = levelCompleted;
		if(lvlCompleted)
			game.getAudioPlayer().lvlCompleted();
	}
	
	public void setMaxLvlOffset(int lvlOffset) {
		this.maxlvlOffsetX = lvlOffset;
	}
	
	public void unpauseGame() {
		paused = false;
	}
	
	public void windowFocusLost() {
		player.resetDirBooleans();
	}

	public Player getPlayer() {
		return player;
	}
	
	public EnemyManager getEnemyManager() {
		return enemyManager;
	}
	
	public void setPlayerDying(boolean playerDying) {
		this.playerDying = playerDying;
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

}
