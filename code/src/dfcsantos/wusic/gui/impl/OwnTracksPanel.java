package dfcsantos.wusic.gui.impl;


import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import dfcsantos.wusic.Wusic.OperatingMode;

class OwnTracksPanel extends AbstractTabPane {

	private final JLabel _ownTracksTabLabel				= newReactiveLabel();
	private final JFileChooser _playingFolderChooser;
    private final JButton _choosePlayingFolder			= new JButton();
    private final JCheckBox _shuffle					= new JCheckBox();

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;

	OwnTracksPanel() {
		_playingFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
	    	if (chosenFolder != null)
	    		_controller.setPlayingFolder(chosenFolder);
		}}, JFileChooser.DIRECTORIES_ONLY);
		_playingFolderChooser.setCurrentDirectory(_controller.playingFolder());

	    _choosePlayingFolder.setText("Playing Folder");
	    _choosePlayingFolder.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent notUsed) {
	    	choosePlayingFolderActionPerformed();
	    }});
	    customPanel().add(_choosePlayingFolder);

	    addShuffle();

		_toAvoidGC = _controller.operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode operatingMode) {
			updateComponents(operatingMode);
		}});
	}

	
	private void addShuffle() {
		_shuffle.setText("Shuffle");
	    _shuffle.setSelected(true);
	    _shuffle.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent notUsed) {
	    	shuffleActionPerformed();
	    }});
	    shuffleActionPerformed();
	    customPanel().add(_shuffle);
	}

	
	@Override
	boolean isMyOperatingMode(OperatingMode operatingMode) {
		return myOperatingMode().equals(operatingMode);
	}

	private OperatingMode myOperatingMode() {
		return OperatingMode.OWN;
	}

	private JLabel newReactiveLabel() {
		return my(ReactiveWidgetFactory.class).newLabel(
			my(Signals.class).adapt(_controller.numberOfOwnTracks(), new Functor<Integer, String>() { @Override public String evaluate(Integer numberOfTracks) {
				return "Own Tracks (" + numberOfTracks + ")";
			}})
		).getMainWidget();
	}

	private void choosePlayingFolderActionPerformed() {
    	_playingFolderChooser.showOpenDialog(null);
    }

    private void shuffleActionPerformed() {
    	_controller.setShuffle(_shuffle.isSelected());
	}

	@Override
    JLabel customTabLabel() {
    	return _ownTracksTabLabel;
    }

	@Override
	void updateComponents(OperatingMode operatingMode) {
		super.updateComponents(operatingMode);
		if (isMyOperatingMode(operatingMode))
			_shuffle.setEnabled(true);
		else
			_shuffle.setEnabled(false);
	}

	@Override
	ControlPanel newControlPanel() {
		return new OwnTracksControlPanel();
	}

	private class OwnTracksControlPanel extends ControlPanel {

		private final JButton _deleteFile = new JButton();

		private OwnTracksControlPanel() {
			_deleteFile.setText("Delete File!");
			_deleteFile.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent notUsed) {
				deleteFileActionPerformed();
			}});
			add(_deleteFile);
		}

		@Override
		boolean isMyOperatingMode(OperatingMode operatingMode) {
			return OwnTracksPanel.this.isMyOperatingMode(operatingMode);
		}

		@Override
		void activateMyOperatingMode() {
			_controller.setOperatingMode(myOperatingMode());
		}

		@Override
		void enableButtons() {
			super.enableButtons();
			_deleteFile.setEnabled(true);
		}

		@Override
		void disableButtons() {
			super.disableButtons();
			_deleteFile.setEnabled(false);
		}

		private void deleteFileActionPerformed() {
		    _controller.deleteTrack();
		}

	}

}
