package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.OperatingMode;

public class NewWusicPanel extends JPanel {

	private static final Wusic Wusic = my(Wusic.class);

	private final JTabbedPane _tabbedPane				= new JTabbedPane();
	private final AbstractTabPane _ownTracksPanel		= new OwnTracksPanel();
	private final AbstractTabPane _sharedTracksPanel	= new SharedTracksPanel();

	{
		_tabbedPane.addTab("Own Tracks", null, _ownTracksPanel, "Own Tracks Mode");
		_tabbedPane.addTab("Shared Tracks", null, _sharedTracksPanel, "Shared Tracks Mode");
		_tabbedPane.addChangeListener(new ChangeListener() { @Override public void stateChanged(ChangeEvent e) {
			Wusic.setOperatingMode(OperatingMode.values()[_tabbedPane.getSelectedIndex()]);
		}});
		add(_tabbedPane);
	}

}
