package dfcsantos.tracks.endorsements.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.Lang;
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
	
	private static final File[] FILE_ARRAY = new File[0];
	
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidCG;

	
	{
		_refToAvoidCG = my(Timer.class).wakeUpNowAndEvery(60*1000, new Runnable(){@Override public void run() {
			endorseRandomTrack();
		}});
		
	}

	
	private void endorseTrack(final File track) {
		my(Threads.class).startDaemon("Endorsing Track", new Runnable() { @Override public void run() {
			try {
				tryToEndorseTrack(track);
			} catch (IOException e) {
				my(BlinkingLights.class).turnOn(LightType.ERROR, "Error reading track: " + track, "This is not a critical error but might indicate problems with your hard drive.");
			}
		}});
	}


	protected void endorseRandomTrack() {
		File[] tracks = listMp3Files(ownTracksFolder());
		if (tracks.length == 0) return;
		
		endorseTrack(pickOneAtRandom(tracks));
	}


	private <T> T pickOneAtRandom(T[] list) {
		return list[new Random().nextInt(list.length)];
	}


	private File[] listMp3Files(File a) {
		return my(IO.class).files().listFiles(a, new String[] {"mp3","MP3"}, true).toArray(FILE_ARRAY);
	}


	private File ownTracksFolder() {
		return my(OwnTracksFolderKeeper.class).ownTracksFolder().currentValue();
	}


	private void tryToEndorseTrack(File track) throws IOException {
		Sneer1024 hash = my(FileReader.class).readIntoTheFileCache(track);
		my(TupleSpace.class).publish(new TrackEndorsement(relativePath(track), track.lastModified(), hash));
	}


	private String relativePath(File track) {
		String prefix = ownTracksPath() + File.separator;
		String result = my(Lang.class).strings().substringAfter(track.getAbsolutePath(), prefix);
		return result.replace('\\', '/');
	}


	private String ownTracksPath() {
		return my(OwnTracksFolderKeeper.class).ownTracksFolder().currentValue().getAbsolutePath();
	}

}
