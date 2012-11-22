package sneer.bricks.snapps.contacts.gui.info.impl;

import static basis.environments.Environments.my;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.codec.SealCodec;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.widgets.reactive.NotificationPolicy;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.bricks.snapps.contacts.gui.info.ContactInfoWindow;
import basis.lang.Functor;
import basis.lang.PickyConsumer;
import basis.lang.exceptions.Refusal;

class ContactInfoWindowImpl extends JFrame implements ContactInfoWindow {

	private boolean _isGuiInitialized = false;

	private JLabel _nicknameLb;
	private TextWidget<JTextField> _nicknameTF;

	private JLabel _sealLb;
	private TextWidget<JTextPane> _sealTP;
	private JScrollPane _sealScroll;


	ContactInfoWindowImpl() {
		addContactEditAction();
	}

	private void addContactEditAction() {
		my(ContactActionManager.class).addContactAction(new ContactAction(){
			@Override public boolean isEnabled() { return true; }
			@Override public boolean isVisible() { return true; }
			@Override public String caption() { return "Edit Contact...";}
			@Override public void run() { open(); }
			@Override public int positionInMenu() { return 100;	};
		}, true);
	}
	
	@Override
	public void open() {
		if(!_isGuiInitialized) {
			_isGuiInitialized = true;
			initGui();
		}
//		my(WindowBoundsSetter.class).setBestBounds(this, my(ContactActionManager.class).baseComponent());
		setLocationRelativeTo(my(ContactActionManager.class).baseComponent());
		setVisible(true);
	}
	
	private void initGui() {
		setTitle("Contact Info:");
		setResizable(false);

		_nicknameLb = new JLabel("Nickname:");
		_nicknameTF = my(ReactiveWidgetFactory.class).newTextField(
			nicknameString(), nicknameSetter(), NotificationPolicy.OnEnterPressedOrLostFocus
		);

		_sealLb = new JLabel("Seal:");
		_sealTP = my(ReactiveWidgetFactory.class).newTextPane(
			contactsFormattedSealString(), contactsSealSetter(), NotificationPolicy.OnEnterPressedOrLostFocus
		);
		_sealTP.getMainWidget().setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		_sealScroll = new JScrollPane(
			_sealTP.getMainWidget(),
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);

		setGridBagLayout(); //GridBagLayout is probably overkill now that some fields have been removed from this window.

		this.setSize(330, 220);
	}


	private PickyConsumer<String> nicknameSetter() {
		return new PickyConsumer<String>(){@Override public void consume(String value) throws Refusal {
			my(Contacts.class).nicknameSetterFor(contact()).consume(value);
		}};
	}

	
	private Signal<String> nicknameString() {
		return my(Signals.class).adaptSignal(
			my(ContactsGui.class).selectedContact(), new Functor<Contact, Signal<String>>() { @Override public Signal<String> evaluate(Contact contact) {
				return (contact == null) ? my(Signals.class).constant("") : contact.nickname();
			}}
		);
	}

	
	private Signal<String> contactsFormattedSealString() {
		return my(Signals.class).adapt( my(Signals.class).adaptSignal(
			my(ContactsGui.class).selectedContact(), new Functor<Contact, Signal<Seal>>() { @Override public Signal<Seal> evaluate(Contact contact) throws RuntimeException {
				return my(ContactSeals.class).sealGiven(contact); }}), new Functor<Seal, String>() { @Override public String evaluate(Seal seal) throws RuntimeException {
					return (seal == null) ? "" : my(SealCodec.class).formattedHexEncode(seal);
		}});
	}

	
	private PickyConsumer<String> contactsSealSetter() {
		return new PickyConsumer<String>() { @Override public void consume(String sealString) throws Refusal {
			String nick = contact().nickname().currentValue();
			ContactSeals contactSeals = my(ContactSeals.class);
			contactSeals.put(nick, contactSeals.unmarshal(sealString));
		}};
	}

	private void setGridBagLayout() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		contentPane.add(_nicknameLb,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0, 0) );

		contentPane.add(_nicknameTF.getComponent(),  new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5), 0, 0) );

		contentPane.add(_sealLb,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0, 0) );

		contentPane.add(_sealScroll,  new GridBagConstraints(0, 2, 5, 1, 0.5, 0.5, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0) );
	}
	
	private Contact contact() {
		return my(ContactsGui.class).selectedContact().currentValue();
	}

}
