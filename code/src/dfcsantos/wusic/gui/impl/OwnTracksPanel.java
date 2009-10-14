package dfcsantos.wusic.gui.impl;


import static sneer.foundation.environments.Environments.my;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

public class OwnTracksPanel extends JPanel {

	private static final Wusic Wusic = my(Wusic.class);

    private JFileChooser _playingFolderChooser;
    private JButton _choosePlayingFolder				= new JButton();

    private JCheckBox _shuffle							= new JCheckBox();

    private JLabel _trackLabel							= my(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackName()).getMainWidget();
    private JLabel _trackTime							= my(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackTime()).getMainWidget();

    private JButton _pauseResume						= new JButton();
    private JButton _back								= new JButton();
    private JButton _skip								= new JButton();
    private JButton _stop								= new JButton();

    private JButton _deleteFile							= new JButton();

	@SuppressWarnings("unused") private WeakContract toAvoidGC;

	{
        _playingFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
        	if (chosenFolder != null)
        		Wusic.setPlayingFolder(chosenFolder);
    	}});
        _playingFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        _choosePlayingFolder.setText("Playing Folder");
        _choosePlayingFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                choosePlayingFolderActionPerformed();
            }
        });

        _shuffle.setText("Shuffle");
        _shuffle.setSelected(false);
        _shuffle.addActionListener(new ActionListener() {
        	@Override public void actionPerformed(ActionEvent e) {
				shuffleActionPerformed();
			}
		});

        _trackLabel.setFont(new Font("Tahoma", 2, 14));
        _trackTime.setFont(new Font("Tahoma", 2, 14));

        toAvoidGC = Wusic.isPlaying().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isPlaying) {
        	_pauseResume.setText(isPlaying ? "||" : ">");
		}});

        _pauseResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pauseResumeActionPerformed();
            }
        });

        _back.setText("<<");
        _back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                backActionPerformed();
            }
        });

        _skip.setText(">>");
        _skip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                skipActionPerformed();
            }
        });

        _stop.setText("Stop");
        _stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopActionPerformed();
            }
        });

        _deleteFile.setText("Delete File!");
        _deleteFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteFileActionPerformed();
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
	                	.addComponent(_shuffle)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                	.addComponent(_choosePlayingFolder)
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
	                	.addComponent(_back)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                	.addComponent(_skip)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                	.addComponent(_stop)
	                	.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                	.addComponent(_deleteFile)
	                )
	            )
	            .addContainerGap()
            )
        );

        layout.setVerticalGroup(
        	layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
   					.addComponent(_shuffle)
        			.addComponent(_choosePlayingFolder)
        		)
        		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                   	.addComponent(_trackTime)
                   	.addComponent(_trackLabel)
        		)
        		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        			.addComponent(_pauseResume)
                    .addComponent(_back)
        			.addComponent(_skip)
                    .addComponent(_stop)
                    .addComponent(_deleteFile)
        		)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        	)
        );
	}

    private void choosePlayingFolderActionPerformed() {
    	_playingFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).ownTracksFolder().currentValue());
    	_playingFolderChooser.showOpenDialog(null);
    }

    private void shuffleActionPerformed() {
    	Wusic.setShuffle(_shuffle.isSelected());
	}

	private void pauseResumeActionPerformed() {                                            
    	Wusic.pauseResume();
    }                                           

	private void backActionPerformed() {
        Wusic.back();
    }

	private void skipActionPerformed() {
        Wusic.skip();
    }

    private void stopActionPerformed() {
        Wusic.stop();
    }

    private void deleteFileActionPerformed() {
        Wusic.noWay();
    }

}
