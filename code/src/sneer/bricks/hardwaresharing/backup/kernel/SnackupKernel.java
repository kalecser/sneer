package sneer.bricks.hardwaresharing.backup.kernel;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Pair;
import sneer.foundation.lang.exceptions.Refusal;

@Brick
public interface SnackupKernel {

	public interface ImportantFile {

	}

	// estou emprestando tantos mega para fulano (int)
	// HardDriveMegabytesLent
	void lendSpaceTo(Contact contact, int megaBytes) throws Refusal;
	
	// fulano acha tal arquivo importante (relativePath, hash, size)
	void peerFileConsideredImportant(Contact contact, String relativePath, Hash hashOfFile, long sizeOfFile);
	
	// fulano deletou arquivo importante (relativePath)
	void peerFileDeleted(Contact contact, String relativePath);
	
	// quais são os arquivos que devo baixar de fulanos? (hash, size)
	ListSignal<Pair<Hash, Long>> peerFilesToBeDownloaded();
	
	// recebi arquivo de fulano (hash)
	void peerFileReceived(Hash hash);
	
	// quais os arquivos baixados que se tornaram desnecessários? (hash)
	ListSignal<Hash> peerFilesToForget();
	
	// arquivo desnecessário deletado (hash)
	void peerFileForgotten(Hash hash);
	
	// ----------------------------------------------------
	
	// fulano está emprestando tantos mega para mim (int)
	// HardDriveMegabytesLent
	
	// tal arquivo mudado ou novo é importante para mim (relativePath, hash, lastModified)
	void ownFileConsideredImportant(String relativePath, Hash hashOfFile, long sizeOfFile, long lastModified);
	
	// quais são meus arquivos importantes? (relativePath, hash, sizeOfFile, lastModified)
	CollectionSignal<ImportantFile> ownFilesConsideredImportant();
	
	// tal arquivo importante foi deletado (relativePath)
	void ownFileDeleted(String relativePath);
	
	// fulano recebeu meu arquivo importante (relativePath, hash)
	void ownFileReceivedByPeer(Contact peer, String relativePath, Hash hashOfFile);
	
	// fulano removeu meu arquivo importante (hash)
	void ownFileForgottenByPeer(Contact peer, Hash hashOfFile);
	
	// qual é a redundância dos meus arquivos? (float)
	float ownFilesRedundancy();
	
}
