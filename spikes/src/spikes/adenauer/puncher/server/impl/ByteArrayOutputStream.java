package spikes.adenauer.puncher.server.impl;

import java.io.OutputStream;


/** Efficient implementation that does not create a new array but requires the given target array to be large enough. */
public class ByteArrayOutputStream extends OutputStream {

	private int pos = 0;
	private final byte[] _target;

	public ByteArrayOutputStream(byte[] target) {
		_target = target;
	}

	
	@Override
	public void write(int b) {
		_target[pos++] = (byte)b;
	}


	@Override
	public void write(byte[] b, int off, int len) {
		System.arraycopy(b, off, _target, pos, len);
		pos += len;
	}


	public int bytesWritten() {
		return pos;
	}

}
