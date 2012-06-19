package sneer.bricks.snapps.games.go.impl.gui.game;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;


public class GameFrame extends JFrame {
	public GameFrame() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 61, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblBlack = new JLabel("Black:");
		GridBagConstraints gbc_lblBlack = new GridBagConstraints();
		gbc_lblBlack.insets = new Insets(0, 0, 5, 5);
		gbc_lblBlack.gridx = 0;
		gbc_lblBlack.gridy = 1;
		getContentPane().add(lblBlack, gbc_lblBlack);
		
		JLabel lblNewLabel = new JLabel("0");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 1;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		JLabel lblWhite = new JLabel("White:");
		GridBagConstraints gbc_lblWhite = new GridBagConstraints();
		gbc_lblWhite.insets = new Insets(0, 0, 5, 5);
		gbc_lblWhite.gridx = 0;
		gbc_lblWhite.gridy = 2;
		getContentPane().add(lblWhite, gbc_lblWhite);
		
		JLabel label = new JLabel("0");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.gridx = 1;
		gbc_label.gridy = 2;
		getContentPane().add(label, gbc_label);
		
		JButton btnPass = new JButton("Pass");
		GridBagConstraints gbc_btnPass = new GridBagConstraints();
		gbc_btnPass.insets = new Insets(0, 0, 5, 0);
		gbc_btnPass.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPass.gridwidth = 2;
		gbc_btnPass.gridx = 0;
		gbc_btnPass.gridy = 3;
		getContentPane().add(btnPass, gbc_btnPass);
		
		JButton btnResign = new JButton("Resign");
		GridBagConstraints gbc_btnResign = new GridBagConstraints();
		gbc_btnResign.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnResign.gridwidth = 2;
		gbc_btnResign.insets = new Insets(0, 0, 0, 5);
		gbc_btnResign.gridx = 0;
		gbc_btnResign.gridy = 4;
		getContentPane().add(btnResign, gbc_btnResign);
	}

}
