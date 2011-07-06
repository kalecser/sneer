package spikes.adenauer.puncher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Rendezvous implements Runnable {
	private static final String PRIVATE_PORT = "privatePort";
	private static final String PRIVATE_IP = "privateIP";
	private static final String PUBLIC_PORT = "publicPort";
	private static final String PUBLIC_IP = "publicIP";

	private static final Map<String, Map<String, String>> _table = new HashMap<String, Map<String,String>>();


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

	
	private byte[] encode(Map<String, String> info) {
		StringBuilder result = new StringBuilder();
		result.append(info.get(PRIVATE_IP));
		result.append(";");
		result.append(info.get(PRIVATE_PORT));
		result.append(";");
		result.append(info.get(PUBLIC_IP));
		result.append(";");
		result.append(info.get(PUBLIC_PORT));
		return result.toString().getBytes();
	}


	private void forwardInfo(String from, String target, DatagramSocket socket) throws IOException {
		Map<String, String> fromInfo = _table.get(from);
		Map<String, String> targetInfo = _table.get(target);
		
		if (targetInfo == null || targetInfo.isEmpty()) {
			display("Info of target: " + target + " not found.");
			return;
		}
		
		display("Forward info to: " + from);
		forward(encode(fromInfo), socket, targetInfo.get(PUBLIC_IP), Integer.valueOf(targetInfo.get(PUBLIC_PORT)));
		display("Forward info to: " + target);
		forward(encode(targetInfo), socket, fromInfo.get(PUBLIC_IP), Integer.valueOf(fromInfo.get(PUBLIC_PORT)));
		display("=====================================");
	}

	
	private void forward(byte[] data, DatagramSocket socket, String ip, int port) throws IOException {
		DatagramPacket targetPacket = new DatagramPacket(data, data.length);
		targetPacket.setSocketAddress(new InetSocketAddress(ip, port));
		socket.send(targetPacket);
		display("Sent info: " + new String(data) + " to address: "+ ip + ":" + port);
	}

	
	private String decodeFromInfo(StringTokenizer fields, DatagramPacket receivedPacket) {
		String from = fields.nextToken();
		Map<String, String> data = new HashMap<String, String>();
		data.put(PRIVATE_IP, fields.nextToken());
		data.put(PRIVATE_PORT, fields.nextToken());
		data.put(PUBLIC_IP, receivedPacket.getAddress().getHostAddress());
		data.put(PUBLIC_PORT, String.valueOf(receivedPacket.getPort()));
		
		refreshTable(from, data);
		
		display("Decoded packet id: " + from + " - Private address: " + data.get(PRIVATE_IP) + ", Private port: " + data.get(PRIVATE_PORT) +   ", Public address: " + data.get(PUBLIC_IP) + " and Public port: "+  data.get(PUBLIC_PORT));
		return from;
	}


	private void refreshTable(String from, Map<String, String> data) {
		if (from == null) return;
		if (data == null || data.isEmpty()) return;
		if (_table.containsKey(from)) _table.remove(from);
		_table.put(from, data);
	}


	private String decodeTargetName(StringTokenizer fields) {
		return fields.nextToken();
	}

	
	private StringTokenizer toFields(DatagramPacket receivedPacket) {
		StringTokenizer fields = new StringTokenizer(new String(receivedPacket.getData()).trim(), ";");
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