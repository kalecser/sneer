package spikes.klaus.thomas.beings.gui;

import static basis.environments.Environments.my;
import basis.brickness.Brickness;
import basis.environments.Environments;
import basis.lang.Closure;
import sneer.bricks.hardware.clock.ticker.ClockTicker;
import sneer.bricks.hardware.gui.guithread.GuiThread;

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
