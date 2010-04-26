package spikes.klaus.go.gui;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.ticker.ClockTicker;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import spikes.klaus.go.Move;
import spikes.klaus.go.GoBoard.StoneColor;

public class GoMain {
	
	
	public GoMain() {
		Environments.runWith(Brickness.newBrickContainer(), new Closure() { @Override public void run() {
			my(ClockTicker.class);
			my(GuiThread.class).invokeAndWaitForWussies(new Closure(){@Override public void run() {
				init();
			}});
		}});
	}
	
	private void init() {
		Register<Move> moveRegister = my(Signals.class).newRegister(null);
		new GoFrame(moveRegister, StoneColor.BLACK, 0);
		new GoFrame(moveRegister, StoneColor.WHITE, 500);
	}

	public static void main(String[] args){
		new GoMain();
	}
	
}
