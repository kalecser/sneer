package sneer.bricks.snapps.chat.gui.panels.impl;

import javax.swing.JPanel;

import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.snapps.chat.gui.panels.ChatPanels;
import sneer.bricks.snapps.chat.gui.panels.Message;
import basis.lang.Consumer;

class ChatPanelsImpl implements ChatPanels {

	@Override
	public JPanel newPanel(ListSignal<Message> messages, Consumer<String> messageSender) {
		return new ChatPanelImpl(messages, messageSender);
	}

}
