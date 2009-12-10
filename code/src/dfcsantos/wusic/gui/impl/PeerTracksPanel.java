package dfcsantos.wusic.gui.impl;
import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.utils.consumers.parsers.integer.IntegerParsers;
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.bricks.skin.widgets.reactive.NotificationPolicy;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;
import dfcsantos.wusic.Wusic.OperatingMode;

class PeerTracksPanel extends AbstractTabPane {

    private final JLabel _peerTracksCountTabLabel			= newReactiveLabel();
	private final JFileChooser _sharedTracksFolderChooser;
    private final JButton _chooseSharedTracksFolder			= new JButton();

    private final JLabel _tracksDownloadAllowanceLabel		= new JLabel();
    private final JTextField _tracksDownloadAllowance		= newReactiveTextField();
    private final JCheckBox _allowTracksDownload			= newReactiveCheckBox();

    @SuppressWarnings("unused")	private final WeakContract _toAvoidGC;

	PeerTracksPanel() {
        _sharedTracksFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
        	if (chosenFolder != null) {
        		_controller.setSharedTracksFolder(chosenFolder);
        	}
    	}}, JFileChooser.DIRECTORIES_ONLY);
    	_sharedTracksFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).sharedTracksFolder().currentValue());

        _chooseSharedTracksFolder.setText("Shared Folder");
        _chooseSharedTracksFolder.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent evt) {
                chooseSharedTracksFolderActionPerformed();
        }});
        customPanel().add(_chooseSharedTracksFolder);

        _allowTracksDownload.setText("Allow Tracks Download");
        customPanel().add(_allowTracksDownload);

        _tracksDownloadAllowanceLabel.setText("-   Limit (MBs):");
        customPanel().add(_tracksDownloadAllowanceLabel);

        _tracksDownloadAllowance.setPreferredSize(new Dimension(42, 18));
        customPanel().add(_tracksDownloadAllowance);

		_toAvoidGC = _controller.operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode operatingMode) {
			updateComponents(operatingMode);
		}});
	}

	@Override
	boolean isMyOperatingMode(OperatingMode operatingMode) {
		return OperatingMode.PEERS.equals(operatingMode);
	}

	private JLabel newReactiveLabel() {
		return my(ReactiveWidgetFactory.class).newLabel(_controller.numberOfPeerTracks()).getMainWidget();
	}

	private JCheckBox newReactiveCheckBox() {
		return my(ReactiveWidgetFactory.class).newCheckBox(
			_controller.isTracksDownloadAllowed(),
			new Consumer<Boolean>() { @Override public void consume(Boolean isTracksDownloadAllowed) { _controller.allowTracksDownload(isTracksDownloadAllowed); } },
			new Runnable() { @Override public void run() { allowTracksDownloadActionPerformed(_controller.isTracksDownloadAllowed().currentValue()); } }
		).getMainWidget();
	}

	private JTextField newReactiveTextField() {
		return my(ReactiveWidgetFactory.class).newTextField(
			_controller.tracksDownloadAllowance(), my(IntegerParsers.class).newIntegerParser(_controller.tracksDownloadAllowanceSetter()), NotificationPolicy.OnEnterPressedOrLostFocus
		).getMainWidget();
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

	private void allowTracksDownloadActionPerformed(boolean isSelected) {
		if (isSelected) {
			_tracksDownloadAllowanceLabel.setEnabled(true);
			_tracksDownloadAllowance.setEnabled(true);
		} else {
			_tracksDownloadAllowanceLabel.setEnabled(false);
			_tracksDownloadAllowance.setEnabled(false);			
		}
	}

    @Override
    JLabel customTabLabel() {
    	return _peerTracksCountTabLabel;
    }

    @Override
    ControlPanel newControlPanel() {
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
		boolean isMyOperatingMode(OperatingMode operatingMode) {
			return PeerTracksPanel.this.isMyOperatingMode(operatingMode);
		}

		@Override
		void enableButtons() {
			super.enableButtons();
			_meToo.setEnabled(true);
			_noWay.setEnabled(true);
		}

		@Override
		void disableButtons() {
			super.disableButtons();
			_meToo.setEnabled(false);
			_noWay.setEnabled(false);
		}

	}

}
