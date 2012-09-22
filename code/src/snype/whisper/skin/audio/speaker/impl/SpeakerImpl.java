package snype.whisper.skin.audio.speaker.impl;

import javax.sound.sampled.LineUnavailableException;

import snype.whisper.skin.audio.speaker.Speaker;

class SpeakerImpl implements Speaker {

	@Override
	public Line acquireLine() throws LineUnavailableException {
		return new LineImpl();
	}
	

}
