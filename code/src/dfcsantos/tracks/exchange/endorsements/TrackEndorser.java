package dfcsantos.tracks.exchange.endorsements;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface TrackEndorser {

	void setOnOffSwitch(Signal<Boolean> onOffSwitch);

}
