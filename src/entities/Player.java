package entities;

import static utilz.constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
import static utilz.constants.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class Player extends Entity {
	private BufferedImage[][] animations;
	private boolean moving = false, attacking = false;
	private boolean left, right, jump;
	private int[][] lvlData;
	private float xDrawOffset = 36 * Game.SCALE;
	private float yDrawOffset = 59 * Game.SCALE;
	private static Scanner input = new Scanner(System.in);
	private static int num;
	private static String source = null;
	private float jumpSpeed = -2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.3f * Game.SCALE;
	
	//statusBarUI
	private BufferedImage statusBarImg;
	
	private int statusBarWidth = (int) (192 * Game.SCALE);
	private int statusBarHeight = (int) (58 * Game.SCALE);
	private int statusBarX = (int) (10 * Game.SCALE);
	private int statusBarY = (int) (10 * Game.SCALE);

	private int healthBarWidth = (int) (150 * Game.SCALE);
	private int healthBarHeight = (int) (4 * Game.SCALE);
	private int healthBarXStart = (int) (34 * Game.SCALE);
	private int healthBarYStart = (int) (14 * Game.SCALE);
	
	private int healthWidth = healthBarWidth;
	
	private int flipX = 0;
	private int flipW = 1;
	
	private boolean attackChecked;
	
	private Playing playing;

	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		this.state = IDLE;
		this.maxHealth = 100;
		this.currentHealth = maxHealth;
		this.walkSpeed = Game.SCALE * 1.0f;
		loadAnimations();
		initHitBoxes( 30 , 22);
		initAttackBox();
	}
	
	public void setSpawn(Point spawn) {
		this.x = spawn.x;
		this.y = spawn.y;
		hitbox.x = x;
		hitbox.y = y;
	}
	
	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (23 * Game.SCALE), (int) (31 * Game.SCALE));
		
	}

	public static String ChooseCharacter() {
		System.out.println("Choose the player: \n1)Uzumaki Naruto\n2)Uchiha Sasuke\n3)Sakura Haruno\n4)Hatake Kakashi");
		num = input.nextInt();
		switch(num) {
		case 1:
			source = "sprite1.png";
			break;
		case 2:
			source = "sprite2.png";
			break;
		case 3:
			source = "sprite3.png";
			break;
		case 4:
			source = "sprite4.png";
		}
		return source;
	}
	
	public void update() {
		updateHealthBar();
		if(currentHealth <= 0) {
			if(state != DEAD) {
				state = DEAD;
				aniTick = 0;
				aniIndex = 0;
				playing.setPlayerDying(true);
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
			}else if(aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
				playing.setGameOver(true);
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAME_OVER);
			}else {
				updateAnimationTick();
			}
			return;
		}
		
		updateAttackBox();
		updatePos();
		
		if(attacking)
			checkAttack();
		
		updateAnimationTick();
		setAnimation();
	}

	private void checkAttack() {
		if(attackChecked || aniIndex != 1)
			return;
		attackChecked = true;
		playing.checkEnemyHit(attackBox);
		playing.getGame().getAudioPlayer().playAttackSound();
	}

	private void updateAttackBox() {
		if(right) {
			attackBox.x = hitbox.x + hitbox.width ;
		}else if(left) {
			attackBox.x = hitbox.x - hitbox.width ;
		}
		attackBox.y = hitbox.y ;
		
	}

	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
		
	}

	public void render(Graphics g, int lvlOffset) {
		g.drawImage(animations[state][aniIndex], (int)(hitbox.x  - xDrawOffset) - lvlOffset + flipX, (int)(hitbox.y - yDrawOffset) , width * flipW, height , null);
		//drawHitBoxes(g, lvlOffset);
		//drawAttackBox(g, lvlOffset);
		drawUI(g);
	}

	private void drawUI(Graphics g) {
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
		g.setColor(Color.red);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
	}

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(state)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
			}

		}

	}

	private void setAnimation() {
		int startAni = state;

		if (moving)
			state = RUNNING;
		else
			state = IDLE;

		if (attacking) {
			state = HIT;
			if(startAni != HIT) {
				aniIndex = 1;
				aniTick = 0;
				return;
			}
		}
		if(jump)
			state = JUMP;

		if (startAni != state)
			resetAniTick();
	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}

	private void updatePos() {
		moving = false;

		if (jump)
			jump();
//		if (!left && !right && !inAir)
//			return;
		
		if(!inAir) {
			if((!left && !right) || (right && left)) {
				return ;
			}
		}

		float xSpeed = 0;

		if (left) {
			xSpeed -= walkSpeed;
			flipX = width;
			flipW = -1;
		}
		if (right) {
			xSpeed += walkSpeed;
			flipX = 0;
			flipW = 1;
		}
		if (!inAir)
			if (!IsEntityOnFloor(hitbox, lvlData))
				inAir = true;

		if (inAir) {
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData) && CanMoveHere(bottomHitbox.x, bottomHitbox.y + airSpeed, bottomHitbox.width, bottomHitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				topHitBox.y += airSpeed;
				bottomHitbox.y += airSpeed;
				airSpeed += GRAVITY;
				updateXPos(xSpeed);
			} else {
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}

		} else
			updateXPos(xSpeed);
		
		hitbox.x = bottomHitbox.x;
        hitbox.y = bottomHitbox.y + bottomHitbox.height;
        bottomHitbox.x = topHitBox.x;
        bottomHitbox.y = topHitBox.y + topHitBox.height;
		moving = true;
	} 

	private void jump() {
		if(inAir) {
			return;
		}
		playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
		inAir = true;
		airSpeed = jumpSpeed;
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
		
	}

	private void updateXPos(float xSpeed) {
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData) && CanMoveHere(topHitBox.x + xSpeed, topHitBox.y, topHitBox.width, topHitBox.height, lvlData) && CanMoveHere(bottomHitbox.x + xSpeed, bottomHitbox.y, bottomHitbox.width, bottomHitbox.height, lvlData)) {
			hitbox.x += xSpeed;
			topHitBox.x += xSpeed;
			bottomHitbox.x += xSpeed;
			moving = true;
		}else {
			topHitBox.x = GetEntityXPosNextToWall(topHitBox, xSpeed);
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
			bottomHitbox.x = GetEntityXPosNextToWall(bottomHitbox, xSpeed);
		}
		
	}
	
	public void changeHealth(int value) {
		currentHealth += value;
		
		if(currentHealth <= 0) {
			currentHealth = 0;
			//gameOver();
		}else if(currentHealth >= maxHealth) {
			currentHealth = maxHealth;
		}
	}

	private void loadAnimations() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

		animations = new BufferedImage[8][6];
		for (int j = 0; j < animations.length; j++)
			for (int i = 0; i < animations[j].length; i++)
				animations[j][i] = img.getSubimage(i * 100, j * 100, 100, 100);
		
		statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

	}

	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
	}

	public void resetDirBooleans() {
		left = false;
		right = false;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}
	
	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}
	
	public void setJump(boolean jump) {
		this.jump = jump;
		
	}

	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		state = IDLE;
		currentHealth = maxHealth;
		 
		hitbox.x = x;
		hitbox.y = y;
		bottomHitbox.x = x;
		topHitBox.x = x;
		
		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}

}