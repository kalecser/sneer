package dfcsantos.tracks.player.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.io.log.Logger;
import dfcsantos.tracks.player.TrackContract;

class TrackContractImpl implements TrackContract {
	
	private final Thread _playingThread; //Refactor: Pass a pausable Stream to JLayer instead of suspending the thread.
	private Status _status;
	
	
	TrackContractImpl(Thread playingThread){
		_playingThread = playingThread;
		_status = Status.NEW;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void pauseResume() {
		if (_status == Status.PLAYING){
			my(Logger.class).log("Paused...");
			_playingThread.suspend();
		}
		if (_status == Status.PAUSED){
			my(Logger.class).log("Resumed...");
			_playingThread.resume();
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void dispose() {
		_playingThread.stop();
		_status = Status.DISPOSED;
	}
	
	
	@Override
	public void startPlaying() {
		_playingThread.start();
		_status = Status.PLAYING;
	}
	
	
	@Override
	public Status status() {
		return _status;
	}
}
