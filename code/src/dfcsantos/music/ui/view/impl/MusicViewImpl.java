package dfcsantos.music.ui.view.impl;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;

class MusicViewImpl implements MusicView {
	private static final int MAX_VOLUME = 100;

	private MusicViewListener listener;

	private final JSlider volumeSlider = newVolumeSlider();

	@SuppressWarnings("unused") private Object refToAvoidGc;

	@Override
	public void init(InstrumentPanel container) {
		Container pane = container.contentPane();
		pane.setLayout(new GridLayout(4, 1));
		pane.add(folderDropDown());
		pane.add(new TrackDisplay());
		pane.add(playerControls());
		pane.add(emotiControls());
		
		initMenu(container.actions());
	}

	
	private JComboBox folderDropDown() {
		JComboBox result = new JComboBox();
		result.addItem("<Inbox - 7 Tracks>");
		result.addItem("classico");
		result.addItem("rock");
		result.addItem("rock/nacional");
		result.addItem("rock/nacional/Ira");
		result.addItem("rock/nacional/legiao");
		result.addItem("rock/Queen");
		result.addItem("samba/raiz");
		result.addItem("sertanejo/raiz");
		return result;
	}


	private void initMenu(MenuGroup<? extends JComponent> actions) {
		actions.addAction(10, "Choose Tracks Folder...", new Runnable() { @Override public void run() {
			listener.chooseTracksFolder();
		}});
		actions.addActionWithCheckBox(20, "Exchange Tracks", listener.isTrackExchangeActive().output(), new Runnable() { @Override public void run() {
			listener.isTrackExchangeActive().setter().consume(!listener.isTrackExchangeActive().output().currentValue());
		}});
		actions.addAction(30, "Downloads...", new Runnable() { @Override public void run() {
			DownloadsView.showInstance();
		}});
	}


	private Component emotiControls() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
		panel.add(new JButton(":D"));
		panel.add(new JButton(":P"));
		panel.add(new JButton(":("));
		return panel;
	}


	private JPanel playerControls() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));

		panel.add(playButton());
		panel.add(skipButton());
		panel.add(stopButton());
		panel.add(shuffleButton());
		panel.add(volumeSlider);
		return panel;
	}


	private JSlider newVolumeSlider() {
		final JSlider vol = new JSlider(SwingConstants.HORIZONTAL, 0, MAX_VOLUME, 0);
		vol.setPreferredSize(new Dimension(60, vol.getPreferredSize().height));
		vol.addChangeListener(new ChangeListener() { @Override public void stateChanged(ChangeEvent e) {
			listener.volumePercent().setter().consume(vol.getValue());
		}});
		return vol;
	}


	private JToggleButton shuffleButton() {
		final JToggleButton button = new JToggleButton("}{");
		button.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.shuffle().setter().consume(button.isSelected());
		}});
		refToAvoidGc = listener.shuffle().output().addReceiver(new Consumer<Boolean>() {  @Override public void consume(Boolean onOff) {
			button.setSelected(onOff);
			button.setToolTipText("Shuffle is " + (onOff ? "on" : "off"));
		}});
		return button;
	}


	private JButton stopButton() {
		JButton stop = new JButton("[]");
		stop.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.stop();
		}});
		return stop;
	}


	private JButton skipButton() {
		JButton skip = new JButton(">>");
		skip.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.skip();
		}});
		return skip;
	}


	private JButton playButton() {
		JButton play = new JButton(">");
		play.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.pauseResume();
		}});
		return play;
	}

	
	@Override
	public int defaultHeight() {
		return 120;
	}

	
	@Override
	public String title() {
		return "Music";
	}


	@Override
	public void setListener(MusicViewListener listener) {
		if (this.listener != null) throw new IllegalStateException();
		this.listener = listener;
		
		listener.volumePercent().output().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer percent) {
			volumeSlider.setValue(percent);
		}});
	}

}
