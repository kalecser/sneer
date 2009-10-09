package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.OperatingMode;

/**
 * 
 * @author daniel
 */
class WusicPanel extends JPanel {

	private static final Wusic Wusic = my(Wusic.class);

    private ButtonGroup _operatingMode				= new ButtonGroup();
    private JRadioButton _ownTracks					= new JRadioButton();
    private JRadioButton _tracksFromPeers 			= new JRadioButton();

    private JFileChooser _ownTracksFolderChooser;
    private JButton _chooseOwnTracksFolder			= new JButton();

    private JCheckBox _shuffleMode					= new JCheckBox();

    private JFileChooser _peerTracksFolderChooser;
    private JButton _choosePeerTracksFolder			= new JButton();

    private JLabel _numberOfTracksFromPeers			= my(ReactiveWidgetFactory.class).newLabel(Wusic.numberOfTracksFetchedFromPeers()).getMainWidget();

    private JLabel _trackLabel						= my(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackName()).getMainWidget();
    private JLabel _trackTime						= my(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackTime()).getMainWidget();

    private JButton _pauseResume					= new JButton();
    private JButton _skip							= new JButton();
    private JButton _stop							= new JButton();

    private JButton _meToo							= new JButton();
    private JButton _noWay							= new JButton();

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	@SuppressWarnings("unused") private WeakContract toAvoidGC2;

	{
		_toAvoidGC = Wusic.operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode mode) {
			update(mode);
		}});

        _operatingMode.add(_ownTracks);
        _ownTracks.setSelected(true);
        _ownTracks.setText("Play Own Tracks");
        _ownTracks.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ownTracksActionPerformed();
            }
        });

        _operatingMode.add(_tracksFromPeers);
        _tracksFromPeers.setText("Play Tracks From Peers");
        _tracksFromPeers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                tracksFromPeersActionPerformed();
            }
        });

        _ownTracksFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
        	if (chosenFolder != null)
        		Wusic.setOwnTracksFolder(chosenFolder);
    	}});
        _ownTracksFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        _chooseOwnTracksFolder.setText("Own Tracks");
        _chooseOwnTracksFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                chooseOwnTracksFolderActionPerformed();
            }
        });

        _shuffleMode.setText("Shuffle Mode");
        _shuffleMode.setSelected(false);
        _shuffleMode.addActionListener(new ActionListener() {
        	@Override public void actionPerformed(ActionEvent e) {
				shuffleModeActionPerformed();
			}
		});

        _peerTracksFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
        	if (chosenFolder != null) {
        		Wusic.setPeerTracksFolder(chosenFolder);
        	}
    	}});
        _peerTracksFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        _choosePeerTracksFolder.setText("Peers Tracks");
        _choosePeerTracksFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                choosePeerTracksFolderActionPerformed();
            }
        });

        _numberOfTracksFromPeers.setFont(new Font("Tahoma", 2, 14));

        _trackLabel.setFont(new Font("Tahoma", 2, 14));
        _trackTime.setFont(new Font("Tahoma", 2, 14));

        toAvoidGC2 = Wusic.isPlaying().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isPlaying) {
        	_pauseResume.setText(isPlaying ? "||" : ">");
		}});

        _pauseResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pauseResumeButtonActionPerformed();
            }
        });

        _skip.setText(">>");
        _skip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                skipButtonActionPerformed();
            }
        });

        _stop.setText("Stop");
        _stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopButtonActionPerformed();
            }
        });

        _meToo.setText("Me Too :)");
        _meToo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                meTooButtonActionPerformed();
            }
        });

        _noWay.setText("Delete File!");
        _noWay.setEnabled(false);
        _noWay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                noWayButtonActionPerformed();
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
        	layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
            	.addContainerGap()
	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	            	.addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	            		.addComponent(_ownTracks)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                	.addComponent(_shuffleMode)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                	.addComponent(_chooseOwnTracksFolder)
	                	.addContainerGap()
	                )
	                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	                	.addComponent(_tracksFromPeers)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                	.addComponent(_numberOfTracksFromPeers)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                	.addComponent(_choosePeerTracksFolder)
	                	.addContainerGap()
	                )
	                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	                	.addComponent(_trackTime)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                	.addComponent(_trackLabel)
	                )
	                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	                	.addComponent(_pauseResume)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                	.addComponent(_skip)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                	.addComponent(_stop)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                	.addComponent(_meToo)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                	.addComponent(_noWay)
	                )
	            )
	            .addContainerGap()
            )
        );

        layout.setVerticalGroup(
        	layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        			.addComponent(_ownTracks)
   					.addComponent(_shuffleMode)
        			.addComponent(_chooseOwnTracksFolder)
        		)
        		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        			.addComponent(_tracksFromPeers)
        			.addComponent(_numberOfTracksFromPeers)
        			.addComponent(_choosePeerTracksFolder)
        		)
        		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                   	.addComponent(_trackTime)
                   	.addComponent(_trackLabel)
        		)
        		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        			.addComponent(_pauseResume)
                    .addComponent(_skip)
                    .addComponent(_stop)
                    .addComponent(_meToo)
                    .addComponent(_noWay)
        		)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        	)
        );
	}

	private void update(OperatingMode mode) {
		switch (mode) {
			case OWN:
				_chooseOwnTracksFolder.setEnabled(true);
				_shuffleMode.setEnabled(true);
				_choosePeerTracksFolder.setEnabled(false);
				_meToo.setEnabled(false);
				_noWay.setText("Delete File!");
				break;

			case PEERS:
				_choosePeerTracksFolder.setEnabled(true);
				_meToo.setEnabled(true);
				_chooseOwnTracksFolder.setEnabled(false);
				_shuffleMode.setEnabled(false);
				_noWay.setText("No Way!");
				break;
		}
	}

    private void chooseOwnTracksFolderActionPerformed() {
    	_ownTracksFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).ownTracksFolder().currentValue());
    	_ownTracksFolderChooser.showOpenDialog(null);
    }

    private void shuffleModeActionPerformed() {
    	Wusic.setShuffleMode(_shuffleMode.isSelected());
	}

    private void choosePeerTracksFolderActionPerformed() {
    	_peerTracksFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).peerTracksFolder().currentValue());
    	_peerTracksFolderChooser.showOpenDialog(null);
    }

    private void ownTracksActionPerformed() {
        Wusic.setOperatingMode(OperatingMode.OWN);
    }

    private void tracksFromPeersActionPerformed() {
    	// The fileChooser will open only if a peerTracksFolder's default value is not used (see TracksFolderKeeperImpl) 
    	if (my(TracksFolderKeeper.class).peerTracksFolder().currentValue() == null)
    		choosePeerTracksFolderActionPerformed();
    	Wusic.setOperatingMode(OperatingMode.PEERS);
    }

	private void pauseResumeButtonActionPerformed() {                                            
    	Wusic.pauseResume();
    }                                           

	private void skipButtonActionPerformed() {
        Wusic.skip();
    }

    private void stopButtonActionPerformed() {
        Wusic.stop();
    }

    private void meTooButtonActionPerformed() {
        Wusic.meToo();
    }

    private void noWayButtonActionPerformed() {
        Wusic.noWay();
    }

	void enableDeleteFileActionPerformed(boolean enabled) {
		_noWay.setEnabled(enabled);
	}

}
