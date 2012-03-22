package sneer.bricks.hardware.ram.meter;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface MemoryMeter {
	
	Signal<Integer> usedMBs();
	Signal<Integer> usedMBsPeak();

	int maxMBs();
	int availableMBs();

}
