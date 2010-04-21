package sneer.tests.adapters.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.expression.tuples.logger.TupleLogger;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.iterables.Iterables;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.sockets.connections.originator.SocketOriginator;
import sneer.bricks.network.computers.sockets.connections.receiver.SocketReceiver;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.heartbeat.Heart;
import sneer.bricks.network.social.heartbeat.stethoscope.Stethoscope;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.own.name.OwnNameKeeper;
import sneer.bricks.pulp.probe.ProbeManager;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.snapps.wind.Shout;
import sneer.bricks.snapps.wind.Wind;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.installer.BrickInstaller;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.arrays.ImmutableByteArray;
import sneer.foundation.lang.exceptions.NotImplementedYet;
import sneer.foundation.lang.exceptions.Refusal;
import sneer.main.SneerVersionUpdater;
import sneer.tests.SovereignParty;
import sneer.tests.adapters.SneerParty;
import sneer.tests.adapters.SneerPartyApiClassLoader;
import sneer.tests.adapters.SneerPartyController;

class SneerPartyControllerImpl implements SneerPartyController, SneerParty {

	static private final String MOCK_ADDRESS = "localhost";

	
	private Collection<Object> _refToAvoidGc = new ArrayList<Object>();
	@SuppressWarnings("unused")	private WeakContract _refToAvoidGc2;

	private File _codeFolder;

	
	@Override
	public void setSneerPort(int port) {
		try {
			my(OwnPort.class).portSetter().consume(port);
		} catch (Refusal e) {
			throw new IllegalArgumentException(e);
		}
	}

	
	@Override
	public void startConnectingTo(SneerParty other) {
		Contact contact = produceContact(other.ownName());

		SneerParty sneerParty = other;
		putSeal(other, contact);
		my(InternetAddressKeeper.class).add(contact, MOCK_ADDRESS, sneerParty.sneerPort());
	}


	private void putSeal(SneerParty other, Contact contact) {
		try {
			my(ContactSeals.class).put(contact.nickname().currentValue(), newSeal(other.seal()));
		} catch (Refusal e) {
			throw new IllegalStateException(e); // Fix Handle this exception.
		}
	}

	
	@Override
	public void waitUntilOnline(SneerParty other) {
		Contact contact = produceContact(other.ownName());
		waitUntilOnline(contact);
	}

    
    @Override
    public void waitUntilOnline(String nickname) {
    	waitUntilOnline(produceContact(nickname));
    }

	
    private void waitUntilOnline(Contact contact) {
		my(SignalUtils.class).waitForValue(isAlive(contact), true);
	}

    
	private Seal newSeal(byte[] bytes) {
		return new Seal(new ImmutableByteArray(bytes));
	}

	
	private Contact produceContact(String contactName) {
		return my(Contacts.class).produceContact(contactName);
	}

	
	@Override
	public String ownName() {
		return my(OwnNameKeeper.class).name().currentValue();
	}

	
	@Override
	public void setOwnName(String newName) {
		my(OwnNameKeeper.class).nameSetter().consume(newName);
	}
	
	
    @Override
    public void giveNicknameTo(SovereignParty peer, String newNickname) {
    	byte[] publicKey = peer.seal();
		Contact contact = waitForContactGiven(publicKey);

		try {
			my(Contacts.class).nicknameSetterFor(contact).consume(newNickname);
		} catch (Refusal e) {
			throw new IllegalStateException(e);
		}
		
		waitUntilOnline(contact);
    }
    
