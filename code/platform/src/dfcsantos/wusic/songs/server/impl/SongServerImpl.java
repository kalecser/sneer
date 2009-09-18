package dfcsantos.wusic.songs.server.impl;

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
import dfcsantos.wusic.SongPlayed;
import dfcsantos.wusic.Track;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.songs.server.SongServer;

public class SongServerImpl implements SongServer {
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidCG;


	{
		_refToAvoidCG = my(Wusic.class).songPlayed().addReceiver(new Consumer<Track>() { @Override public void consume(Track song) {
			announceSong(song);
		}});
		
		my(TupleSpace.class).addSubscription(SongPlayed.class, new Consumer<SongPlayed>() { @Override public void consume(SongPlayed songPlayed) {
			consumeAnnouncement(songPlayed);
		}});

	}

	
	private void announceSong(final Track song) {
		my(Threads.class).startDaemon("Announcing Played Song", new Runnable() { @Override public void run() {
			try {
				tryToAnnounceSong(song);
			} catch (IOException e) {
				my(BlinkingLights.class).turnOn(LightType.ERROR, "Error trying to announce played song: " + song, "This is not a critical error but might indicate problems with your hard drive.");
			}
		}});
	}


	private void tryToAnnounceSong(Track song) throws IOException {
		Sneer1024 hash = my(Hasher.class).hash(song.file());
		String path = song.file().getAbsolutePath();
		my(TupleSpace.class).publish(new SongPlayed(path, hash));
	}

	
	private void consumeAnnouncement(@SuppressWarnings("unused") SongPlayed songPlayed) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

}
