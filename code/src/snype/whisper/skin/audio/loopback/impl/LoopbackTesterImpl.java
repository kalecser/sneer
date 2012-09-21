package snype.whisper.skin.audio.loopback.impl;

import static basis.environments.Environments.my;

import java.io.ByteArrayOutputStream;

import sneer.bricks.hardware.io.log.Logger;
import snype.whisper.skin.audio.loopback.LoopbackTester;


class LoopbackTesterImpl implements LoopbackTester{

	@Override
	public void stop() {
		Recorder.stop();
		Player.stop();
		my(Logger.class).log("Audio Loopback Test stopped.");
	}

	@Override
	public boolean start() {
		my(Logger.class).log("Audio Loopback Test started.");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		boolean isRunning = Player.start(buffer) & Recorder.start(buffer);
		if(!isRunning) stop();
		return isRunning;
	}	
}