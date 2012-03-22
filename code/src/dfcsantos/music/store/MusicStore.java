package dfcsantos.music.store;

import basis.brickness.Brick;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.reactive.Register;

@Brick(Prevalent.class)
public interface MusicStore {
	
	Register<Boolean> isExchangeTracksOn();

	Register<Integer> volumePercent();

	Register<Boolean> shuffle();

}
