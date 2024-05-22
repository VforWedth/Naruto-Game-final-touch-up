package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Gamestate;
import main.Game;
import utilz.LoadSave;

public class LevelManager {

	private Game game;
	private BufferedImage[] levelSprite;
	private BufferedImage[] waterSprite;
	private ArrayList<Level> levels;
	private int lvlIndex = 0;

	public LevelManager(Game game) {
		this.game = game;
		importOutsideSprites();
		//createWater();
		levels = new ArrayList<>();
		buildAllLevels();
	}
//	private void createWater() {
//		waterSprite = new BufferedImage[5];
//		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.WATER_TOP);
//		for (int i = 0; i < 4; i++)
//			waterSprite[i] = img.getSubimage(i * 32, 0, 32, 32);
//		waterSprite[4] = LoadSave.GetSpriteAtlas(LoadSave.WATER_BOTTOM);
//	}
	
	public void loadNextLevel() {
		lvlIndex++;
		if(lvlIndex >= levels.size()) {
			lvlIndex = 0;
			System.out.println("No more levels! Game Completed!");
			Gamestate.state = Gamestate.MENU;
		}
		
		Level newLevel = levels.get(lvlIndex);
		game.getPlaying().getEnemyManager().loadEnemies(newLevel);
		game.getPlaying().getPlayer().loadLvlData(newLevel.getLevelData());
		game.getPlaying().setMaxLvlOffset(newLevel.getLvlOffset());
	}

	private void buildAllLevels() {
		BufferedImage[] allLevels = LoadSave.GetAllLevels();
		for(BufferedImage img : allLevels) {
			levels.add(new Level(img));
		}
		
	}

	private void importOutsideSprites() {
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		levelSprite = new BufferedImage[48];
		for (int j = 0; j < 4; j++)
			for (int i = 0; i < 12; i++) {
				int index = j * 12 + i;
				levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
			}
	}

	public void draw(Graphics g, int lvlOffset) {
		for (int j = 0; j < Game.TILES_IN_HEIGHT; j++)
			for (int i = 0; i < levels.get(lvlIndex).getLevelData()[0].length ; i++) {
				int index = levels.get(lvlIndex).getSpriteIndex(i, j);
				g.drawImage(levelSprite[index], Game.TILES_SIZE * i - lvlOffset, Game.TILES_SIZE * j, Game.TILES_SIZE, Game.TILES_SIZE, null);
			}
	}

	public void update() {

	}

	public Level getCurrentLevel() {
		return levels.get(lvlIndex);
	}
	
	public int getAmountOfLevels() {
		return levels.size();
	}
	
	public int getLevelIndex() {
		return lvlIndex;
	}

}