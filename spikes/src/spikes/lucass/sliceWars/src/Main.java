package spikes.lucass.sliceWars.src;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import spikes.lucass.sliceWars.src.gui.GamePanel;

public class Main {

	private static GamePanel gamePanel;
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e) {
			gamePanel.stopGameThread();
		}});
		
		frame.setLayout(new BorderLayout());
		int numberOfPlayers = 2;
		int lines = 8;
		int columns = 5;
		int randomlyRemoveCells = 12;
		gamePanel = new GamePanel(numberOfPlayers,lines,columns,randomlyRemoveCells);
		frame.add(gamePanel, BorderLayout.CENTER);
		
		frame.setSize(1000, 500);
		frame.setVisible(true);
	}
}
