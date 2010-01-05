package sneer.bricks.network.social;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.lang.ReadOnly;

public interface Contact extends ReadOnly {
	
	Signal<String> nickname();
	
}
