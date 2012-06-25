package sneer.bricks.snapps.games.go.tests.logic;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.games.go.impl.Player;
import sneer.bricks.snapps.games.go.impl.TimerFactory;
import sneer.bricks.snapps.games.go.impl.gui.game.GuiPlayer;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.logic.Move;


public class RunGoWithoutSneer implements Player {
	
	private GuiPlayer _blackFrame;
	private GuiPlayer _whiteFrame;

	public static void main(String[] args) {
		new RunGoWithoutSneer(3);
	}

	public RunGoWithoutSneer(final int boardSize) {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
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
								//Don't care
							}
						}
					};
				}.start();
				return null;
			}
		};
		final int gameID = 1;
		
		_blackFrame = new GuiPlayer(gameID,StoneColor.BLACK,boardSize, timerFactory);
		_blackFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_blackFrame.setAdversary(this);
		_whiteFrame = new GuiPlayer(gameID,StoneColor.WHITE,boardSize, timerFactory);
		_whiteFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_whiteFrame.setAdversary(this);
	}
	
	@Override
	public void play(Move move) {
		simulatePlaySignaling(move);
	}

	private void simulatePlaySignaling(Move move) {
		_blackFrame.play(move);
		_whiteFrame.play(move);
	}

	@Override
	public void setAdversary(Player player) {
		//do nothing
	}

}
