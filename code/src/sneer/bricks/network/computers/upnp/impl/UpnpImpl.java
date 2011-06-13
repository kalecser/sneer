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
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Closure;

@Brick
public class UpnpImpl implements Upnp {
	private static final String TCP_PROTOCOL = "TCP";

	private final Signal<Integer> _ownPort = my(Attributes.class).myAttributeValue(OwnPort.class);
	private final BlinkingLights _lights = my(BlinkingLights.class);
	private final Threads _threads = my(Threads.class);
	private InternetGatewayDevice[] _devices;

	private UpnpImpl() {
		_threads.startDaemon("UPnP devices discovery", new Closure() { @Override public void run() {
			try {
				findPlugAndPlayDevicesOnLAN();
				if (exist()) 
					mappingOwnPort();
				else  
					_lights.turnOn(LightType.INFO, "UPnP Devices.", "Sneer wasn't able to find UPnP devices on your network.", 7000);
			} catch (UnknownHostException uhe) {
				my(Logger.class).log("Unknown local host ip.", uhe);
			} catch (IOException e) {
				my(Logger.class).log("Unable discovery UPnP device(s).", e);
			}
		}});
	}

	private void findPlugAndPlayDevicesOnLAN() throws IOException	{
		_devices = InternetGatewayDevice.getDevices(5000);
	}

	private boolean exist() {
		return (_devices != null) ? true : false;
	}

	private void mappingOwnPort() throws UnknownHostException {
		int ownPort = _ownPort.currentValue();
		String localHostIP = InetAddress.getLocalHost().getHostAddress();

		for (InternetGatewayDevice igd : _devices) {
			UPNPRootDevice device = igd.getIGDRootDevice();
			try {
				if (igd.getSpecificPortMappingEntry(localHostIP, ownPort, TCP_PROTOCOL) != null) continue;
				igd.addPortMapping("Sneer port mapping", null, ownPort, ownPort, localHostIP, 0, TCP_PROTOCOL);
			} catch (Exception e) {
				my(Logger.class).log("Unable discovery or port mapping on " + device.getFriendlyName(), e);
			}
			_lights.turnOn(LightType.GOOD_NEWS, "Port mapping on " + device.getFriendlyName(), "Sneer has successfully on created route on " + device.getFriendlyName() + " to receive incoming connections from others.", 7000);
		}
		_devices = null;
	}
}
