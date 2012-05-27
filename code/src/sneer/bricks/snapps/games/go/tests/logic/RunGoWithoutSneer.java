package sneer.bricks.snapps.games.go.tests.logic;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.games.go.impl.TimerFactory;
import sneer.bricks.snapps.games.go.impl.gui.GuiPlayer;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;


public class RunGoWithoutSneer {
	
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
		GuiPlayer blackFrame = new GuiPlayer(StoneColor.BLACK, 0, timerFactory);
		GuiPlayer whiteFrame = new GuiPlayer(StoneColor.WHITE, 0, timerFactory);
		whiteFrame.setAdversary(blackFrame);
		blackFrame.setAdversary(whiteFrame);
	}

}
