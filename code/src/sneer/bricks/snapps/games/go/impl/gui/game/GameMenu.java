package sneer.bricks.snapps.games.go.impl.gui.game;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Color;


public class GameMenu extends JFrame {
	
	public JButton _passButton;
	public JButton _resignButton;
	private JLabel _blackScore;
	private JLabel _whiteScore;
	private JLabel _message;
	private JPanel fakePanelToTrickWindowBuilder;

	public GameMenu(final JPanel jPanel) {		
		fakePanelToTrickWindowBuilder = new JPanel();
		addMenu(fakePanelToTrickWindowBuilder);
		addMenu(jPanel);
		getContentPane().add(fakePanelToTrickWindowBuilder);
	}

	private void addMenu(final JPanel jPanel) {
		GridBagLayout gbl_jPanel_1 = new GridBagLayout();
		gbl_jPanel_1.columnWidths = new int[]{0, 104, 59, 0};
		gbl_jPanel_1.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_jPanel_1.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_jPanel_1.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		jPanel.setLayout(gbl_jPanel_1);
		
		_message = new JLabel("");
		_message.setForeground(Color.WHITE);
		_message.setFont(new Font("Arial", Font.BOLD, 32));
		GridBagConstraints gbc_lblMessage = new GridBagConstraints();
		gbc_lblMessage.anchor = GridBagConstraints.NORTH;
		gbc_lblMessage.insets = new Insets(0, 0, 5, 5);
		gbc_lblMessage.gridx = 0;
		gbc_lblMessage.gridy = 0;
		jPanel.add(_message, gbc_lblMessage);
		
		JLabel lblBlack = new JLabel("Black:");
		lblBlack.setForeground(Color.WHITE);
		lblBlack.setFont(new Font("Arial", Font.PLAIN, 32));
		GridBagConstraints gbc_lblBlack = new GridBagConstraints();
		gbc_lblBlack.anchor = GridBagConstraints.WEST;
		gbc_lblBlack.insets = new Insets(0, 0, 5, 5);
		gbc_lblBlack.gridx = 1;
		gbc_lblBlack.gridy = 1;
		jPanel.add(lblBlack, gbc_lblBlack);
		
		_blackScore = new JLabel("0");
		_blackScore.setForeground(Color.WHITE);
		_blackScore.setFont(new Font("Arial", Font.PLAIN, 32));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 1;
		jPanel.add(_blackScore, gbc_lblNewLabel);
		
		JLabel lblWhite = new JLabel("White:");
		lblWhite.setForeground(Color.WHITE);
		lblWhite.setFont(new Font("Arial", Font.PLAIN, 32));
		GridBagConstraints gbc_lblWhite = new GridBagConstraints();
		gbc_lblWhite.anchor = GridBagConstraints.WEST;
		gbc_lblWhite.insets = new Insets(0, 0, 5, 5);
		gbc_lblWhite.gridx = 1;
		gbc_lblWhite.gridy = 2;
		jPanel.add(lblWhite, gbc_lblWhite);
		
		_whiteScore = new JLabel("0");
		_whiteScore.setForeground(Color.WHITE);
		_whiteScore.setFont(new Font("Arial", Font.PLAIN, 32));
		GridBagConstraints gbc_lblwhiteScore = new GridBagConstraints();
		gbc_lblwhiteScore.insets = new Insets(0, 0, 5, 0);
		gbc_lblwhiteScore.anchor = GridBagConstraints.EAST;
		gbc_lblwhiteScore.gridx = 2;
		gbc_lblwhiteScore.gridy = 2;
		jPanel.add(_whiteScore, gbc_lblwhiteScore);
		
		_passButton = new JButton("Pass");
		_passButton.setBackground(Color.BLACK);
		_passButton.setForeground(Color.WHITE);
		_passButton.setFont(new Font("Arial", Font.PLAIN, 32));
		GridBagConstraints gbc_btnPass = new GridBagConstraints();
		gbc_btnPass.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPass.insets = new Insets(0, 0, 5, 0);
		gbc_btnPass.gridwidth = 2;
		gbc_btnPass.gridx = 1;
		gbc_btnPass.gridy = 3;
		jPanel.add(_passButton, gbc_btnPass);
		
		_resignButton = new JButton("Resign");
		_resignButton.setBackground(Color.BLACK);
		_resignButton.setForeground(Color.WHITE);
		_resignButton.setFont(new Font("Arial", Font.PLAIN, 32));
		GridBagConstraints gbc_btnResign = new GridBagConstraints();
		gbc_btnResign.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnResign.gridwidth = 2;
		gbc_btnResign.insets = new Insets(0, 0, 5, 0);
		gbc_btnResign.gridx = 1;
		gbc_btnResign.gridy = 4;
		jPanel.add(_resignButton, gbc_btnResign);
	}

	public void setMyTurn(boolean enable) {
		_passButton.setEnabled(enable);
	}

	public void setMessage(String message) {
		_message.setText(message);
	}

	public void setGameEnded() {
		_passButton.setEnabled(false);
		_resignButton.setEnabled(false);
	}
	
	public void updateScore(int blackScore, int whiteScore) { 
		_blackScore.setText(blackScore+"");
		_whiteScore.setText(whiteScore+"");
	}

	public int getMenuWidth() {
		return 163;
	}

}
