package sneer.bricks.snapps.games.go.impl.gui.gameSetup;

import static basis.environments.Environments.my;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.snapps.games.go.impl.network.GoInvitation;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GoSetupInvitation extends JFrame{
	private JTextField textField;
	private JButton btnOk;
	private final int _gameId;
	
	public GoSetupInvitation(final Seal _adversary, final int gameId) {
		
		this._gameId = gameId;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblNewLabel = new JLabel("Tamanho do tabuleiro:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		textField = new JTextField();
		textField.setText("5");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 1;
		getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);
		
		btnOk = new JButton("Ok");
		textField.addKeyListener(new KeyAdapter() { @Override public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				sendInvitation(_adversary);
			}
		}});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOk.gridx = 0;
		gbc_btnOk.gridy = 2;
		getContentPane().add(btnOk, gbc_btnOk);
		
		setSize(211,107);
		
		btnOk.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent arg0) {
			sendInvitation(_adversary);
		}});

		setVisible(true);
	}

	private void sendInvitation(final Seal _adversary) {
		final Integer size = Integer.valueOf(textField.getText());
		final String inviteMessage = "Wanna play Go with " + my(Attributes.class).myAttributeValue(OwnName.class)  + "? Board size "+size;
		final GoInvitation invitation = new GoInvitation(_adversary, inviteMessage,size, _gameId);
		my(TupleSpace.class).add(invitation);
		dispose();
	};
}
