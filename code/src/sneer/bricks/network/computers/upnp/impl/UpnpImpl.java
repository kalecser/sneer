package sneer.bricks.network.computers.upnp.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.sbbi.upnp.devices.UPNPRootDevice;
import net.sbbi.upnp.impls.InternetGatewayDevice;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.upnp.Upnp;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

@Brick
public class UpnpImpl implements Upnp {
	private static final String TCP_PROTOCOL = "TCP";
	private static final String LIGHT_CAPTION = "UPnP ";

	private final Signal<Integer> _ownPort = my(Attributes.class).myAttributeValue(OwnPort.class);
	private final Threads _threads = my(Threads.class);
	private final BlinkingLights _lights = my(BlinkingLights.class);
	private final Light _cantDiscovery = _lights.prepare(LightType.ERROR);
	private final Light _cantMap = _lights.prepare(LightType.ERROR);
	
	@SuppressWarnings("unused") private Object _receptionRefToAvoidGc;
	private InternetGatewayDevice[] _devices;
	private transient Integer _mappedPort = _ownPort.currentValue();
	
	private UpnpImpl() {
		_receptionRefToAvoidGc = _ownPort.addReceiver(new Consumer<Integer>() { @Override public void consume(Integer port) {
			remapOwnPort(port);
		}});
		
		_threads.startDaemon("UPnP devices discovery", new Closure() { @Override public void run() {
			mapOwnPort();
		}});
	}

	private void remapOwnPort(Integer port) {
		if (exitDevicesOnLAN())
			deleteAndAddOwnPortMap(toLocalHostIp());
	}
	
	private void mapOwnPort() {
		if (exitDevicesOnLAN())
			addOwnPortMap(toLocalHostIp());
	}
	
	private boolean exitDevicesOnLAN() {
		try {
			_devices = InternetGatewayDevice.getDevices(5000);
		} catch (IOException ioe) {
			_lights.turnOnIfNecessary(_cantDiscovery, LIGHT_CAPTION, "Sneer wasn't able to find UPnP devices on your network.", ioe);
		}

		if (_devices == null) {
			String captionOrMessage = LIGHT_CAPTION + "devices not found.";
			_lights.turnOn(LightType.INFO, captionOrMessage, "Sneer couldn't able to find UPnP devices on your network.", 10000);
			my(Logger.class).log(LIGHT_CAPTION + "devices not found.");
			return false;
		}
		return true;
	}

	private String toLocalHostIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException uhe) {
			_lights.turnOnIfNecessary(_cantMap, LIGHT_CAPTION + "unknown local host.", "Sneer wasn't able to recovery local ip address.", uhe);
		}
		return null;
	}
	
	private void addOwnPortMap(final String ip) {
		_mappedPort = _ownPort.currentValue();

		for (InternetGatewayDevice igd : _devices) {
			UPNPRootDevice device = igd.getIGDRootDevice();
			try {
				if (igd.getSpecificPortMappingEntry(ip, _mappedPort, TCP_PROTOCOL) != null) continue;
				igd.addPortMapping("Sneer port mapping", null, _mappedPort, _mappedPort, ip, 0, TCP_PROTOCOL);
			} catch (Exception e) {
				_lights.turnOnIfNecessary(_cantMap, LIGHT_CAPTION + device.getFriendlyName() , "Sneer couldn't add the port map from " + device.getFriendlyName(), e);
			}
		}
		_devices = null;
	}

	private void deleteAndAddOwnPortMap(final String ip) {
		for (InternetGatewayDevice igd : _devices) {
			UPNPRootDevice device = igd.getIGDRootDevice();
			try {
				igd.deletePortMapping(ip, _mappedPort, TCP_PROTOCOL);
			} catch (Exception e) {
				_lights.turnOnIfNecessary(_cantMap, LIGHT_CAPTION + device.getFriendlyName() , "Sneer couldn't remove the old port map from " + device.getFriendlyName(), e);
			}
		}
		addOwnPortMap(ip);
	}
}
