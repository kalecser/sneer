package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Dimension;

import javax.swing.JFrame;

import sneer.bricks.expression.files.client.downloads.gui.DownloadListPanels;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Functor;
import dfcsantos.music.Music;

class DownloadsView {

	private static final Music Music = my(Music.class);
	private static DownloadsView instance;
	
    private final JFrame jFrame = newReactiveFrame();

    static void showInstance() {
    	if (instance == null)
    		instance = new DownloadsView();
    	instance.show();
    }
    

	private void show() {
		jFrame.setVisible(true);
	}


	DownloadsView() {
        jFrame.add(my(DownloadListPanels.class).produce(Music.activeDownloads()));
        jFrame.setMinimumSize(new Dimension(365, 80));
        jFrame.setResizable(false);
	}


	private JFrame newReactiveFrame() {
		return my(ReactiveWidgetFactory.class).newFrame(
			my(Signals.class).adapt(Music.activeDownloads().size(), new Functor<Integer, String>() { @Override public String evaluate(Integer numberOfDownloadsRunning) throws RuntimeException {
				return (numberOfDownloadsRunning > 0) ? "Active Downloads' Details:" : "Active Downloads <None>";
			}})
		).getMainWidget();
	}

}
