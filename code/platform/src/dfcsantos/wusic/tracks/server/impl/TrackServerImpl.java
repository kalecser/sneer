package dfcsantos.wusic.tracks.server.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.wusic.TrackPlayed;
import dfcsantos.wusic.Track;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.tracks.server.SongServer;

public class TrackServerImpl implements SongServer {
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidCG;


	{
		_refToAvoidCG = my(Wusic.class).trackPlayed().addReceiver(new Consumer<Track>() { @Override public void consume(Track track) {
			announceTrack(track);
		}});
		
		my(TupleSpace.class).addSubscription(TrackPlayed.class, new Consumer<TrackPlayed>() { @Override public void consume(TrackPlayed trackPlayed) {
			consumeAnnouncement(trackPlayed);
		}});

	}

	
	private void announceTrack(final Track track) {
		my(Threads.class).startDaemon("Announcing Played Song", new Runnable() { @Override public void run() {
			try {
				tryToAnnounceTrack(track);
			} catch (IOException e) {
				my(BlinkingLights.class).turnOn(LightType.ERROR, "Error trying to announce played song: " + track, "This is not a critical error but might indicate problems with your hard drive.");
			}
		}});
	}


	private void tryToAnnounceTrack(Track track) throws IOException {
		Sneer1024 hash = my(Hasher.class).hash(track.file());
		String path = track.file().getAbsolutePath();
		my(TupleSpace.class).publish(new TrackPlayed(path, hash));
	}

	
	private void consumeAnnouncement(@SuppressWarnings("unused") TrackPlayed songPlayed) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

}
