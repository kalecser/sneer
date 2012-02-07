package spikes.adenauer.puncher;

import static spikes.adenauer.puncher.SocketAddressUtils.CHARSET;
import static spikes.adenauer.puncher.SocketAddressUtils.isSameHost;
import static spikes.adenauer.puncher.SocketAddressUtils.marshal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Rendezvous {
	
	static final int SERVER_PORT = 7070;

	private static final Map<String, IpAddresses> addressesByClientId = new HashMap<String, IpAddresses>();

	private static DatagramSocket socket;

	

	public static void main(String[] ignored ) {
		try {
			socket = new DatagramSocket(SERVER_PORT);
			listen();
		} catch (Exception e) {
			display("Failure: " + e.getMessage());
		}
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

	
	private static void handle(DatagramPacket request) throws IOException, InvalidAddress {
		StringTokenizer fields = toFields(request);
		String callerId = fields.nextToken();
		String localAddress = fields.nextToken();
		String requestedId = fields.nextToken();

		keepCallerAddresses(callerId, request, SocketAddressUtils.unmarshal(localAddress));
		rendezvous(callerId, requestedId);
	}

	
	private static void rendezvous(String callerId, String requestedId) throws IOException {
		IpAddresses caller = addressesByClientId.get(callerId);
		IpAddresses requested = addressesByClientId.get(requestedId);
		
		if (requested == null) {
			display("Requested client '" + requestedId + "' not found.");
			return;
		}

		InetSocketAddress callerPublicAddress = caller.publicInternetAddress;
		InetSocketAddress callerLocalAddress = caller.localNetworkAddress;
		InetSocketAddress requestedPublicAddress = requested.publicInternetAddress;
		InetSocketAddress requestedLocalAddress = requested.localNetworkAddress;
		
		InetSocketAddress callerAddress = callerPublicAddress;
		InetSocketAddress requestedAddress = requestedPublicAddress;

		//TODO Send public ip as well as local ip when both behind same host so they can attempt hairpin connection in case they cannot connect locally.
		
		if (isSameLocalNetwork(callerPublicAddress, requestedPublicAddress)) {
			callerAddress = callerLocalAddress;
			requestedAddress = requestedLocalAddress;
		}
		
		display("Forwarding info to: " + requestedId);
		send(marshal(callerAddress), requestedPublicAddress);
		display("Forward info to: " + callerId);
		send(marshal(requestedAddress), callerPublicAddress);
		display("=====================================");
	}


	private static boolean isSameLocalNetwork(InetSocketAddress a1, InetSocketAddress a2) {
		return isSameHost(a1, a2);
	}

	
	private static void send(byte[] data, InetSocketAddress dest) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length, dest);
		socket.send(packet);
		display("Sent info: " + new String(packet.getData(), packet.getOffset(), packet.getLength(), CHARSET) + " to address: "+ dest.toString());
	}

	
	private static void keepCallerAddresses(String caller, DatagramPacket receivedPacket, InetSocketAddress localAddress) {
//		InetSocketAddress publicAddress = new InetSocketAddress(receivedPacket.getAddress(), receivedPacket.getPort());		
		InetSocketAddress publicAddress = (InetSocketAddress)receivedPacket.getSocketAddress();		
		
		display("Caller: " + caller + " - Local address: " + localAddress + ", Public address: " + publicAddress);
		addressesByClientId.put(caller, new IpAddresses(localAddress, publicAddress));
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


	private static void display(String out) {
		System.out.println(out);
	}

}