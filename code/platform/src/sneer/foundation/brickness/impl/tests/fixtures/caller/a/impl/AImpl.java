package sneer.foundation.brickness.impl.tests.fixtures.caller.a.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.foundation.brickness.Caller;
import sneer.foundation.brickness.impl.tests.fixtures.caller.a.A;

public class AImpl implements A {

	@Override
	public Class<?> call() {
		return my(Caller.class).get();
	}

}
