package spikes.adenauer.puncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class Peer {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private static final SocketAddress RENDEZVOUS_SERVER = new InetSocketAddress("wuestefeld.name", 7070);

	private static final int OWN_PORT = 5050;
	private static final String OWN_IP = ownIp();
	private static final DatagramSocket _socket = newSocket(OWN_IP, OWN_PORT);

	
	public static void main(String[] ignored) throws IOException {
		String ownId = readConsole("Own id:");
		String targetId = readConsole("Target id:");

		String message = ownId + ";" + ownIp() +  ";" + OWN_PORT +  ";" + targetId;
		
		InetSocketAddress targetAddress = null;
		do {
			send(message, RENDEZVOUS_SERVER);

			String received;
			try {
				received = receive();
			} catch (SocketTimeoutException e) {
				continue;
			}
			targetAddress = parseTargetAddress(received);

		} while (targetAddress == null);
		
		
		while (true) {
			send("Hello from " + ownId, targetAddress);
			try {
				System.out.println(receive());
			} catch (SocketTimeoutException e) {}
			sleep(1000);
		}
		
	}


	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}


	private static InetSocketAddress parseTargetAddress(String message) {
		System.out.println("Trying to parse: " + message);
		String[] parts = message.split(";");
		if (parts.length != 4)
			return null;
		String ip = parts[2];
		int port = Integer.parseInt(parts[3]);
		return new InetSocketAddress(ip, port);
	}


	private static void send(String message, SocketAddress destination)	throws IOException {
		byte[] bytes = message.getBytes(UTF_8);
		_socket.send(new DatagramPacket(bytes, bytes.length, destination));
	}


	private static String ownIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e);
		}
	}


	private static DatagramSocket newSocket(String localIp, int port) {
		try {
			return new DatagramSocket(new InetSocketAddress(localIp, port));
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
