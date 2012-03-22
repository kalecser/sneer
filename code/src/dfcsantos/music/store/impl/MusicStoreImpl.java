package dfcsantos.music.store.impl;

import static basis.environments.Environments.my;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import dfcsantos.music.store.MusicStore;

public class MusicStoreImpl implements MusicStore {

	private final Register<Boolean> isExchangeOn = my(Signals.class).newRegister(true);
	private final Register<Integer> volumePercent = my(Signals.class).newRegister(50);
	private final Register<Boolean> shuffle = my(Signals.class).newRegister(true);

	@Override
	public Register<Boolean> isExchangeTracksOn() {
		return isExchangeOn;
	}

	@Override
	public Register<Integer> volumePercent() {
		return volumePercent;
	}

	@Override
	public Register<Boolean> shuffle() {
		return shuffle;
	}

}
