package dfcsantos.tracks.execution.player.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Field;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

import sneer.bricks.hardware.io.log.Logger;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;

public class GainJavaSoundAudioDevice extends JavaSoundAudioDevice {

	private int _volumePercent = 0;
	
	public GainJavaSoundAudioDevice(int volumePercent) {
		_volumePercent = volumePercent;
	}
	
	@Override
	protected void createSource() throws JavaLayerException {
		super.createSource();
		volumePercent(_volumePercent);
	}
	
	public void volumePercent(int level) {
		_volumePercent = level;
		float gain = Math.max(- Float.MAX_VALUE, (float)(20 * Math.log10(_volumePercent / 100.0)));
	    try {
			SourceDataLine source = source();
			if (source == null) {
				return;
			}
		    FloatControl volControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
		    if(volControl == null) {
		    	return;
		    }
		    float newGain = Math.min(Math.max(gain, volControl.getMinimum()), volControl.getMaximum());
		    volControl.setValue(newGain);
		} 
	    catch (Exception e) {
	    	e.printStackTrace();
			my(Logger.class).log(e.getMessage());
		}
	}
	
	private SourceDataLine source() throws Exception {
		Field sourceField = getClass().getSuperclass().getDeclaredField("source");
		sourceField.setAccessible(true);
		return (SourceDataLine) sourceField.get(this);
	}
	
}
