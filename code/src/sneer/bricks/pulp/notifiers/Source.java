package sneer.bricks.pulp.notifiers;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.pulsers.Pulser;
import sneer.foundation.lang.Consumer;

public interface Source<T> extends Pulser {

	WeakContract addReceiver(Consumer<? super T> receiver);
	
}
