package spikes.adenauer.puncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;

public class Peer implements Runnable {
	//private static final String RENDEZVOUS_IP = "192.168.0.2";  
	private static final String RENDEZVOUS_IP = "wuestefeld.name";  
	private static final int RENDEZVOUS_PORT = 9876;
	private static final int LISTENER_PORT = 5432;
	private static DatagramSocket _socket;
	
	
	public static void main(String[] ignored) throws IOException {
		_socket = openSocket(LISTENER_PORT);
		startListenOtherPeers();
		try {
			holepunching();
		} catch (Exception e) {
			display(e.getMessage());
		}
	}

	
	private static void holepunching() throws IOException {
		punch(requestTargetInfo());
	}

	
	private static void punch(String targetInfo) throws IOException {
		DatagramSocket socket = openSocket(4321);
		DatagramPacket punchPacket = punchPacket(targetInfo);
		socket.send(punchPacket);
		socket.close();
	}

	
	private static DatagramPacket punchPacket(String targetInfo) throws IOException {
		StringTokenizer fields = new StringTokenizer(targetInfo.trim(), ";");
		String punchData = getId(); 
		fields.nextToken();
		fields.nextToken();
		String targetIP = fields.nextToken();
		int targetPort = Integer.valueOf(fields.nextToken());
		return new DatagramPacket(punchData.getBytes(), punchData.length(), new InetSocketAddress(targetIP, targetPort));
	}


	private static String requestTargetInfo() throws IOException {
		DatagramSocket socket = _socket;
		//DatagramSocket socket = openSocket(6789);
		DatagramPacket request = request();
		socket.send(request);
		return targetInfo(socket);
	}	
	
	
	private static String targetInfo(DatagramSocket socket) throws IOException {
		DatagramPacket targetPacket = new DatagramPacket(new byte[1024], 1024);
		socket.receive(targetPacket);
		socket.close();
		return new String(targetPacket.getData());
	}


	private static DatagramPacket request() throws IOException {
		String data = getId() + ";" + getPrivateIP() +  ";" + LISTENER_PORT +  ";" + targetFromUser();
		return new DatagramPacket(data.getBytes(), data.length(), new InetSocketAddress(RENDEZVOUS_IP, RENDEZVOUS_PORT));
	}

	
	private static DatagramSocket openSocket(int port) throws IOException {
		return new DatagramSocket(port);
	}
	
	
	private static String getId() throws IOException {
		return InetAddress.getLocalHost().getHostName();
	}
	
	
	private static String getPrivateIP() throws IOException {
		return InetAddress.getLocalHost().getHostAddress();
	}
	
	
	private static String targetFromUser() {
		System.out.print("Target: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			return reader.readLine();
		} catch (IOException e1) {
			return " ";
		}
	}
	
	
	private static void display(String out) {
		System.out.println(out);
	}


	private static void startListenOtherPeers() {
		new Thread(new Peer()).start();
	}

	
	@Override
	public void run() {
		try {
			listenerPeers();
		} catch (Exception e) {
			display("Listener failure: " + e.getMessage());
		}
	}


	private void listenerPeers() throws IOException {
		DatagramSocket socket = _socket;
		//DatagramSocket socket = openSocket(LISTENER_PORT);
		while (true) {
			Listener(socket);
		}
	}


	private void Listener(DatagramSocket socket) throws IOException {
		DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
		socket.receive(receivePacket);
		display("Heard: " + new String(receivePacket.getData()).trim());
	}
	
}
