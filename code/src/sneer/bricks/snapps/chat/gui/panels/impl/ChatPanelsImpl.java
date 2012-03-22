package sneer.bricks.snapps.chat.gui.panels.impl;

import javax.swing.JPanel;

import basis.lang.Consumer;

import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.chat.gui.panels.ChatPanels;

class ChatPanelsImpl implements ChatPanels {

	@Override
	public JPanel newPanel(ListSignal<ChatMessage> messages, Consumer<String> messageSender) {
		return new ChatPanelImpl(messages, messageSender);
	}

}
