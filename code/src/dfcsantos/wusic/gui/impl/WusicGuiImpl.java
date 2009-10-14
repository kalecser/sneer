package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JFrame;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
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
    private WusicPanel _wusicPanel;

    private boolean _isInitialized = false;

    {
		my(MainMenu.class).addAction("Wusic", new Runnable() { @Override public void run() {
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

    	result.setResizable(false);
		result.pack();

    	return result;
	}

	private Signal<String> title() {
		return my(Signals.class).adapt(Wusic.playingTrackName(), new Functor<String, String>() { @Override public String evaluate(String track) {
			return "Wusic :: " + track;
		}});
	}

}
