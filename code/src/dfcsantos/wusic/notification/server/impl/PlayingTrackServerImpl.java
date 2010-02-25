package dfcsantos.wusic.notification.server.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.notification.protocol.NullPlayingTrack;
import dfcsantos.wusic.notification.protocol.PlayingTrack;
import dfcsantos.wusic.notification.server.PlayingTrackServer;

class PlayingTrackServerImpl implements PlayingTrackServer {

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGC;

	{
		_refToAvoidGC = my(Wusic.class).playingTrack().addReceiver(new Consumer<Track>() { @Override public void consume(Track playingTrack) {
			broadcastPlayingTrack(playingTrack);
		}});
	}

	private void broadcastPlayingTrack(Track playingTrack) {
		my(TupleSpace.class).acquire(
			(playingTrack == null) ? new NullPlayingTrack() : new PlayingTrack(playingTrack.name())
		);
	}

}
