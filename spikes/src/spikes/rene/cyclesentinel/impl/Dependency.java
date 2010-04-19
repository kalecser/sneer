package spikes.rene.cyclesentinel.impl;

import sneer.foundation.lang.Pair;


class Dependency extends Pair<String, String> {

	public Dependency(String dependent, String provider) {
		super(dependent, provider);
	}

	String dependent() {
		return a;
	}

	String provider() {
		return b;
	}

}
