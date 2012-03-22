package sneer.bricks.pulp.reactive;

import basis.lang.Consumer;

public interface Register<T>{

	Signal<T> output();
	
	Consumer<T> setter();

}
