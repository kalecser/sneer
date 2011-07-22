package dfcsantos.tracks.exchange;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface TrackExchange {

	void setOnOffSwitch(Signal<Boolean> onOffSwitch);
}
