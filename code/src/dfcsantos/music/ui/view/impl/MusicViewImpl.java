package dfcsantos.music.ui.view.impl;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.menu.MenuGroup;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;

class MusicViewImpl implements MusicView {
	private MusicViewListener listener;

	@SuppressWarnings("unused") private Object refToAvoidGc, trackNameRefToAvoidGc, trackTimeRefToAvoidGc, subFordersRefToAvoidGc;

	@Override
	public void init(InstrumentPanel container) {
		Container pane = container.contentPane();
		pane.setLayout(new GridLayout(4, 1));
		pane.add(new SelectorModePanel(listener));
		pane.add(new DisplayerPanel(listener));
		pane.add(new PlayerPanel(listener));
		pane.add(emotions());
		
		initMenu(container.actions());
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
	}

}
