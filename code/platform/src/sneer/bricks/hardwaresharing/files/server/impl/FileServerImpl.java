package sneer.bricks.hardwaresharing.files.server.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardwaresharing.files.cache.FileCache;
import sneer.bricks.hardwaresharing.files.protocol.BigFileBlocks;
import sneer.bricks.hardwaresharing.files.protocol.FileContents;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.hardwaresharing.files.server.FileServer;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.brickness.Tuple;
import sneer.foundation.lang.Consumer;

public class FileServerImpl implements FileServer, Consumer<FileRequest> {
	
	
	@SuppressWarnings("unused") private final WeakContract _fileRequestContract;
	
	
	{
		_fileRequestContract = my(TupleSpace.class).addSubscription(FileRequest.class, this);
	}
	
	
	@Override
	public void consume(FileRequest request) {
		reply(request);
	}


	private void reply(FileRequest request) {
		final Object response = my(FileCache.class).getContents(request.hashOfContents);
		if (response == null) {
			my(Logger.class).log("FileCache miss.");
			return;
		}
		
		Tuple reply = asTuple(response);
		my(TupleSpace.class).publish(reply);

		if (reply instanceof FolderContents) {
			my(Logger.class).log("Sending Folder Contents:");
			for (FileOrFolder fileOrFolder : ((FolderContents)reply).contents)
				my(Logger.class).log("   FileOrFolder: {} date: {} hash: {}", fileOrFolder.name, fileOrFolder.lastModified, fileOrFolder.hashOfContents);
				
		}

	}


	private Tuple asTuple(Object response) {

		if (response == null)
			throw new IllegalArgumentException("response must not be null");
		
		if (response instanceof FolderContents)
			return new FolderContents(((FolderContents)response).contents);
		
		if (response instanceof byte[])
			return asFileContents((byte[])response);
		
		if (response instanceof BigFileBlocks)
			return new BigFileBlocks(((BigFileBlocks)response)._hash);
			
		throw new IllegalStateException("I don know how to obtain a tuple from type: " + response.getClass());
	}


	private FileContents asFileContents(byte[] contents) {
		return new FileContents(my(ImmutableArrays.class).newImmutableByteArray(contents));
	}

	

}