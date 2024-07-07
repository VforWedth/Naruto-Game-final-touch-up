package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import utilz.LoadSave;
import gamestates.Playing;
import levels.Level;
import static utilz.constants.EnemyConstants.*;

public class EnemyManager {
	
	private Playing playing;
	private BufferedImage[][] crabbyArr, pinkstarArr, sharkArr;
	private Level currentLevel;
	

	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
		
	}
	
	public void loadEnemies(Level level) {
		this.currentLevel = level;
	}



	public void update(int[][] lvlData) {
		boolean isAnyActive = false;
		for (Crabby c : currentLevel.getCrabs())
			if (c.isActive()) {
				c.update(lvlData, playing);
				isAnyActive = true;
			}
		
		for (Pinkstar p : currentLevel.getPinkstars())
			if (p.isActive()) {
				p.update(lvlData, playing);
				isAnyActive = true;
			}
		
		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				s.update(lvlData, playing);
				isAnyActive = true;
			}
		
		if(!isAnyActive) 
			playing.setLevelCompleted(true);
	}
	
	public void draw(Graphics g, int xlvlOffset) {
		drawCrabs(g, xlvlOffset);
		drawPinkstars(g, xlvlOffset);
		drawSharks(g, xlvlOffset);
	}
	private void drawSharks(Graphics g, int xLvlOffset) {
		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				g.drawImage(sharkArr[s.getState()][s.getAniIndex()], (int) s.getHitBox().x - xLvlOffset - SHARK_DRAWOFFSET_X + s.flipX(),
						(int) s.getHitBox().y - SHARK_DRAWOFFSET_Y + (int) s.getPushDrawOffset(), SHARK_WIDTH * s.flipW(), SHARK_HEIGHT, null);
				//s.drawHitbox(g, xLvlOffset);
				//s.drawAttackBox(g, xLvlOffset); 
			}
	}

	private void drawPinkstars(Graphics g, int xLvlOffset) {
		for (Pinkstar p : currentLevel.getPinkstars())
			if (p.isActive()) {
				g.drawImage(pinkstarArr[p.getState()][p.getAniIndex()], (int) p.getHitBox().x - xLvlOffset - PINKSTAR_DRAWOFFSET_X + p.flipX(),
						(int) p.getHitBox().y - PINKSTAR_DRAWOFFSET_Y + (int) p.getPushDrawOffset(), PINKSTAR_WIDTH * p.flipW(), PINKSTAR_HEIGHT, null);
				//p.drawHitbox(g, xLvlOffset);
			}
	}
	
	private void drawCrabs(Graphics g, int xlvlOffset) {
		for(Crabby c: currentLevel.getCrabs()) {	
			if(c.isActive()) {
				g.drawImage(crabbyArr[c.getState()][c.aniIndex], (int) c.getHitBox().x - xlvlOffset - 	CRABBY_DRAWOFFSET_X + c.flipX() , (int) c.getHitBox().y - CRABBY_DRAWOFFSET_Y, CRABBY_WIDTH * c.flipW(), CRABBY_HEIGHT, null);
				//c.drawHitbox(g, xlvlOffset);
				//c.drawAttackBox(g, xlvlOffset);
			}
		}
		
	}
	
	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for (Crabby c : currentLevel.getCrabs())
			if (c.isActive())
				if (c.getState() != DEAD && c.getState() != HIT)
					if (attackBox.intersects(c.getHitBox())) {
						c.hurt(20);
						return;
					}
		
		for (Pinkstar p : currentLevel.getPinkstars())
			if (p.isActive()) {
				if (p.getState() == ATTACK && p.getAniIndex() >= 3)
					return;
				else {
					if (p.getState() != DEAD && p.getState() != HIT)
						if (attackBox.intersects(p.getHitBox())) {
							p.hurt(20);
							return;
						}
				}
			}
		
		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				if (s.getState() != DEAD && s.getState() != HIT)
					if (attackBox.intersects(s.getHitBox())) {
						s.hurt(20);
						return;
					}
			}
	}

	private void loadEnemyImgs() {
		crabbyArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE), 9, 5, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
		pinkstarArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.PINKSTAR_ATLAS), 8, 5, PINKSTAR_WIDTH_DEFAULT, PINKSTAR_HEIGHT_DEFAULT);
		sharkArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.SHARK_ATLAS), 8, 5, SHARK_WIDTH_DEFAULT, SHARK_HEIGHT_DEFAULT);
	}
	
	private BufferedImage[][] getImgArr(BufferedImage atlas, int xSize, int ySize, int spriteW, int spriteH) {
		BufferedImage[][] tempArr = new BufferedImage[ySize][xSize];
		for (int j = 0; j < tempArr.length; j++)
			for (int i = 0; i < tempArr[j].length; i++)
				tempArr[j][i] = atlas.getSubimage(i * spriteW, j * spriteH, spriteW, spriteH);
		return tempArr;
	}
	
	public void resetAllEnemies() {
		for(Crabby c:  currentLevel.getCrabs()) 
			c.resetEnemy();
		for (Pinkstar p : currentLevel.getPinkstars())
			p.resetEnemy();
		for (Shark s : currentLevel.getSharks())
			s.resetEnemy();
	}
}