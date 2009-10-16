package dfcsantos.wusic.gui.impl;
import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.TracksFolderKeeper;

class SharedTracksPanel extends AbstractTabPane {

	private JFileChooser _sharedTracksFolderChooser;
    private JButton _chooseSharedTracksFolder = new JButton();

	@SuppressWarnings("unused") private WeakContract toAvoidGC;

	SharedTracksPanel() {
        _sharedTracksFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
        	if (chosenFolder != null) {
        		Wusic.setSharedTracksFolder(chosenFolder);
        	}
    	}});
        _sharedTracksFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        _chooseSharedTracksFolder.setText("Shared Folder");
        _chooseSharedTracksFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                chooseSharedTracksFolderActionPerformed();
            }
        });
        customPanel().add(_chooseSharedTracksFolder);
	}

    private void chooseSharedTracksFolderActionPerformed() {
    	_sharedTracksFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).sharedTracksFolder().currentValue());
    	_sharedTracksFolderChooser.showOpenDialog(null);
    }

	private void meTooActionPerformed() {
        Wusic.meToo();
    }

    private void noWayActionPerformed() {
        Wusic.noWay();
    }

    @Override
    protected ControlPanel controlPanel() {
    	return new ShareTracksControlPanel();
    }

	private class ShareTracksControlPanel extends ControlPanel {

		private final JButton _meToo = new JButton();
		private final JButton _noWay = new JButton();

		private ShareTracksControlPanel() {
	        _meToo.setText("Me Too :)");
	        _meToo.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	                meTooActionPerformed();
	            }
	        });
	        add(_meToo);

	        _noWay.setText("No Way :(");
	        _noWay.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	                noWayActionPerformed();
	            }
	        });
	        add(_noWay);
		}
	}

}
