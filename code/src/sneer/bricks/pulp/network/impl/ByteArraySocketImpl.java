//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Alexandre Nodari.

package sneer.bricks.pulp.network.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.network.ByteArraySocket;

class ByteArraySocketImpl implements ByteArraySocket {

	private final Socket _socket;
	private final DataOutputStream _outputStream;
	private final DataInputStream _inputStream;
	
	public ByteArraySocketImpl(String serverIpAddress, int serverPort) throws IOException {
		this(new Socket(serverIpAddress, serverPort));
	}

	public ByteArraySocketImpl(Socket socket) throws IOException {
		_socket = socket;
		_outputStream = new DataOutputStream(_socket.getOutputStream());
		_inputStream = new DataInputStream(_socket.getInputStream());
		
		_socket.setTcpNoDelay(true);
	}

	@Override
	public void write(byte[] array) throws IOException {
		Light hugeTupleLight = null;
		
		int length = array.length;
		if (length > 65534) {
			hugeTupleLight = my(BlinkingLights.class).turnOn(LightType.WARN, "Huge tuple is being sent.", "Huge tuple is being sent. Size: " + length);
			_outputStream.writeChar(65535);
			_outputStream.writeInt(length);
		} else
			_outputStream.writeChar((char)length); //Writes a char as a 2-byte value, high byte first.

		_outputStream.write(array);
		_outputStream.flush();
		
		if (hugeTupleLight != null)
			my(BlinkingLights.class).turnOffIfNecessary(hugeTupleLight);
	}

	@Override
	public byte[] read() throws IOException {
		int length = _inputStream.readChar();
		
		if (length == 65535) length = _inputStream.readInt();
		
		byte[] result = new byte[length];
		_inputStream.readFully(result);
		return result;
	}

	@Override
	public void close() {
		my(IO.class).crash(_inputStream);
		my(IO.class).crash(_outputStream);
		try {
			_socket.close();
		} catch (IOException e) {
			// Yes. The correct thing is to do nothing.
		}
	}
}
