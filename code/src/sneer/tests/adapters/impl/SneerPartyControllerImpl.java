package sneer.tests.adapters.impl;

import static basis.environments.Environments.my;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.expression.tuples.logger.TupleLogger;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.hardware.ram.iterables.Iterables;
import sneer.bricks.hardwaresharing.backup.Snackup;
import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.addresses.own.port.OwnPort;
import sneer.bricks.network.computers.connections.ConnectionManager;
import sneer.bricks.network.computers.http.server.tests.HttpServerTest;
import sneer.bricks.network.computers.udp.holepuncher.server.listener.StunServerListener;
import sneer.bricks.network.computers.udp.server.UdpServer;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.rendezvous.Rendezvous;
import sneer.bricks.pulp.blinkinglights.*;
import sneer.bricks.pulp.probe.ProbeManager;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.web.Web;
import sneer.bricks.snapps.wind.Wind;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.*;
import sneer.bricks.softwaresharing.git.tests.GitTest;
import sneer.bricks.softwaresharing.mapper.RepositoryMapper;
import sneer.bricks.softwaresharing.stager.BrickStager;
import sneer.bricks.softwaresharing.stager.tests.BrickStagerTest;
import sneer.main.SneerVersionUpdater;
import sneer.tests.SovereignParty;
import sneer.tests.adapters.*;
import basis.lang.*;
import basis.lang.arrays.ImmutableByteArray;
import basis.lang.exceptions.NotImplementedYet;
import basis.lang.exceptions.Refusal;
import basis.util.concurrent.Latch;

class SneerPartyControllerImpl implements SneerPartyController, SneerParty {

	static private final String MOCK_ADDRESS = "localhost";
	
	private Collection<Object> _refToAvoidGc = new ArrayList<Object>();
	private File _codeFolder;	
	private String _nameOfExpectedCaller;

	private Web web;
	
	private void createDefaultImportantFolder() {
		File folder = new File(my(FolderConfig.class).tmpFolder().get(), "ImportantFolder");
		folder.mkdirs();
		setFolderToSync(folder);
	}
		
	@Override
	public void setSneerPort(int port) {
		my(Attributes.class).myAttributeSetter(OwnPort.class).consume(port);
	}

	
	@Override //This is a comment
	public void startConnectingTo(SneerParty other) {
		Contact contact = produceContact(other.ownName());

		putSeal(other, contact);
		try {
			my(InternetAddressKeeper.class).put(contact, MOCK_ADDRESS, other.sneerPort());
		} catch (Refusal e) {
			throw new IllegalStateException(e);
		}
	}


