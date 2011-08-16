package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;

class TrackDisplay extends JPanel {
	//private static final Format _timeFormater = new SimpleDateFormat("mm:ss");

	private final JLabel _trackLabel = my(ReactiveWidgetFactory.class).newLabel(playingTrackName()).getMainWidget();
	private final JLabel _trackTime	 = my(ReactiveWidgetFactory.class).newLabel(playingTrackTime()).getMainWidget();

	TrackDisplay() {
		super(new FlowLayout(FlowLayout.CENTER, 9, 5));
		add(_trackLabel);
	    add(_trackTime);
	}

	private Signal<String> playingTrackName() {
		return my(Signals.class).constant("Here Comes The Sun");
//		return my(Signals.class).adapt(_controller.playingTrack(), new Functor<Track, String>() { @Override public String evaluate(Track track) {
//			return (track == null) ? "<No track to play>" : my(Lang.class).strings().abbreviate(track.name(), 50);
//		}});
	}

	private Signal<String> playingTrackTime() {
		return my(Signals.class).constant("12'34\"");
//		return my(Signals.class).adapt(_controller.playingTrackTime(), new Functor<Integer, String>() { @Override public String evaluate(Integer timeElapsed) {
//			return _timeFormater.format(new Date(timeElapsed));
//		}});
	}

}
