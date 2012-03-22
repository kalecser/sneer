package sneer.bricks.expression.tuples;


import static basis.environments.Environments.my;
import basis.lang.Immutable;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;

public abstract class Tuple extends Immutable {

	protected Tuple() {
		this(null);
	}
	
	
	protected Tuple(Seal addressee_) {
		addressee = addressee_;
	}

	
	public final Seal publisher = ownSeal();

	public final long publicationTime = my(Clock.class).time().currentValue();
	
	public final Seal addressee;
	
	private static Seal ownSeal() {
		Seal result = my(OwnSeal.class).get().currentValue();
		if (result == null)
			throw new IllegalStateException();
		return result;
	}
}
