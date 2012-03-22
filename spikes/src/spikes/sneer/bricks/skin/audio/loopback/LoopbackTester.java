package spikes.sneer.bricks.skin.audio.loopback;

import basis.brickness.Brick;

@Brick
public interface LoopbackTester {

	boolean start();
	void stop();
}