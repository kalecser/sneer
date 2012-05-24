package sneer.bricks.snapps.wind.gui.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sneer.bricks.hardware.gui.trayicon.TrayIcons;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
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
import basis.lang.Consumer;
import basis.lang.Functor;

class WindGuiImpl implements WindGui {

	private Container _container;
	private final Wind _wind = my(Wind.class);
	private final JPanel chatPanel = my(ChatPanels.class).newPanel(convert(_wind.shoutsHeard()), _wind.megaphone()); 
	
	@SuppressWarnings("unused") private Object _refToAvoidGc;

	
	public WindGuiImpl() {
		my(InstrumentRegistry.class).registerInstrument(this);
	} 

	
	private ListSignal<Message> convert(ListSignal<ChatMessage> shoutsHeard) {
		return my(CollectionSignals.class).adapt(shoutsHeard, new Functor<ChatMessage, Message>() {

			@Override
			public Message evaluate(ChatMessage message) {
				return my(PrivateChat.class).convert(message);
			}
		});
	}


	@Override
	public void init(InstrumentPanel window) {
		_container = window.contentPane();
		initGui();
		initShoutAnnouncer();
	}

	private void initGui() {
		_container.setLayout(new BorderLayout());
		_container.add(chatPanel,BorderLayout.CENTER);
	}

	private void initShoutAnnouncer() {
		_refToAvoidGc = _wind.shoutsHeard().addReceiver(new Consumer<CollectionChange<ChatMessage>>() { @Override public void consume(CollectionChange<ChatMessage> shout) {
			shoutAlert(shout.elementsAdded());
		}});
	}
	
	private void shoutAlert(Collection<ChatMessage> shouts) {
		Window window = SwingUtilities.windowForComponent(_container);
		boolean windowActive = window.isActive();
		if(windowActive) return;
		
		alertUser(shouts);
	}

	private synchronized void alertUser(Collection<ChatMessage> shouts) {
		String shoutsAsString = shoutsAsString(shouts);
		my(TrayIcons.class).messageBalloon("New shouts heard", shoutsAsString);
		// _player.play(this.getClass().getResource("alert.wav"));
	}

	private String shoutsAsString(Collection<ChatMessage> shouts) {
		StringBuilder ret = new StringBuilder();
		for (ChatMessage shout : shouts){
			if (ret.length() > 0) ret.append("\n");
			Seal publisher = shout.publisher;
			ret.append(nicknameOf(publisher) + " - " + shout.text);
		}
		return ret.toString();
	}

	private String nicknameOf(Seal publisher) {
		Signal<String> result = my(ContactSeals.class).nicknameGiven(publisher);
		return result == null
			? "Unknown"
			: result.currentValue();
	}

	@Override
	public int defaultHeight() {
		return 248;
	}

	@Override
	public String title() {
		return "Wind";
	}
}
