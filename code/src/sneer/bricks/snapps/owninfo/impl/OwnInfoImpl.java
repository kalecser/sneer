package sneer.bricks.snapps.owninfo.impl;

import static basis.environments.Environments.my;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.PickyConsumer;

import sneer.bricks.hardware.cpu.utils.consumers.parsers.integer.IntegerParsers;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.codec.SealCodec;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.dyndns.ownaccount.DynDnsAccount;
import sneer.bricks.pulp.dyndns.ownaccount.DynDnsAccountKeeper;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.widgets.reactive.NotificationPolicy;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.skin.windowboundssetter.WindowBoundsSetter;
import sneer.bricks.snapps.owninfo.OwnInfo;

class OwnInfoImpl extends JFrame implements OwnInfo {
	
	private Environment _environment;
	private TextWidget<JTextField>  _yourOwnName;
	private TextWidget<JTextField> _sneerPort;
	private JTextArea _ownSeal;
	
	private final JTextField _dynDnsHost = new JTextField();
	private final JTextField _dynDnsUser = new JTextField();
	private final JPasswordField _dynDnsPassword = new JPasswordField();

	private final MainMenu _mainMenu = my(MainMenu.class);	
	
	@SuppressWarnings("unused")
	private Object _refToAvoidGC;
	
	OwnInfoImpl() {
		setDummyInfoIfNecessary();
		
		addOpenWindowAction();

		_environment = my(Environment.class);
		initGui();
		restoreFieldData();
		
		my(WindowBoundsSetter.class).runWhenBaseContainerIsReady(new Closure() { @Override public void run() {
			openIfNeedConfig();
		}});
	}

	
	private void setDummyInfoIfNecessary() {
		if (!"true".equals(System.getProperty("sneer.dummy")))
			return;
		
		my(Attributes.class).myAttributeSetter(OwnName.class).consume("Dummy");
		my(Attributes.class).myAttributeSetter(OwnPort.class).consume(7777);
	}

	
	protected void openIfNeedConfig() {
		if (!ownName().currentValue().trim().isEmpty()) return;
		open();
		tipForMacUsers();
	}

	
	private void tipForMacUsers() {
		if (System.getProperty("os.name").toLowerCase().contains("mac"))
			JOptionPane.showMessageDialog(this, "IMPORTANT TIP:\n\nUse Control-C and Control-V instead of\nCommand-C and Command-V for copy\nand paste.", "Tip for Mac Users", JOptionPane.INFORMATION_MESSAGE);
	}


	private void open() {
		if(isVisible()) return;
		
//		my(WindowBoundsSetter.class).setBestBounds(this);
		setLocationRelativeTo(_mainMenu.menu().getWidget());
		setVisible(true);
		_yourOwnName.getMainWidget().requestFocus();
	}
	
	private void initDynDnsAccount(String dynDnsHost, String dynDnsUserName, String dynDnsPassword) {
		if (dynDnsUserName == null) return;
	
		my(DynDnsAccountKeeper.class).accountSetter().consume(
				new DynDnsAccount(dynDnsHost, dynDnsUserName, dynDnsPassword));
	}

	private void initGui() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Own Info");

		// these numbers are made up out of thin air
		setMinimumSize(new Dimension(200, 100));
		setMaximumSize(new Dimension(600, 300));
		setResizable(false);
		
		java.awt.Container pnl = getContentPane();
		
		_yourOwnName = newTextField(ownName(), ownNameSetter());
		
		_sneerPort = newTextField(ownPort(), ownPortSetter());

		String formattedHexString = my(SealCodec.class).formattedHexEncode(my(OwnSeal.class).get().currentValue());
		_ownSeal = new JTextArea(formattedHexString);
		_ownSeal.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		_ownSeal.setEditable(false);
		_ownSeal.setTabSize(3);
		_ownSeal.setWrapStyleWord(true);
		JScrollPane sealScroll = new JScrollPane(_ownSeal, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		pnl.setLayout(new GridBagLayout());
		
		addWidget(_yourOwnName.getComponent(), "Own Name:", 0);
		addWidget(_sneerPort.getComponent(), "Sneer TCP Port:", 1);
		addWidget(sealScroll, "Own Seal:", 2);
		
		JPanel pnlDynDns = new JPanel();
		pnlDynDns.setLayout(new GridBagLayout());

		pnlDynDns.setBorder(new TitledBorder("Own DynDns [Optional]"));
//		getContentPane().add(pnlDynDns,
//				new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0,
//						GridBagConstraints.CENTER,	GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),0, 0));
		
		addWidget(pnlDynDns, _dynDnsHost, "Host:", 0);
		addWidget(pnlDynDns, _dynDnsUser, "User:", 1);
		addWidget(pnlDynDns, _dynDnsPassword, "Password:", 2);
		
		final JButton btn = new JButton("OK");
		getContentPane().add(btn,
				new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5),0, 0));

		btn.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent ignored) {
			submit();
			setVisible(false);
		}});
		pack();
	}

	private Consumer<String> ownNameSetter() {
		return my(Attributes.class).myAttributeSetter(OwnName.class);
	}

	private Signal<String> ownName() {
		return my(Attributes.class).myAttributeValue(OwnName.class);
	}

	private PickyConsumer<String> ownPortSetter() {
		return my(IntegerParsers.class).newIntegerParser(
			my(Attributes.class).myAttributeSetter(OwnPort.class)
		);
	}

	private Signal<Integer> ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class);
	}

	private void addWidget(JComponent widget, String label, int y) { addWidget(getContentPane(), widget, label, y);	}
	private void addWidget(Container container, JComponent widget, String label, int y) {
		container.add(new JLabel(label),
				new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5),0, 0));
		
		container.add(widget,
				new GridBagConstraints(1, y, 1, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),0, 0));
	}

	private TextWidget<JTextField> newTextField(final Signal<?> signal, final PickyConsumer<String> setter) {
		return my(ReactiveWidgetFactory.class).newTextField(signal, setter, NotificationPolicy.OnEnterPressedOrLostFocus);
	}

	private void submit() {
		Environments.runWith(_environment, new Closure(){ @Override public void run() {
			try {
				storeFieldData();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(OwnInfoImpl.this, ex.getMessage());
				ex.printStackTrace();
			}
		}});
	}
	
	private void restoreFieldData() {
		DynDnsAccount account = my(DynDnsAccountKeeper.class).ownAccount().currentValue();
		if (account == null) return;
		_dynDnsHost.setText(account.host);
		_dynDnsUser.setText(account.user);
		_dynDnsPassword.setText(account.password);
	}

	private void storeFieldData() throws Exception {
		initDynDnsAccount(trim(_dynDnsHost), trim(_dynDnsUser), trim(_dynDnsPassword));
	}

	private String trim(JTextField field) {
		return field.getText().trim();
	}
	
	private void addOpenWindowAction() {
		_mainMenu.menu().addAction(10, "Own Info...", new Closure() { @Override public void run() {
			open();
		}});
	}

}
