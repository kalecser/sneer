package dfcsantos.tracks.endorsements.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import dfcsantos.tracks.endorsements.TrackEndorsement;
import dfcsantos.tracks.endorsements.TrackEndorser;
import dfcsantos.tracks.folder.TracksFolderKeeper;

public class TrackEndorserImpl implements TrackEndorser {
	
	private static final File[] FILE_ARRAY = new File[0];
	
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidCG;

	
	{
		_refToAvoidCG = my(Timer.class).wakeUpNowAndEvery(15*1000, new Runnable(){@Override public void run() {
			endorseRandomTrack();
		}});
	}

	
	private void endorseRandomTrack() {
		File[] tracks = listMp3Files(sharedTracksFolder());
		if (tracks.length == 0) return;
		
		endorseTrack(pickOneAtRandom(tracks));
	}


	private void endorseTrack(final File track) {
		try {
			tryToEndorseTrack(track);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error reading track: " + track, "This is not a critical error but might indicate problems with your hard drive.");
		}
	}
	
	
	private void tryToEndorseTrack(File track) throws IOException {
		Sneer1024 hash = my(FileMap.class).put(track);
		my(TupleSpace.class).publish(new TrackEndorsement(relativePath(track), track.lastModified(), hash));
	}
	
	
	private <T> T pickOneAtRandom(T[] list) {
		return list[new Random().nextInt(list.length)];
	}


	private File[] listMp3Files(File a) {
		return my(IO.class).files().listFiles(a, new String[] {"mp3","MP3"}, true).toArray(FILE_ARRAY);
	}


	private File sharedTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder().currentValue();
	}


	private String relativePath(File track) {
		String prefix = sharedTracksPath() + File.separator;
		String result = my(Lang.class).strings().substringAfter(track.getAbsolutePath(), prefix);
		return result.replace('\\', '/');
	}


	private String sharedTracksPath() {
		return my(TracksFolderKeeper.class).peerTracksFolder().currentValue().getAbsolutePath();
	}

}
