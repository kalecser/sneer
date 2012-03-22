package sneer.bricks.pulp.notifiers;

import basis.lang.Consumer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.pulsers.Pulser;

public interface Source<T> extends Pulser {

	WeakContract addReceiver(Consumer<? super T> receiver);
	
}
