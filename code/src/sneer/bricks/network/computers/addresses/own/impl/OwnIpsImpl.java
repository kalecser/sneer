package sneer.bricks.network.computers.addresses.own.impl;

import static basis.environments.Environments.my;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.network.computers.addresses.own.OwnIps;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.Closure;

public class OwnIpsImpl implements OwnIps {

	private static final long TWO_MINUTES = 1000 * 60 * 2;
	private SetRegister<InetAddress> ownIps = my(CollectionSignals.class).newSetRegister();
	private Light light = my(BlinkingLights.class).prepare(LightType.ERROR);

	{
		my(Timer.class).wakeUpNowAndEvery(TWO_MINUTES, new Closure() { @Override public void run() {
			update();
		}});
	}
	
	
	@Override
	public SetSignal<InetAddress> get() {
		return ownIps.output();
	}


	private void update() {
		Collection<InetAddress> currentIps = findOwnIps();
		for (InetAddress old : ownIps.output())
			if (!currentIps.contains(old))
				ownIps.remove(old);
		
		ownIps.addAll(currentIps);
	}


	private Collection<InetAddress> findOwnIps() {
		try {
			Collection<InetAddress> ret = tryToFindOwnIps();
			my(BlinkingLights.class).turnOffIfNecessary(light);
			return ret;
		} catch (SocketException e) {
			my(BlinkingLights.class).turnOnIfNecessary(light, "Network Error", e);
			return Collections.EMPTY_LIST;
		}
	}


	private static Collection<InetAddress> tryToFindOwnIps() throws SocketException {
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

	
	public static void main(String[] args) throws SocketException {
		System.out.println(tryToFindOwnIps());
	}
	
}
