package spikes.demos;

import static sneer.foundation.environments.Environments.my;

import java.awt.Image;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.skin.imgselector.ImageSelector;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;
import sneer.foundation.lang.Consumer;

public class ImageSelectorDemo  {

	public static void main(String[] args) throws Exception {
		Environments.runWith(Brickness.newBrickContainer(), new ClosureX<Exception>() { @Override public void run() throws Exception {
			ImageSelector imageSelector = Environments.my(ImageSelector.class);
			imageSelector.open(new Consumer<Image>() { @Override public void consume(Image valueObject) {
				//OK
			}});
	
			my(Threads.class).sleepWithoutInterruptions(30000);
		}});
	}

}
