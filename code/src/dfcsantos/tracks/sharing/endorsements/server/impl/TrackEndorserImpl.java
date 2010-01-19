package dfcsantos.tracks.sharing.endorsements.server.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.algorithms.crypto.Crypto;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Closure;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;
import dfcsantos.tracks.sharing.endorsements.protocol.TrackEndorsement;
import dfcsantos.tracks.sharing.endorsements.server.TrackEndorser;

class TrackEndorserImpl implements TrackEndorser {

	private static final File[] FILE_ARRAY = new File[0];

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGC;

	{
		_refToAvoidGC = my(Timer.class).wakeUpNowAndEvery(60 * 1000, new Closure() { @Override public void run() {
			endorseRandomTrack();
		}});
	}

	private void endorseRandomTrack() {
		File[] tracks = listMp3Files(sharedTracksFolder());
		if (tracks.length == 0) return;

		endorseTrack(pickOneAtRandom(tracks));
	}

	private void endorseTrack(final File track) {
		Sneer1024 hash;

		try {
			hash = my(Crypto.class).digest(track);
		} catch (IOException e) {
			my(Logger.class).log("Error computing hash for ", track);
			return;
		}

		if (my(FileMap.class).getFile(hash) == null) {
			my(Logger.class).log("Track not mapped: ", track);
			return;
		}

		my(TupleSpace.class).acquire(new TrackEndorsement(relativePath(track), track.lastModified(), hash));
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
		String result = my(Lang.class).strings().substringAfterLast(track.getAbsolutePath(), prefix);
		return result.replace('\\', '/');
	}

	private String sharedTracksPath() {
		return my(TracksFolderKeeper.class).sharedTracksFolder().currentValue().getAbsolutePath();
	}

}