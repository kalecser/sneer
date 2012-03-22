package sneer.bricks.network.social;

import basis.lang.ReadOnly;
import sneer.bricks.pulp.reactive.Signal;

public interface Contact extends ReadOnly {
	
	Signal<String> nickname();
	
}
