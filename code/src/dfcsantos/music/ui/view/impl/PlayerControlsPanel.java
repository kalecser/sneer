package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sneer.bricks.skin.main.icons.Icons;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.ui.view.MusicViewListener;

final class PlayerControlsPanel extends JPanel {
	private static final int MAX_VOLUME = 100;
	private static final Dimension buttonSize = new Dimension(35, 30);

	private final JSlider volumeSlider = newVolumeSlider();
	private final Icon _playIcon;
	private final Icon _pauseIcon;
	private final JButton _play;
	
	private final MusicViewListener _listener;
	
	@SuppressWarnings("unused") private Object refToAvoidGc, refToAvoidGc2;

	
	PlayerControlsPanel(MusicViewListener listener) {
		_listener = listener;
		_playIcon = load("play.png");
		_pauseIcon = load("pause.png");
		_play = playButton();
		
		initOrAddControls();
		
		listener.volumePercent().output().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer percent) {
			volumeSlider.setValue(percent);
		}});

		refToAvoidGc2 = _listener.playingTrackName().addReceiver(new Consumer<String>() {  @Override public void consume(String currentTrackName) {
			changeIconOfPlayButton(currentTrackName);
		}});
	}
	
	
	private void changeIconOfPlayButton(String currentTrackName) {
		if (!currentTrackName.equals("<No track to play>"))
			setPauseIconToPlayButton();
		else
			setPlayIconToPlayButton();
	}


	private void initOrAddControls() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		add(_play);
		add(skipButton());
		add(stopButton());
		add(shuffleButton());
		add(volumeControl());
	}

	
	private Icon load(String icon) {
		return my(Icons.class).load(this.getClass(), icon);
	}
	
	
	private JButton playButton() {
		JButton button = new JButton(_playIcon);
		button.setPreferredSize(buttonSize);
		button.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.pauseResume();
		}});
		
		return button;
	}

	
	private JButton skipButton() {
		JButton button = new JButton(load("next.png"));
		button.setPreferredSize(buttonSize);
		button.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.skip();
		}});
		
		return button;
	}

	
	private JButton stopButton() {
		JButton button = new JButton(load("stop.png"));
		button.setPreferredSize(buttonSize);
		button.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.stop();
		}});
		
		return button;
	}

	
	private JToggleButton shuffleButton() {
		final JToggleButton button = new JToggleButton(load("shuffle.png"));
		button.setPreferredSize(buttonSize);
		button.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			_listener.shuffle().setter().consume(button.isSelected());
		}});

		refToAvoidGc = _listener.shuffle().output().addReceiver(new Consumer<Boolean>() {  @Override public void consume(Boolean onOff) {
			button.setSelected(onOff);
			button.setToolTipText("Shuffle is " + (onOff ? "on" : "off"));
		}});
		
		return button;
	}
	
	
	private JPanel volumeControl() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(volumeSlider);
		return panel;
	}

	
	private JSlider newVolumeSlider() {
		final JSlider vol = new JSlider(SwingConstants.HORIZONTAL, 0, MAX_VOLUME, 0);
		vol.setPreferredSize(new Dimension(70, 15));
		vol.addChangeListener(new ChangeListener() { @Override public void stateChanged(ChangeEvent e) {
			_listener.volumePercent().setter().consume(vol.getValue());
		}});
		return vol;
	}
	
	
	private void setPlayIconToPlayButton() {
		_play.setIcon(_playIcon);
	}

	
	private void setPauseIconToPlayButton() {
		_play.setIcon(_pauseIcon);
	}
}
