package sneer.bricks.hardware.cpu.threads.latches.impl;

import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;

class LatchesImpl implements Latches {

	@Override
	public Latch produce() {
		return new LatchImpl(1);
	}

	@Override
	public Latch produce(int count) {
		return new LatchImpl(count);
	}

}