	@Override
	public void acceptConnectionFrom(String otherName) {
		if (_nameOfExpectedCaller != null) throw new IllegalStateException();
		_nameOfExpectedCaller = otherName;
		approveConnectionRequestsIfAny();
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
		return my(Attributes.class).myAttributeValue(OwnName.class).currentValue();
	}

	
	@Override
	public void setOwnName(String newName) {
		my(Attributes.class).myAttributeSetter(OwnName.class).consume(newName);
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

	@Override
	public Collection<String> contactNicknames() {
		return my(CollectionUtils.class).map(my(Contacts.class).contacts().currentElements(), new Functor<Contact, String>() {  @Override public String evaluate(Contact value) {
			return value.nickname().currentValue();
		}});
	}

    
	private Signal<Boolean> isAlive(Contact contact) {
		return my(ConnectionManager.class).connectionFor(contact).isConnected();
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
        return my(Attributes.class).myAttributeValue(OwnPort.class).currentValue();
    }

	
	@Override
	public void shout(String phrase) {
		my(Wind.class).megaphone().consume(phrase);
	}

	
	@Override
	public void waitForShouts(final String shoutsExpected) {
		final Latch latch = new Latch();

		WeakContract contract = my(Wind.class).shoutsHeard().addPulseReceiver(new Closure() { @Override public void run() {
			openLatchIfShoutsHeard(shoutsExpected, latch);
		}});
		openLatchIfShoutsHeard(shoutsExpected, latch);
		
		latch.waitTillOpen();
		contract.dispose();
	}

	
	private void openLatchIfShoutsHeard(String shoutsExpected, Latch latch) {
		String shoutsHeard = concat(my(Wind.class).shoutsHeard());
		if (shoutsHeard.equals(shoutsExpected))
			latch.open();
	}

	
	private String concat(Iterable<ChatMessage> shouts) {
		List<ChatMessage> sorted = my(Iterables.class).sortByToString(shouts);
		return my(Lang.class).strings().join(sorted, ", ");
	}

	
	@Override
	public void configDirectories(File dataFolder, File tmpFolder, File codeFolder, File srcFolder, File binFolder, File stageFolder, Path gitFolder) {
		my(FolderConfig.class).storageFolder().set(dataFolder);
		my(FolderConfig.class).tmpFolder().set(tmpFolder);
		my(FolderConfig.class).srcFolder().set(srcFolder);
		my(FolderConfig.class).binFolder().set(binFolder);
		
		my(FolderConfig.class).stageFolder().set(stageFolder);

		my(FolderConfig.class).gitFolder().set(gitFolder);
		_codeFolder = codeFolder;
	}

	
	private void startSnapps() {
		startAndKeep(JavaCompiler.class);
		
		startAndKeep(ProbeManager.class);
		startAndKeep(Rendezvous.class);

		startAndKeep(TupleLogger.class);

		startAndKeep(Wind.class);

		startAndKeep(FileServer.class);
	}

	
	private void startAndKeep(Class<?> snapp) {
		_refToAvoidGc.add(my(snapp));
	}


	@Override
	public void loadUnsharedBrick(String brickName) {
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
		return isAlive(contactGiven(nickname)).currentValue();
	}
	
	@Override
	public boolean isContact(String nickname) {
		return contactGiven(nickname) != null;
	}


	private Contact contactGiven(String nickname) {
		return my(Contacts.class).contactGiven(nickname);
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

		final Latch latch = new Latch();
		
		WeakContract contract = my(BrickSpace.class).newBuildingFound().addReceiver(new Consumer<Seal>() { @Override public void consume(Seal publisher) {
			my(Logger.class).log(">>>>>New brick configuration found for: " + print(publisher));

//			if (my(BrickSpace.class).availableBricks().isEmpty()) throw new IllegalStateException("There are no available bricks.");

			if (isBrickAvailable(brickName, brickStatus)) latch.open();
		}});
		if (isBrickAvailable(brickName, brickStatus)) latch.open();

		latch.waitTillOpen();
		contract.dispose();
	}

	
	private boolean isBrickAvailable(final String brickName, final String brickStatus) {
		for (BrickHistory brickInfo : my(BrickSpace.class).availableBricks()) {
			my(Logger.class).log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Brick found: " + brickInfo.name() + " status: " + brickInfo.status());
			if (brickInfo.name().equals(brickName)
				&& brickInfo.status().currentValue().name().equals(brickStatus))
				return true;
		};
		return false;
	}
	
	
	@Override
	public void stageBricksForInstallation(String... brickNames) {
		for (String brickName : brickNames)
			setChosenForExecution(brickName);
		
		my(BrickStager.class).stageBricksForInstallation();
	}

	
	@Override
	public void enableCodeSharing() {
		try {
			copyRepositoryCode();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		startAndKeep(BrickSpace.class);
	}

	
	private void copyRepositoryCode() throws IOException {
		my(Logger.class).log("Copying necessary repository code...");

		BrickStagerTest.copyBrickBaseToSrcFolder();
		BrickStagerTest.copyClassesToSrcFolder(
			sneer.main.Sneer.class,
			sneer.main.SneerVersionUpdater.class,
			sneer.main.SneerFolders.class,
			sneer.main.SneerCodeFolders.class
		);
		
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

	
	private File repositoryBinFolder() {
		return my(ClassUtils.class).classpathRootFor(getClass());
	}

	
	private void setChosenForExecution(String brickName) {
		final BrickHistory brick = availableBrick(brickName);
		final BrickVersion singleVersion = singleVersionOf(brick);
		brick.setChosenForExecution(singleVersion, true);
	}

	
	private BrickVersion singleVersionOf(BrickHistory brick) {
		if (brick.versions().size() != 1)
			throw new IllegalStateException();
		return brick.versions().get(0);
	}

	
	private BrickHistory availableBrick(String brickName) {
		for (BrickHistory brick : my(BrickSpace.class).availableBricks())
			if (brick.name().equals(brickName))
				return brick;
		throw new IllegalArgumentException();
	}

	
	@Override
	public	void crash() {

		if (web != null) {
			web.crash();
		}
		my(UdpServer.class).crash();
		my(Threads.class).crashAllThreads();
	}

	
	@Override
	public void start(String name, int port) {
		generatePublicKey(name);

		throwOnBlinkingErrors();
		installStagedCodeIfNecessary();

		setOwnName(name);
		setSneerPort(port);

		createDefaultImportantFolder();
		startSnapps();
		startApprovingConnectionRequests();
		accelerateHeartbeat();
	}
	
	private void startApprovingConnectionRequests() {
		_refToAvoidGc.add(my(BlinkingLights.class).lights().addPulseReceiver(new Closure() {@Override public void run() {
			approveConnectionRequestsIfAny();
		}}));
	}
	
	
	private void runAcceptAction(Light light) {
		for (Action action : light.actions()) {
			if (action.caption().equals("Accept")) {
				action.run();
				return;
			}
		}
		throw new IllegalStateException("Accept action not found.");
	}

	private void generatePublicKey(String name) {
		if (my(OwnKeys.class).ownPublicKey().currentValue() != null) return;
		my(OwnKeys.class).generateKeyPair(pkSeedFrom(name));
	}

	private void approveConnectionRequestsIfAny() {
		for (Light light : my(BlinkingLights.class).lights())
			if (light.caption().currentValue().startsWith(_nameOfExpectedCaller + " wants to connect to you")) {
				runAcceptAction(light);
				_nameOfExpectedCaller = null;
			}
	}
	
	private void installStagedCodeIfNecessary() {
		File stageFolder = my(FolderConfig.class).stageFolder().get();
		try {
			String backupLabel = "" + System.currentTimeMillis();
			SneerVersionUpdater.installNewVersionIfPresent(stageFolder , backupLabel, _codeFolder);
		} catch (IOException e) {
			throw new IllegalStateException(e); //We have to treat this.
		}
	}

	private byte[] pkSeedFrom(String name) {
		try {
			return name.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}


	
	private void throwOnBlinkingErrors() {
		_refToAvoidGc.add(my(BlinkingLights.class).lights().addReceiver(new Consumer<CollectionChange<Light>>() { @Override public void consume(CollectionChange<Light> value) {
			for (Light l : value.elementsAdded())
				if (l.type() == LightType.ERROR)
					throw new IllegalStateException("ERROR blinking light detected", l.error());
		}}));
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


	@Override
	public void setFolderToSync(File folder) {
		my(Snackup.class).folderToSyncSetter().consume(folder.getAbsolutePath());
		if (folderToSync() == null)
			throw new IllegalStateException();
	}


	@Override
	public void waitForSync() {
		my(SignalUtils.class).waitForValue(my(Snackup.class).isSynced(), true);
	}


	@Override
	public void recoverFileFromBackup(String fileName) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}


	@Override
	public void lendSpaceTo(String contactNick, int megaBytes) {
		Contact contact = contactGiven(contactNick);
		try {
			my(Snackup.class).lendSpaceTo(contact, megaBytes);
		} catch (Refusal e) {
			throw new IllegalStateException(e);
		}
	}

	
	@Override
	public File folderToSync() {
		String currentValue = my(Snackup.class).folderToSync().currentValue();
		return currentValue == null ? null : new File(currentValue);
	}

	
	@Override
	public void startStunServer() {
		my(StunServerListener.class);
	}

	@Override
	public void gitPrepareEmptyRepo() {
		try {
			GitTest.prepareEmptyRepo(my(FolderConfig.class).gitFolder().get());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	
	@Override
	public void gitPrepareRepoWithOneCommit() {
		try {
			GitTest.prepareRepoWithOneCommit(my(FolderConfig.class).gitFolder().get());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		startAndKeep(RepositoryMapper.class);
	}

	
	@Override
	public void gitPullFrom(String contactNickname) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	@Override
	public boolean gitHasOneCommit() {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void startHomePage() {
		web = my(Web.class);
	}

	@Override
	public String wgetOrCry(String url) {
		try {
			return HttpServerTest.replyFor(Web.PORT, url);
		} catch (Exception e) {
			throw new basis.lang.exceptions.NotImplementedYet(e);
		}
	}

	@Override
	public void startHomePage(int port) {
		System.setProperty("sneer.web.port", ""+port);
		my(Web.class);
	}

}

