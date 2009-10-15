package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.OperatingMode;

public class WusicPanel extends JPanel {

	private static final Wusic Wusic = my(Wusic.class);

	private JTabbedPane tabbedPane = new JTabbedPane();
	private OwnTracksPanel ownTracksPanel = new OwnTracksPanel();
	private SharedTracksPanel sharedTracksPanel = new SharedTracksPanel();

	{
		tabbedPane.addTab("Own Tracks", ownTracksPanel);
		tabbedPane.addTab("Shared Tracks", sharedTracksPanel);
		tabbedPane.addChangeListener(new ChangeListener() { @Override public void stateChanged(ChangeEvent e) {
			Wusic.setOperatingMode(OperatingMode.values()[tabbedPane.getSelectedIndex()]);
		}});

		this.add(tabbedPane);
	}
}
