package sneer.bricks.hardwaresharing.files.server.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.BigFileBlocks;
import sneer.bricks.hardwaresharing.files.protocol.FileContents;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.hardwaresharing.files.server.FileServer;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
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
		try {
			reply(request);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error trying to reply FileServer request: " + request, "This might indicate a problem with your file device.", e, 30000);
		}
	}


	private void reply(FileRequest request) throws IOException {
		Object response = getContents(request);
		
		if (response == null) {
			my(Logger.class).log("FileCache miss.");
			return;
		}
		
		Tuple reply = asTuple(response);
		my(TupleSpace.class).publish(reply);

		logFolderActivity(reply);

	}


	private void logFolderActivity(Tuple reply) {
		if (reply instanceof FolderContents) {
			my(Logger.class).log("Sending Folder Contents:");
			for (FileOrFolder fileOrFolder : ((FolderContents)reply).contents)
				my(Logger.class).log("   FileOrFolder: {} date: {} hash: {}", fileOrFolder.name, fileOrFolder.lastModified, fileOrFolder.hashOfContents);
		}
	}


	private Object getContents(FileRequest request) {
		Object response = my(FileMap.class).getFile(request.hashOfContents);
		return response == null
			? my(FileMap.class).getFolder(request.hashOfContents)
			: response;
	}


	private Tuple asTuple(Object response) throws IOException {
		if (response instanceof FolderContents)
			return new FolderContents(((FolderContents)response).contents);
		
		if (response instanceof File)
			return asFileContents((File)response);
		
		if (response instanceof BigFileBlocks)
			return new BigFileBlocks(((BigFileBlocks)response)._contents);
			
		throw new IllegalStateException("I don know how to obtain a tuple from type: " + response.getClass());
	}


	private FileContents asFileContents(File file) throws IOException {
		byte[] bytes = my(IO.class).files().readBytes(file);
		return new FileContents(my(ImmutableArrays.class).newImmutableByteArray(bytes));
	}

	

}