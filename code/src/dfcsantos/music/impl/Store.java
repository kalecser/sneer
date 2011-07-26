package dfcsantos.music.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.software.bricks.statestore.BrickStateStore;
import dfcsantos.music.Music;

class Store {

	private static final BrickStateStore _delegate = my(BrickStateStore.class);

	static Object[] restore() {
		Object result = _delegate.readObjectFor(Music.class);
		return (result != null) ? (Object[]) result : null;
	}

	static void save(Object... downloadAllowanceState) {
		_delegate.writeObjectFor(Music.class, downloadAllowanceState);
	}

}
