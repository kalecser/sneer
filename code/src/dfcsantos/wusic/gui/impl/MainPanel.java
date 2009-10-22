package dfcsantos.wusic.gui.impl;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class MainPanel extends JPanel {

	private final JTabbedPane _tabbedPane				= new JTabbedPane();
	private final AbstractTabPane _ownTracksPanel		= new OwnTracksPanel();
	private final AbstractTabPane _peerTracksPanel		= new PeerTracksPanel();

	MainPanel(Dimension panelSize) {
		_tabbedPane.addTab(null, _ownTracksPanel);
		_tabbedPane.setTabComponentAt(0, _ownTracksPanel.customTabLabel());
		_tabbedPane.setMnemonicAt(0, KeyEvent.VK_O);

		_tabbedPane.addTab(null, _peerTracksPanel);
		_tabbedPane.setTabComponentAt(1, _peerTracksPanel.customTabLabel());
		_tabbedPane.setMnemonicAt(1, KeyEvent.VK_S);

		_tabbedPane.addChangeListener(new ChangeListener() { @Override public void stateChanged(ChangeEvent e) {
			// Wusic.setOperatingMode(OperatingMode.values()[_tabbedPane.getSelectedIndex()]); Fix: Come up with another strategy to change playing mode
		}});

		_tabbedPane.setPreferredSize(panelSize);

		add(_tabbedPane);
	}

}
