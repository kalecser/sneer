package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.software.bricks.statestore.BrickStateStore;
import dfcsantos.wusic.Wusic;

class Store {

	private static final BrickStateStore _delegate = my(BrickStateStore.class);

	static Object[] restore() {
		Object result = _delegate.readObjectFor(Wusic.class, WusicImpl.class.getClassLoader());
		return (result != null) ? (Object[]) result : null;
	}

	static void save(Object... downloadAllowanceState) {
		_delegate.writeObjectFor(Wusic.class, downloadAllowanceState);
	}

}
