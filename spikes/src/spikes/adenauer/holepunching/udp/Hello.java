package spikes.adenauer.holepunching.udp;

import java.net.DatagramPacket;
import java.util.StringTokenizer;

import spikes.adenauer.holepunching.EndPoint;
import spikes.adenauer.holepunching.Node;
import spikes.adenauer.holepunching.prevalence.AllNodes;

public class Hello implements Runnable {
	private DatagramPacket _receivedPacket;
	
	public Hello(DatagramPacket receivedPacket) {
		_receivedPacket = receivedPacket;
	}
	
	@Override
	public void run() {
		StringTokenizer fields = extractFieldsFrom(_receivedPacket);
		String id = fields.nextToken();
		out("Id:" + id);
		EndPoint internal = extractInternalEndPointFrom(fields);
		EndPoint external = extractExternalEndPointFrom(_receivedPacket);
		AllNodes.add(id, new Node(internal, external));
	}

	private StringTokenizer extractFieldsFrom(DatagramPacket receivedPacket) {
		String receivedData = new String(receivedPacket.getData());
		return new StringTokenizer(receivedData.trim(), ";");
	}

	private EndPoint extractInternalEndPointFrom(StringTokenizer fields) {
		String internalIp = fields.nextToken();
		int internalPort = Integer.valueOf(fields.nextToken());
		out("Internal ip: " + internalIp + " Internal port: " + internalPort);
		return new EndPoint(internalIp, internalPort);
	}

	private EndPoint extractExternalEndPointFrom(DatagramPacket receivedPacket) {
		String externalIp = receivedPacket.getAddress().getHostAddress();
		int externalPort = receivedPacket.getPort();
		out("External ip: " + externalIp + " External port: " + externalPort);
		return new EndPoint(externalIp, externalPort);
	}

	private void out(String msg) {
		System.out.println(msg);
	}
}

