package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JFrame;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Functor;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.gui.WusicGui;

/**
 *
 * @author daniel
 */
class WusicGuiImpl implements WusicGui {
    
    private static final Wusic Wusic = my(Wusic.class);
	private javax.swing.JMenuItem chooseMyTracksFolder;
    private javax.swing.JMenu mainMenu;
    private javax.swing.JMenuBar mainMenuBar;
    
    private JFrame _frame;

    private boolean _isInitialized = false;

    
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
		mainMenuBar = new javax.swing.JMenuBar();
		mainMenu = new javax.swing.JMenu();
		chooseMyTracksFolder = new javax.swing.JMenuItem();

        mainMenu.setText("File");
        chooseMyTracksFolder.setText("Choose Song Folder");
        chooseMyTracksFolder.setName("chooseSongFolderMenu"); // NOI18N                    Thread.sleep(100);
        chooseMyTracksFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseMyTracksFolderActionPerformed();
            }
        });
        mainMenu.add(chooseMyTracksFolder);
        chooseMyTracksFolder.getAccessibleContext().setAccessibleName("chooseSongFolderMenu");

        mainMenuBar.add(mainMenu);


		result = my(ReactiveWidgetFactory.class).newFrame(title()).getMainWidget();
		result.setJMenuBar(mainMenuBar);
    	result.getContentPane().add(new WusicPanel());
    	result.pack();
		return result;
	}
	
    private void chooseMyTracksFolderActionPerformed() {
        // TODO add your handling code here:
    }


	
	private Signal<String> title() {
		return my(Signals.class).adapt(Wusic.trackPlayingName(), new Functor<String, String>() { @Override public String evaluate(String track) {
			return "Wusic :: " + track;
		}});
	}
	
}
