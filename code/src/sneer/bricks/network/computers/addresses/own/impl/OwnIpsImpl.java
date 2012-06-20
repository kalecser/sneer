package sneer.bricks.network.computers.addresses.own.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.pulp.blinkinglights.LightType.ERROR;
import static sneer.bricks.pulp.blinkinglights.LightType.INFO;
import static sneer.bricks.pulp.blinkinglights.LightType.WARNING;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.network.computers.addresses.own.OwnIps;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.Closure;
import basis.lang.Consumer;

public class OwnIpsImpl implements OwnIps {

	private static final long TWO_MINUTES = 1000 * 60 * 2;
	private SetRegister<InetAddress> ownIps = my(CollectionSignals.class).newSetRegister();
	private Light error = my(BlinkingLights.class).prepare(ERROR);
	private Light noIpsFound = my(BlinkingLights.class).prepare(WARNING);
	
	@SuppressWarnings("unused")
	private final Object refToAvoidGC = ownIps.output().addReceiver(new Consumer<CollectionChange<InetAddress>>() { @Override public void consume(CollectionChange<InetAddress> change) {
		log(change);
	}});

	{
		update();
		my(Timer.class).wakeUpEvery(TWO_MINUTES, new Closure() { @Override public void run() {
			update();
		}});
	}
	
	
	@Override
	public SetSignal<InetAddress> get() {
		return ownIps.output();
	}

	
	private void log(CollectionChange<InetAddress> change) {
		for (InetAddress addr : change.elementsAdded())
			my(BlinkingLights.class).turnOn(INFO, "IP " + addr.getHostAddress() + " found", "Your machine can receive connections on this IP address.", 7000);			
		
		for (InetAddress addr : change.elementsRemoved())
			my(BlinkingLights.class).turnOn(INFO, "IP " + addr.getHostAddress() + " removed", "Your machine can no longer receive connections on this IP", 7000);
	}

	
	private void update() {
		Collection<InetAddress> currentIps = findOwnIps();
		for (InetAddress old : ownIps.output())
			if (!currentIps.contains(old))
				ownIps.remove(old);
		
		ownIps.addAll(currentIps);
	}


	private Collection<InetAddress> findOwnIps() {
		Collection<InetAddress> ret;
		
		try {
			ret = tryToFindOwnIps();
			my(BlinkingLights.class).turnOffIfNecessary(error);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(error, "Error searching for IPs", e);
			return Collections.EMPTY_LIST;
		}
		
		if(ret.isEmpty())
			my(BlinkingLights.class).turnOnIfNecessary(noIpsFound, "No IPs found", "No IPv4 address found on this machine. Check your network settings.");
		else 
			my(BlinkingLights.class).turnOffIfNecessary(noIpsFound);
		
		return ret;
	}


	private static Collection<InetAddress> tryToFindOwnIps() throws IOException {
		if ("true".equals(System.getProperty("sneer.testmode"))) return Arrays.asList(InetAddress.getByName("127.0.0.1"));
				
		Collection<InetAddress> ret = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
		while (nis.hasMoreElements())
			collectAddressesIn(ret, nis.nextElement());
		return ret;
	}


	private static void collectAddressesIn(Collection<InetAddress> collector, NetworkInterface ni) {
		Enumeration<InetAddress> addresses = ni.getInetAddresses();
		while (addresses.hasMoreElements()) {
			InetAddress addr = addresses.nextElement();
			if (!(addr instanceof Inet4Address)) continue;
			if (addr.isLoopbackAddress()) continue;
			collector.add(addr);
		}
	}

	
	public static void main(String[] args) throws IOException {
		System.out.println(tryToFindOwnIps());
	}
	
}
