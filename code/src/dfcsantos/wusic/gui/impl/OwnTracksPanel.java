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
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic.OperatingMode;

class OwnTracksPanel extends AbstractTabPane {

	private final JLabel _ownTracksTabLabel				= new JLabel("Own Tracks");
	private final JFileChooser _playingFolderChooser;
    private final JButton _choosePlayingFolder			= new JButton();
    private final JCheckBox _shuffle					= new JCheckBox();

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;

	OwnTracksPanel() {
		_playingFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
	    	if (chosenFolder != null)
	    		_controller.setPlayingFolder(chosenFolder);
		}}, JFileChooser.DIRECTORIES_ONLY);
		_playingFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).playingFolder().currentValue());

	    _choosePlayingFolder.setText("Playing Folder");
	    _choosePlayingFolder.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	            choosePlayingFolderActionPerformed();
	        }
	    });
	    customPanel().add(_choosePlayingFolder);

	    _shuffle.setText("Shuffle");
	    _shuffle.setSelected(false);
	    _shuffle.addActionListener(new ActionListener() {
	    	@Override public void actionPerformed(ActionEvent e) {
				shuffleActionPerformed();
			}
		});
	    customPanel().add(_shuffle);

		_toAvoidGC = _controller.operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode operatingMode) {
			updateComponents(operatingMode);
		}});
	}

	@Override
	boolean isMyOperatingMode(OperatingMode operatingMode) {
		return OperatingMode.OWN.equals(operatingMode);
	}

	private void choosePlayingFolderActionPerformed() {
    	_playingFolderChooser.showOpenDialog(null);
    }

    private void shuffleActionPerformed() {
    	_controller.setShuffle(_shuffle.isSelected());
	}

	private void deleteFileActionPerformed() {
	    _controller.noWay();
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
			_deleteFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					deleteFileActionPerformed();
				}
			});
			add(_deleteFile);
		}

		@Override
		boolean isMyOperatingMode(OperatingMode operatingMode) {
			return OwnTracksPanel.this.isMyOperatingMode(operatingMode);
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

	}

}
