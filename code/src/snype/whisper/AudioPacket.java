package snype.whisper;

import java.io.Serializable;

public class AudioPacket implements Serializable {

	private static final long serialVersionUID = 1L;

	public AudioPacket(byte[][] content) {
		_content = content;
	}

	public final byte[][] _content;

}
