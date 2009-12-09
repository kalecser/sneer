package sneer.bricks.hardware.ram.recycler.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.ram.meter.MemoryMeter;
import sneer.bricks.hardware.ram.recycler.MemoryRecycler;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.Consumer;

class MemoryRecyclerImpl implements MemoryRecycler {

	private final MemoryMeter _memoryMeter = my(MemoryMeter.class);

	@SuppressWarnings("unused") private WeakContract _usedMemoryConsumerContract;

	{
		_usedMemoryConsumerContract = _memoryMeter.usedMBs().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer usedMemory) {
			recycleUsedMemoryIfNecessary(usedMemory);
		}});
	}

	private void recycleUsedMemoryIfNecessary(int usedMemory) {
		if (usedMemory > usedMemorySafeLimit()) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Recycling memory", "Total Memory: " + _memoryMeter.maxMBs() + " MB\nSafe Limit: " + usedMemorySafeLimit() + " MB\nAvailable Memory: " + _memoryMeter.availableMBs() + " MB", 7000);
			System.gc();  
		}
	}

	private int usedMemorySafeLimit() {
		return (int) (0.8 * MemoryMeter.maxMBs()) + 1;
	}

}
