package dfcsantos.music.ui.view.impl;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JComponent;

import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.menu.MenuGroup;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;

class MusicViewImpl implements MusicView {
	private MusicViewListener listener;

	@Override
	public void init(InstrumentPanel container) {
		Container pane = container.contentPane();
		pane.setLayout(new GridLayout(4, 1));
		pane.add(new FolderSelectionPanel(listener));
		pane.add(new PlayingTrackPanel(listener));
		pane.add(new PlayerControlsPanel(listener));
		pane.add(new SmileyPanel(listener));
		
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
