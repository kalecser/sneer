package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;
import java.awt.Font;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.Track;
import dfcsantos.wusic.Wusic;

class TrackDisplay extends JPanel {

	private static final Format TIME_FORMATTER = new SimpleDateFormat("mm:ss");

	private static final Wusic _controller = my(Wusic.class);

	private final JLabel _trackLabel = my(ReactiveWidgetFactory.class).newLabel(playingTrackName()).getMainWidget();
	private final JLabel _trackTime	 = my(ReactiveWidgetFactory.class).newLabel(playingTrackTime()).getMainWidget();

	TrackDisplay() {
		super(new FlowLayout(FlowLayout.LEFT, 9, 3));

		_trackLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(_trackLabel);

		_trackTime.setFont(new Font("Tahoma", Font.PLAIN, 14));
	    add(_trackTime);

	}

	private Signal<String> playingTrackName() {
		return my(Signals.class).adapt(_controller.playingTrack(), new Functor<Track, String>() { @Override public String evaluate(Track track) {
			return (track == null) ? "<No track to play>" : (track.name().length() >= 54) ? track.name().substring(0, 51).concat("...") : track.name();
		}});
	}

	private Signal<String> playingTrackTime() {
		return my(Signals.class).adapt(_controller.playingTrackTime(), new Functor<Integer, String>() { @Override public String evaluate(Integer timeElapsed) {
			return TIME_FORMATTER.format(new Date(timeElapsed));
		}});
	}

}