	private Signal<Boolean> isAlive(Contact contact) {
		return my(Stethoscope.class).isAlive(contact);
	}

	
	private Contact waitForContactGiven(byte[] seal) {
		while (true) {
			Contact contact = my(ContactSeals.class).contactGiven(new Seal(new ImmutableByteArray(seal)));
			if (contact != null) return contact;
			my(Threads.class).sleepWithoutInterruptions(10);
			my(Clock.class).advanceTime(60 * 1000);
		}
	}

	
	@Override
    public byte[] seal() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}

	
	@Override
    public void navigateAndWaitForName(String nicknamePath, String expectedName) {
		//nicknamePath.split("/")
		//my(SignalUtils.class).waitForValue() might be useful.
		throw new NotImplementedYet();
    }

	
	@Override
	public int sneerPort() {
        return my(OwnPort.class).port().currentValue();
    }

	
	@Override
	public void shout(String phrase) {
		my(Wind.class).megaphone().consume(phrase);
	}

	
	@Override
	public void waitForShouts(final String shoutsExpected) {
		final Latch latch = my(Latches.class).produce();

		WeakContract contract = my(Wind.class).shoutsHeard().addPulseReceiver(new Closure() { @Override public void run() {
			openLatchIfShoutsHeard(shoutsExpected, latch);
		}});
		openLatchIfShoutsHeard(shoutsExpected, latch);
		
		latch.waitTillOpen();
		contract.dispose();
	}

	
	private void openLatchIfShoutsHeard( String shoutsExpected, Latch latch) {
		String shoutsHeard = concat(my(Wind.class).shoutsHeard());
		if (shoutsHeard.equals(shoutsExpected))
			latch.open();
	}

	
	private String concat(Iterable<Shout> shouts) {
		List<Shout> sorted = my(Iterables.class).sortByToString(shouts);
		return my(Lang.class).strings().join(sorted, ", ");
	}

	
	@Override
	public void configDirectories(File dataFolder, File tmpFolder, File codeFolder, File srcFolder, File binFolder, File stageFolder) {
		my(FolderConfig.class).storageFolder().set(dataFolder);
		my(FolderConfig.class).tmpFolder().set(tmpFolder);
		my(FolderConfig.class).srcFolder().set(srcFolder);
		my(FolderConfig.class).binFolder().set(binFolder);
		
		my(FolderConfig.class).stageFolder().set(stageFolder);
		_codeFolder = codeFolder;
	}

	
	private void startSnapps() {
		startAndKeep(SocketOriginator.class);
		startAndKeep(SocketReceiver.class);
		startAndKeep(ProbeManager.class);

		startAndKeep(TupleLogger.class);

		startAndKeep(Wind.class);

		startAndKeep(FileServer.class);

		startAndKeep(Heart.class);
	}

	
	private void startAndKeep(Class<?> snapp) {
		_refToAvoidGc.add(my(snapp));
	}


	@Override
	public void loadBrick(String brickName) {
		try {
			SneerPartyApiClassLoader apiClassLoader = (SneerPartyApiClassLoader) apiClassLoader();
			my(apiClassLoader.loadUnsharedBrickClass(brickName));
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	private ClassLoader apiClassLoader() {
		return SneerPartyController.class.getClassLoader();
	}
	
	@Override
	public boolean isOnline(String nickname) {
		Contact contact = my(Contacts.class).contactGiven(nickname);
		return isAlive(contact).currentValue();
	}

	
	private void accelerateHeartbeat() {
		my(Threads.class).startStepping(new Closure() { @Override public void run() {
			my(Clock.class).advanceTime(1000);
			my(Threads.class).sleepWithoutInterruptions(20);
		}});
	}

	
	@Override
	public void waitForAvailableBrick(final String brickName, final String brickStatus) {
		my(Logger.class).log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Waiting for brick: " + brickName + " status: " + brickStatus);

		final Latch latch = my(Latches.class).produce();
		
		WeakContract contract = my(BrickSpace.class).newBuildingFound().addReceiver(new Consumer<Seal>() { @Override public void consume(Seal publisher) {
			my(Logger.class).log("New brick configuration found for: " + print(publisher));
			if (isBrickAvailable(brickName, brickStatus)) latch.open();
		}});
		if (isBrickAvailable(brickName, brickStatus)) latch.open();

		latch.waitTillOpen();
		contract.dispose();
	}

	
	private boolean isBrickAvailable(final String brickName, final String brickStatus) {
		for (BrickInfo brickInfo : my(BrickSpace.class).availableBricks()) {
			my(Logger.class).log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Brick found: " + brickInfo.name() + " status: " + brickInfo.status().name());
			if (brickInfo.name().equals(brickName)
				&& brickInfo.status().name().equals(brickStatus))
				return true;
		};
		return false;
	}
	
	
	@Override
	public void stageBricksForInstallation(String... brickNames) {
		for (String brickName : brickNames)
			setStagedForInstallation(brickName);
		
		my(BrickInstaller.class).stageBricksForInstallation();
	}

	
	@Override
	public void enableCodeSharing() {
		try {
			tryToCopyRepositoryCode();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		startAndKeep(BrickSpace.class);
	}

	
	private void tryToCopyRepositoryCode() throws IOException {
		my(Logger.class).log("Copying necessary repository code...");
		copyRepositorySources();
		copyUnupdatableBinFiles();
		my(Logger.class).log("Copying necessary repository code... done.");
	}

	
	private void copyUnupdatableBinFiles() throws IOException {
		copyNecessaryRepositoryBinFiles(
			"sneer/main/Sneer.class",
			"sneer/main/SneerVersionUpdater.class",
			"sneer/main/SneerVersionUpdater$ExclusionFilter.class",
			"sneer/main/SneerCodeFolders.class"
		);
	}

	
	private void copyNecessaryRepositoryBinFiles(String... fileNames) throws IOException {
		for (String fileName : fileNames)
			copyNecessaryRepositoryBinFile(fileName);
	}

	
	private void copyNecessaryRepositoryBinFile(String fileName) throws IOException {
		File from = new File(repositoryBinFolder(), fileName);
		File to = new File(testBinFolder(), fileName);
		my(IO.class).files().copyFile(from, to);
	}

	
	private void copyRepositorySources() throws IOException {
		copyToSourceFolder(new File(repositoryBinFolder().getParentFile(), "src"));
	}

	
	private File repositoryBinFolder() {
		return my(ClassUtils.class).classpathRootFor(getClass());
	}

	
	private void setStagedForInstallation(String brickName) {
		final BrickInfo brick = availableBrick(brickName);
		final BrickVersion singleVersion = singleVersionOf(brick);
		brick.setStagedForInstallation(singleVersion, true);
	}

	
	private BrickVersion singleVersionOf(BrickInfo brick) {
		if (brick.versions().size() != 1)
			throw new IllegalStateException();
		return brick.versions().get(0);
	}

	
	private BrickInfo availableBrick(String brickName) {
		for (BrickInfo brick : my(BrickSpace.class).availableBricks())
			if (brick.name().equals(brickName))
				return brick;
		throw new IllegalArgumentException();
	}

	
	@Override
	public void crash() {
		my(Threads.class).crashAllThreads();
	}

	
	@Override
	public void start() {
		throwOnBlinkingErrors();
		
		installStagedCodeIfNecessary();
		
		startSnapps();
		accelerateHeartbeat();
	}

	
	private void installStagedCodeIfNecessary() {
		File stageFolder = my(FolderConfig.class).stageFolder().get();
		try {
			String backupLabel = "" + System.currentTimeMillis();
			SneerVersionUpdater.installNewVersionIfPresent(stageFolder , backupLabel, _codeFolder);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	
	private void throwOnBlinkingErrors() {
		_refToAvoidGc2 = my(BlinkingLights.class).lights().addReceiver(new Consumer<CollectionChange<Light>>() { @Override public void consume(CollectionChange<Light> value) {
			for (Light l : value.elementsAdded())
				if (l.type() == LightType.ERROR)
					throw new IllegalStateException("ERROR blinking light detected", l.error());
		}});
	}

	
	@Override
	public void copyToSourceFolder(File folder) throws IOException {
		my(IO.class).files().copyFolder(folder, testSrcFolder());
	}

	
	private File testBinFolder() {
		return my(FolderConfig.class).binFolder().get();
	}

	
	private File testSrcFolder() {
		return my(FolderConfig.class).srcFolder().get();
	}

	
	private String print(Seal seal) {
		return seal.equals(my(OwnSeal.class).get().currentValue())
			? "myself"
			: my(ContactSeals.class).contactGiven(seal).nickname().toString();
	}
}
