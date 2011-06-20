package spikes.adenauer.holepunching;

import spikes.adenauer.holepunching.udp.UDPServer;

public class Main {
	public static void main(String[] ignored) {
		new Thread(new UDPServer(), "UDPServer").start();
	}
}
