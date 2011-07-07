package spikes.adenauer.puncher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Rendezvous implements Runnable {
	private static final Charset UTF_8 = Charset.forName("UTF-8");


	private static final String PRIVATE_ADDRESS = "privateAddress";
	private static final String PUBLIC_ADDRESS = "publicAddress";
	private static final Map<String, Map<String, InetSocketAddress>> _endPoints = new HashMap<String, Map<String, InetSocketAddress>>();


	public static void main(String[] ignored ) {
		new Thread(new Rendezvous()).start();
	}
	
	
	@Override
	public void run() {
		try {
			listener(new DatagramSocket(7070));
		} catch (Exception e) {
			display("Failure on listener: " + e.getMessage());
		}
	}
	

	private void listener(DatagramSocket socket) {
		while (true){
			process(socket);
		}
	}

	
	private void process(DatagramSocket socket) {
		try {
			manager(receivedPacketsFrom(socket), socket);
		} catch (Exception e) {
			display("Process failure: " + e.getMessage());
		}
	}


	private void manager(DatagramPacket receivedPacket, DatagramSocket socket) throws IOException {
		if (receivedPacket == null) return;
		StringTokenizer fields = toFields(receivedPacket);
		String from = decodeFromInfo(fields, receivedPacket);
		String target = decodeTargetName(fields);
		forwardInfo(from, target, socket);
	}

	
	private byte[] encode(Map<String, InetSocketAddress> info) {
		InetSocketAddress privateAddress = info.get(PRIVATE_ADDRESS);
		InetSocketAddress publicAddress = info.get(PUBLIC_ADDRESS);
		
		StringBuilder result = new StringBuilder();
		result.append(privateAddress.getAddress().getHostAddress());
		result.append(";");
		result.append(privateAddress.getPort());
		result.append(";");
		result.append(publicAddress.getAddress().getHostAddress());
		result.append(";");
		result.append(publicAddress.getPort());
		result.append(";");
		return result.toString().getBytes();
	}


	private void forwardInfo(String from, String target, DatagramSocket socket) throws IOException {
		Map<String, InetSocketAddress> fromEndPoint = _endPoints.get(from);
		Map<String, InetSocketAddress> targetEndPoint = _endPoints.get(target);
		
		if (targetEndPoint == null || targetEndPoint.isEmpty()) {
			display("Info of target: " + target + " not found.");
			return;
		}
	
		display("Forward info to: " + target);
		forward(encode(fromEndPoint), socket, targetEndPoint.get(PUBLIC_ADDRESS));
		display("Forward info to: " + from);
		forward(encode(targetEndPoint), socket, fromEndPoint.get(PUBLIC_ADDRESS));
		display("=====================================");
	}

	
	private void forward(byte[] data, DatagramSocket socket, SocketAddress destination) throws IOException {
		DatagramPacket targetPacket = new DatagramPacket(data, data.length, destination);
		socket.send(targetPacket);
		display("Sent info: " + new String(targetPacket.getData(), targetPacket.getOffset(), targetPacket.getLength(), UTF_8) + " to address: "+ destination.toString());
	}

	
	private String decodeFromInfo(StringTokenizer fields, DatagramPacket receivedPacket) {
		String from = fields.nextToken();
		InetSocketAddress privateAddress = new InetSocketAddress(fields.nextToken(), Integer.valueOf(fields.nextToken()));		
		InetSocketAddress publicAddress = new InetSocketAddress(receivedPacket.getAddress(), receivedPacket.getPort());		
		
		Map<String, InetSocketAddress> endPoint = new HashMap<String, InetSocketAddress>();
		endPoint.put(PRIVATE_ADDRESS, privateAddress);
		endPoint.put(PUBLIC_ADDRESS, publicAddress);
		
		refreshEndPoint(from, endPoint);
		
		display("Decoded packet id: " + from + " - Private address: " + privateAddress.getAddress().getHostAddress() + ", Private port: " + privateAddress.getPort() +   ", Public address: " + publicAddress.getAddress().getHostAddress() + " and Public port: "+  publicAddress.getPort());
		return from;
	}


	private void refreshEndPoint(String from, Map<String, InetSocketAddress> endPoint) {
		if (from == null) return;
		if (endPoint == null || endPoint.isEmpty()) return;
		if (_endPoints.containsKey(from)) _endPoints.remove(from);
		_endPoints.put(from, endPoint);
	}


	private String decodeTargetName(StringTokenizer fields) {
		return fields.nextToken();
	}

	
	private StringTokenizer toFields(DatagramPacket receivedPacket) {
		String result = new String(receivedPacket.getData(), receivedPacket.getOffset(), receivedPacket.getLength(), UTF_8);
		StringTokenizer fields = new StringTokenizer(result.trim(), ";");
		return fields;
	}


	private DatagramPacket receivedPacketsFrom(DatagramSocket socket) throws IOException {
		byte[] receivedData = new byte[1024];
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		socket.receive(receivedPacket);
		return receivedPacket;
	}

	
	private void display(String out) {
		System.out.println(out);
	}

}