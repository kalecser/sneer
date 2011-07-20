package sneer.bricks.hardwaresharing.backup.kernel.impl;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardwaresharing.backup.kernel.SnackupKernel;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.lang.Pair;

class SnackupKernelImpl implements SnackupKernel {

	@Override
	public void peerFileConsideredImportant(Contact contact,
			String relativePath, Hash hashOfFile, long sizeOfFile) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void peerFileDeleted(Contact contact, String relativePath) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public ListSignal<Pair<Hash, Long>> peerFilesToBeDownloaded() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void peerFileReceived(Hash hash) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public ListSignal<Hash> peerFilesToForget() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void peerFileForgotten(Hash hash) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void ownFileConsideredImportant(String relativePath,
			Hash hashOfFile, long sizeOfFile, long lastModified) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public CollectionSignal<ImportantFile> ownFilesConsideredImportant() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void ownFileDeleted(String relativePath) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void ownFileReceivedByPeer(Contact peer, String relativePath,
			Hash hashOfFile) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void ownFileForgottenByPeer(Contact peer, Hash hashOfFile) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public float ownFilesRedundancy() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

}
