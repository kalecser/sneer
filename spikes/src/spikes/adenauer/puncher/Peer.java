package spikes.adenauer.puncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

public class Peer {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private static final SocketAddress RENDEZVOUS_SERVER = new InetSocketAddress("dynamic.sneer.me", 7070);
	
	private static final int OWN_PORT = 5050;
	private static final DatagramSocket _socket = newSocket(OWN_PORT);
	private static final String OWN_IP = ownIp();

	
	public static void main(String[] ignored) throws IOException {
		final String ownId = readConsole("Own id:");
		
		new Thread() {  @Override public void run() {
			while (true) {
				String targetId = JOptionPane.showInputDialog("Enter Target");
				String message = ownId + ";" + OWN_IP +  ";" + OWN_PORT +  ";" + targetId;
				try {
					send(message, RENDEZVOUS_SERVER);
				} catch (IOException e) {
					throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
				}
			}
		}}.start();
		
		Set<InetSocketAddress> targets = new HashSet<InetSocketAddress>();
		long lastTime = System.currentTimeMillis();
		while (true) {
			String received;
			try {
				received = receive();
				System.out.println("Received: " + received);
				InetSocketAddress target = parseTargetAddress(received);
				if (target != null) targets.add(target);
			} catch (SocketTimeoutException e) {}
			
			long now = System.currentTimeMillis();
			if (now - lastTime > 1000) {
				lastTime = now;
				for (InetSocketAddress t : targets)
					send("Hello from " + ownId, t);
			}
		}
		
	}


	private static InetSocketAddress parseTargetAddress(String message) {
		String[] parts = message.split(";");
		if (parts.length != 2)
			return null;
		String ip = parts[0];
		int port = Integer.parseInt(parts[1]);
		return new InetSocketAddress(ip, port);
	}

	
	synchronized
	private static void send(String message, SocketAddress destination)	throws IOException {
		byte[] bytes = message.getBytes(UTF_8);
		_socket.send(new DatagramPacket(bytes, bytes.length, destination));
	}


	private static String ownIp() {
//		try {
//			return InetAddress.getLocalHost().getHostAddress();
//		} catch (UnknownHostException e) {
//			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e);
//		}
		return _socket.getLocalAddress().getHostAddress();
	}


	private static DatagramSocket newSocket(int port) {
		try {
			return new DatagramSocket(new InetSocketAddress(port));
		} catch (SocketException e) {
			throw new IllegalStateException(e); // Fix Handle this exception.
		}
	}


	private static String receive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
		_socket.setSoTimeout(3000);
		_socket.receive(packet);
		return new String(packet.getData(), packet.getOffset(), packet.getLength(), UTF_8);
	}
	
	
	private static String readConsole(String prompt) {
		System.out.print(prompt);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			return reader.readLine();
		} catch (IOException e1) {
			return " ";
		}
	}
	
	

	
	
	
	
	
	
//	private static DatagramPacket punchPacket(String targetInfo) throws IOException {
//		StringTokenizer fields = new StringTokenizer(targetInfo.trim(), ";");
//		String punchData = localHostName(); 
//		fields.nextToken();
//		fields.nextToken();
//		String targetIP = fields.nextToken();
//		int targetPort = Integer.valueOf(fields.nextToken());
//		return new DatagramPacket(punchData.getBytes(), punchData.length(), new InetSocketAddress(targetIP, targetPort));
//	}


//	private static String localHostName() throws IOException {
//		String result = InetAddress.getLocalHost().getHostName();
//		System.out.println(result);
//		return result;
//	}
	
	
//	private static void display(String out) {
//		System.out.println(out);
//	}


}
