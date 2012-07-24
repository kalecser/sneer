package dfcsantos.music.ui.view.impl;

import static basis.environments.Environments.my;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.collections.ListChange;
import sneer.bricks.skin.main.icons.Icons;
import basis.lang.Consumer;
import dfcsantos.music.ui.view.MusicViewListener;

final class FolderSelectionPanel extends JPanel {

	FolderSelectionPanel(MusicViewListener listener) {
		_listener = listener;
		_selector = newSelector();
		_folderChoices = (MutableComboBoxModel<String>) _selector.getModel();
		_trackDownloadedIcon = newTrackDownloadedIcon();

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(_selector);
		add(_trackDownloadedIcon);
		addComponentListener(newComponentListener());

		initModel();
		initExternalEvents();
	}

	private JComboBox<String> newSelector() {
		JComboBox<String> selector = new JComboBox<String>();
		selector.addActionListener(newSelectorActionListener());
		return selector;
	}

	private ActionListener newSelectorActionListener() {
		return new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			if (isEventInternal) return;
			String folderChosen = (String)((JComboBox<String>) e.getSource()).getModel().getSelectedItem();
			if (folderChosen == null) return;
			_listener.playingFolderChosen(folderChosen);
		}};
	}
	
	
	private JLabel newTrackDownloadedIcon() {
		Icon icon = my(Icons.class).load(this.getClass(), "envelop.png");
		JLabel newTrackIcon = new JLabel(icon);
		newTrackIcon.setPreferredSize(new Dimension(20, 20));
		newTrackIcon.setVisible(false);
		newTrackIcon.addMouseListener(newTrackDonwloadedIconMouseListenter());
		return newTrackIcon;
	}

	private MouseListener newTrackDonwloadedIconMouseListenter() {
		return new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
			_listener.playingInboxFolder();
		}};
	}
	
	
	private ComponentListener newComponentListener () {
		return new ComponentAdapter() { @Override public void componentResized(ComponentEvent e) {
			if (e.getID() != ComponentEvent.COMPONENT_RESIZED) return;
			if (_trackDownloadedIcon.isVisible())
				resizeSelectorToShowTrackDownloadedIcon( ((JPanel) e.getSource()).getSize().width );
			else
				resizeSelectorToHideTrackDownloadedIcon( ((JPanel) e.getSource()).getSize().width );
		}};
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

	private void initExternalEvents() {
		_refToAvoidGc2 = _listener.enableTrackDownloaded().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean trackDownloaded) {
			showTrackDownloadedIcon(trackDownloaded);
		}}); 

		_refToAvoidGc3 = _listener.choiceSelected().addReceiver(new Consumer<String>() { @Override public void consume(String choice) {
			selectChoice(choice);
		}});
	}


	private void showTrackDownloadedIcon(Boolean trackDownloaded) {
		_trackDownloadedIcon.setVisible(trackDownloaded);
		if (trackDownloaded)
			resizeSelectorToShowTrackDownloadedIcon(getSize().width);
		else 
			resizeSelectorToHideTrackDownloadedIcon(getSize().width);
	}

	synchronized
	private void resizeSelectorToShowTrackDownloadedIcon(int panelWidth) {
		int widthSelector = panelWidth - (_trackDownloadedIcon.getSize().width + (BORDER_SIZE * 2));  
		resizeSelector(widthSelector);
	}
	
	synchronized
	private void resizeSelectorToHideTrackDownloadedIcon(int panelWidth) {
		int widthSelector = panelWidth - BORDER_SIZE;  
		resizeSelector(widthSelector);
	}
	
	private void resizeSelector(int width) {
		_selector.setPreferredSize(new Dimension(width, _selector.getMinimumSize().height));
	}

	
	private void selectChoice(String choice) {
		if (choice == null) return;
		isEventInternal = true;
		_selector.setSelectedItem(choice);
		isEventInternal = false;
	}
	
	@SuppressWarnings("unused")	private WeakContract _refToAvoidGc, _refToAvoidGc2, _refToAvoidGc3;

	private final MusicViewListener _listener;
	private final JComboBox<String> _selector;
	private final MutableComboBoxModel<String> _folderChoices;
	private final JLabel _trackDownloadedIcon;
	
	private boolean isEventInternal;
	
	private static final int BORDER_SIZE = 10;
}