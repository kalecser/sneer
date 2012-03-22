package sneer.bricks.hardware.ram.recycler.impl;

import static basis.environments.Environments.my;
import basis.lang.Consumer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.ram.meter.MemoryMeter;
import sneer.bricks.hardware.ram.recycler.MemoryRecycler;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;

class MemoryRecyclerImpl implements MemoryRecycler {

	private static final MemoryMeter MemoryMeter = my(MemoryMeter.class);

	@SuppressWarnings("unused") private WeakContract _usedMemoryConsumerContract;

	{
		_usedMemoryConsumerContract = MemoryMeter.usedMBs().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer usedMemory) {
			recycleUsedMemoryIfNecessary(usedMemory);
		}});
	}

	private void recycleUsedMemoryIfNecessary(int usedMemory) {
		if (usedMemory <= usedMemorySafeLimit()) return;

		my(BlinkingLights.class).turnOn(LightType.WARNING, "Recycling memory", "Total Memory: " + MemoryMeter.maxMBs() + " MB\nSafe Limit: " + usedMemorySafeLimit() + " MB\nAvailable Memory: " + MemoryMeter.availableMBs() + " MB", 7000);
		System.gc();  
	}

	private int usedMemorySafeLimit() {
		return (int) (0.8 * MemoryMeter.maxMBs()) + 1;
	}

}
