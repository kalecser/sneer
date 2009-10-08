package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

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

    private JFrame _frame;
    private JMenuBar _mainMenuBar;
    private JMenu _mainMenu;
    private JCheckBoxMenuItem _enableDeleteFile;

    private WusicPanel _wusicPanel;

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
		JFrame result = my(ReactiveWidgetFactory.class).newFrame(title()).getMainWidget();

		_wusicPanel = new WusicPanel();
		result.getContentPane().add(_wusicPanel);

		_mainMenu = new JMenu();
		_mainMenu.setText("File");

		_mainMenuBar = new JMenuBar();
        _mainMenuBar.add(_mainMenu);

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

	private Signal<String> title() {
		return my(Signals.class).adapt(Wusic.playingTrackName(), new Functor<String, String>() { @Override public String evaluate(String track) {
			return "Wusic :: " + track;
		}});
	}

}
