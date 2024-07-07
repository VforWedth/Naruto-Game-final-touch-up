package gamestates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import entities.Player;
import main.Game;
import ui.ChoiceButton;
import utilz.LoadSave;

public class Choice extends State implements Statemethods {
 
 private static ChoiceButton[] button = new ChoiceButton[4];
 private BufferedImage backgroundImg;

 public Choice(Game game) {
	 super(game);
	 loadButtons();
	 backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.CHOICE_BACKGROUND);
 }

 private void loadButtons() {
	 button[0] = new ChoiceButton(Game.GAME_WIDTH/4 - 100, (int) (220 * Game.SCALE), 0, Gamestate.MENU);
	 button[1] = new ChoiceButton(3 * Game.GAME_WIDTH/4 - 100, (int) (220 * Game.SCALE), 1, Gamestate.MENU);
	 button[2] = new ChoiceButton(2 * Game.GAME_WIDTH/4 - 100, (int) (220 * Game.SCALE), 2, Gamestate.MENU);
	 button[3] = new ChoiceButton(Game.GAME_WIDTH - 100, (int) (220 * Game.SCALE), 3, Gamestate.MENU);
  
 }

 @Override
 public void update() {
	 for(ChoiceButton cb : button) {
		 cb.update();
	 }
 }

 @Override
 public void draw(Graphics g) {
	 g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT,null);
	 for(ChoiceButton cb: button) {
		 cb.draw(g);
	 }
 }


 @Override
 public void mouseClicked(MouseEvent e) {
  
 }

 @Override
 public void mousePressed(MouseEvent e) {
	 for(ChoiceButton cb: button) {
		 if(isIn(e, cb)) {
			 cb.setMousePressed(true);
			 break;
		 	}
	 }
 }

 @Override
 public void mouseReleased(MouseEvent e) {
	 for(ChoiceButton cb: button) {
		 if(isIn(e, cb)) {
			 if(cb.isMousePressed()) {
				 System.out.println("before : "+cb.getCode());
				 cb.setCode(cb.getRowIndex());
				 System.out.println("after : "+cb.getCode());
//for(ChoiceButton cButton : button) {
				 if(cb.getCode() == cb.getRowIndex()) {
					 Player.ChooseCharacter(cb.getCode());
					 LoadSave.PLAYER_ATLAS = Player.ChooseCharacter(cb.getCode());
//      }
				 	}
				 cb.applyGamestate();
    }
    if(cb.getState() == Gamestate.MENU)
    	game.getAudioPlayer().playSong(0);
    	break;
		 }
	 }
  resetButtons();
 }
 
 private void resetButtons() {
  for(ChoiceButton cb : button)
   cb.resetBools();
 }

 @Override
 public void mouseMoved(MouseEvent e) {
	 for(ChoiceButton cb: button)
		 cb.setMouseOver(false);
  
  for(ChoiceButton cb: button)
	  if(isIn(e, cb)) {
		  cb.setMouseOver(true);
		  break;
   }
  
 }

 @Override
 public void keyPressed(KeyEvent e) {
	 if(e.getKeyCode() == KeyEvent.VK_ENTER) {
		 Gamestate.state = Gamestate.MENU;
	 }
 }

 @Override
 public void keyReleased(KeyEvent e) {
  
 }

}