package spikes.adenauer.holepunching.udp.tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

public class UDPHolePunchingTest extends Assert {

	@Test
	public void helloPacket() throws IOException {
		sendPacket();
	}

	private void sendPacket() throws IOException {
		DatagramSocket clientSocket = new DatagramSocket();
		clientSocket.send(packet());
	}
	
	private DatagramPacket packet() throws UnknownHostException { 
		InetAddress IPAddress = InetAddress.getByName("localhost");
		String contentPacket = createContentPacket(IPAddress);
		return new DatagramPacket(contentPacket.getBytes(), contentPacket.length(), IPAddress, 9876);
	}	
	
	private String createContentPacket(InetAddress IPAddress) {
		return "neide;" + IPAddress.getHostAddress() + ";9090";
	}
}
