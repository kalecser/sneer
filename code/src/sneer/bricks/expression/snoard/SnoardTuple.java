package sneer.bricks.expression.snoard;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;


public class SnoardTuple extends Tuple {
	public Object clipValue;

	public SnoardTuple(Object clipValue, Seal addressee) {
		super(addressee);
		this.clipValue = clipValue;
	}
}
