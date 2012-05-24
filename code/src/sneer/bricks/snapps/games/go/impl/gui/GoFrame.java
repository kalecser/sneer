package sneer.bricks.snapps.games.go.impl.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.network.Player;
import basis.lang.Closure;

public class GoFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final StoneColor _side;
	
	public GoFrame(final Player remotePlayer, StoneColor side, int horizontalPosition, final TimerFactory timerFactory) {
		_side = side;
	
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Go - " + _side.name());	  
	    setResizable(false);
	    addComponentPanel(remotePlayer, timerFactory); 
	    setVisible(true);
	    int bord=getInsets().left+getInsets().right;
	    setBounds(horizontalPosition*(500+bord)+100, 100, 500+bord, 575);
		//this is for when the game is running on a single window
		//setLocationRelativeTo(null);
	}

	private void addComponentPanel(final Player remotePlayer, final TimerFactory timerFactory) {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		final GoBoardPanel goBoardPanel = new GoBoardPanel(remotePlayer,timerFactory, _side);
		remotePlayer.setAdversary(goBoardPanel);
		contentPane.add(goBoardPanel, BorderLayout.CENTER);
		
		JPanel goEastPanel = new JPanel();
		
		goEastPanel.setLayout(new FlowLayout());
		goEastPanel.add(new GoScorePanel(goBoardPanel.scoreBlack(), goBoardPanel.scoreWhite(), goBoardPanel));
		
		JSeparator space= new JSeparator(SwingConstants.VERTICAL);
		space.setPreferredSize(new Dimension(30,0));
		
		goEastPanel.add(space);
		Closure pass = new Closure() { @Override public void run() {
			goBoardPanel.passTurn();
		}};
		Closure resign = new Closure() { @Override public void run() {
			goBoardPanel.resignTurn();
		}}; 
		goEastPanel.add(new ActionsPanel(pass,resign, _side, goBoardPanel));
				
		contentPane.add(goEastPanel, BorderLayout.SOUTH);
	}

}
