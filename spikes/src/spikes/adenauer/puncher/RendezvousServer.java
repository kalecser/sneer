package spikes.adenauer.puncher;

import static spikes.adenauer.puncher.SocketAddressUtils.CHARSET;
import static spikes.adenauer.puncher.SocketAddressUtils.marshal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class RendezvousServer {
	
	static final int SERVER_PORT = 7070;

	private static final Map<String, IpAddresses> addressesByClientId = new HashMap<String, IpAddresses>();

	static final String KEEP_ALIVE = "keep alive";

	static private DatagramSocket socket;
	

	public static void main(String[] ignored ) {
		try {
			initSockets();
			listen();
		} catch (Exception e) {
			println("Failure: " + e.getMessage());
		}
	}


	private static void initSockets() throws SocketException {
		socket = new DatagramSocket(SERVER_PORT);
	}
	
	
	private static void listen() {
		while (true) {
			try {
				handle(receivePacket());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	private static void handle(DatagramPacket request) throws IOException, UnableToParseAddress {
		StringTokenizer fields = toFields(request);
		
		String firstToken = fields.nextToken();
		if (handleKeepAlive(firstToken)) return;
		
		String callerId = firstToken;
		String localAddress = fields.nextToken();
		String requestedId = fields.nextToken();

		keepCallerAddresses(callerId, request, SocketAddressUtils.unmarshal(localAddress));
		rendezvous(callerId, requestedId);
	}


	private static boolean handleKeepAlive(String firstToken) {
		if (firstToken.equals(KEEP_ALIVE)) {
			System.out.print(".");
			return true;
		}
		System.out.println();
		return false;
	}

	
	private static void rendezvous(String callerId, String requestedId) throws IOException {
		IpAddresses caller = addressesByClientId.get(callerId);
		IpAddresses requested = addressesByClientId.get(requestedId);
		
		if (requested == null) {
			println("Requested client '" + requestedId + "' not found.");
			return;
		}

		println("Forwarding info to: " + requestedId);
		send(marshal(caller), requested.publicInternetAddress);
		println("Forward info to: " + callerId);
		send(marshal(requested), caller.publicInternetAddress);
		println("=====================================");
	}


	private static void send(byte[] data, InetSocketAddress dest) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length, dest);
		socket.send(packet);
		println("Sent info: " + new String(packet.getData(), packet.getOffset(), packet.getLength(), CHARSET) + " to address: "+ dest.toString());
	}

	
	private static void keepCallerAddresses(String caller, DatagramPacket receivedPacket, InetSocketAddress localAddress) {
		InetSocketAddress publicAddress = (InetSocketAddress)receivedPacket.getSocketAddress();		
		
		println("Caller: " + caller + " - Local address: " + localAddress + ", Public address: " + publicAddress);
		addressesByClientId.put(caller, new IpAddresses(publicAddress, localAddress));
	}


	private static StringTokenizer toFields(DatagramPacket receivedPacket) {
		String result = new String(receivedPacket.getData(), receivedPacket.getOffset(), receivedPacket.getLength(), CHARSET);
		return new StringTokenizer(result.trim(), ";");
	}


	private static DatagramPacket receivePacket() throws IOException {
		byte[] result = new byte[1024];
		DatagramPacket receivedPacket = new DatagramPacket(result, result.length);
		socket.receive(receivedPacket);
		return receivedPacket;
	}


	private static void println(String out) {
		System.out.println(out);
	}

}