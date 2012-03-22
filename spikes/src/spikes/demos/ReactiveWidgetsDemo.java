package spikes.demos;

import static basis.environments.Environments.my;
import basis.brickness.Brickness;
import basis.environments.Environments;
import basis.lang.ClosureX;
import sneer.bricks.hardware.gui.timebox.TimeboxedEventQueue;

public class ReactiveWidgetsDemo {

	public static void main(String[] args) throws Exception {
		Environments.runWith(Brickness.newBrickContainer(), new ClosureX<Exception>() { @Override public void run() throws Exception {
			my(TimeboxedEventQueue.class).startQueueing(500000);
			my(spikes.demos.reactivewidgets.Demo.class);
		}});
	}

}

