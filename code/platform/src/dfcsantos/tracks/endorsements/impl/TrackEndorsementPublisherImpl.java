package dfcsantos.tracks.endorsements.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.reader.FileReader;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import dfcsantos.tracks.endorsements.TrackEndorsement;
import dfcsantos.tracks.endorsements.TrackEndorsementPublisher;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;

public class TrackEndorsementPublisherImpl implements TrackEndorsementPublisher {
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidCG;

	{
		_refToAvoidCG = my(Timer.class).wakeUpNowAndEvery(60*1000, new Runnable(){@Override public void run() {
			endorseRandomTrack();
		}});
		
	}

	
	private void endorseTrack(final File track) {
		my(Threads.class).startDaemon("Endorsing Track Played", new Runnable() { @Override public void run() {
			try {
				tryToEndorseTrack(track);
			} catch (IOException e) {
				my(BlinkingLights.class).turnOn(LightType.ERROR, "Error trying to endorse track played: " + track, "This is not a critical error but might indicate problems with your hard drive.");
			}
		}});
	}


	protected void endorseRandomTrack() {

		ArrayList<File> tracks = new ArrayList<File>(my(IO.class).files().listFiles(currentFolder(), new String[] {"mp3","MP3"}, true));
		if (tracks.isEmpty()) return;
		Random random = new Random();
		int randomIndex = random.nextInt( tracks.size());
		endorseTrack(tracks.get(randomIndex));
	}


	private File currentFolder() {
		return my(OwnTracksFolderKeeper.class).ownTracksFolder().currentValue();
	}


	private void tryToEndorseTrack(File track) throws IOException {
		Sneer1024 hash = my(FileReader.class).readIntoTheFileCache(track);
		String path = track.getAbsolutePath();
		my(TupleSpace.class).publish(new TrackEndorsement(path, track.lastModified(), hash));
	}

}
