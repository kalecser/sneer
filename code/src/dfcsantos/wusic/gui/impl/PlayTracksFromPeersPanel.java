package dfcsantos.wusic.gui.impl;
import static sneer.foundation.environments.Environments.my;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic;

public class PlayTracksFromPeersPanel extends JPanel {

	private static final Wusic Wusic = my(Wusic.class); 

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

	@SuppressWarnings("unused") private WeakContract toAvoidGC;

	{
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

        toAvoidGC = Wusic.isPlaying().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isPlaying) {
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

        _noWay.setText("No Way :(");
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

    private void choosePeerTracksFolderActionPerformed() {
    	_peerTracksFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).peerTracksFolder().currentValue());
    	_peerTracksFolderChooser.showOpenDialog(null);
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
