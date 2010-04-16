package dfcsantos.tracks.endorsements.server;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface TrackEndorser {

	void setOnOffSwitch(Signal<Boolean> onOffSwitch);

}
