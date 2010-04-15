package dfcsantos.wusic.gui.impl;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

class MainPanel extends JPanel {

	private final JTabbedPane _tabbedPanel				= new JTabbedPane();
	private final AbstractTabPane _ownTracksPanel		= new OwnTracksPanel();
	private final AbstractTabPane _peerTracksPanel		= new PeerTracksPanel();

	MainPanel(Dimension panelSize) {
		_tabbedPanel.addTab(null, _ownTracksPanel);
		_tabbedPanel.setTabComponentAt(0, _ownTracksPanel.customTabLabel());
		_tabbedPanel.setMnemonicAt(0, KeyEvent.VK_O);

		_tabbedPanel.addTab(null, _peerTracksPanel);
		_tabbedPanel.setTabComponentAt(1, _peerTracksPanel.customTabLabel());
		_tabbedPanel.setMnemonicAt(1, KeyEvent.VK_P);

		add(_tabbedPanel);
	}

}
