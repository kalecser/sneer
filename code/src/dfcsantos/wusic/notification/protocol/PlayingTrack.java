package dfcsantos.wusic.notification.protocol;

import sneer.bricks.pulp.tuples.Tuple;

public class PlayingTrack extends Tuple {

	public final String name;

	public PlayingTrack(String name_) {
		name = name_;
	}

}
