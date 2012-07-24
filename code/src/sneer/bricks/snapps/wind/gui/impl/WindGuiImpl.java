package sneer.bricks.snapps.wind.gui.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JPanel;

import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.chat.PrivateChat;
import sneer.bricks.snapps.chat.gui.panels.ChatPanels;
import sneer.bricks.snapps.chat.gui.panels.Message;
import sneer.bricks.snapps.wind.Wind;
import sneer.bricks.snapps.wind.gui.WindGui;
import basis.lang.Functor;

class WindGuiImpl implements WindGui {

	private final Wind _wind = my(Wind.class);
	private final JPanel chatPanel = my(ChatPanels.class).newPanel(convert(_wind.shoutsHeard()), _wind.megaphone()); 
	
	@SuppressWarnings("unused") private Object _refToAvoidGc;

	
	WindGuiImpl() {
		my(InstrumentRegistry.class).registerInstrument(this);
	} 

	
	private ListSignal<Message> convert(ListSignal<ChatMessage> shoutsHeard) {
		return my(CollectionSignals.class).adapt(shoutsHeard, new Functor<ChatMessage, Message>() { @Override public Message evaluate(ChatMessage message) {
			return my(PrivateChat.class).convert(message);
		}});
	}


	@Override
	public void init(InstrumentPanel window) {
		Container container = window.contentPane();
		container.setLayout(new BorderLayout());
		container.add(chatPanel,BorderLayout.CENTER);
	}

	
	@Override public int defaultHeight() { return 248; }
	@Override public String title() { return "Wind"; }
	
}
