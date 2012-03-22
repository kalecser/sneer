package sneer.bricks.pulp.reactive.gates.logic.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.gates.logic.LogicGates;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class NotTest extends BrickTestBase {

	@Test
	public void test() {
		boolean initialValue = false;
		Register<Boolean> signalHolder = my(Signals.class).newRegister(initialValue);

		Signal<Boolean> original = signalHolder.output();
		Signal<Boolean> inverse = my(LogicGates.class).not(signalHolder.output());

		assertInverse(original, inverse);
		toggle(signalHolder);
		assertInverse(original, inverse);
		toggle(signalHolder);
		assertInverse(original, inverse);

		// Two toggles brings the original signal back to its initial value
		assertEquals(initialValue, original.currentValue());
		assertEquals(!initialValue, inverse.currentValue());
	}

	private void assertInverse(Signal<Boolean> original, Signal<Boolean> inverse) {
		assertEquals(original.currentValue(), !inverse.currentValue());
	}

	private void toggle(Register<Boolean> register) {
		register.setter().consume(!register.output().currentValue());
	}

}
