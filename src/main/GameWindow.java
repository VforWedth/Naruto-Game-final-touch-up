package main;

import javax.swing.JFrame;

public class GameWindow {
	private JFrame jframe;
	public GameWindow(GamePanel gamePanel) {
		jframe = new JFrame();
		
		
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.add(gamePanel);
		
		jframe.pack();
		jframe.setLocationRelativeTo(null);
		jframe.setResizable(true);
		jframe.setVisible(true);
	}
	
}


