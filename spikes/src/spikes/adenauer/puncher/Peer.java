package spikes.adenauer.puncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class Peer {

	private static final SocketAddress RENDEZVOUS_SERVER = new InetSocketAddress("wuestefeld.name", 7070);

	private static final int OWN_PORT = 5050;
	private static final DatagramSocket _socket = newSocket(OWN_PORT);

	
	public static void main(String[] ignored) throws IOException {
		String ownId = readConsole("Own id:");
		String targetId = readConsole("Target id:");

		String message = ownId + ";" + ownIp() +  ";" + OWN_PORT +  ";" + targetId;
		send(message, RENDEZVOUS_SERVER);
//		_socket.receive(p);
	}


	private static void send(String message, SocketAddress destination)	throws IOException {
		byte[] bytes = message.getBytes("UTF-8");
		_socket.send(new DatagramPacket(bytes, bytes.length, destination));
	}


	private static String ownIp() {
		return _socket.getLocalAddress().getHostAddress();
	}


	private static DatagramSocket newSocket(int port) {
		try {
			return new DatagramSocket(port);
		} catch (SocketException e) {
			throw new IllegalStateException(e); // Fix Handle this exception.
		}
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


//	private static String targetInfo(DatagramSocket socket) throws IOException {
//		DatagramPacket targetPacket = new DatagramPacket(new byte[1024], 1024);
//		socket.receive(targetPacket);
//		//socket.close();
//		return new String(targetPacket.getData());
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
