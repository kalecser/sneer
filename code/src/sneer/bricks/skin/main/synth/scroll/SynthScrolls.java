package sneer.bricks.skin.main.synth.scroll;

import javax.swing.JScrollPane;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.foundation.brickness.Brick;

@Brick(GUI.class)
public interface SynthScrolls {

	JScrollPane create();
}
