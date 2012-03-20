//package spikes.adenauer.puncher;
//
//import static spikes.adenauer.puncher.SocketAddressUtils.CHARSET;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.SocketAddress;
//import java.net.SocketException;
//import java.net.SocketTimeoutException;
//import java.net.UnknownHostException;
//import java.util.HashSet;
//import java.util.Set;
//
//import javax.swing.JOptionPane;
//
//public class Client {
//
//	private static final String ownId = readConsole("Own id:");
//	private static final String OWN_IP = ownIp();
//	private static final int OWN_PORT = 5050;
//	private static final DatagramSocket _socket = newSocket(OWN_PORT);
//
//	private static final SocketAddress RENDEZVOUS_SERVER = new InetSocketAddress("dynamic.sneer.me", RendezvousServer.SERVER_PORT);
//
//	private static final Set<IpAddresses> peers = new HashSet<IpAddresses>();
//
//	
//	public static void main(String[] ignored) {
//		new Thread() {  @Override public void run() {
//			while (true) keepServerConnectionAlive();
//		}}.start();
//
//		new Thread() {  @Override public void run() {
//			while (true) askForTargetAddress();
//		}}.start();
//
//		new Thread() {  @Override public void run() {
//			while (true) receiveGreetingOrPeerAddress();
//		}}.start();
//
//		new Thread() {  @Override public void run() {
//			while (true) greetPeersAndSleepABit();
//		}}.start();
//	}
//
//
//	private static void keepServerConnectionAlive() {
//		send(RendezvousServer.KEEP_ALIVE, RENDEZVOUS_SERVER);
//		try { Thread.sleep(1000); } catch (InterruptedException e) { throw new IllegalStateException(e); }
//	}
//
//
//	private static void askForTargetAddress() {
//		String targetId = JOptionPane.showInputDialog("Enter Target");
//		String message = ownId + ";" + OWN_IP +  ":" + OWN_PORT +  ";" + targetId;
//		send(message, RENDEZVOUS_SERVER);
//	}
//	
//	
//	private static void receiveGreetingOrPeerAddress() {
//		try {
//			String received = receive();
//			System.err.println("Received on " + System.currentTimeMillis() + ": " + received);
//			capturePeerAddress(received);
//		} catch (SocketTimeoutException e) {
//			//OK.
//		} catch (IOException e) {
//			System.out.println(e.getClass().getName() + ": " + e.getMessage());
//		}
//	}
//
//
//	private static void capturePeerAddress(String received) {
//		try {
//			IpAddresses peer = SocketAddressUtils.unmarshalAddresses(received);
//			peers.add(peer);
//			System.out.println("Peer address added.");
//		} catch (UnableToParseAddress e) {
//			// Not a peer address.
//		}
//	}
//
//
//	private static void greetPeersAndSleepABit() {
//		for (IpAddresses ips : peers) {
//			send2("Public hello from " + ownId, ips.publicInternetAddress);
//			if (!ips.localNetworkAddress.getAddress().isLoopbackAddress())
//				send("Local hello from " + ownId, ips.localNetworkAddress);
//		}
//		
//		try { Thread.sleep(1000); } catch (InterruptedException e) { throw new IllegalStateException(e); }
//	}
//
//
//	synchronized
//	private static void send(String message, SocketAddress destination) {
//		byte[] bytes = message.getBytes(CHARSET);
//		try {
//			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, destination);
//			System.out.println(destination + ":" + packet.getPort());
//			_socket.send(packet);
//		} catch (IOException e) {
//			throw new IllegalStateException("Not implemented yet");
//		}
//	}
//	synchronized
//	private static void send2(String message, SocketAddress destination) {
//		byte[] bytes = message.getBytes(CHARSET);
//		try {
//			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, destination);
////			packet.setPort((packet.getPort() + portScan++) % 65536);
////			System.out.println(destination + ":" + packet.getPort());
//			_socket.send(packet);
//		} catch (IOException e) {
//			throw new IllegalStateException("Not implemented yet");
//		}
//	}
//
//
//	private static String ownIp() {
//		try {
//			return InetAddress.getLocalHost().getHostAddress();
//		} catch (UnknownHostException e) {
//			throw new IllegalStateException("Not implemented yet");
//		}
//	}
//
//
//	private static DatagramSocket newSocket(int port) {
//		try {
//			DatagramSocket result = new DatagramSocket(new InetSocketAddress(port));
//			result.setSoTimeout(3000);
//			return result;
//		} catch (SocketException e) {
//			throw new IllegalStateException(e); // Fix Handle this exception.
//		}
//	}
//
//
//	private static String receive() throws IOException {
//		DatagramPacket result = new DatagramPacket(new byte[1024], 1024);
//		_socket.receive(result);
//		System.out.println("Received packet from: " + result.getAddress() + " port: " + result.getPort());
//		return new String(result.getData(), result.getOffset(), result.getLength(), CHARSET);
//	}
//
//
//	private static String readConsole(String prompt) {
//		System.out.print(prompt);
//		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//		try {
//			return reader.readLine();
//		} catch (IOException e) {
//			throw new IllegalStateException("Not implemented yet");
//		}
//	}
//
//}
