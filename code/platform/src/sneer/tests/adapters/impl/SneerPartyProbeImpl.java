package sneer.tests.adapters.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.iterables.Iterables;
import sneer.bricks.hardwaresharing.files.server.FileServer;
import sneer.bricks.network.computers.sockets.connections.originator.SocketOriginator;
import sneer.bricks.network.computers.sockets.connections.receiver.SocketReceiver;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.ContactManager;
import sneer.bricks.network.social.heartbeat.Heart;
import sneer.bricks.network.social.heartbeat.stethoscope.Stethoscope;
import sneer.bricks.network.social.loggers.tuples.TupleLogger;
import sneer.bricks.pulp.internetaddresskeeper.InternetAddressKeeper;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.own.name.OwnNameKeeper;
import sneer.bricks.pulp.port.PortKeeper;
import sneer.bricks.pulp.probe.ProbeManager;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.snapps.wind.Shout;
import sneer.bricks.snapps.wind.Wind;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.bricks.software.bricks.snappstarter.SnappStarter;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.code.compilers.java.JavaCompilerException;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickSpace;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.installer.BrickInstaller;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.Seal;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.exceptions.NotImplementedYet;
import sneer.foundation.lang.exceptions.Refusal;
import sneer.tests.SovereignParty;
import sneer.tests.adapters.SneerParty;
import sneer.tests.adapters.SneerPartyProbe;

class SneerPartyProbeImpl implements SneerPartyProbe, SneerParty {
	
	static private final String MOCK_ADDRESS = "localhost";
	private Collection<Object> _referenceToAvoidGc = new ArrayList<Object>();

	@Override
	public void setSneerPort(int port) {
		try {
			my(PortKeeper.class).portSetter().consume(port);
		} catch (Refusal e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void connectTo(SneerParty party) {
		Contact contact = produceContact(party.ownName());

		SneerParty sneerParty = party;
		//storePublicKey(contact, new PublicKey(sneerParty.publicKey()));
		my(InternetAddressKeeper.class).add(contact, MOCK_ADDRESS, sneerParty.sneerPort());

		waitUntilOnline(contact);
	}

	private Contact produceContact(String contactName) {
		return my(ContactManager.class).produceContact(contactName);
	}

//	private void storePublicKey(Contact contact, PublicKey publicKey) {
//		_keyManager.addKey(contact, publicKey);
//	}


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
			my(ContactManager.class).nicknameSetterFor(contact).consume(newNickname);
		} catch (Refusal e) {
			throw new IllegalStateException(e);
		}
		
		waitUntilOnline(contact);
    }
    
    @Override
    public void waitUntilOnline(String nickname) {
    	waitUntilOnline(produceContact(nickname));
    }

	private void waitUntilOnline(Contact contact) {
		//System.out.println("WAITING FOR ONLINE: " + contact.nickname().currentValue() + " " + contact);
		my(SignalUtils.class).waitForValue(isAlive(contact), true);
	}

	private Signal<Boolean> isAlive(Contact contact) {
		return my(Stethoscope.class).isAlive(contact);
	}

	private Contact waitForContactGiven(byte[] publicKey) {
		while (true) {
			Contact contact = my(Seals.class).contactGiven(new Seal(publicKey));
			if (contact != null) return contact;
			my(Threads.class).sleepWithoutInterruptions(10);
			my(Clock.class).advanceTime(60 * 1000);
		}
	}

	@Override
    public byte[] seal() {
		return my(Seals.class).ownSeal().bytes();
	}

	@Override
    public void navigateAndWaitForName(String nicknamePath, String expectedName) {
		//nicknamePath.split("/")
		//my(SignalUtils.class).waitForValue() might be useful.
		throw new NotImplementedYet();
    }


	@Override
	public int sneerPort() {
        return my(PortKeeper.class).port().currentValue();
    }

	@Override
	public void shout(String phrase) {
		my(Wind.class).megaphone().consume(phrase);
	}

