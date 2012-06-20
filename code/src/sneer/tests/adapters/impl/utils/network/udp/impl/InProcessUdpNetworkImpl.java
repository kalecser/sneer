package sneer.tests.adapters.impl.utils.network.udp.impl;

import java.net.BindException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import basis.lang.Functor;

import sneer.tests.adapters.impl.utils.network.udp.InProcessUdpNetwork;


public class InProcessUdpNetworkImpl implements InProcessUdpNetwork {

	private final Map<Integer, InProcessUdpSocket> socketsByNumber = new HashMap<Integer, InProcessUdpSocket>();
	private final Functor<Integer, InProcessUdpSocket> socketsByNumberFinder = new Functor<Integer, InProcessUdpSocket>() {  @Override public InProcessUdpSocket evaluate(Integer port) {
		return socketsByNumber.get(port);
	}};
	
	@Override
	synchronized
	public UdpSocket openSocket(int portNumber) throws SocketException {
		checkAvailability(portNumber);
		InProcessUdpSocket result = new InProcessUdpSocket(portNumber, socketsByNumberFinder);
		socketsByNumber.put(portNumber, result);
		return result;
	}
	

	private void checkAvailability(int portNumber) throws BindException {
		InProcessUdpSocket port = socketsByNumber.get(portNumber);
		if (port == null || port.isCrashed()) return;
		throw new BindException("Port already in use: " + portNumber);
	}

}
