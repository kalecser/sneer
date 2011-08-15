package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
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
import sneer.bricks.skin.main.icons.Icons;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;

class MusicViewImpl implements MusicView {
	private static final int MAX_VOLUME = 100;
	private static final Dimension BUTTON_SIZE = new Dimension(18, 18);
	
	private final Icon playIcon = load("play.png"); 
	private final Icon pauseIcon = load("pause.png");

	private final JSlider volumeSlider = newVolumeSlider();

	private MusicViewListener listener;

	@SuppressWarnings("unused") private Object refToAvoidGc;

	@Override
	public void init(InstrumentPanel container) {
		Container pane = container.contentPane();
		pane.setLayout(new GridLayout(4, 1));
		pane.add(folderDropDown());
		pane.add(new TrackDisplay());
		pane.add(playerControls());
		pane.add(volumeControl());
		
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


	private JPanel playerControls() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

		panel.add(playButton());
		panel.add(skipButton());
		panel.add(stopButton());
		panel.add(shuffleButton());
		panel.add(meTooButton());
		panel.add(deleteButton());
		panel.add(noWayButton());
		return panel;
	}


	private JButton playButton() {
		JButton play = new JButton(playIcon);
		play.setPreferredSize(BUTTON_SIZE);
		play.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			play((JButton) e.getSource());
		}});
		return play;
	}

	
	private void play(JButton play) {
		listener.pauseResume();
		if (play.getIcon() == playIcon)
			play.setIcon(pauseIcon);
		else
			play.setIcon(playIcon);
	}


	private JButton skipButton() {
		Icon icon = load("skip.png");
		JButton skip = new JButton(icon);
		skip.setPreferredSize(BUTTON_SIZE);
		skip.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.skip();
		}});
		return skip;
	}

	
	private JButton stopButton() {
		Icon icon = load("stop.png");
		JButton stop = new JButton(icon);
		stop.setPreferredSize(BUTTON_SIZE);
		stop.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.stop();
		}});
		return stop;
	}

	
	private JToggleButton shuffleButton() {
		Icon icon = load("shuffle.png");
		final JToggleButton shuffle = new JToggleButton(icon);
		shuffle.setPreferredSize(BUTTON_SIZE);
		shuffle.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.shuffle().setter().consume(shuffle.isSelected());
		}});
		refToAvoidGc = listener.shuffle().output().addReceiver(new Consumer<Boolean>() {  @Override public void consume(Boolean onOff) {
			shuffle.setSelected(onOff);
			shuffle.setToolTipText("Shuffle is " + (onOff ? "on" : "off"));
		}});
		return shuffle;
	}
	

	private JButton meTooButton() {
		Icon icon = load("metoo.png");
		JButton meToo = new JButton(icon);
		meToo.setPreferredSize(BUTTON_SIZE);
		meToo.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.meToo();
		}});
		return meToo;
	}


	private JButton deleteButton() {
		Icon icon = load("delete.png");
		JButton delete = new JButton(icon);
		delete.setPreferredSize(BUTTON_SIZE);
		delete.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.deleteTrack();
		}});
		return delete;
	}


	private JButton noWayButton() {
		Icon icon = load("noway.png");
		JButton noWay = new JButton(icon);
		noWay.setPreferredSize(BUTTON_SIZE);
		noWay.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.noWay();
		}});
		return noWay;
	}
	
	
	private Icon load(String fileName) {
		return my(Icons.class).load(this.getClass(), fileName);
	}

	
	private JPanel volumeControl() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(volumeSlider);
		return panel;
	}

	
	private JSlider newVolumeSlider() {
		final JSlider vol = new JSlider(SwingConstants.HORIZONTAL, 0, MAX_VOLUME, 0);
		//vol.setPreferredSize(new Dimension(60, vol.getPreferredSize().height));
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
