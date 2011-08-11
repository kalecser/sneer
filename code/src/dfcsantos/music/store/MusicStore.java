package dfcsantos.music.store;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.reactive.Register;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface MusicStore {
	
	Register<Boolean> isExchangeTracksOn();

	Register<Integer> volumePercent();

	Register<Boolean> shuffle();

}
