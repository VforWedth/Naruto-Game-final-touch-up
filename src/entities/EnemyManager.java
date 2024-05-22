package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import utilz.LoadSave;
import gamestates.Playing;
import levels.Level;
import utilz.LoadSave.*;
import static utilz.constants.EnemyConstants.*;

public class EnemyManager {
	
	private Playing playing;
	private BufferedImage[][] CrabbyArr;
	private ArrayList<Crabby> crabbies = new ArrayList<>(); 

	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
		
	}
	
	public void loadEnemies(Level level) {
		crabbies = level.getCrabs();
	}



	public void update(int[][] lvlData, Player player) {
		boolean isAnyActive = false;
		for(Crabby c : crabbies) 
			if(c.isActive()) {
				c.update(lvlData, player);
				isAnyActive = true;
			}
			if(!isAnyActive) 
				playing.setLevelCompleted(true);
	}
	
	public void draw(Graphics g, int xlvlOffset) {
		drawCrabs(g, xlvlOffset);
	}

	private void drawCrabs(Graphics g, int xlvlOffset) {
		for(Crabby c: crabbies) {	
			if(c.isActive()) {
				g.drawImage(CrabbyArr[c.getState()][c.aniIndex], (int) c.getHitBox().x - xlvlOffset - 	CRABBY_DRAWOFFSET_X + c.flipX() , (int) c.getHitBox().y - CRABBY_DRAWOFFSET_Y, CRABBY_WIDTH * c.flipW(), CRABBY_HEIGHT, null);
				c.drawHitbox(g, xlvlOffset);
				//c.drawAttackBox(g, xlvlOffset);
			}
		}
		
	}
	
	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for(Crabby c: crabbies) {
			if(c.isActive())
				if(attackBox.intersects(c.getHitBox())) {
					c.hurt(10);
					return;	
				}
		}
	}

	private void loadEnemyImgs() {
		CrabbyArr = new BufferedImage[5][9];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);
		for (int j = 0; j < CrabbyArr.length; j++)
			for (int i = 0; i < CrabbyArr[j].length; i++)
				CrabbyArr[j][i] = temp.getSubimage(i * CRABBY_WIDTH_DEFAULT, j * CRABBY_HEIGHT_DEFAULT, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
	}
	
	public void resetAllEnemies() {
		for(Crabby c: crabbies) {
			c.resetEnemy();
		}
	}
}