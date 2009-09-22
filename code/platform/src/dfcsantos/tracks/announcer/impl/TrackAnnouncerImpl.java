package dfcsantos.tracks.announcer.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import dfcsantos.tracks.announcer.TrackAnnouncer;
import dfcsantos.tracks.announcer.TrackAnnouncement;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;

public class TrackAnnouncerImpl implements TrackAnnouncer {
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidCG;

	{
		_refToAvoidCG = my(Timer.class).wakeUpNowAndEvery(60*1000, new Runnable(){@Override public void run() {
			announceRandomTrack();
		}});
		
	}

	
	private void announceTrack(final File track) {
		my(Threads.class).startDaemon("Announcing Played Song", new Runnable() { @Override public void run() {
			try {
				tryToAnnounceTrack(track);
			} catch (IOException e) {
				my(BlinkingLights.class).turnOn(LightType.ERROR, "Error trying to announce played song: " + track, "This is not a critical error but might indicate problems with your hard drive.");
			}
		}});
	}


	protected void announceRandomTrack() {

		ArrayList<File> tracks = new ArrayList<File>(my(IO.class).files().listFiles(currentFolder(), new String[] {"mp3","MP3"}, true));
		if (tracks.isEmpty()) return;
		Random random = new Random();
		int randomIndex = random.nextInt( tracks.size());
		announceTrack(tracks.get(randomIndex));
	}


	private File currentFolder() {
		return my(OwnTracksFolderKeeper.class).ownTracksFolder().currentValue();
	}


	private void tryToAnnounceTrack(File track) throws IOException {
		Sneer1024 hash = my(Hasher.class).hash(track);
		String path = track.getAbsolutePath();
		my(TupleSpace.class).publish(new TrackAnnouncement(path, hash));
	}

}
