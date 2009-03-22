package wheel.reactive.tests;

import sneer.pulp.events.receivers.impl.Solder;
import sneer.pulp.reactive.Register;
import sneer.pulp.reactive.Signal;
import sneer.pulp.reactive.impl.RegisterImpl;
import wheel.lang.Consumer;
import static wheel.lang.Types.cast;

public class LoopbackTester {

	public LoopbackTester(Signal<?> input, Consumer<?> output) {
		_output = cast(output);
				
		Signal<Object> castedInput = cast(input);

		_referenceToAvoidGc1 = new Solder<Object>(castedInput, _inputValue1.setter());
		_referenceToAvoidGc2 = new Solder<Object>(castedInput, _inputValue2.setter());
		_referenceToAvoidGc3 = new Solder<Object>(castedInput, _inputValue3.setter());
	}
	
	private final Register<Object> _inputValue1 = new RegisterImpl<Object>(null);
	private final Register<Object> _inputValue2 = new RegisterImpl<Object>(null);
	private final Register<Object> _inputValue3 = new RegisterImpl<Object>(null);
	
	private final Consumer<Object> _output;
	@SuppressWarnings("unused")	private final Object _referenceToAvoidGc1;
	@SuppressWarnings("unused")	private final Object _referenceToAvoidGc2;
	@SuppressWarnings("unused")	private final Object _referenceToAvoidGc3;

	public void test() {
		testWithStrings();
		testWithIntegers();
	}
	
	public void testWithStrings() {
		testLoopbackWith("foo");
		testLoopbackWith("bar");
	}

	public void testWithIntegers() {
		testLoopbackWith(42);
		testLoopbackWith(17);
	}
	
	private void testLoopbackWith(Object object) {
		_output.consume(object);
		
		waitAndAssertEquals(object, _inputValue1.output());
		waitAndAssertEquals(object, _inputValue2.output());
		waitAndAssertEquals(object, _inputValue3.output());
	}

	private void waitAndAssertEquals(Object expected, Signal<Object> input) {
		long t0 = System.currentTimeMillis();
		while (!expected.equals(input.currentValue())) {
			Thread.yield();
			if (System.currentTimeMillis() - t0 > 1000)
				throw new RuntimeException("Timeout");
		}
	}

}
