package dfcsantos.wusic.gui.impl.old;

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
class WusicGuiImpl000 implements WusicGui {
    
    private static final Wusic Wusic = my(Wusic.class);

    private JFrame _frame;

    
    {
		Environments.my(MainMenu.class).addAction("Wusic", new Runnable() { @Override public void run() {
			if (_frame == null)
				_frame = initFrame();
			
			_frame.setVisible(true);
		}});
	}

    
	private JFrame initFrame() {
		JFrame result;
		result = my(ReactiveWidgetFactory.class).newFrame(title()).getMainWidget();
    	result.getContentPane().add(new WusicPanel000());
    	result.pack();
		return result;
	}

	
	private Signal<String> title() {
		return my(Signals.class).adapt(Wusic.trackPlaying(), new Functor<String, String>() { @Override public String evaluate(String track) {
			return "Wusic :: " + track;
		}});
	}
	
}
