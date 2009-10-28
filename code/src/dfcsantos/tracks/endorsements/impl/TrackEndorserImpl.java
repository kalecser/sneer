package dfcsantos.tracks.endorsements.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Random;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import dfcsantos.tracks.Tracks;
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
		Sneer1024 hash = my(FileMap.class).getHash(track);
		if (hash == null) {
			my(Logger.class).log("Track not mapped: ", track);
			return;
		}

		my(TupleSpace.class).publish(new TrackEndorsement(relativePath(track), track.lastModified(), hash));
	}
	
	
	private <T> T pickOneAtRandom(T[] list) {
		return list[new Random().nextInt(list.length)];
	}


	private File[] listMp3Files(File folder) {
		return my(Tracks.class).listMp3FilesFromFolder(folder).toArray(FILE_ARRAY);
	}


	private File sharedTracksFolder() {
		return my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
	}


	private String relativePath(File track) {
		String prefix = sharedTracksPath() + File.separator;
		String result = my(Lang.class).strings().substringAfter(track.getAbsolutePath(), prefix);
		return result.replace('\\', '/');
	}


	private String sharedTracksPath() {
		return my(TracksFolderKeeper.class).sharedTracksFolder().currentValue().getAbsolutePath();
	}

}
