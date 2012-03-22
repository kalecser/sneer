package dfcsantos.music.ui.view;

import basis.brickness.Brick;
import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.skin.main.instrumentregistry.Instrument;

@Brick(GUI.class)
public interface MusicView extends Instrument {
	
	void setListener(MusicViewListener listener);
	
}
