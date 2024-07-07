package main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import utilz.LoadSave;

public class GameWindow {
	private JFrame jframe;
	private ImageIcon logo = new ImageIcon(getClass().getClassLoader().getResource(LoadSave.ICON));
	public GameWindow(GamePanel gamePanel) {
		jframe = new JFrame();
		
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.add(gamePanel);
		
		jframe.setResizable(false);
		jframe.pack();
		jframe.setLocationRelativeTo(null);
		jframe.setVisible(true);
		jframe.setIconImage(logo.getImage());
		jframe.addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowLostFocus(WindowEvent e) {
				gamePanel.getGame().windowFocusLost();
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}
	
}


