package sneer.bricks.hardware.ram.recycler.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.ram.recycler.MemoryRecycler;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Consumer;

public class MemoryRecyclerTest extends BrickTest {

	@Ignore
	@Test (timeout = 4000)
	public void memoryRecycleBlinkingLightTurnedOn() {
		final Latch latch = my(Latches.class).produce();
		@SuppressWarnings("unused")
		WeakContract refToAvoidGc = my(BlinkingLights.class).lights().addReceiver(new Consumer<CollectionChange<Light>>() { @Override public void consume(CollectionChange<Light> lights) {
			if (!lights.elementsAdded().isEmpty()) latch.open();
		}});

		my(MemoryRecycler.class);

		@SuppressWarnings("unused") byte[] load = loadEnoughBytesToActivateRecycler();

		latch.waitTillOpen();
	}

	private byte[] loadEnoughBytesToActivateRecycler() {
		return new byte[(int) (0.7 * Runtime.getRuntime().maxMemory())];
	}

}
