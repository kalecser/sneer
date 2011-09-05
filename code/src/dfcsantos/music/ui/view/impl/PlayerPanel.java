package dfcsantos.music.ui.view.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sneer.foundation.lang.Consumer;

import dfcsantos.music.ui.view.MusicViewListener;

public class PlayerPanel extends JPanel {
	private static final int MAX_VOLUME = 100;

	private final MusicViewListener _listener;

	private final JSlider volumeSlider = newVolumeSlider();

	@SuppressWarnings("unused") private Object refToAvoidGc;

	public PlayerPanel(MusicViewListener listener) {
		_listener = listener;

		setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));

		add(playButton());
		add(skipButton());
		add(stopButton());
		add(shuffleButton());
		add(volumeControl());

		listener.volumePercent().output().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer percent) {
			volumeSlider.setValue(percent);
		}});
	}
	
	private JButton playButton() {
		JButton play = new JButton(">");
		play.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.pauseResume();
		}});
		return play;
	}

	
	private JButton skipButton() {
		JButton skip = new JButton(">>");
		skip.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.skip();
		}});
		return skip;
	}

	
	private JButton stopButton() {
		JButton stop = new JButton("||");
		stop.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.stop();
		}});
		return stop;
	}

	
	private JToggleButton shuffleButton() {
		final JToggleButton shuffle = new JToggleButton("}{");
		shuffle.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.shuffle().setter().consume(shuffle.isSelected());
		}});
		refToAvoidGc = _listener.shuffle().output().addReceiver(new Consumer<Boolean>() {  @Override public void consume(Boolean onOff) {
			shuffle.setSelected(onOff);
			shuffle.setToolTipText("Shuffle is " + (onOff ? "on" : "off"));
		}});
		return shuffle;
	}
	
	private JPanel volumeControl() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(volumeSlider);
		return panel;
	}

	
	private JSlider newVolumeSlider() {
		final JSlider vol = new JSlider(SwingConstants.HORIZONTAL, 0, MAX_VOLUME, 0);
		vol.setPreferredSize(new Dimension(50, 15));
		vol.addChangeListener(new ChangeListener() { @Override public void stateChanged(ChangeEvent e) {
			_listener.volumePercent().setter().consume(vol.getValue());
		}});
		return vol;
	}
}
