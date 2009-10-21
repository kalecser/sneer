package dfcsantos.wusic.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import dfcsantos.wusic.Wusic;

class TrackDisplay extends JPanel {

	private static final Wusic Wusic = my(Wusic.class);

	private final JLabel _trackLabel = my(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackName()).getMainWidget();
	private final JLabel _trackTime  = my(ReactiveWidgetFactory.class).newLabel(Wusic.playingTrackTime()).getMainWidget();

	TrackDisplay() {
		super(new FlowLayout(FlowLayout.LEFT, 9, 3));

		_trackTime.setFont(new Font("Tahoma", Font.PLAIN, 14));
	    add(_trackTime);

	     _trackLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
	    add(_trackLabel);;
	}

}
