package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.foundation.lang.Consumer;

import dfcsantos.music.ui.view.MusicViewListener;


final class PlayingTrackPanel extends JPanel {
	private static final Format _timeFormater = new SimpleDateFormat("mm:ss");
	private final MusicViewListener _listener;
	private final JLabel _trackName = new JLabel();
	private final JLabel _trackTime = new JLabel();
	
	@SuppressWarnings("unused") private Object refToAvoidGc1, refToAvoidGc2; 
	
	
	PlayingTrackPanel(MusicViewListener listener) {
		_listener = listener;
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(_trackName);
		add(_trackTime);

		refToAvoidGc1 = _listener.playingTrackName().addReceiver(new Consumer<String>() {  @Override public void consume(String name) {
			setTrackName(name);
		}});
		
		refToAvoidGc2 = _listener.playingTrackTime().addReceiver(new Consumer<Integer>() {  @Override public void consume(Integer timeElapsed) {
			setTrackTime(timeElapsed);
		}});
	}

	private void setTrackName(String name) {
		_trackName.setText(my(Lang.class).strings().abbreviate(name, 36));
		_trackName.setToolTipText(name);
	}

	
	private void setTrackTime(Integer timeElapsed  ) {
		String time = _timeFormater.format(new Date(timeElapsed));
		_trackTime.setText(time);
	}

}
