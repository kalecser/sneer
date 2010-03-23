package sneer.bricks.snapps.contacts.gui.info.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.codec.SealCodec;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.ips.keeper.InternetAddress;
import sneer.bricks.network.computers.ips.keeper.InternetAddressKeeper;
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
import sneer.bricks.skin.windowboundssetter.WindowBoundsSetter;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.bricks.snapps.contacts.gui.info.ContactInfoWindow;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.PickyConsumer;
import sneer.foundation.lang.exceptions.Refusal;

class ContactInfoWindowImpl extends JFrame implements ContactInfoWindow{

	private final ContactInternetAddressList _contactAddresses = new ContactInternetAddressList();
	
	private final ListWidget<InternetAddress> _lstAddresses; {
		final Object ref[] = new Object[1];
		my(GuiThread.class).invokeAndWait(new Closure(){ @Override public void run() {//Fix Use GUI Nature
			ref[0] = my(ReactiveWidgetFactory.class).newList(_contactAddresses.addresses(), 
			new LabelProvider<InternetAddress>(){
				@Override public Signal<? extends Image> imageFor(InternetAddress element) {
					return my(Signals.class).constant(null);
				}

				@Override public Signal<String> textFor(InternetAddress element) {
					return my(Signals.class).constant(element.host()+" : "+element.port());
				}});
		}});
		_lstAddresses = (ListWidget<InternetAddress>) ref[0];
	}
	
	private final JTextField _host = new JTextField();
	private final JTextField _port = new JTextField();
	private TextWidget<JTextPane> _seal;
	private InternetAddress _selectedAdress;
	
	private boolean _isGuiInitialized = false;
	private TextWidget<JTextField> _txtNickname;

	ContactInfoWindowImpl() {
		addContactEditAction();
	}

	private void addContactEditAction() {
		my(ContactActionManager.class).addContactAction(new ContactAction(){
			@Override public boolean isEnabled() { return true; }
			@Override public boolean isVisible() { return true; }
			@Override public String caption() { return "Edit Contact...";}
			@Override public void run() { open(); }
		}, true);
	}
	
	private void open() {
		if(!_isGuiInitialized) {
			_isGuiInitialized = true;
			initGui();
		}
		my(WindowBoundsSetter.class).setBestBounds(this, my(ContactActionManager.class).baseComponent());
		setVisible(true);
	}
	
