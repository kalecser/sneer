package dfcsantos.wusic.gui.impl;


import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	private JPanel _northPanel 							= new JPanel();

	private JLabel _trackLabel							= my(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackName()).getMainWidget();
    private JLabel _trackTime							= my(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackTime()).getMainWidget();
    private JPanel _trackDisplay						= new JPanel();

    private JButton _pauseResume						= new JButton();
    private JButton _back								= new JButton();
    private JButton _skip								= new JButton();
    private JButton _stop								= new JButton();
    private JButton _deleteFile							= new JButton();
    private JPanel _controlPanel						= new JPanel();

    private JPanel _southPanel							= new JPanel();

	@SuppressWarnings("unused") private WeakContract toAvoidGC;

	OwnTracksPanel() {
		init();	
	}

	private void init() {
		setComponents();
		setLayout();
		buildPanel();
	}

	private void setComponents() {
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

        _trackTime.setFont(new Font("Tahoma", 2, 14));
        _trackLabel.setFont(new Font("Tahoma", 2, 14));

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
	}

	private void setLayout() {
		this.setLayout(new BorderLayout(3, 3));
			_northPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 9, 3));
			_southPanel.setLayout(new GridLayout(2,1));
        		_trackDisplay.setLayout(new FlowLayout(FlowLayout.LEFT, 9, 3));
        		_controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 9, 3));
	}

	private void buildPanel() {
        _northPanel.add(_choosePlayingFolder);
        _northPanel.add(_shuffle);

        _trackDisplay.add(_trackTime);
        _trackDisplay.add(_trackLabel);

        _controlPanel.add(_pauseResume);
        _controlPanel.add(_back);
        _controlPanel.add(_skip);
        _controlPanel.add(_stop);
        _controlPanel.add(_deleteFile);

		_southPanel.add(_trackDisplay);
		_southPanel.add(_controlPanel);

        this.add(_northPanel, BorderLayout.NORTH);
        this.add(_southPanel, BorderLayout.SOUTH);
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
