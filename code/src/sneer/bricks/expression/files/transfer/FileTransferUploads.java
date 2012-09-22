package sneer.bricks.expression.files.transfer;

import static basis.environments.Environments.my;

import java.util.HashMap;
import java.util.Map;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import basis.lang.Pair;


public class FileTransferUploads {

	private Map<Upload, Light> uploadLights = new HashMap<>();
	BlinkingLights blinkingLights;
	
	{
		blinkingLights = my(BlinkingLights.class);
	}

	public class Upload extends Pair<Seal, String> {
		public Upload(FileTransferStatus status) {
			super(status.publisher, status.path);
		}
	}

	public void handleStatus(FileTransferStatus status) {
		Upload upload = new Upload(status);
		Light light = uploadLights.get(upload);
		if (light == null) {
			light = blinkingLights.prepare(LightType.INFO);
			uploadLights.put(upload, light);
		}
		blinkingLights.turnOffIfNecessary(light);
		blinkingLights.turnOnIfNecessary(light, status.toString(), "Upload status", 15000);
	}
}
