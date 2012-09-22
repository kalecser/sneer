package snype.whisper.speextuples;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.software.bricks.snapploader.Snapp;
import basis.brickness.Brick;

@Snapp
@Brick
public interface SpeexTuples2 {

	static final int FRAMES_PER_AUDIO_PACKET = 10;

	void addTalker(Seal who);

	void removeTalker(Seal who);

	boolean hasTalkers();

}
