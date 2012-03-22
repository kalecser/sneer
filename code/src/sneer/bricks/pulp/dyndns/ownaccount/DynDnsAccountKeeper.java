package sneer.bricks.pulp.dyndns.ownaccount;

import basis.brickness.Brick;
import basis.lang.Consumer;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface DynDnsAccountKeeper {
	
	Signal<DynDnsAccount> ownAccount();

	Consumer<DynDnsAccount> accountSetter();

}
