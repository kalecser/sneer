package sneer.bricks.expression.files.hasher.impl;

import static basis.environments.Environments.my;

import java.io.UnsupportedEncodingException;

import sneer.bricks.expression.files.hasher.FolderContentsHasher;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Digester;
import sneer.bricks.hardware.cpu.crypto.Hash;

class FolderContentsHasherImpl implements FolderContentsHasher {

	@Override
	public Hash hash(FolderContents folder) {
		Digester digester = my(Crypto.class).newDigester();
		for (FileOrFolder entry : folder.contents)
			digester.update(hash(entry).bytes.copy());
		return digester.digest();
	}

	private static Hash hash(FileOrFolder entry) {
		Digester digester = my(Crypto.class).newDigester();
		digester.update(bytesUtf8(entry.name));
		digester.update(entry.hashOfContents.bytes.copy());
		return digester.digest();
	}
	
	private static byte[] bytesUtf8(String string) {
		try {
			return string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

}
