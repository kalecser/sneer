package dfcsantos.music.ui.view.impl;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.ui.view.MusicViewListener;


final class FolderSelectionPanel extends JPanel {
	private final MusicViewListener listener;
	private final DefaultComboBoxModel folderChoices;
	
	
	@SuppressWarnings("unused") private Object refToAvoidGc1;

	
	FolderSelectionPanel(MusicViewListener listener) {
		this.listener = listener; 

		JComboBox selector = newFolderChoices();
		folderChoices = (DefaultComboBoxModel) selector.getModel();
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(selector);
		
		refToAvoidGc1 = listener.playingFolderChoices().addReceiver(new Consumer<CollectionChange<String>>() {  @Override public void consume(CollectionChange<String> value) {
				refresh(value);
		}});
	}
	

	private JComboBox newFolderChoices() {
		final JComboBox comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			listener.playingFolderChosen((String) folderChoices.getSelectedItem());
		}});
		return comboBox;
	}

	
	private void refresh(CollectionChange<String> value) {
		for (String newFolder : value.elementsAdded())
			folderChoices.addElement(newFolder);
		
		for (String oldFolder : value.elementsRemoved())
			folderChoices.removeElement(oldFolder);
	}

}