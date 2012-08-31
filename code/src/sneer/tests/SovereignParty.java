package sneer.tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.software.code.compilers.CompilerException;

public interface SovereignParty {

	//Freedom1
	String ownName();
	void setOwnName(String newName);
	byte[] seal();

	//Freedom2
	void giveNicknameTo(SovereignParty peer, String nickname);
	Collection<String> contactNicknames();
	boolean isContact(String nickname);
	boolean isOnline(String nickname);
	void waitUntilOnline(String nickname);
	void navigateAndWaitForName(String nicknamePath, String expectedName);

	//Freedom5
	void shout(String string);
	void waitForShouts(String shoutsExpected);
	Channel openControlChannel(String contactNick);

	//Freedom6
	void setFolderToSync(File folder);
	File folderToSync();
	void lendSpaceTo(String contactNick, int megaBytes);
	void waitForSync();
	void recoverFileFromBackup(String fileName);
	
	//Freedom7
	void enableCodeSharing();
	void waitForAvailableBrick(String brickName, String brickStatus);
	void stageBricksForInstallation(String... brickNames) throws IOException, CompilerException;
	void copyToSourceFolder(File folderWithBricks) throws IOException;
	void loadUnsharedBrick(String brickName);

	//Freedom7 Git
	void gitPrepareEmptyRepo();
	void gitPrepareRepoWithOneCommit();
	void gitPullFrom(String contactNickname);
	boolean gitHasOneCommit();

}