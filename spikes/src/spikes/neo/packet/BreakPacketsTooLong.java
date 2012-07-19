package spikes.neo.packet;

import static basis.environments.Environments.my;
import static basis.environments.Environments.runWith;
import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import basis.brickness.Brickness;
import basis.lang.Closure;
import basis.lang.Consumer;

public class BreakPacketsTooLong {
	
	public static void main(String[] args) {
		runWith(Brickness.newBrickContainer(), new Closure() { @Override public void run() {
			test();
		}});
	}
	private static void test() {
		String[] messages = { "Hey Neide", "How are you?" };
		final StringBuilder log = new StringBuilder();
		
		PacketSchedulerMock schedulerMock = new PacketSchedulerMock(messages);
		
		PacketScheduler scheduler = my(UdpPackets.class).splitScheduler(schedulerMock, 4);
		Consumer<byte[]> receiver = my(UdpPackets.class).joinReceiver(new Consumer<byte[]>() { @Override public void consume(byte[] value) {
			log.append("| " + new String(value));
		}});
		
		while(true) {
			receiver.consume(scheduler.highestPriorityPacketToSend());
			scheduler.previousPacketWasSent();
			
			if(!schedulerMock.hasMorePackets())
				break;
		}
		
		System.out.println(log);
	}

}
