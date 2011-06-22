package spikes.adenauer.puncher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Rendezvous implements Runnable {
	//private static Map<String, Map<String, String>> table = new HashMap<String, Map<String,String>>();
	
	@Override
	public void run() {
		try {
			process(socket());
		} catch (Exception e) {
			display("Failure on listener: " + e.getMessage());
		} 
	}
	

	private void process(DatagramSocket socket) throws IOException {
		while (true){
			DatagramPacket readPacket = read(socket);
			manager(readPacket);
			//write()
		}
	}

	
	private void manager(DatagramPacket readPacket) {
		if (readPacket == null) return;
		//StringTokenizer fields = new StringTokenizer(new String(readPacket.getData()), ";");
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

	
