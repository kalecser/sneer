package sneer.bricks.hardware.cpu.threads.latches;

import sneer.bricks.hardware.cpu.threads.Latch;
import sneer.foundation.brickness.Brick;

@Brick
public interface Latches {

	Latch newLatch();

}
