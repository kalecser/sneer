package spikes.adenauer.lookandfeel;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertBlue;

public class JGoodiesLF {

	public static void main (String [] ignored) throws Exception {
		createUIUsing(jgoodiesLookAndFeel());
	}


	private static void createUIUsing(final LookAndFeel lf) throws Exception {
		UIManager.setLookAndFeel(lf);

		JFrame window = new JFrame();
		window.add(uiComponents());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		window.setBounds(100, 100, 300, 50);
		window.pack();
		window.setVisible(true);
	}

	private static LookAndFeel jgoodiesLookAndFeel() {
		//LookAndFeel p3dLookAndFeel = new Plastic3DLookAndFeel();
		PlasticLookAndFeel.setPlasticTheme(new DesertBlue());
		return new Plastic3DLookAndFeel();
	}

	private static JPanel uiComponents() {
		GridLayout gridLayout = new GridLayout(2, 2);
		//gridLayout.setHgap(10);
		gridLayout.setVgap(5);
		
		JPanel componentPanel = new JPanel(gridLayout);
		JTextField firstNameField = new JTextField();
		JLabel firstNameLabel = new JLabel("First name: ");
		JTextField lastNameField = new JTextField();
		JLabel lastNameLabel = new JLabel("Last name: ");
		
		Dimension fieldsSize = new Dimension(200, 20);
		Dimension labelsSize = new Dimension(100, 20);
		
		firstNameField.setPreferredSize(fieldsSize);
		firstNameLabel.setPreferredSize(labelsSize);
		lastNameField.setPreferredSize(fieldsSize);
		lastNameLabel.setPreferredSize(labelsSize);

		componentPanel.add(firstNameLabel);
		componentPanel.add(firstNameField);
		componentPanel.add(lastNameLabel);
		componentPanel.add(lastNameField);
		return componentPanel;	
	}
}
