package spikes.rene.Krypton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class KryptonGui {
	public static void main(String[] args) {
		new KryptonGui();
	}

	private JTextField _field;
	
	KryptonGui() {
		JFrame window=new JFrame("Krypton Gui");
		window.setBounds((1280-300)/2,(720-130)/2, 300,130);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container c=window.getContentPane();
		c.setLayout(new FlowLayout());
		Listener list=new Listener(this);
		
		c.add(new JLabel("Nome do arquivo:"));
		c.add(_field=new JTextField(24));

		JButton button;
		c.add(button=new JButton("Krypt"));
		button.addActionListener(list);
		c.add((button=new JButton("Compress")));
		button.addActionListener(list);
		c.add((button=new JButton("Decompress")));
		button.addActionListener(list);
		
		window.setVisible(true);
	}
	
	protected void react(String func) {
		String password="", filename=_field.getText();
		if (func.equals("krypt")) {
			password=JOptionPane.showInputDialog(null, "Enter the password:", "Password", JOptionPane.QUESTION_MESSAGE);
			if (password==null) return;
			if (password.equals("")) return;
		}
		try {new KryptonSilent(func,filename,password);}
		catch (IOException e1) {System.err.println("Erro:\nIOException:\n"+e1.getMessage());};
	}

	class Listener implements ActionListener {
		private KryptonGui _gui;
		Listener (KryptonGui gui) {_gui=gui;}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String function="";

			if (String.valueOf(e.getActionCommand()).equals("Krypt")) function="krypt";
			if (String.valueOf(e.getActionCommand()).equals("Compress")) function="compr";
			if (String.valueOf(e.getActionCommand()).equals("Decompress")) function="decom";
			
			_gui.react(function);
		}
	}
}
