package dfcsantos.music.ui.view;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.skin.main.instrumentregistry.Instrument;
import sneer.foundation.brickness.Brick;

@Brick(GUI.class)
public interface MusicView extends Instrument {
	
	void setListener(MusicViewListener listener);
	
}
