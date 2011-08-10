package dfcsantos.music.ui.view;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.skin.main.instrumentregistry.Instrument;
import sneer.foundation.brickness.Brick;

@Brick(GUI.class)
public interface MusicView {
	
	Instrument initInstrument(MusicViewListener listener);
	void setVolume(int volume);
	
}
