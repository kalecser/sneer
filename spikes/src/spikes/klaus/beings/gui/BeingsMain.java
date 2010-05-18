package spikes.klaus.beings.gui;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.ticker.ClockTicker;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class BeingsMain {
	
	public static void main(String[] args){
		Environments.runWith(Brickness.newBrickContainer(), new Closure() { @Override public void run() {
			my(ClockTicker.class);
			my(GuiThread.class).invokeAndWaitForWussies(new Closure(){@Override public void run() {
				new BeingsFrame();
			}});
		}});
	}
	
}
