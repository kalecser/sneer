package sneer.foundation.brickness.impl.tests.fixtures.caller.b.impl;

import sneer.foundation.brickness.impl.tests.fixtures.caller.a.A;
import sneer.foundation.brickness.impl.tests.fixtures.caller.b.B;
import static sneer.foundation.environments.Environments.my;

class BImpl implements B {

	@Override
	public Class<?> call() {
		return my(A.class).call();
	}

}
