package spikes.lucass.sliceWars.src.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import spikes.lucass.sliceWars.src.RemotePlay;
import spikes.lucass.sliceWars.src.RemotePlayListener;
import spikes.lucass.sliceWars.src.logic.Player;

public class GuiPlayer implements RemotePlayListener {
	
	private GamePanel _gamePanel;

	public GuiPlayer(final Player windowOwner, final RemotePlayListener remotePlayer,final long randomSeed) {
		Random random = new Random(randomSeed);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		frame.setLayout(new BorderLayout());
		int numberOfPlayers = 2;
		int lines = 6;
		int columns = 6;
		int randomlyRemoveCells = 12;
		_gamePanel = new GamePanel(numberOfPlayers,lines,columns,randomlyRemoveCells, random);
		frame.addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e) {
			_gamePanel.stopGameThread();
		}});
		_gamePanel.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e) {
			if(!windowOwner.equals(_gamePanel.currentPlayer())) return;
			remotePlayer.play(new RemotePlay(e.getX(),e.getY()));
		}});
		frame.add(_gamePanel, BorderLayout.CENTER);
		
		frame.setSize(1200, 900);
		frame.setVisible(true);
	}
	
	@Override
	public void play(RemotePlay play) {
		_gamePanel.play(play.getX(), play.getY());
	}

}
