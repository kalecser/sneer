package dfcsantos.music.ui.view.impl;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import sneer.foundation.lang.Consumer;
import dfcsantos.music.ui.view.MusicViewListener;


final class FolderSelectionPanel extends JPanel {
	private final MusicViewListener _listener;
	private final JComboBox _selector = newComboBox();

	
	@SuppressWarnings("unused") private Object refToAvoidGc;

	
	FolderSelectionPanel(MusicViewListener listener) {
		_listener = listener; 
		
		setLayout(new FlowLayout(FlowLayout.LEADING));
		add(_selector);

		refToAvoidGc = _listener.subSharedTracksFolders().addReceiver(new Consumer<Set<String>>() {  @Override public void consume(Set<String> subFoldersPath) {
			loadPathOfSubForders(subFoldersPath);
		}});
	}
	

	private JComboBox newComboBox() {
		final JComboBox subFolders = new JComboBox();
		subFolders.setMaximumRowCount(4);
		subFolders.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			selectMode();
		}});
		
		//downloadedPeersTrackersRefToAvoidGC = listener.numberOfPeerTracks().addReceiver(new Consumer<Integer>() {  @Override public void consume(Integer qty) {
		//	DefaultComboBoxModel model = (DefaultComboBoxModel) subSharedTracksFolders.getModel();
		//	model.insertElementAt("<Inbox - " + qty + " Tracks>", 0);
		//	subSharedTracksFolders.setModel(model);
		//}});
		
		return subFolders;
	}

	
	private void selectMode(){
		if (_selector.getSelectedIndex() == 0)
			_listener.setPeersOperatingMode();
		else {
			_listener.setOwnOperatingMode();
			_listener.setPlayingFolder((String) _selector.getSelectedItem());
		}
	}

	
	private void loadPathOfSubForders(final Set<String> subTracksFoldersPath) {
		if (subTracksFoldersPath == null) return; 
		_selector.removeAllItems();
		_selector.addItem("<Inbox - 7 Tracks>");
		for (String path : subTracksFoldersPath)
			_selector.addItem(path);
	}

}