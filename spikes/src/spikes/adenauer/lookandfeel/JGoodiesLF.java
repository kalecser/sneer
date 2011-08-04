package spikes.adenauer.lookandfeel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
	private static JFrame _parent;
	
	
	public static void main (String [] ignored) throws Exception {
		createUIUsing(jgoodiesLookAndFeel());
	}


	private static void createUIUsing(final LookAndFeel lf) throws Exception {
		UIManager.setLookAndFeel(lf);

		_parent = new JFrame();
		_parent.add(form(), BorderLayout.NORTH);
		_parent.add(fileChoose(), BorderLayout.SOUTH);
		_parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		_parent.setBounds(100, 100, 300, 50);
		_parent.pack();
		_parent.setVisible(true);
	}

	private static LookAndFeel jgoodiesLookAndFeel() {
		//LookAndFeel p3dLookAndFeel = new Plastic3DLookAndFeel();
		PlasticLookAndFeel.setPlasticTheme(new DesertBlue());
		return new Plastic3DLookAndFeel();
	}

	private static JPanel form() {
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

		GridLayout gridLayout = new GridLayout(2, 2);
		gridLayout.setVgap(5);
		JPanel componentPanel = new JPanel(gridLayout);

		componentPanel.add(firstNameLabel);
		componentPanel.add(firstNameField);
		componentPanel.add(lastNameLabel);
		componentPanel.add(lastNameField);
		return componentPanel;	
	}
	

	private static JPanel fileChoose() {
		JButton callFileChooser = new JButton("File");
		callFileChooser.addActionListener( new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			NativeFileChooser fileChooser = new NativeFileChooser(null);
			fileChooser.setResizable(true);
			fileChooser.setVisible(true);
			
			System.out.println("file selected: " + fileChooser.getDirectory() + fileChooser.getFile());
		}});
		JPanel fileChoosePanel = new JPanel();
		fileChoosePanel.add(callFileChooser, BorderLayout.WEST);
		return fileChoosePanel;
	}

	private static class NativeFileChooser extends FileDialog {

		public NativeFileChooser(Frame parent) {
			super(parent, "Open");
			setMode(FileDialog.LOAD);
		}

		@Override
		public void setDirectory(String dir) {
			System.out.println("directory : " + dir);
			super.setDirectory(dir);
		}

		@Override
		public void setFile(String file) {
			System.out.println("file: " + file);
			super.setFile(file);
		}
	}
}
