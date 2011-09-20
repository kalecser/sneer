package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.skin.main.icons.Icons;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.ui.view.MusicViewListener;

final class SmileyPanel extends JPanel {
	private static final Dimension buttonSize = new Dimension(35, 30);
	private final MusicViewListener _listener;
	@SuppressWarnings("unused") private WeakContract _refToAvoidGc;

	SmileyPanel(MusicViewListener listener) {
		_listener = listener;

		setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		add(meTooButton());
		add(deleteAndNowayPanel());
	}
	
	
	private JButton meTooButton() {
		final JButton meToo = new JButton(load("metoo.png"));
		meToo.setToolTipText("Me Too");
		meToo.setPreferredSize(buttonSize);
		meToo.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.meToo();
		}});
		
		_refToAvoidGc = _listener.enableMeToo().addReceiver(new Consumer<Boolean>() {  @Override public void consume(Boolean available) {
			meToo.setEnabled(available);
		}});
		
		return meToo;
	}

	
	private Icon load(String icon) {
		return my(Icons.class).load(this.getClass(), icon);
	}

	
	private JPanel deleteAndNowayPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		panel.add(mehButton());
		panel.add(noWayButton());
		return panel;
	}
	
	
	private JButton mehButton() {
		JButton meh = new JButton(load("meh.png"));
		meh.setToolTipText("Meh");
		meh.setPreferredSize(buttonSize);
		meh.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.meh();
		}});
		return meh;
	}


	private JButton noWayButton() {
		JButton noWay = new JButton(load("noway.png"));
		noWay.setToolTipText("No Way");
		noWay.setPreferredSize(buttonSize);
		noWay.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.noWay();
		}});
		return noWay;
	}

}
