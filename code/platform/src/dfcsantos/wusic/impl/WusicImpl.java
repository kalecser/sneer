package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;
import dfcsantos.tracks.player.TrackContract;
import dfcsantos.tracks.player.TrackPlayer;
import dfcsantos.wusic.Track;
import dfcsantos.wusic.Wusic;

public class WusicImpl implements Wusic {

	private Enumeration<Track> _playlist;
	private final Register<String> _trackPlaying = my(Signals.class).newRegister("");
	private TrackContract _currentTrackContract;
	private TrackPlayer _trackPlayer = my(TrackPlayer.class);
	@SuppressWarnings("unused")
	private final WeakContract _refToAvoidGC;
	
	{	
		_refToAvoidGC = my(OwnTracksFolderKeeper.class).ownTracksFolder().addReceiver(new Consumer<File>() {@Override public void consume(File ownTracksFolder) {
			_playlist = new RecursiveFolderPlaylist(ownTracksFolder);
		}});
	}
	
	@Override
	public void setMyTracksFolder(File ownTracksFolder) {
		my(OwnTracksFolderKeeper.class).setOwnTracksFolder(ownTracksFolder);
	}

	@Override
	public void pauseResume(){
		_currentTrackContract.pauseResume();
	}
	

	@Override
	public void skip() {
		stop();
		playNextTrack();
	}
	
	
	private void playNextTrack() {
		Track trackToPlay = nextTrack();
		if (trackToPlay == null) return;
		play(trackToPlay);
	}


	private Track nextTrack()  {
		if (!_playlist.hasMoreElements()) {
			my(BlinkingLights.class).turnOn(LightType.WARN, "No songs found", "Please choose a folder with MP3 files in it or in its subfolders.", 10000);
		}
		return _playlist.nextElement();
	}


	private void play(final Track track) {
		FileInputStream stream = openFileStream(track);
		if (stream == null) return;
		_trackPlaying.setter().consume(track.info());
		_currentTrackContract = _trackPlayer.startPlaying(stream, new Runnable() { @Override public void run() {
			playNextTrack();
		}});
	}


	private FileInputStream openFileStream(Track trackToPlay) {
		try {
			return new FileInputStream(trackToPlay.file());
		} catch (FileNotFoundException e) {
			my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to find file " + trackToPlay.file() , "File might have been deleted manually.", 15000);
			return null;
		}
	}


	private void stop() {
		if (_currentTrackContract != null)
			_currentTrackContract.dispose();
	}

	@Override
	public Signal<String> trackPlaying() {
		return _trackPlaying.output();
	}


	@Override
	public void chooseTrackSource(TrackSource source) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}


	@Override
	public void meToo() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}


	@Override
	public void noWay() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}



	@Override
	public void start() {
		playNextTrack();
	}
}
