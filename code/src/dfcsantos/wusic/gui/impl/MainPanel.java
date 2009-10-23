package dfcsantos.wusic.gui.impl;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
		_tabbedPane.setMnemonicAt(1, KeyEvent.VK_P);

		_tabbedPane.setPreferredSize(panelSize);

		add(_tabbedPane);
	}

}
