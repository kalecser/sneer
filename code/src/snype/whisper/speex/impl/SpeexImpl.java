package snype.whisper.speex.impl;

import snype.whisper.speex.Decoder;
import snype.whisper.speex.Encoder;
import snype.whisper.speex.Speex;

class SpeexImpl implements Speex {

	@Override
	public Decoder createDecoder() {
		return new DecoderImpl();
	}

	@Override
	public Encoder createEncoder() {
		return new EncoderImpl();
	}

}
