package dfcsantos.tracks.client;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick
public interface TrackClient {

	Signal<Integer> numberOfPeerTracks();

}
