package sneer.bricks.snapps.chat.gui.panels;

import javax.swing.JPanel;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import basis.brickness.Brick;
import basis.lang.Consumer;

@Brick(GUI.class)
public interface ChatPanels {

	JPanel newPanel(ListSignal<Message> messages, Consumer<String> messageSender);

}
