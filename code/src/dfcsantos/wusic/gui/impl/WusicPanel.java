package dfcsantos.wusic.gui.impl;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class WusicPanel extends JPanel {

	private JTabbedPane tabbedPane = new JTabbedPane();
	private OwnTracksPanel ownTracksPanel = new OwnTracksPanel();
	private SharedTracksPanel sharedTracksPanel = new SharedTracksPanel();

	{
		tabbedPane.addTab("Own Tracks", ownTracksPanel);
		tabbedPane.addTab("Shared Tracks", sharedTracksPanel);

		this.add(tabbedPane);
		this.setPreferredSize(new Dimension(480, 140));
	}

}
