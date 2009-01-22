package wheel.reactive.impl;

import wheel.lang.PickyConsumer;
import wheel.lang.exceptions.IllegalParameter;

public class IntegerParser implements PickyConsumer<String> {

	private final PickyConsumer<Integer> _endConsumer;

	public IntegerParser(PickyConsumer<Integer> endConsumer) {
		_endConsumer = endConsumer;
	}

	
	public void consume(String string) throws IllegalParameter {
		int result = 0;
		
		try {
			result = Integer.parseInt(string);
		} catch (NumberFormatException e) {
			throw new IllegalParameter(string + " is not a valid number.");
		}
		
		_endConsumer.consume(result);
	}

}