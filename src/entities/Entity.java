package entities;

import static utilz.HelpMethods.CanMoveHere;
import static utilz.constants.Directions.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;

public abstract class Entity {

	protected float x, y;
	protected int width, height;
	protected Rectangle2D.Float hitbox,playerbox,topHitBox, bottomHitbox;
	protected int aniTick, aniIndex;
	protected int state;
	protected float airSpeed;
	protected boolean inAir = false;
	protected int maxHealth;
	protected int currentHealth;
	protected Rectangle2D.Float attackBox;
	protected float walkSpeed = 1.0f * Game.SCALE;
	
	protected int pushBackDir;
	protected float pushDrawOffset;
	protected int pushBackOffsetDir = UP;


	public Entity(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

	}
	
	protected void updatePushBackDrawOffset() {
		float speed = 0.95f;
		float limit = -30f;

		if (pushBackOffsetDir == UP) {
			pushDrawOffset -= speed;
			if (pushDrawOffset <= limit)
				pushBackOffsetDir = DOWN;
		} else {
			pushDrawOffset += speed;
			if (pushDrawOffset >= 0)
				pushDrawOffset = 0;
		}
	}
	
	protected void pushBack(int pushBackDir, int[][] lvlData, float speedMulti) {
		float xSpeed = 0;
		if (pushBackDir == LEFT)
			xSpeed = -walkSpeed;
		else
			xSpeed = walkSpeed;

		if (CanMoveHere(hitbox.x + xSpeed * speedMulti, hitbox.y, hitbox.width, hitbox.height, lvlData))
			hitbox.x += xSpeed * speedMulti;
	}
	
//	protected void pushPlayerBack(int pushBackDir, int[][] lvlData, float speedMulti) {
//		float xSpeed = 0;
//		if (pushBackDir == LEFT)
//			xSpeed = -walkSpeed;
//		else
//			xSpeed = walkSpeed;
//
//		if (CanMoveHere(playerbox.x + xSpeed * speedMulti, playerbox.y, playerbox.width, playerbox.height, lvlData) && CanMoveHere(topHitBox.x + xSpeed * speedMulti, topHitBox.y, topHitBox.width, topHitBox.height, lvlData) && CanMoveHere(bottomHitbox.x + xSpeed * speedMulti, bottomHitbox.y, bottomHitbox.width, bottomHitbox.height, lvlData))
//			playerbox.x += xSpeed * speedMulti;
//			topHitBox.x += xSpeed * speedMulti;
//			bottomHitbox.x += xSpeed * speedMulti;
//	}
	
	protected void drawHitBoxes(Graphics g, int lvlOffset) {
        // Top hitbox debugging
        g.setColor(java.awt.Color.RED);
        g.drawRect((int) topHitBox.x - lvlOffset, (int) topHitBox.y, (int) topHitBox.width, (int) topHitBox.height);

        // Bottom hitbox debugging
        g.setColor(java.awt.Color.GREEN);
        g.drawRect((int) bottomHitbox.x - lvlOffset, (int) bottomHitbox.y, (int) bottomHitbox.width, (int) bottomHitbox.height);
        
        // actual working hitbox for jumping
        g.setColor(java.awt.Color.pink);
        g.drawRect((int) playerbox.x - lvlOffset,(int) playerbox.y,(int) playerbox.width,(int) playerbox.height);

        
    }
	
	protected void drawAttackBox(Graphics g, int xlvlOffset) {
		g.setColor(Color.red);
		g.drawRect((int) attackBox.x - xlvlOffset,(int)  attackBox.y, (int) attackBox.width, (int) attackBox.height);
		
	}

    protected void initPlayerBoxes(float width, float height) {
        topHitBox = new Rectangle2D.Float(x,  y + height*2 , (int)(width * Game.SCALE), (int)(height * Game.SCALE));
        bottomHitbox = new Rectangle2D.Float(x, y + height*2 , (int)(width * Game.SCALE), (int)(height * Game.SCALE));
        playerbox = new Rectangle2D.Float(x, y, (int)(width * Game.SCALE), (int)(height * Game.SCALE));
    }
    
    protected void drawHitbox(Graphics g, int xLvlOffset) {
		g.setColor(Color.PINK);
		g.drawRect((int) hitbox.x - xLvlOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}
    
    protected void initHitbox(int width, int height) {
		hitbox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
	}
    
    public int getState() {
    	return state;
    }
    
    public int getAniIndex() {
    	return aniIndex;
    }
    
    protected void newState(int state) {
    	this.state = state;
    	aniTick = 0;
    	aniIndex = 0;
    	
    }
    
    public Rectangle2D.Float getHitBox(){
    	return hitbox;
    }
    
    public Rectangle2D.Float getPlayerBox(){
    	return playerbox;
    }

    public Rectangle2D.Float getTopHitBox() {
        return topHitBox;
    }

    public Rectangle2D.Float getBottomHitBox() {
        return bottomHitbox;
    }
}