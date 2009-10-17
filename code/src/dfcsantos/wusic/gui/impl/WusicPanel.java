package dfcsantos.wusic.gui.impl;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class WusicPanel extends JPanel {

	private final JTabbedPane _tabbedPane				= new JTabbedPane();
	private final AbstractTabPane _ownTracksPanel		= new OwnTracksPanel();
	private final AbstractTabPane _sharedTracksPanel	= new SharedTracksPanel();

	WusicPanel() {
		_tabbedPane.addTab("Own Tracks", null, _ownTracksPanel, "Own Tracks Mode");
		_tabbedPane.addTab("Shared Tracks", null, _sharedTracksPanel, "Shared Tracks Mode");
		_tabbedPane.addChangeListener(new ChangeListener() { @Override public void stateChanged(ChangeEvent e) {
			// Wusic.setOperatingMode(OperatingMode.values()[_tabbedPane.getSelectedIndex()]); Fix: Come up with another strategy to change playing mode
		}});
		add(_tabbedPane);
	}

}
