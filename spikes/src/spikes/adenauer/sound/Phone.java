package spikes.adenauer.sound;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

public class Phone {
	

	public static void main(String[] ignored)  {
			showAvalableMixers();
			showFileTypes();
	}
	
		private static void showAvalableMixers(){
			Mixer.Info[] mixes = AudioSystem.getMixerInfo();
			for (Mixer.Info info : mixes)  
				showInfo(info);
		}
	
		private static void showInfo(Mixer.Info info) {
			showMixerInfo(info);
			try {
				showClipUsing(info);
			} catch (Exception e) {
				print("Impossible to show mixer. (" +e.getMessage()+ ")");
			}
		}
	
		private static void showClipUsing(Mixer.Info info) throws LineUnavailableException {
			Clip clip = AudioSystem.getClip(info);
			showClipInfo(clip);
		}
	
		private static void showClipInfo(Clip clip) {
			Line.Info info = clip.getLineInfo();
			print("Line info: " + info.toString());
			
			Control[] controls = clip.getControls();
			for (Control control : controls)
				print("Control type: " + control.getType().toString());
			
			blankLinePrint();
		}
	
		private static void showMixerInfo(Mixer.Info info) {
			print("Mixer: " + info.getName());
			print("Vendor: " + info.getVendor());
			print("Version:" + info.getVersion());
			print("Description: " + info.getDescription());
			blankLinePrint();
		}

	
	private static void showFileTypes() {
		Type[] types = AudioSystem.getAudioFileTypes();
		for (Type type : types)
			showFileSupported(type);
	}
	
		private static void showFileSupported(AudioFileFormat.Type type) {
			print("File type supported: " + type.getExtension());
		}
	
	
	private static void print(String out) {
		System.out.println(out);
	}
	
	
	private static void blankLinePrint() {
		System.out.println();
	}
}
