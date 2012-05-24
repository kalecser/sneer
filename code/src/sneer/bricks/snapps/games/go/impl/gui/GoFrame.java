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

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.network.Player;
import basis.lang.Closure;

public class GoFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final StoneColor _side;
	private GoBoardPanel _goBoardPanel;
	
	public static void main(String[] args) {
		TimerFactory timerFactory = new TimerFactory() {
			@Override
			public WeakContract wakeUpEvery(final int interval, final Runnable scroller) {
				new Thread(){
					@Override
					public void run() {
						while(true){
							try {
								scroller.run();
								Thread.sleep(interval);
							} catch (InterruptedException e) {
								throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
							}
						}
					};
				}.start();
				return null;
			}
		};
		GoFrame blackFrame = new GoFrame(StoneColor.BLACK, 0, timerFactory);
		GoFrame whiteFrame = new GoFrame(StoneColor.WHITE, 0, timerFactory);
		whiteFrame.setAdversary(blackFrame._goBoardPanel);
	}
	
	public GoFrame(StoneColor side, int horizontalPosition, final TimerFactory timerFactory) {
		_side = side;
	
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Go - " + _side.name());	  
	    setResizable(false);
	    addComponentPanel(timerFactory); 
	    setVisible(true);
	    int bord=getInsets().left+getInsets().right;
	    setBounds(horizontalPosition*(500+bord)+100, 100, 500+bord, 575);
		//this is for when the game is running on a single window
		//setLocationRelativeTo(null);
	}

	public void setAdversary(final Player remotePlayer){
		_goBoardPanel.setAdversary(remotePlayer);		
		remotePlayer.setAdversary(_goBoardPanel);
	}
	
	private void addComponentPanel(final TimerFactory timerFactory) {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		_goBoardPanel = new GoBoardPanel(timerFactory, _side);
		contentPane.add(_goBoardPanel, BorderLayout.CENTER);
		
		JPanel goEastPanel = new JPanel();
		
		goEastPanel.setLayout(new FlowLayout());
		goEastPanel.add(new GoScorePanel(_goBoardPanel.scoreBlack(), _goBoardPanel.scoreWhite(), _goBoardPanel));
		
		JSeparator space= new JSeparator(SwingConstants.VERTICAL);
		space.setPreferredSize(new Dimension(30,0));
		
		goEastPanel.add(space);
		Closure pass = new Closure() { @Override public void run() {
			_goBoardPanel.passTurn();
		}};
		Closure resign = new Closure() { @Override public void run() {
			_goBoardPanel.resignTurn();
		}}; 
		goEastPanel.add(new ActionsPanel(pass,resign, _side, _goBoardPanel));
				
		contentPane.add(goEastPanel, BorderLayout.SOUTH);
	}

}
