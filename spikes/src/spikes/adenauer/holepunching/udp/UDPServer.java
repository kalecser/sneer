package spikes.adenauer.holepunching.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UDPServer implements Runnable {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void run(){
    	try {
    		start();
		} catch (Exception e) {
			logger.log(Level.ALL, "Start server fail", e);
		}	
    }
    
    private void start() throws Exception {
    	DatagramSocket serverSocket = new DatagramSocket(9876);
        while (true){
        	receive(serverSocket);
        }
    }
    
    private void receive(DatagramSocket socket) {
    	try {
        	process(receivedPacketFrom(socket));
    	} catch (Exception e) {
			logger.log(Level.ALL, "Receive data failure from " + socket.getInetAddress().getHostAddress(), e);
		}
    }
  
    private DatagramPacket receivedPacketFrom(DatagramSocket socket) throws IOException { //receive method thrown RuntimeException :/
    	DatagramPacket dataPacket = new DatagramPacket(new byte[100], 100); 
    	socket.receive(dataPacket);
    	return dataPacket;
    }

    private void process(DatagramPacket receivedPacket) {
    	new Thread(new Hello(receivedPacket)).start();
    }
}
