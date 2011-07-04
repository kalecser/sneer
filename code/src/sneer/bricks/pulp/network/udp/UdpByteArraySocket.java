//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Adenauer Gabriel.

package sneer.bricks.pulp.network.udp;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.network.ByteArraySocket;

class UdpByteArraySocket implements ByteArraySocket {

	private static final int MAX_ARRAY_SIZE = 1024 * 20;
	private final DatagramSocket _socket;
	private DatagramPacket _packet = null;
	private byte[] _dataReceivedFromServer = null;

	
	public UdpByteArraySocket(String serverIpAddress, int serverPort) throws IOException {
		InetAddress address = InetAddress.getByName(serverIpAddress);
		_socket = newSocket(new InetSocketAddress(address, serverPort));
		_packet = newPacket(); 
	}


	public UdpByteArraySocket(DatagramPacket packet) throws IOException {
		_socket = newSocket(packet.getSocketAddress());
		synchronizePorts(packet.getSocketAddress());
		_packet = packet;
		_dataReceivedFromServer = packet.getData();
	}


	@Override
	public void write(byte[] array) throws IOException {
		int length = array.length;
		if (length > MAX_ARRAY_SIZE) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Packet greater than " + MAX_ARRAY_SIZE + " bytes cannot be sent.", "Size: " + length);
			return;
		}
		_packet.setData(array);
		System.out.println("Write to : " + _socket.getRemoteSocketAddress().toString() + " -> " + new String(array));
		_socket.send(_packet);
		
		if (!_socket.isConnected()) {
			display("Waiting packet to synchronize Socket Ports...");
			_socket.receive(_packet);
			_socket.connect(_packet.getAddress(), _packet.getPort());
			display("Socket conected to: " + _packet.getSocketAddress().toString());
		}
	}

	
	@Override
	public byte[] read() throws IOException {
		if (hasDataFromServer()) return dataFromServer();
		_socket.receive(_packet);
		System.out.println("Read from: " + _packet.getSocketAddress().toString()+ " -> " + new String(_packet.getData()));
		return _packet.getData();
	}

	
	@Override
	public void close() {
		_socket.close();
	}

	
	String remoteIP() {
		return _socket.getInetAddress().getHostAddress();
	}
	
	
	private DatagramSocket newSocket(SocketAddress socketAddress) throws IOException {
		DatagramSocket socket = new DatagramSocket(socketAddress);
		return socket;
	}

	private void  synchronizePorts(SocketAddress address) throws IOException {
		DatagramPacket packet = newPacket(); //Packet to synchronize port whit new socket. 
		packet.setSocketAddress(address); 
		display("Sending packet to synchronize Socket Ports: " + address.toString());
		_socket.send(packet);
	}
	
	
	
	private DatagramPacket newPacket() {
		byte[] array = new  byte[MAX_ARRAY_SIZE];
		return new DatagramPacket(array, array.length);
	}

	
	private boolean hasDataFromServer() {
		return _dataReceivedFromServer == null ? false : true;
	}
	
	
	private byte[] dataFromServer() {
		byte[] result = _dataReceivedFromServer;
		_dataReceivedFromServer = null;
		return result;
	}
	

	private void display(String msg) {
		System.out.println(msg);
	}
	
}