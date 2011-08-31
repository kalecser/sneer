package spikes.adenauer;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Anything extends JFrame {
	public static void main(String [] ignored) {
		setLookAndFeel();
		new Anything();
	}
	
	
	public Anything() {
		Container panel = getContentPane();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 2;
		panel.add(label(), c);
		
		c.gridx = 1;
		c.gridy = 1;
		panel.add(imageButton(), c);
		
		setTitle("Imagen button");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 200, 200);
		setVisible(true);
	}
	
	private JButton imageButton() {
		URL path = getClass().getResource("menu.png");
		JButton button = new JButton(new ImageIcon(path));
		button.setPreferredSize(new Dimension(20, 20));
		button.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			System.out.println("Image button....");
		}});
		return button;
	}


	private JLabel label() {
		URL path = getClass().getResource("menu.png");
		JLabel label = new JLabel(new ImageIcon(path));
		label.setPreferredSize(new Dimension(30, 10));
		
		label.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("mouseReleased");
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("mousePressed");
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				System.out.println("mouseExited");
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				System.out.println("mouseEntered");
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("mouseClicked");
			}
		});
		
		label.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				System.out.println("Lost focus");
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("Gain focus");
			}
		});
		return label;
	}	
	
	
	private static void setLookAndFeel() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		        if ("Nimbus".equals(info.getName()))
		            UIManager.setLookAndFeel(info.getClassName());
		} catch (Exception e) {
			// Default look and feel will be used.
		}
	}
}
