package sneer.bricks.snapps.games.go.impl.gui.game;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class GameMenu extends JFrame {
	
	public JButton _passButton;
	public JButton _resignButton;
	private JLabel _blackScore;
	private JLabel _whiteScore;

	public GameMenu() {
		final JPanel jPanel = new JPanel();
		addMenu(jPanel);
		getContentPane().add(jPanel);
	}

	public void addMenu(final JPanel jPanel) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 72, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		jPanel.setLayout(gridBagLayout);
		
		JLabel lblBlack = new JLabel("Black:");
		GridBagConstraints gbc_lblBlack = new GridBagConstraints();
		gbc_lblBlack.insets = new Insets(0, 0, 5, 5);
		gbc_lblBlack.gridx = 0;
		gbc_lblBlack.gridy = 1;
		jPanel.add(lblBlack, gbc_lblBlack);
		
		_blackScore = new JLabel("0");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 1;
		jPanel.add(_blackScore, gbc_lblNewLabel);
		
		JLabel lblWhite = new JLabel("White:");
		GridBagConstraints gbc_lblWhite = new GridBagConstraints();
		gbc_lblWhite.insets = new Insets(0, 0, 5, 5);
		gbc_lblWhite.gridx = 0;
		gbc_lblWhite.gridy = 2;
		jPanel.add(lblWhite, gbc_lblWhite);
		
		_whiteScore = new JLabel("0");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.gridx = 1;
		gbc_label.gridy = 2;
		jPanel.add(_whiteScore, gbc_label);
		
		_passButton = new JButton("Pass");
		GridBagConstraints gbc_btnPass = new GridBagConstraints();
		gbc_btnPass.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPass.insets = new Insets(0, 0, 5, 0);
		gbc_btnPass.gridwidth = 2;
		gbc_btnPass.gridx = 0;
		gbc_btnPass.gridy = 3;
		jPanel.add(_passButton, gbc_btnPass);
		
		_resignButton = new JButton("Resign");
		GridBagConstraints gbc_btnResign = new GridBagConstraints();
		gbc_btnResign.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnResign.gridwidth = 2;
		gbc_btnResign.insets = new Insets(0, 0, 5, 0);
		gbc_btnResign.gridx = 0;
		gbc_btnResign.gridy = 4;
		jPanel.add(_resignButton, gbc_btnResign);
	}

	public void setMyTurn(boolean enable) {
		_passButton.setEnabled(enable);
		_resignButton.setEnabled(enable); 
	}

	public void updateScore(int blackScore, int whiteScore) { 
		_blackScore.setText(blackScore+"");
		_whiteScore.setText(whiteScore+"");
	}

}
