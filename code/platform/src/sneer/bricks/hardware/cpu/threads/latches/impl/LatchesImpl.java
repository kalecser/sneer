package sneer.bricks.hardware.cpu.threads.latches.impl;

import sneer.bricks.hardware.cpu.threads.Latch;
import sneer.bricks.hardware.cpu.threads.impl.LatchImpl;
import sneer.bricks.hardware.cpu.threads.latches.Latches;

class LatchesImpl implements Latches {

	@Override
	public Latch newLatch() {
		return new LatchImpl();
	}

}
