package sneer.bricks.snapps.games.sliceWars.impl.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import sneer.bricks.snapps.games.sliceWars.impl.RemotePlay;
import sneer.bricks.snapps.games.sliceWars.impl.RemotePlayListener;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;

public class GuiPlayer implements RemotePlayListener {
	
	private GamePanel _gamePanel;
	private JFrame frame;

	public GuiPlayer(final Player windowOwner, final RemotePlayListener remotePlayer,
					 final long randomSeed, final int numberOfPlayers, final int lines,
					 final int columns, final int randomlyRemoveCells) {
		Random random = new Random(randomSeed);
		frame = new JFrame();
		
		frame.setLayout(new BorderLayout());
		_gamePanel = new GamePanel(numberOfPlayers,lines,columns,randomlyRemoveCells, random);
		frame.addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e) {
			_gamePanel.stopGameThread();
		}});
		_gamePanel.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e) {
			if(!windowOwner.equals(_gamePanel.currentPlayer())) return;
			RemotePlay play = new RemotePlay(e.getX(),e.getY());
			remotePlayer.play(play);
		}});
		frame.add(_gamePanel, BorderLayout.CENTER);
		
		frame.setSize(1200, 900);
		frame.setVisible(true);
	}
	
	@Override
	public void play(RemotePlay play) {
		_gamePanel.play(play.getX(), play.getY());
	}

	public void setKillOnClose(){
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
