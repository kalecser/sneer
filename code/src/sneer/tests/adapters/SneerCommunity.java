package sneer.tests.adapters;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.log.tests.LoggerMocks;
import sneer.bricks.pulp.network.Network;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.environments.ProxyInEnvironment;
import sneer.foundation.languagesupport.LanguageJarFinder;
import sneer.tests.SovereignCommunity;
import sneer.tests.SovereignParty;
import sneer.tests.adapters.impl.SneerPartyApiClassLoaderImpl;
import sneer.tests.adapters.impl.utils.network.InProcessNetwork;

public class SneerCommunity implements SovereignCommunity {

	private final Network _network = new InProcessNetwork();
	private int _nextPort = 10000;

	private final File _tmpFolder;

	private final Set<SneerParty> _allParties = new HashSet<SneerParty>();
	
	
	public SneerCommunity(File tmpFolder) {
		_tmpFolder = tmpFolder;
	}
	
	
	@Override
	public SovereignParty createParty(final String name) {
		int port = _nextPort++;
		return createParty(name, port);
	}


	private SovereignParty createParty(final String name, int port) {
		File sneerHome = rootFolder(name);
		File dataFolder        = makeFolder(sneerHome, "data");
		File tmpFolder         = makeFolder(sneerHome, "tmp");
		File currentCodeFolder = makeFolder(sneerHome, "code/current");
		File privateBin        = makeFolder(sneerHome, "code/current/bin");
		File privateSrc        = makeFolder(sneerHome, "code/current/src");
		File stageFolder       = new File  (sneerHome, "code/stage");
		File sharedBin = my(ClassUtils.class).classpathRootFor(SneerCommunity.class);
		
		Environment container = Brickness.newBrickContainer(_network, newLogger(name));
		URLClassLoader apiClassLoader = apiClassLoader(privateBin, sharedBin, name);
		
		SneerParty partyImpl = (SneerParty)EnvironmentUtils.retrieveFrom(container, loadProbeClassUsing(apiClassLoader));
		final SneerParty party = ProxyInEnvironment.newInstance(container, partyImpl);
		
		party.configDirectories(dataFolder, tmpFolder, currentCodeFolder, privateSrc, privateBin, stageFolder);
		party.setOwnName(name);
		party.setSneerPort(port);

		party.start();
		
		_allParties.add(party);
		return party;
	}


	@Override
	public SovereignParty newSession(SovereignParty party) {
		SneerParty sneerParty = (SneerParty)party;
		crash(sneerParty);
		return createParty(sneerParty.ownName(), sneerParty.sneerPort());
	}


	private void crash(SneerParty sneerParty) {
		sneerParty.crash();
		_allParties.remove(sneerParty);
	}

	private File makeFolder(File parent, String child) {
		File result = new File(parent, child);
		if (!result.exists() && !result.mkdirs())
			throw new IllegalStateException("Could not create folder '" + result + "'!");
		return result;
	}

	private Class<?> loadProbeClassUsing(URLClassLoader apiClassLoader) {
		try {
			return apiClassLoader.loadClass(SneerPartyController.class.getName());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	private URLClassLoader apiClassLoader(File privateBin, File sharedBin, final String name) {
		URL[] langJars = LanguageJarFinder.langSupportJars(sharedBin);
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
		SneerParty partyA = (SneerParty)a;
		SneerParty partyB = (SneerParty)b;
		partyA.startConnectingTo(partyB);
		partyB.startConnectingTo(partyA);
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


}
