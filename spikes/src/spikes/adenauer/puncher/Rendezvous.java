package spikes.adenauer.puncher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Rendezvous implements Runnable {
	private static final String INTERNAL_PORT = "internalPort";
	private static final String INTERNAL_IP = "internalIP";
	private static final String EXTERNAL_PORT = "externalPort";
	private static final String EXTERNAL_IP = "externalIP";

	private static final Map<String, Map<String, String>> table = new HashMap<String, Map<String,String>>();
	
	@Override
	public void run() {
		try {
			listener(socket());
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
			manager(read(socket), socket);
		} catch (Exception e) {
			display("Process failure: " + e.getMessage());
		}
	}


	private byte[] encode(Map<String, String> info) {
		StringBuilder result = new StringBuilder();
		result.append(info.get(INTERNAL_IP));
		result.append(";");
		result.append(info.get(INTERNAL_PORT));
		result.append(";");
		result.append(info.get(EXTERNAL_IP));
		result.append(";");
		result.append(info.get(EXTERNAL_PORT));
		
		return result.toString().getBytes();
	}


	private void manager(DatagramPacket receivedPacket, DatagramSocket socket) throws IOException {
		if (receivedPacket == null) return;
		StringTokenizer fields = toFields(receivedPacket);
		String from = decodeFromInfo(fields, receivedPacket);
		String target = decodeTargetName(fields);
		forwardInfo(from, target, socket);
	}

	
	private void forwardInfo(String from, String target, DatagramSocket socket) throws IOException {
		Map<String, String> fromInfo = table.get(from);
		Map<String, String> targetInfo = table.get(target);
		
		forward(encode(fromInfo), socket, targetInfo.get(EXTERNAL_IP), Integer.valueOf(targetInfo.get(EXTERNAL_PORT)));
		forward(encode(targetInfo), socket, fromInfo.get(EXTERNAL_IP), Integer.valueOf(fromInfo.get(EXTERNAL_PORT)));
	}

	private void forward(byte[] data, DatagramSocket socket, String ip, int port) throws IOException {
		DatagramPacket targetPacket = new DatagramPacket(data, data.length);
		targetPacket.setSocketAddress(new InetSocketAddress(ip, port));
		socket.send(targetPacket);
	}

	private String decodeFromInfo(StringTokenizer fields, DatagramPacket receivedPacket) {
		String from = fields.nextToken();
		Map<String, String> data = new HashMap<String, String>();
		data.put(INTERNAL_IP, fields.nextToken());
		data.put(INTERNAL_PORT, fields.nextToken());
		data.put(EXTERNAL_IP, receivedPacket.getAddress().getHostAddress());
		data.put(EXTERNAL_PORT, String.valueOf(receivedPacket.getPort()));
		table.put(from, data);
		return from;
	}

	
	private String decodeTargetName(StringTokenizer fields) {
		return fields.nextToken();
	}

	
	private StringTokenizer toFields(DatagramPacket receivedPacket) {
		StringTokenizer fields = new StringTokenizer(new String(receivedPacket.getData()).trim(), ";");
		return fields;
	}


	private DatagramPacket read(DatagramSocket socket) throws IOException {
		byte[] receivedData = new byte[1024];
		DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
		
		socket.receive(receivedPacket);
		return receivedPacket;
	}

	
	private DatagramSocket socket() throws IOException {
		return new DatagramSocket(9876);
	}
	
	private void display(String out) {
		System.out.println(out);
	}

	public static void main(String[] ignored ) {
		new Thread(new Rendezvous()).start();
	}
}