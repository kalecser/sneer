package dfcsantos.wusic.gui.impl;


import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.TracksFolderKeeper;

class OwnTracksPanel extends AbstractTabPane {

	private final JLabel _ownTracksTabLabel				= new JLabel("Own Tracks");

	private final JFileChooser _playingFolderChooser;
    private final JButton _choosePlayingFolder			= new JButton();
    private final JCheckBox _shuffle					= new JCheckBox();

	OwnTracksPanel() {
		_playingFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
	    	if (chosenFolder != null)
	    		Wusic.setPlayingFolder(chosenFolder);
		}}, JFileChooser.DIRECTORIES_ONLY);
		_playingFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).ownTracksFolder().currentValue());

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
	}

	private void choosePlayingFolderActionPerformed() {
    	_playingFolderChooser.showOpenDialog(null);
    }

    private void shuffleActionPerformed() {
    	Wusic.setShuffle(_shuffle.isSelected());
	}

	private void deleteFileActionPerformed() {
	    Wusic.noWay();
	}

	@Override
    JLabel customTabLabel() {
    	return _ownTracksTabLabel;
    }

	@Override
	protected ControlPanel controlPanel() {
		return new OwnTracksControlPanel();
	}

	private class OwnTracksControlPanel extends ControlPanel {

		private final JButton _deleteFile = new JButton();

		private OwnTracksControlPanel() {
			setName("Share Tracks' Control Panel");

			_deleteFile.setText("Delete File!");
			_deleteFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					deleteFileActionPerformed();
				}
			});
			add(_deleteFile);
		}
	}

}
