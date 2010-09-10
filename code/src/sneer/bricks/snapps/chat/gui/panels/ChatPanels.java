package sneer.bricks.snapps.chat.gui.panels;

import javax.swing.JPanel;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick(GUI.class)
public interface ChatPanels {

	JPanel newPanel(ListSignal<ChatMessage> messages, Consumer<String> messageSender);

}
