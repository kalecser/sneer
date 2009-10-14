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

public class SharedTracksPanel extends JPanel {

	private static final Wusic Wusic = my(Wusic.class); 

    private JFileChooser _sharedTracksFolderChooser;
    private JButton _chooseSharedTracksFolder			= new JButton();

    private JLabel _trackLabel							= my(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackName()).getMainWidget();
    private JLabel _trackTime							= my(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackTime()).getMainWidget();

    private JButton _pauseResume						= new JButton();
    private JButton _back								= new JButton();
    private JButton _skip								= new JButton();
    private JButton _stop								= new JButton();

    private JButton _meToo								= new JButton();
    private JButton _noWay								= new JButton();

	@SuppressWarnings("unused") private WeakContract toAvoidGC;

	{
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

        _meToo.setText("Me Too :)");
        _meToo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                meTooActionPerformed();
            }
        });

        _noWay.setText("No Way :(");
        _noWay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                noWayActionPerformed();
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
        	layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
            	.addContainerGap()
	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	            	.addComponent(_chooseSharedTracksFolder, GroupLayout.Alignment.LEADING)
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
       			.addComponent(_chooseSharedTracksFolder)
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
                    .addComponent(_meToo)
                    .addComponent(_noWay)
        		)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        	)
        );
	}

    private void chooseSharedTracksFolderActionPerformed() {
    	_sharedTracksFolderChooser.setCurrentDirectory(my(TracksFolderKeeper.class).sharedTracksFolder().currentValue());
    	_sharedTracksFolderChooser.showOpenDialog(null);
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

    private void meTooActionPerformed() {
        Wusic.meToo();
    }

    private void noWayActionPerformed() {
        Wusic.noWay();
    }

}
