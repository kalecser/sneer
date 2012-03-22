package spikes.demos;

import static basis.environments.Environments.my;

import java.awt.Image;

import basis.brickness.Brickness;
import basis.environments.Environments;
import basis.lang.ClosureX;
import basis.lang.Consumer;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.skin.imgselector.ImageSelector;

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
