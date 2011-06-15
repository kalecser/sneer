package sneer.bricks.network.computers.upnp.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.sbbi.upnp.impls.InternetGatewayDevice;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.upnp.Upnp;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Closure;

@Brick
public class UpnpImpl2 implements Upnp {
	private static final String TCP_PROTOCOL = "TCP";
	private static final String CAPTION = "UPnP ";

	private final Signal<Integer> _ownPort = my(Attributes.class).myAttributeValue(OwnPort.class);
	private final BlinkingLights _lights = my(BlinkingLights.class);
	private final Light _cantDiscover = _lights.prepare(LightType.ERROR);
	private final Light _cantMap = _lights.prepare(LightType.ERROR);
	
	@SuppressWarnings("unused") private Object _receptionRefToAvoidGc;
	private InternetGatewayDevice[] _devices;
	private String _localHostIp; 
	private transient int _mappedPort;
	
	private UpnpImpl2() {
		_receptionRefToAvoidGc = _ownPort.addPulseReceiver(new Runnable() { @Override public void run() {
			startMapping();
		}});
	}

	private void startMapping() {
		my(Threads.class).startDaemon(CAPTION, new Closure() { @Override public void run() {
			mapOwnPort();
		}});

	}
	
	private void mapOwnPort() {
		try {
			findUpnpDevicesOnNetwork();
			recoveryLocalHostIP();
			
			if (notExistDevicesOnNetwork())
				_lights.turnOn(LightType.INFO, CAPTION + "devices not found.", "Sneer not found any UPnP device(s) on your network.", 10000);
			else {	
				for (InternetGatewayDevice igd : _devices) {
					if (_mappedPort != 0)
						deleteMappedOwnPortOn(igd, _localHostIp, _mappedPort);
					
					try {
						addCurrentOwnPortOn(igd, _localHostIp, _ownPort.currentValue());
					} catch (Exception e) {
						_lights.turnOnIfNecessary(_cantMap, CAPTION + igd.getIGDRootDevice().getFriendlyName() , "Sneer couldn't add the port map from " + igd.getIGDRootDevice().getFriendlyName(), e);
					}
				}
			}
			_lights.turnOn(LightType.INFO, "Mapped own port on " + CAPTION + "devices.", "Sneer mapped own port on all UPnP devices of your network.", 10000);
		//} catch (UnknownHostException uhe) {
		//	_lights.turnOnIfNecessary(_cantDiscovery, CAPTION + "unknown local host ip", "Exception occured during reciver local host ip.", uhe);
		//} catch (IOException e) {
		//	_lights.turnOnIfNecessary(_cantDiscovery, CAPTION + "discover.", "Exception occured during search UPnP devices.", e);
		} catch (Exception e) {
			_lights.turnOnIfNecessary(_cantDiscover, CAPTION + "discover erro.", "Exception occured during search UPnP devices or recovery local host ip address.", e);
		} finally {
			_mappedPort = _ownPort.currentValue();
			_devices = null;
		}
	}
	
	private void findUpnpDevicesOnNetwork() throws IOException {
		_devices = InternetGatewayDevice.getDevices(5000);
	}

	private boolean notExistDevicesOnNetwork() {
		return (_devices == null) ? true : false; 
	}
	
	private void recoveryLocalHostIP() throws UnknownHostException {
		_localHostIp = InetAddress.getLocalHost().getHostAddress();
	}
	
	private void addCurrentOwnPortOn(final InternetGatewayDevice igd, final String ip, final int currentOwnPort) throws Exception {
		if (igd.getSpecificPortMappingEntry(ip, currentOwnPort, TCP_PROTOCOL) == null);
		igd.addPortMapping("Sneer port mapping", null, currentOwnPort, currentOwnPort, ip, 0, TCP_PROTOCOL);
	}

	private void deleteMappedOwnPortOn(final InternetGatewayDevice igd, final String ip, final int mappedOwnPort) {
		try { 
			igd.deletePortMapping(ip, mappedOwnPort, TCP_PROTOCOL);
		} catch (Exception e) {
			_lights.turnOnIfNecessary(_cantMap, CAPTION + igd.getIGDRootDevice().getFriendlyName() , "Sneer couldn't remove the port map from " + igd.getIGDRootDevice().getFriendlyName(), e);
		}	
	}
}