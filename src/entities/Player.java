package entities;

import static utilz.constants.PlayerConstants.*;
import static utilz.constants.PlayerConstants.HIT;
import static utilz.constants.Directions.*;
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
	private float fallSpeedAfterCollision = 0.4f * Game.SCALE;
	
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

	private int powerBarWidth = (int) (104 * Game.SCALE);
	private int powerBarHeight = (int) (2 * Game.SCALE);
	private int powerBarXStart = (int) (44 * Game.SCALE);
	private int powerBarYStart = (int) (34 * Game.SCALE);
	private int powerWidth = powerBarWidth;
	private int powerMaxValue = 200;
	private int powerValue = powerMaxValue;
	private int healthWidth = healthBarWidth;
	
	private int flipX = 0;
	private int flipW = 1;
	
	private int tileY = 0;
	
	private boolean powerAttackActive;
	private int powerAttackTick;
	private int powerGrowSpeed = 15;
	private int powerGrowTick;
	
	public static final int CHARACTER_NARUTO = 1;
	public static final int CHARACTER_SASUKE = 2;
	public static final int CHARACTER_SAKURA = 3;
	public static final int CHARACTER_KAKASHI = 4;
	public static int currentCharacter;
	private CharacterManager characterManager;

	
	private boolean attackChecked;
	
	private Playing playing;

	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		this.state = IDLE;
		this.maxHealth = 100;
		this.currentHealth = maxHealth;
		this.walkSpeed = Game.SCALE * 1.0f;
		this.characterManager = new CharacterManager();    //CharacterManager implement
		loadAnimations();
		initPlayerBoxes(30 , 22);
		initAttackBox();
	}
	
	public void setSpawn(Point spawn) {
		this.x = spawn.x;
		this.y = spawn.y;
		topHitBox.x = x;
		playerbox.x = x;
		bottomHitbox.x = x;
		topHitBox.y = y;
		playerbox.y = y;
		bottomHitbox.y= y;
	
		
	}
	
	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (23 * Game.SCALE), (int) (35 * Game.SCALE));
		resetAttackBox();
	}

	public static String ChooseCharacter() {
	    System.out.println("Choose the player: \n1)Uzumaki Naruto\n2)Uchiha Sasuke\n3)Sakura Haruno\n4)Hatake Kakashi");
	    num = input.nextInt();
	    switch(num) {
	        case 1:
	            currentCharacter = CHARACTER_NARUTO;
	            source = "sprite1.png";
	            break;
	        case 2:
	            currentCharacter = CHARACTER_SASUKE;
	            source = "sprite2.png";
	            break;
	        case 3:
	            currentCharacter = CHARACTER_SAKURA;
	            source = "sprite3.png";
	            break;
	        case 4:
	            currentCharacter = CHARACTER_KAKASHI;
	            source = "sprite4.png";
	            break;
	    }
	    return source;
	}

	
	public void update() {
		updateHealthBar();
		updatePowerBar();
		
		if(currentHealth <= 0) {
			if(state != DEAD) {
				state = DEAD;
				aniTick = 0;
				aniIndex = 0;
				playing.setPlayerDying(true);
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
				
				// Check if player died in air
				if (!IsEntityOnFloor(playerbox, lvlData)) {
					inAir = true;
					airSpeed = 0;
				}
			}else if(aniIndex == characterManager.getSpriteAmount(currentCharacter, DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
				playing.setGameOver(true);
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAME_OVER);
			}else {
				updateAnimationTick();
				
				// Fall if in air
				if (inAir)
					if (CanMoveHere(playerbox.x, playerbox.y + airSpeed, playerbox.width, playerbox.height, lvlData)) {
						playerbox.y += airSpeed;
						airSpeed += GRAVITY;
					} else
						inAir = false;
			}
			return;
		}
		
		updateAttackBox();
		
//		if(state == HIT) {
//			if(aniIndex <= characterManager.getSpriteAmount(currentCharacter, state) - 3)
//				pushPlayerBack(pushBackDir, lvlData, 1.25f);
//			updatePushBackDrawOffset();
//				
//		}else
		updatePos();
		
		if(moving) {
			checkPotionTouched();
			checkSpikesTouched();
			checkInsideWater();
			tileY = (int) (playerbox.y / Game.TILES_SIZE);
			if(powerAttackActive) {
				powerAttackTick++;
				if (powerAttackTick >= 35) {
					powerAttackTick = 0;
					powerAttackActive = false;
				}
			}
		}
		if(attacking || powerAttackActive)
			checkAttack();
		
		updateAnimationTick();
		setAnimation();
	}

	private void checkInsideWater() {
		if (IsEntityInWater(playerbox, playing.getLevelManager().getCurrentLevel().getLevelData()))
			currentHealth = 0;
	}
	private void checkSpikesTouched() {
		playing.checkSpikesTouched(this);
		
	}

	private void checkPotionTouched() {
		playing.checkPotionTouched(playerbox);
		
	}

	private void checkAttack() {
		if(attackChecked || aniIndex != 1)
			return;
		attackChecked = true;
		
		if(powerAttackActive)
			attackChecked = false;
		
		playing.checkEnemyHit(attackBox);
		playing.checkObjectHit(attackBox);
		playing.getGame().getAudioPlayer().playAttackSound();
	}
	
	private void setAttackBoxOnRightSide() {
		attackBox.x = playerbox.x + playerbox.width - (int) (Game.SCALE * 5);
	}
	private void setAttackBoxOnLeftSide() {
		attackBox.x = playerbox.x - playerbox.width - (int)(Game.SCALE * 1);
	}

	private void updateAttackBox() {
		if(right && left) {
			if(flipW == 1) {
				setAttackBoxOnRightSide();
			}else {
				setAttackBoxOnLeftSide();
			}
		}else if(right || (powerAttackActive && flipW == 1))
			setAttackBoxOnRightSide();
		else if (left || (powerAttackActive && flipW == -1))
			setAttackBoxOnLeftSide();
		
		attackBox.y = bottomHitbox.y;
	}

	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
		
	}
	
	private void updatePowerBar() {
		powerWidth = (int)((powerValue / (float) powerMaxValue) * powerBarWidth);
		
		powerGrowTick++;
		if(powerGrowTick >= powerGrowSpeed) {
			powerGrowTick = 0;
			changePower(1);
		}
	}

	public void render(Graphics g, int lvlOffset) {
		g.drawImage(animations[state][aniIndex], (int)(playerbox.x  - xDrawOffset) - lvlOffset + flipX, (int)(playerbox.y - yDrawOffset) , width * flipW, height , null);
		//drawHitBoxes(g, lvlOffset);
		//drawAttackBox(g, lvlOffset);
		drawUI(g);
	}

	private void drawUI(Graphics g) {
		// Background ui
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);

		// Health bar
		g.setColor(Color.red);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);

		// Power Bar
		g.setColor(Color.yellow);
		g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);
	}

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= characterManager.getSpriteAmount(currentCharacter, state)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
				if(state == HIT) {
					newState(IDLE);
					airSpeed = 0f;
					if(!IsFloor(playerbox, 0, lvlData))
						inAir = true;
				}
			}

		}

	}

	private void setAnimation() {
		int startAni = state;
		
		if(state == HIT)
			return;
		
		if (moving)
			state = RUNNING;
		else
			state = IDLE;
		
		if(inAir) {
			if(airSpeed < 0)
				state = JUMP;
		}
		
		if(powerAttackActive) {
			state = HIT;
			aniIndex = 1;
			aniTick = 0;
			return;
		}
//		if(attacking)
//			state = HIT;
		if (attacking) {
			state = HIT;
			if(startAni != HIT) {
				aniIndex = 1;
				aniTick = 0;
				return;
			}
		}
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
		
		if(!inAir) 
			if(!powerAttackActive)
				if((!left && !right) || (right && left)) 
					return ;		

		float xSpeed = 0;

		if (left && !right) {
			xSpeed -= walkSpeed;
			flipX = width;
			flipW = -1;
		}
		if (right && !left) {
			xSpeed += walkSpeed;
			flipX = 0;
			flipW = 1;
		}
		if(powerAttackActive) {
			if ((!left && !right) || (left && !right)) {
				if(flipW == -1)
					xSpeed = -walkSpeed;
				else
					xSpeed = walkSpeed;
			}
			xSpeed *= 3;
		}
		if (!inAir)
			if (!IsEntityOnFloor(playerbox, lvlData))
				inAir = true;

		if (inAir && !powerAttackActive) {
			if (CanMoveHere(playerbox.x, playerbox.y + airSpeed, playerbox.width, playerbox.height, lvlData) && CanMoveHere(bottomHitbox.x, bottomHitbox.y + airSpeed, bottomHitbox.width, bottomHitbox.height, lvlData)) {
				playerbox.y += airSpeed;
				topHitBox.y += airSpeed;
				bottomHitbox.y += airSpeed;
				airSpeed += GRAVITY;
				updateXPos(xSpeed);
			} else {
				playerbox.y = GetEntityYPosUnderRoofOrAboveFloor(playerbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}

		} else
			updateXPos(xSpeed);
		
		playerbox.x = bottomHitbox.x;
		playerbox.y = bottomHitbox.y + bottomHitbox.height;
        bottomHitbox.x = topHitBox.x;
        bottomHitbox.y = topHitBox.y + topHitBox.height;	
        
        moving = true;
	} 

	private void jump() {
		if(inAir) 
			return;
		playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
		inAir = true;
		//attacking = false;
		airSpeed = jumpSpeed;
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
		
	}

	private void updateXPos(float xSpeed) {
		if (CanMoveHere(playerbox.x + xSpeed, playerbox.y, playerbox.width, playerbox.height, lvlData) && CanMoveHere(topHitBox.x + xSpeed, topHitBox.y, topHitBox.width, topHitBox.height, lvlData) && CanMoveHere(bottomHitbox.x + xSpeed, bottomHitbox.y, bottomHitbox.width, bottomHitbox.height, lvlData)) {
			playerbox.x += xSpeed;
			topHitBox.x += xSpeed;
			bottomHitbox.x += xSpeed;
			moving = true;
		}else {
			topHitBox.x = GetEntityXPosNextToWall(topHitBox, xSpeed);
			playerbox.x = GetEntityXPosNextToWall(playerbox, xSpeed);
			bottomHitbox.x = GetEntityXPosNextToWall(bottomHitbox, xSpeed);
			if(powerAttackActive) {
				powerAttackActive = false;
				powerAttackTick = 0;
			}
		}
		
	}
	
	public void changeHealth(int value) {
		if (value < 0) {
			if (state == HIT)
				return;
			else
				newState(HIT);
		}

		currentHealth += value;
		currentHealth = Math.max(Math.min(currentHealth, maxHealth), 0);
	}
	
	public void changeHealth(int value, Enemy e) {
		if (state == HIT)
			return;
		changeHealth(value);
		pushBackOffsetDir = UP;
		pushDrawOffset = 0;

		if (e.getHitBox().x < playerbox.x)
			pushBackDir = RIGHT;
		else
			pushBackDir = LEFT;
	}
	
	public void changePower(int value) {
		powerValue += value;
		powerValue = Math.max(Math.min(powerValue, powerMaxValue), 0);
		
	}
	public void kill() {
		currentHealth = 0;
	}

	private void loadAnimations() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

		animations = new BufferedImage[8][6];
		for (int j = 0; j < animations.length; j++)
			for (int i = 0; i < animations[j].length; i++)
				animations[j][i] = img.getSubimage(i * 100, j * 100, 100, 100);
		
		statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

	}
	
	public int getSpriteAmount(int character, int action) {          // for character manager sprite amount
	    return characterManager.getSpriteAmount(character, action);
	}

	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
		if(!IsEntityOnFloor(playerbox, lvlData))
			inAir = true;
	}

	public void resetDirBooleans() {
		//jump = false;
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
		airSpeed = 0f;
		state = IDLE;
		currentHealth = maxHealth;
		powerAttackActive = false;
		powerAttackTick = 0;
		powerValue = powerMaxValue;
		 
		playerbox.x = x;
		playerbox.y = y;
		bottomHitbox.x = x;
		topHitBox.x = x;
		bottomHitbox.y= y;
		topHitBox.y = y;
		
		resetAttackBox();
		
		if (!IsEntityOnFloor(playerbox, lvlData))
			inAir = true;
	}
	private void resetAttackBox() {
		if(flipW == 1) 
			setAttackBoxOnRightSide();
		else 
			setAttackBoxOnLeftSide();
	}

	public int getTileY() {
		return tileY;
	}

	public void powerAttack() {
		if (powerAttackActive)
			return;
		if (powerValue >= 60) {
			powerAttackActive = true;
			changePower(-60);
		}

	}
}