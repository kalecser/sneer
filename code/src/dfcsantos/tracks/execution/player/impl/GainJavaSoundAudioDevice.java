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
	private Field _sourceField = null;
	
	public GainJavaSoundAudioDevice(int volumePercent) {
		_volumePercent = volumePercent;
	}
	
	@Override
	protected void createSource() throws JavaLayerException {
		super.createSource();
		forceGain();
	}
	
	public void volumePercent(int level) {
		_volumePercent = Math.max(0, Math.min(100, level));
		forceGain();
	}

	private void forceGain() {
		try {
			SourceDataLine source = source();
			if (source == null) {
				return;
			}
		    FloatControl volControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
		    if(volControl == null) {
		    	return;
		    }
		    float gain = Math.min(Math.max(dBGain(), volControl.getMinimum()), volControl.getMaximum());
		    volControl.setValue(gain);
		} 
	    catch (Exception e) {
	    	e.printStackTrace();
			my(Logger.class).log(e.getMessage());
		}
	}

	private float dBGain() {
		return Math.max(- Float.MAX_VALUE, (float)(20 * Math.log10(_volumePercent / 100.0)));
	}
	
	private SourceDataLine source() throws Exception {
		if(_sourceField == null) {
			_sourceField = getClass().getSuperclass().getDeclaredField("source");
			_sourceField.setAccessible(true);
		}
		return (SourceDataLine) _sourceField.get(this);
	}
	
}
