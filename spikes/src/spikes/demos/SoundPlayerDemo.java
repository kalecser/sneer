package spikes.demos;

import static basis.environments.Environments.my;
import basis.brickness.Brickness;
import basis.environments.Environments;
import basis.lang.ClosureX;
import spikes.sneer.bricks.skin.audio.player.SoundPlayer;

public class SoundPlayerDemo {
	
	private final SoundPlayer _player = my(SoundPlayer.class);
	
	SoundPlayerDemo() {
		_player.play(SoundPlayerDemo.class.getResource("alert1.wav"));
		_player.play(SoundPlayerDemo.class.getResource("alert2.wav"));
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}
	
	public static void main(String[] args) throws Exception {
		Environments.runWith(Brickness.newBrickContainer(), new ClosureX<Exception>() { @Override public void run() throws Exception {
			new SoundPlayerDemo();
		}});
	}

}
