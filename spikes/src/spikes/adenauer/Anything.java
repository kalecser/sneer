package spikes.adenauer;

import java.awt.Button;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class Anything extends JFrame {
	public static void main(String [] ignored) {
		new Anything();
	}
	
	public Anything() {
		Container panel = getContentPane();
		panel.add(imageButton());
		setTitle("Imagen button");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 100, 100);
		setVisible(true);
	}
		
	private Button imageButton() {
		Button button = new Button("Test");
		button.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			System.out.println("Image button....");
		}});
		return button;
	}
}
