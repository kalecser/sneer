package spikes.demos;

import static sneer.foundation.environments.Environments.my;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environments;

public class ReactiveWidgetsDemo {

	
	public static void main(String[] args) throws Exception {
		Environments.runWith(Brickness.newBrickContainer(), new Runnable(){ @Override public void run() {
			try {
				my(spikes.demos.reactivewidgets.Demo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}});
	}
}
