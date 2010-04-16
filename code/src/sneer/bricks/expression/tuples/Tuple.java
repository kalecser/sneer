package sneer.bricks.expression.tuples;


import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.lang.Immutable;

public abstract class Tuple extends Immutable {

	protected Tuple() {
		this(null);
	}
	
	
	protected Tuple(Seal addressee_) {
		addressee = addressee_;
	}

	
	public final Seal publisher = my(OwnSeal.class).oldGet();
	public final long publicationTime = my(Clock.class).time().currentValue();
	
	public final Seal addressee;
	
	
//	public void stamp(Seal publisher_, long time) {
//		if (publisher != null)
//			throw new IllegalStateException("Tuple was already stamped.");
//
//		setField("publisher", publisher_);
////		System.out.println("Setting: " + time);
//		setField("publicationTime", time);
////		System.out.println("Set: " + publicationTime);
//	}
//
//
//	private void setField(String fieldName, Object value) {
//		try {
//			Field field = Tuple.class.getField(fieldName);
//			field.setAccessible(true);
//			field.set(this, value);
//		} catch (Exception e) {
//			throw new IllegalStateException(e);
//		}
//	}
	
}
