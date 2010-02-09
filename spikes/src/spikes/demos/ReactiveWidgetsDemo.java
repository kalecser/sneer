package spikes.demos;

import static sneer.foundation.environments.Environments.my;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;

public class ReactiveWidgetsDemo {

	public static void main(String[] args) throws Exception {
		Environments.runWith(Brickness.newBrickContainer(), new ClosureX<Exception>() { @Override public void run() throws Exception {
			my(spikes.demos.reactivewidgets.Demo.class);
		}});
	}

}

