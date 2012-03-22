package dfcsantos.tracks.exchange;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface TrackExchange {

	void setOnOffSwitch(Signal<Boolean> onOffSwitch);
}
