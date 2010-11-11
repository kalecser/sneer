package sneer.bricks.snapps.chat.gui.panels.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultStyledDocument;

import sneer.bricks.hardware.gui.trayicon.TrayIcons;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.skin.widgets.reactive.NotificationPolicy;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.skin.widgets.reactive.autoscroll.ReactiveAutoScroll;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.foundation.lang.Consumer;

class ChatPanelImpl extends JPanel {

	private final ListSignal<ChatMessage> _messages;
	private final JScrollPane _listScrollPane;
	private final JTextPane _shoutsList = new JTextPane();
	private final ShoutPainter _shoutPainter = new ShoutPainter((DefaultStyledDocument) _shoutsList.getStyledDocument());

	private final TextWidget<JTextPane> _messageInputPane;

	@SuppressWarnings("unused") private Object _refToAvoidGc;
	

	ChatPanelImpl(ListSignal<ChatMessage> messages, Consumer<String> messageSender) {
		_messages = messages;
		_listScrollPane = my(ReactiveAutoScroll.class).create(messages, new Consumer<CollectionChange<ChatMessage>>() { @Override public void consume(CollectionChange<ChatMessage> change) {
			if (!change.elementsRemoved().isEmpty()){
				_shoutPainter.repaintAllShouts(_messages);
				return;
			}

			for (ChatMessage shout : change.elementsAdded())
				_shoutPainter.appendMessage(shout);
		}});

		_messageInputPane = my(ReactiveWidgetFactory.class).newTextPane(
			my(Signals.class).newRegister("").output(), messageSender, NotificationPolicy.OnEnterPressed
		);
		
		init();
	} 
	
	private void init() {
		initGui();
		initShoutAnnouncer();
		new WindClipboardSupport();
	}

	private void initGui() {
		_listScrollPane.getViewport().add(_shoutsList);
		JScrollPane inputScrollPane = new JScrollPane();
		JPanel horizontalLimit = new JPanel(){
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				preferredSize.setSize(getWidth()-30, preferredSize.getHeight());
				return preferredSize;
			}
		};
		horizontalLimit.setLayout(new BorderLayout());
		horizontalLimit.add(_messageInputPane.getComponent());
		inputScrollPane.getViewport().add(horizontalLimit);	
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _listScrollPane, inputScrollPane);
		split.setBorder(new EmptyBorder(0,0,0,0));

		split.setOpaque(false);
		split.setDividerLocation((int) (getHeight()*0.68)); 
		split.setDividerSize(3);
		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);

		_shoutsList.setBorder(new EmptyBorder(0,0,0,0));
		_messageInputPane.getComponent().setBorder(new EmptyBorder(0,0,0,0));
		
		_shoutsList.addFocusListener(new FocusListener(){
			@Override public void focusGained(FocusEvent e) { 	_shoutsList.setEditable(false); }
			@Override public void focusLost(FocusEvent e) {		_shoutsList.setEditable(true); }});
	}

	private void initShoutAnnouncer() {
		_refToAvoidGc = _messages.addReceiver(new Consumer<CollectionChange<ChatMessage>>() { @Override public void consume(CollectionChange<ChatMessage> shout) {
			shoutAlert(shout.elementsAdded());
		}});
	}
	
	private void shoutAlert(Collection<ChatMessage> shouts) {
		Window window = SwingUtilities.windowForComponent(this);
		boolean windowActive = window.isActive();
		if(windowActive) return;
		
		alertUser(shouts);
	}

	private synchronized void alertUser(Collection<ChatMessage> shouts) {
		String shoutsAsString = shoutsAsString(shouts);
		my(TrayIcons.class).messageBalloon("New shouts heard", shoutsAsString);
	}

	private String shoutsAsString(Collection<ChatMessage> messages) {
		StringBuilder shoutsAsString = new StringBuilder();
		for (ChatMessage message : messages){
			
			if (shoutsAsString.length() > 0){
				shoutsAsString.append("\n");
			}
			
			Seal publisher = message.publisher;
			shoutsAsString.append(nicknameOf(publisher) + " - " + message.text);
		}
		return shoutsAsString.toString();
	}

	private String nicknameOf(Seal publisher) {
		Signal<String> result = my(ContactSeals.class).nicknameGiven(publisher);
		return result == null
			? "Unknown"
			: result.currentValue();
	}

	private final class WindClipboardSupport implements ClipboardOwner{
		
		private WindClipboardSupport(){
			addKeyStrokeListener();
		}

		private void addKeyStrokeListener() {
			int modifiers = getPortableSoModifiers();
			final KeyStroke ctrlc = KeyStroke.getKeyStroke(KeyEvent.VK_C, modifiers);
			_shoutsList.getInputMap().put(ctrlc,  "ctrlc");
			_shoutsList.getActionMap().put("ctrlc",  new AbstractAction(){@Override public void actionPerformed(ActionEvent e) {
				copySelectedShoutToClipboard();
			}});
		}
		
		@Override
		public void lostOwnership(Clipboard arg0, Transferable arg1) {
			my(Logger.class).log("Lost Clipboard Ownership.");
		}
		
		private int getPortableSoModifiers() {
			return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		}
		
		private void copySelectedShoutToClipboard() {
			StringSelection fieldContent = new StringSelection(_shoutsList.getSelectedText());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(fieldContent, this);	
		}
	}


}