	private void initGui() {
		setTitle("Contact Info:");

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Internet Adresses:"));
		JLabel labNickname = new JLabel("Nickname:");
		JLabel labPort = new JLabel("Port:");
		JLabel labHost = new JLabel("Host:");
		JLabel labSeal = new JLabel("Seal:");
		
		Signal<String> nickname = my(Signals.class).adaptSignal(
			my(ContactsGui.class).selectedContact(), new Functor<Contact, Signal<String>>() { @Override public Signal<String> evaluate(Contact contact) {
				return (contact == null) ? my(Signals.class).constant("") : contact.nickname();
			}}
		);

		PickyConsumer<String> setter = new PickyConsumer<String>(){@Override public void consume(String value) throws Refusal {
			my(Contacts.class).nicknameSetterFor(selectedContact()).consume(value);
		}};

		_txtNickname = my(ReactiveWidgetFactory.class).newTextField(nickname, setter, NotificationPolicy.OnEnterPressedOrLostFocus);

		_seal = my(ReactiveWidgetFactory.class).newTextPane(contactsFormattedSealString(), contactsSealSetter());
		_seal.getMainWidget().setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		JScrollPane sealScroll =
			new JScrollPane(
				_seal.getMainWidget(),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
			);

		JScrollPane addressesScroll = my(SynthScrolls.class).create();
		addressesScroll.getViewport().add(addresses());

		JButton btnNew = new JButton("New");
		btnNew.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			newInternetAddress();
		}});
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			saveInternetAddress();
		}});
		
		JButton btnDel = new JButton("Delete");
		btnDel.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			delInternetAddress();
		}});
		
		setGridBagLayout(panel, labNickname, labSeal, sealScroll, labPort, labHost, addressesScroll, btnNew, btnSave, btnDel);
		addListSelectionListestener();

		this.setSize(400, 350);
	}

	private Signal<String> contactsFormattedSealString() {
		return my(Signals.class).adaptSignal(
			my(ContactsGui.class).selectedContact(), new Functor<Contact, Signal<String>>() { @Override public Signal<String> evaluate(Contact contact) throws RuntimeException {
				if (contact == null) return my(Signals.class).constant("");
				return my(Signals.class).adapt(
					my(ContactSeals.class).sealGiven(contact), new Functor<Seal, String>() { @Override public String evaluate(Seal seal) throws RuntimeException {
						return seal == null ? "" : my(SealCodec.class).formattedHexEncode(seal);						
					}}
				);
			}}
		);
	}

	private PickyConsumer<String> contactsSealSetter() {
		return new PickyConsumer<String>() { @Override public void consume(String sealString) throws Refusal {
			if (sealString != null && sealString.isEmpty()) sealString = null;
			String nick = selectedContact().nickname().currentValue();
			my(ContactSeals.class).put(nick, decode(sealString));
		}};
	}

	private Seal decode(String sealString) throws Refusal {
		try {
			return my(SealCodec.class).hexDecode(sealString);
		} catch (DecodeException de) {
			throw new Refusal(de.getMessage());
		}
	}

	private void setGridBagLayout(JPanel panel, JLabel labNickname, JLabel labSeal, JScrollPane sealScroll, JLabel labPort, JLabel labHost, JScrollPane addressesScroll, JButton btnNew, JButton btnSave, JButton btnDel) {
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(labNickname,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0, 0) );

		getContentPane().add(_txtNickname.getComponent(),  new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5), 0, 0) );

		getContentPane().add(labSeal,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0, 0) );

		getContentPane().add(sealScroll,  new GridBagConstraints(0, 2, 5, 1, 1.0, 1.0, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0) );

		getContentPane().add(panel,  new GridBagConstraints(0, 3, 2, 1, 1.0, 0.2, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0) );
		
		panel.setLayout(new GridBagLayout());
		panel.add(addressesScroll,  new GridBagConstraints(0, 1, 12, 1, 0.6, 0.6, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0) );

		panel.add(labHost, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,5,0,5), 0, 0) );

		panel.add(_host, new GridBagConstraints(1, 2, 7, 1, 2.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0) );

		panel.add(labPort, new GridBagConstraints(9, 2, 1, 1, 0.0, 0.0, 
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0, 0) );
		
		panel.add(_port, new GridBagConstraints(10, 2, 2, 1, 0.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,5), 0, 0) );
		
		panel.add(btnNew, new GridBagConstraints(7, 4, 1, 1, 0.0, 0.0, 
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0) );

		panel.add(btnSave, new GridBagConstraints(8, 4, 3, 1, 0.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0) );

		panel.add(btnDel, new GridBagConstraints(11, 4, 1, 1, 0.0, 0.0, 
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0) );
	}
	
	private void addListSelectionListestener() {
		addresses().getSelectionModel().addListSelectionListener(
				new ListSelectionListener(){ @Override public void valueChanged(ListSelectionEvent e) {
					InternetAddress selected = (InternetAddress) addresses().getSelectedValue();
					if(selected==null){
						cleanFields();
						return;
					}
					
					_selectedAdress = selected;
					_host.setText(_selectedAdress.host());
					_port.setText(""+_selectedAdress.port());
				}});
	}
	
	private void logPortValueException(Exception e) {
		my(BlinkingLights.class).turnOn(LightType.ERROR, "Invalid Port Value: " + _port.getText(), e.getMessage(), e, 20000);
		my(Logger.class).log("Invalid Port Value: {}", _port.getText());
		_port.requestFocus();
	}
	
	private void saveInternetAddress() {
		try {
			InternetAddress address = _selectedAdress;
			if(address == null || _host.getText().trim().length()==0) return;
			
			if(  _selectedAdress.host().equals(_host.getText())
			&& _selectedAdress.port()== Integer.parseInt(_port.getText())
			&& _selectedAdress.contact() == my(ContactsGui.class).selectedContact().currentValue()){
				_lstAddresses.clearSelection();
				return;
			}
			
			newInternetAddress();
			my(InternetAddressKeeper.class).remove(address);
			_lstAddresses.clearSelection();
		} catch (NumberFormatException e) {
			logPortValueException(e);			
		}
	}
	
	private void delInternetAddress() {
		InternetAddress address = _selectedAdress;
		if(address == null || _host.getText().trim().length()==0) return;
		
		my(InternetAddressKeeper.class).remove(address);
		_lstAddresses.clearSelection();
	}
	
	private void newInternetAddress() {
		try{
			String host = _host.getText();
			if(host==null || host.trim().length()==0) return;
			
			int port = Integer.parseInt(_port.getText());
			my(InternetAddressKeeper.class).add(selectedContact(), host, port);
			
			_lstAddresses.clearSelection();
			cleanFields();
		} catch (NumberFormatException e) {
			logPortValueException(e);			
		}
	}
	
	private JList addresses() {
		return _lstAddresses.getMainWidget();
	}

	private void cleanFields() {
		_host.setText("");
		_port.setText("");
	}
	
	private Contact selectedContact() {
		return my(ContactsGui.class).selectedContact().currentValue();
	}

}
