package sneer.bricks.network.social.contacts;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.lang.ReadOnly;

public interface Contact extends ReadOnly {
	
	Signal<String> nickname();
	
}
