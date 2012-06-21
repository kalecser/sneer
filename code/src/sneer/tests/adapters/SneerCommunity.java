package sneer.tests.adapters;

import static basis.environments.Environments.my;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.log.tests.LoggerMocks;
import sneer.bricks.network.computers.tcp.TcpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.tests.SovereignCommunity;
import sneer.tests.SovereignParty;
import sneer.tests.adapters.impl.SneerPartyApiClassLoaderImpl;
import sneer.tests.adapters.impl.utils.network.InProcessNetwork;
import sneer.tests.adapters.impl.utils.network.udp.impl.InProcessUdpNetworkImpl;
import basis.brickness.Brickness;
import basis.environments.Environment;
import basis.environments.EnvironmentUtils;
import basis.environments.ProxyInEnvironment;
import basis.languagesupport.JarFinder;

public class SneerCommunity implements SovereignCommunity {
	
	private final TcpNetwork _tcpNetwork = new InProcessNetwork();
	private final UdpNetwork _udpNetwork = new InProcessUdpNetworkImpl();
	private int _nextPort = 10000;

	private final File _tmpFolder;

	private final List<SneerParty> _allParties = new ArrayList<SneerParty>();
	
	
	public SneerCommunity(File tmpFolder) {
		_tmpFolder = tmpFolder;
		startStunServer();
	}

	
	public List<SneerParty> allParties() {
		return Collections.unmodifiableList(_allParties);
	}
	
	
	@Override
	public SovereignParty createParty(final String name) {
		int port = _nextPort++;
		return createParty(name, port);
	}


	private SneerParty createParty(String name, int port) {
		File sneerHome = rootFolder(name);
		File dataFolder        = makeFolder(sneerHome, "data");
		File tmpFolder         = makeFolder(sneerHome, "tmp");
		File currentCodeFolder = makeFolder(sneerHome, "code/current");
		File privateBin        = makeFolder(sneerHome, "code/current/bin");
		File privateSrc        = makeFolder(sneerHome, "code/current/src");
		File stageFolder       = new File  (sneerHome, "code/stage");
		File sharedBin = my(ClassUtils.class).classpathRootFor(SneerCommunity.class);
		
		Environment container = Brickness.newBrickContainer(_udpNetwork, _tcpNetwork, newLogger(name));
		URLClassLoader apiClassLoader = apiClassLoader(privateBin, sharedBin, name);
		
		SneerParty partyImpl = (SneerParty)EnvironmentUtils.retrieveFrom(container, loadControllerUsing(apiClassLoader));
		final SneerParty ret = ProxyInEnvironment.newInstance(container, partyImpl);
		
		ret.configDirectories(dataFolder, tmpFolder, currentCodeFolder, privateSrc, privateBin, stageFolder);
		_allParties.add(ret);

		ret.start(name, port);
		return ret;
	}


	@Override
	public SovereignParty newSession(SovereignParty party) {
		SneerParty sneerParty = (SneerParty)party;
		String ownName = sneerParty.ownName();
		int ownPort = sneerParty.sneerPort();

		crash(sneerParty);

		return createParty(ownName, ownPort);
	}


	@Override
	public void crash(SovereignParty party) {
		((SneerParty) party).crash();
		_allParties.remove(party);
	}

	
	private File makeFolder(File parent, String child) {
		File result = new File(parent, child);
		if (!result.exists() && !result.mkdirs())
			throw new IllegalStateException("Could not create folder '" + result + "'!");
		return result;
	}

	
	private Class<?> loadControllerUsing(URLClassLoader apiClassLoader) {
		try {
			return apiClassLoader.loadClass(SneerPartyController.class.getName());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	
	private URLClassLoader apiClassLoader(File privateBin, File sharedBin, final String name) {
		URL[] langJars = JarFinder.languageSupportJars(sharedBin);
		URL[] classPath = new URL[langJars.length + 2];
		classPath[0] = toURL(privateBin);
		classPath[1] = toURL(sharedBin);
		System.arraycopy(langJars, 0, classPath, 2, langJars.length);
		return new SneerPartyApiClassLoaderImpl(classPath, SneerCommunity.class.getClassLoader(), name);
	}

	
	private URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	
	private File rootFolder(String name) {
		String home = "sneer-" + name.replace(' ', '_');
		return makeFolder(_tmpFolder, home);
	}

	
	@Override
	public void connect(SovereignParty a, SovereignParty b) {
		if (a.isContact(b.ownName()))
			return;
		SneerParty partyA = (SneerParty)a;
		SneerParty partyB = (SneerParty)b;
		partyA.startConnectingTo(partyB);
		partyB.acceptConnectionFrom(partyA.ownName());
		partyA.waitUntilOnline(partyB);
		partyB.waitUntilOnline(partyA);
	}

	
	private Logger newLogger(final String name) {
		return my(LoggerMocks.class).newInstance(name);
	}
	

	@Override
	public void crash() {
		for (SneerParty party : _allParties) party.crash();
		_allParties.clear();
	}


	private void startStunServer() {
		createParty("Stun Server", 0).startStunServer();
	}

}
