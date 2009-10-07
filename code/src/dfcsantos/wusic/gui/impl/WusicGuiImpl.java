package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.gui.WusicGui;

/**
 *
 * @author daniel
 */
class WusicGuiImpl implements WusicGui {

    private static final Wusic Wusic = my(Wusic.class);

	private JFileChooser _tracksFolderChooser;
    private JMenuItem _chooseMyTracksFolder;
    private JCheckBoxMenuItem _enableDeleteFile;
    private JMenu _mainMenu;
    private JMenuBar _mainMenuBar;
    private JFrame _frame;

    private boolean _isInitialized = false;

	private WusicPanel _wusicPanel;

    {
		Environments.my(MainMenu.class).addAction("Wusic", new Runnable() { @Override public void run() {
			if (!_isInitialized){
				_isInitialized = true;
				_frame = initFrame();
				Wusic.start();
			}
			_frame.setVisible(true);
		}});
	}

	private JFrame initFrame() {
		JFrame result;
		result = my(ReactiveWidgetFactory.class).newFrame(title()).getMainWidget();
		_wusicPanel = new WusicPanel();
		result.getContentPane().add(_wusicPanel);

		_mainMenu = new JMenu();
		_mainMenu.setText("File");

		_mainMenuBar = new JMenuBar();
        _mainMenuBar.add(_mainMenu);

		_chooseMyTracksFolder = new JMenuItem();
        _chooseMyTracksFolder.setText("Configure Root Track Folder");
        _chooseMyTracksFolder.setName("configureTrackFolderMenu");
        _chooseMyTracksFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                chooseMyTracksFolderActionPerformed();
            }
        });
        _chooseMyTracksFolder.getAccessibleContext().setAccessibleName("chooseSongFolderMenu");
        _mainMenu.add(_chooseMyTracksFolder);

        _tracksFolderChooser = my(FileChoosers.class).newFileChooser(new Consumer<File>() { @Override public void consume(File chosenFolder) {
        	if (chosenFolder != null) {
        		Wusic.setMyTracksFolder(chosenFolder);
        	}
    	}});
        _tracksFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        _tracksFolderChooser.setCurrentDirectory(my(OwnTracksFolderKeeper.class).ownTracksFolder().currentValue());

        _enableDeleteFile = new JCheckBoxMenuItem();
        _enableDeleteFile.setText("Enable Delete Button");
        _enableDeleteFile.setSelected(false);
        _enableDeleteFile.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
				_wusicPanel.enableDeleteFileActionPerformed(_enableDeleteFile.isSelected());
			}
		});

        _mainMenu.add(_enableDeleteFile);

		result.setJMenuBar(_mainMenuBar);
    	result.pack();
    	result.setResizable(false);

    	return result;
	}

    private void chooseMyTracksFolderActionPerformed() {
    	_tracksFolderChooser.showOpenDialog(null);
    	_tracksFolderChooser.setCurrentDirectory(my(OwnTracksFolderKeeper.class).ownTracksFolder().currentValue());
    }

	private Signal<String> title() {
		return my(Signals.class).adapt(Wusic.playingTrackName(), new Functor<String, String>() { @Override public String evaluate(String track) {
			return "Wusic :: " + track;
		}});
	}

}
