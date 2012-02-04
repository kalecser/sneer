package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.collections.ListChange;
import sneer.bricks.skin.main.icons.Icons;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.ui.view.MusicViewListener;

final class FolderSelectionPanel extends JPanel implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if (isEventInternal) return;
		String folderChosen = (String)((JComboBox) e.getSource()).getModel().getSelectedItem();
		if (folderChosen == null) return;
		_listener.playingFolderChosen(folderChosen);
	}
	
	
	FolderSelectionPanel(MusicViewListener listener) {
		_listener = listener;
		_selector = newSelector();
		_trackDownloadedIcon = newTrackDownloadedIcon();

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(_selector);
		add(_trackDownloadedIcon);
		initModel();

		_folderChoices = (MutableComboBoxModel) _selector.getModel();

		_refToAvoidGc2 = _listener.enableTrackDownloaded().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean trackDownloaded) {
			showTrackDownloadedIcon(trackDownloaded);
		}}); 
	}

	private JComboBox newSelector() {
		JComboBox selector = new JComboBox();
		selector.addActionListener(this);
		selector.setPreferredSize(new Dimension(250, selector.getMinimumSize().height));
		selector.addFocusListener(newFocusListener());
		
		return selector;
	}

	private FocusListener newFocusListener() {
		return new FocusListener() { 
			@Override
			public void focusLost(FocusEvent e) {
				if (!_trackDownloadedIcon.isVisible()) return;
				_trackDownloadedIcon.setVisible(false);
				_selector.setPreferredSize(new Dimension(250, _selector.getMinimumSize().height));
			}
			
			@Override public void focusGained(FocusEvent e) {}
		};
	}
	
	
	private JLabel newTrackDownloadedIcon() {
		Icon icon = my(Icons.class).load(this.getClass(), "envelop.png");
		JLabel newTrackIcon = new JLabel(icon);
		newTrackIcon.setPreferredSize(new Dimension(20, 20));
		newTrackIcon.setVisible(false);
		
		return newTrackIcon;
	}

	
	private void initModel() {
		_refToAvoidGc = _listener.playingFolderChoices().addListReceiverAsVisitor(new ListChange.Visitor<String>() {

			@Override
			public void elementReplaced(int index, String oldElement, String newElement) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void elementRemoved(int index, String element) {
				isEventInternal = true;
				_folderChoices.removeElementAt(index);
				isEventInternal = false;
			}
			
			@Override
			public void elementMoved(int index, int newIndex, String newElement) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void elementAdded(int index, String element) {
				isEventInternal = true;
				_folderChoices.insertElementAt(element, index);
				if (element.equals(_listener.playingFolder()))
					_folderChoices.setSelectedItem(element);
				isEventInternal = false;
			}
		});
	}

	private void showTrackDownloadedIcon(Boolean trackDownloaded) {
		if (trackDownloaded == false) return;
		_trackDownloadedIcon.setVisible(true);
		_selector.setPreferredSize(new Dimension(220, _selector.getMinimumSize().height));
	}

	
	@SuppressWarnings("unused")	private WeakContract _refToAvoidGc, _refToAvoidGc2;

	private final MusicViewListener _listener;
	private MutableComboBoxModel _folderChoices;
	private final JComboBox _selector;
	private final JLabel _trackDownloadedIcon;
	
	private boolean isEventInternal;
}