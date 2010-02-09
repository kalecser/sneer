package dfcsantos.tracks.sharing.playingtracks.server.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.sharing.playingtracks.protocol.NullPlayingTrack;
import dfcsantos.tracks.sharing.playingtracks.protocol.PlayingTrack;
import dfcsantos.tracks.sharing.playingtracks.server.PlayingTrackServer;
import dfcsantos.wusic.Wusic;

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
