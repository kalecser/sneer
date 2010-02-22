package dfcsantos.tracks.sharing.endorsements.client;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface TrackClient {

	void setOnOffSwitch(Signal<Boolean> onOffSwitch);

}
