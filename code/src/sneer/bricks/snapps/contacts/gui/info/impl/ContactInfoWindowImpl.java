package sneer.bricks.snapps.contacts.gui.info.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.log.Logger;
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
import sneer.bricks.skin.main.synth.scroll.SynthScrolls;
import sneer.bricks.skin.widgets.reactive.LabelProvider;
import sneer.bricks.skin.widgets.reactive.ListWidget;
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

	private JLabel _friendsLb;
	private JComboBox _friendsCb;
	private JButton _acceptFriendBt;

	private ListWidget<InternetAddress> _inetAddressesList;
	private InternetAddress _selectedAdress;
	private JPanel _inetAddressesPanel;
	private JScrollPane _inetAddressesScroll;

	private JLabel _hostLb;
	private JTextField _hostTF;

	private JLabel _portLb;
	private JTextField _portTF;

	private JButton _newBt;
	private JButton _saveBt;
	private JButton _deleteBt;

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

		_friendsLb = new JLabel("Friends:");
		_friendsCb = new JComboBox(new String[] { "Whatsit", "Whatsisname", "Whatsername", "Whatchamacallit" });
		_acceptFriendBt = new JButton("Accept");

		_inetAddressesList = my(ReactiveWidgetFactory.class).newList(new ContactInternetAddressList().addresses(), 
			new LabelProvider<InternetAddress>() {
				@Override public Signal<Image> imageFor(InternetAddress element) {
					return my(Signals.class).constant(null);
				}

				@Override public Signal<String> textFor(InternetAddress element) {
					return my(Signals.class).constant(element.host() + " : " + element.port().currentValue());
				}
			}
		);

		 _inetAddressesPanel = new JPanel();
		_inetAddressesScroll = my(SynthScrolls.class).create();
		_inetAddressesScroll.getViewport().add(addresses());

		_hostLb = new JLabel("Host:");
		_hostTF = new JTextField();

		_portLb = new JLabel("Port:");
		_portTF = new JTextField();

		_newBt = new JButton("New");
		_newBt.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			newInternetAddress();
		}});

		_saveBt = new JButton("Save");
		_saveBt.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			saveInternetAddress();
		}});

		_deleteBt = new JButton("Delete");
		_deleteBt.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			delInternetAddress();
		}});

		setGridBagLayout();
		addListSelectionListestener();

		this.setSize(330, 310);
	}

	private PickyConsumer<String> nicknameSetter() {
		PickyConsumer<String> nicknameSetter = new PickyConsumer<String>(){@Override public void consume(String value) throws Refusal {
			my(Contacts.class).nicknameSetterFor(selectedContact()).consume(value);
		}};
		return nicknameSetter;
	}

	private Signal<String> nicknameString() {
		Signal<String> nickname = my(Signals.class).adaptSignal(
			my(ContactsGui.class).selectedContact(), new Functor<Contact, Signal<String>>() { @Override public Signal<String> evaluate(Contact contact) {
				return (contact == null) ? my(Signals.class).constant("") : contact.nickname();
			}}
		);
		return nickname;
	}

	private Signal<String> contactsFormattedSealString() {
		return my(Signals.class).adapt(
			my(Signals.class).adaptSignal(
				my(ContactsGui.class).selectedContact(),
				new Functor<Contact, Signal<Seal>>() { @Override public Signal<Seal> evaluate(Contact contact) throws RuntimeException {
					return my(ContactSeals.class).sealGiven(contact);
				}}
			), new Functor<Seal, String>() { @Override public String evaluate(Seal seal) throws RuntimeException {
				return (seal == null) ? "" : my(SealCodec.class).formattedHexEncode(seal);
			}});
	}

	private PickyConsumer<String> contactsSealSetter() {
		return new PickyConsumer<String>() { @Override public void consume(String sealString) throws Refusal {
			String nick = selectedContact().nickname().currentValue();
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

		contentPane.add(_friendsLb,  new GridBagConstraints(0, 3, 2, 1, 1.0, 0.2, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0) );

		contentPane.add(_friendsCb,  new GridBagConstraints(0, 3, 2, 1, 1.0, 0.2, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0) );

		contentPane.add(_acceptFriendBt,  new GridBagConstraints(0, 3, 2, 1, 1.0, 0.2, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0) );

		_inetAddressesPanel.setLayout(new GridBagLayout());
		_inetAddressesPanel.add(_inetAddressesScroll,  new GridBagConstraints(0, 1, 12, 1, 1.0, 1.0, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0) );

		_inetAddressesPanel.add(_hostLb, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,5,0,5), 0, 0) );

		_inetAddressesPanel.add(_hostTF, new GridBagConstraints(1, 2, 7, 1, 2.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0) );

		_inetAddressesPanel.add(_portLb, new GridBagConstraints(9, 2, 1, 1, 0.0, 0.0, 
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0, 0) );
		
		_inetAddressesPanel.add(_portTF, new GridBagConstraints(10, 2, 2, 1, 0.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,5), 0, 0) );
		
		_inetAddressesPanel.add(_newBt, new GridBagConstraints(7, 4, 1, 1, 0.0, 0.0, 
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0) );

		_inetAddressesPanel.add(_saveBt, new GridBagConstraints(8, 4, 3, 1, 0.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0) );

		_inetAddressesPanel.add(_deleteBt, new GridBagConstraints(11, 4, 1, 1, 0.0, 0.0, 
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0) );
	}
	
	private void addListSelectionListestener() {
		addresses().getSelectionModel().addListSelectionListener(
			new ListSelectionListener() { @Override public void valueChanged(ListSelectionEvent e) {
				InternetAddress selected = (InternetAddress) addresses().getSelectedValue();
					if (selected == null) {
						cleanFields();
						return;
					}

					_selectedAdress = selected;
					_hostTF.setText(_selectedAdress.host());
					_portTF.setText(""+_selectedAdress.port());
			}}
		);
	}

	private void logPortValueException(Exception e) {
		my(BlinkingLights.class).turnOn(LightType.ERROR, "Invalid Port Value: " + _portTF.getText(), e.getMessage(), e, 20000);
		my(Logger.class).log("Invalid Port Value: {}", _portTF.getText());
		_portTF.requestFocus();
	}

	private void saveInternetAddress() {
		try {
			InternetAddress address = _selectedAdress;
			if (address == null || _hostTF.getText().trim().length() == 0) return;

			if (_selectedAdress.host().equals(_hostTF.getText()) &&
				_selectedAdress.port().currentValue() == Integer.parseInt(_portTF.getText()) &&
				_selectedAdress.contact() == my(ContactsGui.class).selectedContact().currentValue()) {

				_inetAddressesList.clearSelection();
				return;
			}

			newInternetAddress();
			my(InternetAddressKeeper.class).remove(address);
			_inetAddressesList.clearSelection();
		} catch (NumberFormatException e) {
			logPortValueException(e);			
		}
	}

	private void delInternetAddress() {
		InternetAddress address = _selectedAdress;
		if(address == null || _hostTF.getText().trim().length() == 0) return;

		my(InternetAddressKeeper.class).remove(address);
		_inetAddressesList.clearSelection();
	}

	private void newInternetAddress() {
		try{
			String host = _hostTF.getText();
			if (host == null || host.trim().length() == 0) return;
			
			int port = Integer.parseInt(_portTF.getText());
			my(InternetAddressKeeper.class).add(selectedContact(), host, port);

			_inetAddressesList.clearSelection();
			cleanFields();
		} catch (NumberFormatException e) {
			logPortValueException(e);			
		}
	}

	private JList addresses() {
		return _inetAddressesList.getMainWidget();
	}

	private void cleanFields() {
		_hostTF.setText("");
		_portTF.setText("");
	}

	private Contact selectedContact() {
		return my(ContactsGui.class).selectedContact().currentValue();
	}

}
