package sneer.bricks.snapps.chat.gui.panels.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.skin.widgets.reactive.NotificationPolicy;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.skin.widgets.reactive.autoscroll.ReactiveAutoScroll;
import sneer.bricks.snapps.chat.gui.panels.Message;
import basis.lang.Consumer;

class ChatPanelImpl extends JPanel {

	private final ListSignal<Message> _messages;

	private final JScrollPane _listScrollPane;
	private final JTextPane _messagesList = new JTextPane();
	private final MessagePainter _painter = new MessagePainter((DefaultStyledDocument) _messagesList.getStyledDocument());
	private final TextWidget<JTextPane> _inputPane;
	private JSplitPane split;
	private boolean firstTime = true;

	@SuppressWarnings("unused") private Object _refToAvoidGc;
	

	ChatPanelImpl(ListSignal<Message> messages, Consumer<String> messageSender) {
		_messages = messages;
		_listScrollPane = my(ReactiveAutoScroll.class).create(_messages, new Consumer<CollectionChange<Message>>() { @Override public void consume(CollectionChange<Message> change) {
			if (!change.elementsRemoved().isEmpty()) {
				_painter.repaintAll(_messages);
				return;
			}
			for (Message msg : change.elementsAdded())
				_painter.append(msg);

		}});

		_inputPane = my(ReactiveWidgetFactory.class).newTextPane(
			my(Signals.class).newRegister("").output(), messageSender, NotificationPolicy.OnEnterPressed
		);
		
		init();
	} 
	
	
	private void init() {
		initGui();
		initUserAlerts();
		new ChatClipboardSupport();
	}

	
	private void initGui() {
		_listScrollPane.getViewport().add(_messagesList);
		JScrollPane inputScrollPane = new JScrollPane();
		JPanel horizontalLimit = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				preferredSize.setSize(getWidth()-30, preferredSize.getHeight());
				return preferredSize;
			}
		};
		horizontalLimit.setLayout(new BorderLayout());
		horizontalLimit.add(_inputPane.getComponent());
		inputScrollPane.getViewport().add(horizontalLimit);	
		
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _listScrollPane, inputScrollPane);
		split.setBorder(new EmptyBorder(0,0,0,0));
		split.setOpaque(false);
		split.setDividerSize(3);
		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);
		
		_messagesList.setBorder(new EmptyBorder(0,0,0,0));
		_inputPane.getComponent().setBorder(new EmptyBorder(0,0,0,0));
		
		_messagesList.addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent e) { _messagesList.setEditable(false); }
			@Override public void focusLost(FocusEvent e) {	_messagesList.setEditable(true); }
		});
	}
	

	@Override
	public void paint(Graphics g) {
		if (firstTime) {
			firstTime = false;
			split.setDividerLocation(0.8);
			_inputPane.getMainWidget().requestFocus();
		}
		super.paint(g);
	}


	private void initUserAlerts() {
		_refToAvoidGc = _messages.addReceiver(new Consumer<CollectionChange<Message>>() { @Override public void consume(CollectionChange<Message> msgs) {
			alertUserIfNecessary(msgs.elementsAdded());
		}});
	}
	
	
	private void alertUserIfNecessary(Collection<Message> newMessages) {
		Collection<Message> msgs = withoutMessagesByMe(newMessages);
		
		if (msgs.isEmpty()) return;
		
		Window window = SwingUtilities.windowForComponent(this);
		if (window == null || !window.isActive())
			alertUser(msgs);
	}


	private Collection<Message> withoutMessagesByMe(Collection<Message> newMessages) {
		Collection<Message> result = new ArrayList<Message>();
		for (Message message : newMessages)
			if (!message.isByMe()) result.add(message);
		return result;
	}

	
	private synchronized void alertUser(Collection<Message> messages) {
		String singleAuthor = singleAuthor(messages);
		boolean showAuthors = singleAuthor == null;
		String title = showAuthors ? "Chat" : singleAuthor;
		my(TrayIcons.class).messageBalloon(
			title,
			asString(messages, showAuthors)
		);
		//my(SoundPlayer.class).play(this.getClass().getResource("alert.wav"));
	}

	
	private String singleAuthor(Collection<Message> messages) {
		String firstAuthor = null;
		for (Message message : messages) {
			if (firstAuthor == null) firstAuthor = message.author();
			if (!message.author().equals(firstAuthor)) return null; //Several authors found.
		}
		return firstAuthor;
	}


	private String asString(Collection<Message> messages, boolean showAuthors) {
		StringBuilder ret = new StringBuilder();
		Iterator<Message> it = messages.iterator();
		while (it.hasNext()) {
			Message message = it.next();
			if (showAuthors) ret.append(message.author() + ": ");
			ret.append(message.text());
			if (it.hasNext()) ret.append("\n\n");
		}
		return ret.toString();
	}

	
	private final class ChatClipboardSupport implements ClipboardOwner{
		
		private ChatClipboardSupport(){
			addKeyStrokeListener();
		}

		private void addKeyStrokeListener() {
			int modifiers = getPortableSoModifiers();
			final KeyStroke ctrlc = KeyStroke.getKeyStroke(KeyEvent.VK_C, modifiers);
			_messagesList.getInputMap().put(ctrlc,  "ctrlc");
			_messagesList.getActionMap().put("ctrlc",  new AbstractAction(){@Override public void actionPerformed(ActionEvent e) {
				copySelectedMessageToClipboard();
			}});
		}
		
		@Override
		public void lostOwnership(Clipboard arg0, Transferable arg1) {
			my(Logger.class).log("Lost Clipboard Ownership.");
		}
		
		private int getPortableSoModifiers() {
			return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		}
		
		private void copySelectedMessageToClipboard() {
			StringSelection fieldContent = new StringSelection(_messagesList.getSelectedText());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(fieldContent, this);	
		}
	}
	
}
