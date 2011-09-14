package dfcsantos.music.ui.view.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.collections.ListChange;
import dfcsantos.music.ui.view.MusicViewListener;


final class FolderSelectionPanel extends JPanel implements ActionListener {
	private final MusicViewListener _listener;
	private final MutableComboBoxModel _folderChoices;

	@SuppressWarnings("unused")	private WeakContract _refToAvoidGc;
	
	FolderSelectionPanel(MusicViewListener listener) {
		_listener = listener;
		
		JComboBox selector = newSelector();
		_folderChoices = (MutableComboBoxModel) selector.getModel();
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(selector);
		initModel();
	}

	private JComboBox newSelector() {
		JComboBox selector = new JComboBox();
		selector.addActionListener(this);
		selector.setPreferredSize(new Dimension(250, selector.getMinimumSize().height));
		return selector;
	}

	private void initModel() {
		_refToAvoidGc = _listener.playingFolderChoices().addListReceiverAsVisitor(new ListChange.Visitor<String>() {
			@Override
			public void elementReplaced(int index, String oldElement, String newElement) {
				_folderChoices.removeElementAt(index);
				_folderChoices.insertElementAt(newElement, index);
			}

			@Override
			public void elementRemoved(int index, String element) {
				_folderChoices.removeElementAt(index);
			}
			
			@Override
			public void elementMoved(int index, int newIndex, String newElement) {
				_folderChoices.removeElementAt(index);
				_folderChoices.insertElementAt(newElement, newIndex);
			}
			
			@Override
			public void elementAdded(int index, String element) {
				_folderChoices.addElement(element);
				if ( _folderChoices.getSelectedItem() != null ) return; 
				if ( _folderChoices.getSize() > 0  ) _folderChoices.setSelectedItem(_folderChoices.getElementAt(0));
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String folderChosen = (String)((JComboBox) e.getSource()).getModel().getSelectedItem();
		if (folderChosen != null)
			_listener.playingFolderChosen(folderChosen);
	}
}