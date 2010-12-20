package sneer.bricks.hardware.cpu.threads.latches;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Factory;

@Brick
public interface Latches extends Factory<Latch> {

	Latch produce(int count);

}
