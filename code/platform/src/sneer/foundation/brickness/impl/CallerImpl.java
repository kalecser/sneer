package sneer.foundation.brickness.impl;

import sneer.foundation.brickness.Caller;

public class CallerImpl implements Caller {

	@Override
	public Class<?> get() {
		try {
			return Class.forName(Thread.currentThread().getStackTrace()[3].getClassName());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

}
