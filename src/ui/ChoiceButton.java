package ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import utilz.LoadSave;
import static utilz.constants.UI.Buttons.*;

public class ChoiceButton {
 public int code;
 private int xPos, yPos, rowIndex, index;
 private int xOffsetCenter = C_WIDTH / 2;
 private Gamestate state;
 private BufferedImage[] imgs;
 private boolean mouseOver, mousePressed;
 private Rectangle bounds;

 public ChoiceButton(int xPos, int yPos, int rowIndex, Gamestate state) {
  this.xPos = xPos;
  this.yPos = yPos;
  this.rowIndex = rowIndex;
  this.state = state;
  loadImgs();
  initBounds();
 }

 private void initBounds() {
  bounds = new Rectangle(xPos - xOffsetCenter, yPos, C_WIDTH, C_HEIGHT);
 }

 private void loadImgs() {
  imgs = new BufferedImage[3];
  BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CHOICE_BUTTONS);
  for (int i = 0; i < imgs.length; i++)
   imgs[i] = temp.getSubimage(i * C_WIDTH_DEFAULT,  0, C_WIDTH_DEFAULT, C_HEIGHT_DEFAULT);
  
 }

 public void draw(Graphics g) {
  g.drawImage(imgs[index], xPos - xOffsetCenter, yPos, C_WIDTH, C_HEIGHT, null);
 }

 public void update() {
  index = 0;
  if (mouseOver)
   index = 1;
  if (mousePressed)
   index = 2;
 }

 public int getRowIndex() {
  return rowIndex;
 }
 
 public int getCode() {
  System.out.println("code : "+code);
  return code;
 }

 public void setCode(int rowIndex) {
  System.out.println("Setting code to: " + rowIndex);
  this.code = rowIndex;
 }

 public boolean isMouseOver() {
  return mouseOver;
 }

 public void setMouseOver(boolean mouseOver) {
  this.mouseOver = mouseOver;
 }

 public boolean isMousePressed() {
  return mousePressed;
 }

 public void setMousePressed(boolean mousePressed) {
  this.mousePressed = mousePressed;
 }

 public Rectangle getBounds() {
  return bounds;
 }
 
 public void applyGamestate() {
  Gamestate.state = state;
 }

 public void resetBools() {
  mouseOver = false;
  mousePressed = false;
 }
 public Gamestate getState() {
  return state;
 }

}