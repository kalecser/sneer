package dfcsantos.tracks.client.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.announcer.TrackAnnouncement;
import dfcsantos.tracks.client.TrackClient;

public class TrackClientImpl implements TrackClient {
	@SuppressWarnings("unused")
	private final WeakContract _refToAvoidGC;

	{
		_refToAvoidGC = my(TupleSpace.class).addSubscription(TrackAnnouncement.class, new Consumer<TrackAnnouncement>(){@Override public void consume(TrackAnnouncement trackAnnouncement) {
			consumeTrackAnnouncement(trackAnnouncement);
		}});
	}

	private void consumeTrackAnnouncement(TrackAnnouncement trackAnnouncement) {
		my(FileClient.class).fetchToCache(trackAnnouncement.hash);
	}
}
