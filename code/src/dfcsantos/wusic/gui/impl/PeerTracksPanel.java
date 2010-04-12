package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import sneer.bricks.expression.files.client.downloads.gui.DownloadsPanel;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.utils.consumers.parsers.integer.IntegerParsers;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.bricks.skin.widgets.reactive.NotificationPolicy;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic.OperatingMode;

class PeerTracksPanel extends AbstractTabPane {

    private final JLabel _peerTracksCountTabLabel			= newReactiveLabel();
	private final JFileChooser _sharedTracksFolderChooser;
    private final JButton _chooseSharedTracksFolder			= new JButton();

    private final JLabel _downloadAllowanceLabel			= new JLabel();
    private final JTextField _downloadAllowance				= newReactiveTextField();
    private final JCheckBox _downloadActivity				= newReactiveCheckBox();

    private final JButton _downloadsDetails					= new JButton();
    private final JFrame _downloadsDetailsWindow			= newReactiveFrame();

    @SuppressWarnings("unused")	private final WeakContract _toAvoidGC;

	PeerTracksPanel() {
        _sharedTracksFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
        	if (chosenFolder != null) {
        		_controller.setSharedTracksFolder(chosenFolder);
        	}
    	}}, JFileChooser.DIRECTORIES_ONLY);
    	_sharedTracksFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).sharedTracksFolder().currentValue());

        _chooseSharedTracksFolder.setText("Shared Folder");
        _chooseSharedTracksFolder.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent notUsed) {
        	chooseSharedTracksFolderActionPerformed();
        }});
        customPanel().add(_chooseSharedTracksFolder);

        _downloadActivity.setText("Download Tracks");
        customPanel().add(_downloadActivity);

        _downloadAllowanceLabel.setText("-   Limit (MBs):");
        customPanel().add(_downloadAllowanceLabel);

        _downloadAllowance.setPreferredSize(new Dimension(42, 18));
        customPanel().add(_downloadAllowance);

        _downloadsDetails.setText("Details >>");
        _downloadsDetails.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent notUsed) {
        	downloadsDetailsActionPerformed();
        }});
        customPanel().add(_downloadsDetails);

        _downloadsDetailsWindow.add(new DownloadsPanel(_controller.activeTrackDownloads()));
        _downloadsDetailsWindow.setLocationRelativeTo(customPanel().getTopLevelAncestor());
        _downloadsDetailsWindow.setMinimumSize(new Dimension(350, 80));
        _downloadsDetailsWindow.setResizable(false);
        _downloadsDetailsWindow.setVisible(false);

		_toAvoidGC = _controller.operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode operatingMode) {
			updateComponents(operatingMode);
		}});
	}

	@Override
	boolean isMyOperatingMode(OperatingMode operatingMode) {
		return myOperatingMode().equals(operatingMode);
	}

	private OperatingMode myOperatingMode() {
		return OperatingMode.PEERS;
	}

	private JLabel newReactiveLabel() {
		return my(ReactiveWidgetFactory.class).newLabel(
			my(Signals.class).adapt(_controller.numberOfPeerTracks(), new Functor<Integer, String>() { @Override public String evaluate(Integer numberOfTracks) {
				return "Peer Tracks (" + numberOfTracks + ")";
			}})
		).getMainWidget();
	}

	private JCheckBox newReactiveCheckBox() {
		return my(ReactiveWidgetFactory.class).newCheckBox(
			_controller.isTrackDownloadActive(),
			_controller.trackDownloadActivator(),
			new Closure() { @Override public void run() {
				allowDownloadsActionPerformed(_controller.isTrackDownloadActive().currentValue());
			}}
		).getMainWidget();
	}

	private JTextField newReactiveTextField() {
		return my(ReactiveWidgetFactory.class).newTextField(
			_controller.trackDownloadAllowance(), my(IntegerParsers.class).newIntegerParser(_controller.trackDownloadAllowanceSetter()), NotificationPolicy.OnEnterPressedOrLostFocus
		).getMainWidget();
	}

	private JFrame newReactiveFrame() {
		return my(ReactiveWidgetFactory.class).newFrame(
			my(Signals.class).adapt(_controller.activeTrackDownloads().size(), new Functor<Integer, String>() { @Override public String evaluate(Integer numberOfDownloadsRunning) throws RuntimeException {
				return (numberOfDownloadsRunning > 0) ? "Active Downloads' Details:" : "Active Downloads <None>";
			}})
		).getMainWidget();
	}

    private void chooseSharedTracksFolderActionPerformed() {
    	_sharedTracksFolderChooser.showOpenDialog(null);
    }

	private void allowDownloadsActionPerformed(boolean isSelected) {
		if (isSelected) {
			_downloadAllowanceLabel.setEnabled(true);
			_downloadAllowance.setEnabled(true);
		} else {
			_downloadAllowanceLabel.setEnabled(false);
			_downloadAllowance.setEnabled(false);			
		}
	}

	private void downloadsDetailsActionPerformed() {
		_downloadsDetailsWindow.setVisible(true);
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
		@SuppressWarnings("unused") private final WeakContract _toAvoidGC2;

		private PeerTracksControlPanel() {
	        _meToo.setText("Me Too :)");
	        _meToo.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent notUsed) {
	        	meTooActionPerformed();
	        }});
	        _toAvoidGC2 = _controller.playingTrack().addPulseReceiver(new Runnable() { @Override public void run() {
	        	if (isMyOperatingMode(_controller.operatingMode().currentValue()))
	        		_meToo.setEnabled(true);
	        }});
	        add(_meToo);

	        _noWay.setText("No Way :(");
	        _noWay.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent notUsed) {
	        	noWayActionPerformed();
	        }});
	        add(_noWay);
		}

		@Override
		boolean isMyOperatingMode(OperatingMode operatingMode) {
			return PeerTracksPanel.this.isMyOperatingMode(operatingMode);
		}

		@Override
		void activateMyOperatingMode() {
			_controller.setOperatingMode(myOperatingMode());
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

		private void meTooActionPerformed() {
			_meToo.setEnabled(false);
	        _controller.meToo();
	    }

	    private void noWayActionPerformed() {
	        _controller.deleteTrack();
	    }

	}

}
