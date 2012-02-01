package sneer.bricks.snapps.contacts.gui.info.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.codec.SealCodec;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.widgets.reactive.NotificationPolicy;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.bricks.snapps.contacts.gui.info.ContactInfoWindow;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.PickyConsumer;
import sneer.foundation.lang.exceptions.Refusal;

class ContactInfoWindowImpl extends JFrame implements ContactInfoWindow {

	private boolean _isGuiInitialized = false;

	private JLabel _nicknameLb;
	private TextWidget<JTextField> _nicknameTF;

	private JLabel _sealLb;
	private TextWidget<JTextPane> _sealTP;
	private JScrollPane _sealScroll;

	private JPanel _inetAddressesPanel;

	private JLabel _hostLb;
	private JTextField _hostTF;

	private JLabel _portLb;
	private JTextField _portTF;

	private JButton closeButton;

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
	
	private void open() {
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

		 _inetAddressesPanel = new JPanel();

		_hostLb = new JLabel("Host Address (Optional)");
		_hostTF = new JTextField();

		_portLb = new JLabel("Port (Optional)");
		_portTF = new JTextField();

		closeButton = new JButton("Close"); closeButton.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			close();
		}});

		setGridBagLayout();
		initInternetAddressFields();

		this.setSize(330, 310);
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
			my(ContactSeals.class).put(nick, decode(sealString));
		}};
	}

	
	private Seal decode(String sealString) throws Refusal {
		if (sealString == null) return null;

		String cleanedSealString = my(Lang.class).strings().deleteWhitespace(sealString);
		if (cleanedSealString.isEmpty()) return null;

		try {
			return my(SealCodec.class).hexDecode(cleanedSealString);
		} catch (DecodeException de) {
			throw new Refusal(de.getMessage());
		}
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

		contentPane.add(_inetAddressesPanel,  new GridBagConstraints(0, 3, 2, 1, 1.0, 0.2, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0) );

		_inetAddressesPanel.setLayout(new GridBagLayout());

		_inetAddressesPanel.add(_hostLb, new GridBagConstraints(0, 2, 6, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,3,0,0), 0, 0) );

		_inetAddressesPanel.add(_hostTF, new GridBagConstraints(0, 3, 6, 1, 2.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0) );

		_inetAddressesPanel.add(_portLb, new GridBagConstraints(6, 2, 6, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,3,0,0), 0, 0) );
		
		_inetAddressesPanel.add(_portTF, new GridBagConstraints(6, 3, 6, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0) );
		
		_inetAddressesPanel.add(closeButton, new GridBagConstraints(8, 4, 3, 1, 0.0, 0.0, 
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,0,5,0), 0, 0) );
	}
	
	
	private void initInternetAddressFields() {
		InternetAddress address = my(InternetAddressKeeper.class).get(contact());
		if (address == null) {
			cleanFields();
			return;
		}

		_hostTF.setText(address.host());
		_portTF.setText(""+address.port());
	}

	
	private void display(Refusal r) {
		my(BlinkingLights.class).turnOn(LightType.ERROR, "Invalid Host or Port Value", r.getMessage(), r, 20000);
	}

	
	private void close() {
		setVisible(false);
		
		if (host().isEmpty()) {
			my(InternetAddressKeeper.class).remove(contact());
			return;
		}
		
		try {
			my(InternetAddressKeeper.class).put(contact(), host(), port());
		} catch (Refusal r) {
			display(r);
		}
	}

	private String host() {
		return textIn(_hostTF);
	}
	private int port() throws Refusal {
		String result = textIn(_portTF);
		try {
			return Integer.parseInt(result);
		} catch (NumberFormatException e) {
			throw new Refusal("Not a valid port number: " + result);
		}
	}

	private String textIn(JTextField field) {
		String text = field.getText();
		return text == null ? "" : text.trim();
	}


	private void cleanFields() {
		_hostTF.setText("");
		_portTF.setText("");
	}

	
	private Contact contact() {
		return my(ContactsGui.class).selectedContact().currentValue();
	}

}
