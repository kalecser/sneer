package dfcsantos.music.ui.view.impl;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import dfcsantos.music.ui.view.MusicViewListener;

final class SmileyPanel extends JPanel {

	private final MusicViewListener _listener;


	SmileyPanel(MusicViewListener listener) {
		_listener = listener;

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(meTooButton());
		add(deleteButton());
		add(noWayButton());
	}
	
	
	private JButton meTooButton() {
		JButton meToo = new JButton(":D");
		meToo.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.meToo();
		}});
		return meToo;
	}


	private JButton deleteButton() {
		JButton delete = new JButton(":P");
		delete.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.deleteTrack();
		}});
		return delete;
	}


	private JButton noWayButton() {
		JButton noWay = new JButton(":(");
		noWay.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.noWay();
		}});
		return noWay;
	}

}
