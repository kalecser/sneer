package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;

class MusicViewImpl implements MusicView {
	private static final int MAX_VOLUME = 100;
	private static final Format _timeFormater = new SimpleDateFormat("mm:ss");

	private final JSlider volumeSlider = newVolumeSlider();

	private MusicViewListener listener;

	@SuppressWarnings("unused") private Object refToAvoidGc, trackNameRefToAvoidGc, trackTimeRefToAvoidGc ;

	@Override
	public void init(InstrumentPanel container) {
		Container pane = container.contentPane();
		pane.setLayout(new GridLayout(4, 1));
		pane.add(folderDropDown());
		pane.add(trackDisplay());
		pane.add(playerControls());
		pane.add(emotions());
		
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

	
	private JPanel trackDisplay() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(trackName());
		panel.add(trackTime());
		return panel;
	}

	
	private JLabel trackName() {
		final JLabel trackName = new JLabel();
		trackNameRefToAvoidGc = listener.playingTrackName().addReceiver(new Consumer<String>() {  @Override public void consume(String name) {
			trackName.setText(my(Lang.class).strings().abbreviate(name, 40));
			trackName.setToolTipText(name);
		}});
		return trackName;
	}

	
	private JLabel trackTime() {
		final JLabel trackTime = new JLabel();
		trackTimeRefToAvoidGc = listener.playingTrackTime().addReceiver(new Consumer<Integer>() {  @Override public void consume(Integer timeElapsed) {
			String time = _timeFormater.format(new Date(timeElapsed));
			trackTime.setText(time);
		}});
		return trackTime;
	}


	private JPanel playerControls() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));

		panel.add(playButton());
		panel.add(skipButton());
		panel.add(stopButton());
		panel.add(shuffleButton());
		panel.add(volumeControl());
		return panel;
	}

	
	private JButton playButton() {
		JButton play = new JButton(">");
		play.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.pauseResume();
		}});
		return play;
	}

	
	private JButton skipButton() {
		JButton skip = new JButton(">>");
		skip.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.skip();
		}});
		return skip;
	}

	
	private JButton stopButton() {
		JButton stop = new JButton("||");
		stop.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.stop();
		}});
		return stop;
	}

	
	private JToggleButton shuffleButton() {
		final JToggleButton shuffle = new JToggleButton("}{");
		shuffle.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.shuffle().setter().consume(shuffle.isSelected());
		}});
		refToAvoidGc = listener.shuffle().output().addReceiver(new Consumer<Boolean>() {  @Override public void consume(Boolean onOff) {
			shuffle.setSelected(onOff);
			shuffle.setToolTipText("Shuffle is " + (onOff ? "on" : "off"));
		}});
		return shuffle;
	}
	
	
	private JPanel emotions() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		panel.add(meTooButton());
		panel.add(deleteButton());
		panel.add(noWayButton());
		return panel;
	}


	private JButton meTooButton() {
		JButton meToo = new JButton(":D");
		meToo.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.meToo();
		}});
		return meToo;
	}


	private JButton deleteButton() {
		JButton delete = new JButton(":P");
		delete.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.deleteTrack();
		}});
		return delete;
	}


	private JButton noWayButton() {
		JButton noWay = new JButton(":(");
		noWay.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.noWay();
		}});
		return noWay;
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
			listener.volumePercent().setter().consume(vol.getValue());
		}});
		return vol;
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
