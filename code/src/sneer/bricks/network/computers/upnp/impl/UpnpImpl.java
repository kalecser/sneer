package sneer.bricks.network.computers.upnp.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.UPNPResponseException;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.upnp.Upnp;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import basis.brickness.Brick;
import basis.lang.Closure;
import basis.lang.Consumer;

@Brick
public class UpnpImpl implements Upnp {
	
	private static final String TCP_PROTOCOL = "TCP";
	private static final String UDP_PROTOCOL = "UDP";
	@SuppressWarnings("unused") private final Object _refToAvoidGc;
	private int _previousMappedPort = 0;

	
	private UpnpImpl() {
		_refToAvoidGc = ownPort().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer port) {
			startMapping(port);
		}});
	}

	
	private void startMapping(final int port) {
		my(Threads.class).startDaemon("UPnP", new Closure() { @Override public void run() {
			map(port);
		}});
	}

	
	synchronized
	private void map(int port) {
		try {
			tryToUpdateMap(port);
		} catch (Exception e) {
			blink(e, "port " + port);
		} finally {
			_previousMappedPort = port;
		}
	}


	private void tryToUpdateMap(int port) throws Exception {  //sbbi lib throws RuntimeExceptions :(
		InternetGatewayDevice[] devices = InternetGatewayDevice.getDevices(5000);
		if (devices == null || devices.length == 0) {
			my(BlinkingLights.class).turnOn(LightType.INFO, "No UPnP devices found.", "There are apparently no UPnP devices on your network. That's OK.", 6000);
			return;
		}
		
		for (InternetGatewayDevice device : devices)
			updateMap(device, port);
	}
	
	
	private void updateMap(InternetGatewayDevice device, int port) {
		try {
			tryToUpdateMapping(device, port);
		} catch (Exception e) {
			blink(e, "port " + port + " on device " + pretty(device));
		}
	}


	private void tryToUpdateMapping(InternetGatewayDevice device, int port) throws Exception { //sbbi lib throws RuntimeExceptions :(
		String ip = localHostIp();
		addMappingIfNecessary(device, ip, port);
		deleteMappingIfNecessary(device, _previousMappedPort);
	}


	private void addMappingIfNecessary(InternetGatewayDevice device, String ip, int port) throws Exception {
		if (port == 0) return;
		addMapping(device, ip, port, UDP_PROTOCOL);
		addMapping(device, ip, port, TCP_PROTOCOL);
		my(BlinkingLights.class).turnOn(LightType.GOOD_NEWS, "UPnP port " + port + " opened.", "Sneer port opened on UPnP network device " + pretty(device), 15000);
	}


	private void addMapping(InternetGatewayDevice device, String ip, int port, String protocol) throws IOException, UPNPResponseException {
		if (device.addPortMapping("Sneer", null, port, port, ip, 0, protocol))
			my(Logger.class).log("UPnP port {} mapped for {}.", port, protocol);
	}
	

	private void deleteMappingIfNecessary(InternetGatewayDevice device, int previousPort) {
		if (previousPort == 0) return;
		try {
			deleteMapping(device, previousPort, UDP_PROTOCOL);
			deleteMapping(device, previousPort, TCP_PROTOCOL);
		} catch (Exception e) {
			my(Logger.class).log("UPnP failed to delete old port " + previousPort + ". " + e.getClass() + ": " + e.getMessage());
		}
	}

	
	private void deleteMapping(InternetGatewayDevice device, int previousPort, String protocol) throws IOException, UPNPResponseException {
		if (device.deletePortMapping(null, previousPort, protocol))
			my(Logger.class).log("UPnP port {} mapping deleted for {}.", previousPort, protocol);
	}

	
	private String pretty(InternetGatewayDevice device) {
		return device.getIGDRootDevice().getFriendlyName();
	}

	
	private String localHostIp() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
	
	
	private Signal<Integer> ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class);
	}

	
	private void blink(Exception e, String situation) {
		String caption = "UPnP error tying to map " + situation;
		my(BlinkingLights.class).turnOn(LightType.ERROR, caption, "This makes it harder for your contacts to reach you.", e, 15000);
	}
	
}
