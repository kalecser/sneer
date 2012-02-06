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

	private static final Map<String, Map<String, InetSocketAddress>> _endPoints = new HashMap<String, Map<String, InetSocketAddress>>();

	private static final String PRIVATE_ADDRESS = "privateAddress";
	private static final String PUBLIC_ADDRESS = "publicAddress";
	

	public static void main(String[] ignored ) {
		new Thread(new Rendezvous()).start();
	}
	
	
	@Override
	public void run() {
		try {
			listenTo(new DatagramSocket(7070));
		} catch (Exception e) {
			display("Failure on listener: " + e.getMessage());
		}
	}
	

	private void listenTo(DatagramSocket socket) {
		while (true) {
			try {
				manager(receivedPacketFrom(socket), socket);
			} catch (Exception e) {
				display("Process failure: " + e.getMessage());
			}
		}
	}

	
	private void manager(DatagramPacket receivedPacket, DatagramSocket socket) throws IOException {
		if (receivedPacket == null) return;
		StringTokenizer fields = toFields(receivedPacket);
		String from = decodeFromInfo(fields, receivedPacket);
		String target = decodeTargetName(fields);
		forwardInfo(from, target, socket);
	}

	
	private byte[] encode(InetSocketAddress address) {
		StringBuilder result = new StringBuilder();
		result.append(address.getAddress().getHostAddress());
		result.append(";");
		result.append(address.getPort());
		return result.toString().getBytes();
	}


	private void forwardInfo(String from, String target, DatagramSocket socket) throws IOException {
		Map<String, InetSocketAddress> fromEndPoint = _endPoints.get(from);
		Map<String, InetSocketAddress> targetEndPoint = _endPoints.get(target);
		
		if (targetEndPoint == null || targetEndPoint.isEmpty()) {
			display("Info of target: " + target + " not found.");
			return;
		}

		InetSocketAddress fromPublicAddress = fromEndPoint.get(PUBLIC_ADDRESS);
		InetSocketAddress fromPrivateAddress = fromEndPoint.get(PRIVATE_ADDRESS);
		InetSocketAddress targetPublicAddress = targetEndPoint.get(PUBLIC_ADDRESS);
		InetSocketAddress targetPrivateAddress = targetEndPoint.get(PRIVATE_ADDRESS);
		
		InetSocketAddress fromInfo = fromPublicAddress;
		InetSocketAddress targetInfo = targetPublicAddress;
		
		
		if (sameWAN(fromPublicAddress, targetPublicAddress) && sameLAN(fromPrivateAddress, targetPrivateAddress)) {
			fromInfo = fromPrivateAddress;
			targetInfo = targetPrivateAddress;
		}
		
		display("Forward info to: " + target);
		forward(encode(fromInfo), socket, targetPublicAddress);
		display("Forward info to: " + from);
		forward(encode(targetInfo), socket, fromPublicAddress);
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
		return new StringTokenizer(result.trim(), ";");
	}


	private DatagramPacket receivedPacketFrom(DatagramSocket socket) throws IOException {
		byte[] result = new byte[1024];
		DatagramPacket receivedPacket = new DatagramPacket(result, result.length);
		socket.receive(receivedPacket);
		return receivedPacket;
	}

	private boolean sameLAN(InetSocketAddress fromPrivateAddress, InetSocketAddress targetPrivateAddress) {
		if (fromPrivateAddress == null) return false;
		if (targetPrivateAddress == null) return false;
		String[] address = fromPrivateAddress.getAddress().getHostAddress().split("\\D"); 
		String[] otherAddress = targetPrivateAddress.getAddress().getHostAddress().split("\\D"); 
		
		return (address[0].equals(otherAddress[0]) && 
				address[1].equals(otherAddress[1]) && 
				address[2].equals(otherAddress[2])) ? true : false;
	}

	private boolean sameWAN(InetSocketAddress fromPublicAddress, InetSocketAddress targetPublicAddress ) {
		if (fromPublicAddress == null) return false;
		if (targetPublicAddress == null) return false;
		return fromPublicAddress.getAddress().equals(targetPublicAddress.getAddress());
	}

	
	private void display(String out) {
		System.out.println(out);
	}

}