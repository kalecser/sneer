package dfcsantos.tracks.exchange.endorsements.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.exchange.endorsements.TrackEndorsement;
import dfcsantos.tracks.exchange.endorsements.TrackEndorser;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class TrackEndorserImpl implements TrackEndorser {

	private static final int ENDORSEMENT_INTERVAL = 60 * 1000;

	private static final File[] FILE_ARRAY = new File[0];

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();

	private Random _random = new Random(my(Clock.class).time().currentValue());

	private WeakContract _timerContract;
	@SuppressWarnings("unused") private WeakContract _refToAvoidGC;

	@Override
	public void setOnOffSwitch(Signal<Boolean> onOffSwitch) {
		_onOffSwitch.set(onOffSwitch);

		_refToAvoidGC = onOffSwitch.addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isOn) {
			turnSwitch(isOn);
		}});
	}

	private void turnSwitch(Boolean isOn) {
		if (isOn) {
			my(FileServer.class);
			_timerContract = my(Timer.class).wakeUpNowAndEvery(ENDORSEMENT_INTERVAL, new Closure() { @Override public void run() {
				endorseRandomTrack();
			}});

			return;
		}

		if (_timerContract != null) { 
			_timerContract.dispose();
			_timerContract = null;
		}
	}

	private void endorseRandomTrack() {
		File[] tracks = listMp3Files(sharedTracksFolder());
		if (tracks.length == 0) return;

		endorseTrack(pickOneAtRandom(tracks));
	}

	private void endorseTrack(final File track) {
		Hash hash;

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

		my(TupleSpace.class).add(new TrackEndorsement(relativePath(track), track.lastModified(), hash));
	}

	private <T> T pickOneAtRandom(T[] list) {
		return list[_random.nextInt(list.length)];
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