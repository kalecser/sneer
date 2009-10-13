package dfcsantos.wusic.gui.impl;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class NewWusicPanel extends JPanel {

	private JTabbedPane tabbedPane = new JTabbedPane();
	private PlayOwnTracksPanel playOwnTracksPanel = new PlayOwnTracksPanel();
	private PlayTracksFromPeersPanel playTracksFromPeersPanel = new PlayTracksFromPeersPanel();

	{
		tabbedPane.addTab("Play Own Tracks", playOwnTracksPanel);
		tabbedPane.addTab("Play Tracks From Peers", playTracksFromPeersPanel);

		this.add(tabbedPane);
	}

}
