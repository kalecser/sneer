package spikes.sneer.bricks.snapps.whisper.speex;

import basis.brickness.Brick;

@Brick
public interface Speex {

	Encoder createEncoder();

	Decoder createDecoder();

}
