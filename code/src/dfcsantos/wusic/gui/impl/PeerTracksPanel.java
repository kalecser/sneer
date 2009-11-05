package dfcsantos.wusic.gui.impl;
import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic.OperatingMode;

class PeerTracksPanel extends AbstractTabPane {

    private final JLabel _peerTracksCountTabLabel = my(ReactiveWidgetFactory.class).newLabel(_controller.numberOfPeerTracks()).getMainWidget();
	private final JFileChooser _sharedTracksFolderChooser;
    private final JButton _chooseSharedTracksFolder = new JButton();

	PeerTracksPanel() {
        _sharedTracksFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
        	if (chosenFolder != null) {
        		_controller.setSharedTracksFolder(chosenFolder);
        	}
    	}}, JFileChooser.DIRECTORIES_ONLY);
    	_sharedTracksFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).sharedTracksFolder().currentValue());

        _chooseSharedTracksFolder.setText("Shared Folder");
        _chooseSharedTracksFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                chooseSharedTracksFolderActionPerformed();
            }
        });
        customPanel().add(_chooseSharedTracksFolder);
	}

	@Override
	OperatingMode panelOperatingMode() {
		return OperatingMode.PEERS;
	}

    private void chooseSharedTracksFolderActionPerformed() {
    	_sharedTracksFolderChooser.showOpenDialog(null);
    }

	private void meTooActionPerformed() {
        _controller.meToo();
    }

    private void noWayActionPerformed() {
        _controller.noWay();
    }

    @Override
    JLabel customTabLabel() {
    	return _peerTracksCountTabLabel;
    }

    @Override
    ControlPanel controlPanel() {
    	return new PeerTracksControlPanel();
    }

	private class PeerTracksControlPanel extends ControlPanel {

		private final JButton _meToo = new JButton();
		private final JButton _noWay = new JButton();

		private PeerTracksControlPanel() {
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

		@Override
		OperatingMode controlPanelOperatingMode() {
			return panelOperatingMode();
		}

	}

}