	@Override
	public void waitForShouts(final String shoutsExpected) {
		final Latch latch = my(Latches.class).newLatch();

		WeakContract contract = my(Wind.class).shoutsHeard().addPulseReceiver(new Runnable() { @Override public void run() {
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
	public void configDirectories(File dataFolder, File tmpFolder, File ownBin, File platformSrcFolder, File platformBinFolder) {
		my(FolderConfig.class).storageFolder().set(dataFolder);
		my(FolderConfig.class).tmpFolder().set(tmpFolder);
		my(FolderConfig.class).ownBinFolder().set(ownBin);
		my(FolderConfig.class).platformSrcFolder().set(platformSrcFolder);
		my(FolderConfig.class).platformBinFolder().set(platformBinFolder);
	}


	private void startSnapps() {
		my(SnappStarter.class).startSnapps(); //Will find only the Snapps installed (Freedom7) in the local test ownBin and platformBin folders, not the ones in the classpath. 
		
		startAndKeep(SocketOriginator.class);
		startAndKeep(SocketReceiver.class);
		startAndKeep(ProbeManager.class);

		startAndKeep(TupleLogger.class);

		startAndKeep(Wind.class);

		startAndKeep(FileServer.class);
		startAndKeep(BrickSpace.class);

		startAndKeep(Stethoscope.class);
		startAndKeep(Heart.class);
	}

	private void startAndKeep(Class<?> snapp) {
		_referenceToAvoidGc.add(my(snapp));
	}

	@Override
	public boolean isOnline(String nickname) {
		Contact contact = my(ContactManager.class).contactGiven(nickname);
		return isAlive(contact).currentValue();
	}

	private void accelerateHeartbeat() {
		my(Threads.class).startStepping(new Runnable() { @Override public void run() {
			my(Clock.class).advanceTime(1000);
			my(Threads.class).sleepWithoutInterruptions(20);
		}});
	}

	@Override
	public void waitForAvailableBrick(final String brickName, final String brickStatus) {
		my(Logger.class).log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Waiting for brick: " + brickName + " status: " + brickStatus);

		final Latch latch = my(Latches.class).newLatch();
		
		WeakContract contract = my(BrickSpace.class).newBrickConfigurationFound().addReceiver(new Consumer<Seal>() { @Override public void consume(Seal publisher) {
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
	public void stageBricksForInstallation(String... brickNames) throws IOException, JavaCompilerException {
		for (String brickName : brickNames) stageBrickForInstallation(brickName);

		copyNecessaryClassFilesToTestPlatformBin();
		my(BrickInstaller.class).prepareStagedBricksInstallation();
	}

	private void copyNecessaryClassFilesToTestPlatformBin() throws IOException {
		copyNecessaryClassToTestPlatformBin(Brick.class);
		copyNecessaryClassToTestPlatformSrc(Snapp.class);
		new File(testPlatformBin(), "sneer/main").mkdir();
		new File(testPlatformBin(), "sneer/tests").mkdir();
	}

	private void copyNecessaryClassToTestPlatformBin(Class<?> clazz) throws IOException {
		String classFileName = my(ClassUtils.class).relativeClassFileName(clazz);
		my(IO.class).files().copyFile(
			new File(platformBin()    , classFileName),
			new File(testPlatformBin(), classFileName)
		);
	}

	private void copyNecessaryClassToTestPlatformSrc(Class<?> clazz) throws IOException {
		String classFileName = my(ClassUtils.class).relativeJavaFileName(clazz);
		my(IO.class).files().copyFile(
			new File(platformSrc()    , classFileName),
			new File(testPlatformSrc(), classFileName)
		);
	}

	private File platformBin() {
		return my(ClassUtils.class).classpathRootFor(getClass());
	}

	private File platformSrc() {
		return new File(platformBin().getParent(), "src");
	}

	private File testPlatformBin() {
		return my(FolderConfig.class).platformBinFolder().get();
	}

	private File testPlatformSrc() {
		return my(FolderConfig.class).platformSrcFolder().get();
	}

	private void stageBrickForInstallation(String brickName) {
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
		commitStagedBricksInstallation();
		
		startSnapps();
		accelerateHeartbeat();
	}

	private void commitStagedBricksInstallation() {
		try {
			my(BrickInstaller.class).commitStagedBricksInstallation();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	

	@Override
	public void copyToSourceFolder(File folderWithBricks) throws IOException {
		
		my(IO.class).files().copyFolder(folderWithBricks, platformSrcFolder());
	}

	private File platformSrcFolder() {
		return my(FolderConfig.class).platformSrcFolder().get();
	}

	private String print(Seal seal) {
		return seal.equals(my(Seals.class).ownSeal())
			? "myself"
			: my(Seals.class).contactGiven(seal).nickname().toString();
	}
}

