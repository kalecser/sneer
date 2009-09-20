package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;

import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.FolderConfig;
import dfcsantos.songplayer.SongContract;
import dfcsantos.songplayer.SongPlayer;
import dfcsantos.wusic.Track;
import dfcsantos.wusic.Wusic;

public class WusicImpl implements Wusic {

	private Enumeration<Track> _playlist;
	private final Register<String> _songPlaying = my(Signals.class).newRegister("");
	private SongContract _currentSongContract;
	private SongPlayer _songPlayer = my(SongPlayer.class);
	private EventNotifier<Track> _songPlayed = my(EventNotifiers.class).newInstance();
	
	{	
		setMySongsFolder(defaultSongsFolder());
	}
	
	@Override
	public void setMySongsFolder(File playlistFolder) {
		_playlist = new RecursiveFolderPlaylist(playlistFolder);
	}

	
	
	private File defaultSongsFolder() {
		File result = new File(my(FolderConfig.class).storageFolder().get() ,"media/songs");
		result.mkdirs();
		return result;
	}



	@Override
	public void pauseResume(){
		_currentSongContract.pauseResume();
	}
	

	@Override
	public void skip() {
		stop();
		playNextSong();
	}
	
	
	@Override
	public EventSource<Track> songPlayed() {
		return _songPlayed.output();
	}


	private void playNextSong() {
		Track songToPlay = nextSong();
		if (songToPlay == null) return;
		play(songToPlay);
	}


	private Track nextSong()  {
		if (!_playlist.hasMoreElements()) {
			my(BlinkingLights.class).turnOn(LightType.WARN, "No songs found", "Please choose a folder with MP3 files in it or in its subfolders.", 10000);
		}
		return _playlist.nextElement();
	}


	private void play(final Track song) {
		FileInputStream stream = openFileStream(song);
		if (stream == null) return;
		_currentSongContract = _songPlayer.startPlaying(stream, new Runnable() { @Override public void run() {
			_songPlayed.notifyReceivers(song);
			playNextSong();
		}});
	}


	private FileInputStream openFileStream(Track songToPlay) {
		try {
			return new FileInputStream(songToPlay.file());
		} catch (FileNotFoundException e) {
			my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to find file " + songToPlay.file() , "File might have been deleted manually.", 15000);
			return null;
		}
	}


	private void stop() {
		if (_currentSongContract != null)
			_currentSongContract.dispose();
	}

	@Override
	public Signal<String> songPlaying() {
		return _songPlaying.output();
	}


	@Override
	public void chooseSongSource(SongSource source) {
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
		playNextSong();
	}
}
