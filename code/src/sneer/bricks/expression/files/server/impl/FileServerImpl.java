package sneer.bricks.expression.files.server.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileContents;
import sneer.bricks.expression.files.protocol.FileContentsFirstBlock;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FileRequest;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.expression.files.protocol.Protocol;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.arrays.ImmutableByteArray;

public class FileServerImpl implements FileServer, Consumer<FileRequest> {

	@SuppressWarnings("unused") private final WeakContract _fileRequestContract;


	{
		_fileRequestContract = my(TupleSpace.class).addSubscription(FileRequest.class, this);
	}


	@Override
	public void consume(FileRequest request) {
		try {
			tryToReply(request);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error trying to reply FileServer request: " + request, "This might indicate a problem with your file device.", e, 30000);
		}
	}


	private void tryToReply(FileRequest request) throws IOException {
		if (isFromMyself(request)) return;

		Tuple response = createResponseFor(request);
		if (response == null) return;
		my(TupleSpace.class).acquire(response);
	}


	private boolean isFromMyself(FileRequest request) {
		return my(OwnSeal.class).get().currentValue().equals(request.publisher);
	}


	private Tuple createResponseFor(FileRequest request) throws IOException {
		Object response = getContents(request);

		if (response == null) {
			my(Logger.class).log("FileCache miss.");
			return null;
		}

		return createTuple(response, request);
	}


	private Object getContents(FileRequest request) {
		Object response = my(FileMap.class).getFolderContents(request.hashOfContents);
		if (response != null) return response;
		
		String path = my(FileMap.class).getFile(request.hashOfContents);
		return path == null ? null : new File(path);
	}


	private Tuple createTuple(Object response, FileRequest request) throws IOException {
		if (response instanceof FolderContents)
			return newFolderContents(response);

		if (response instanceof File) {
			return newFileContents((File)response, request);
		}

		throw new IllegalStateException("I dont know how to obtain a tuple from type: " + response.getClass());
	}


	private Tuple newFolderContents(Object response) {
		FolderContents folderContents = new FolderContents(((FolderContents)response).contents);
		log(folderContents);
		return folderContents;
	}


	private FileContents newFileContents(File requestedFile, FileRequest request) throws IOException {
		ImmutableByteArray bytes = null;
		if (requestedFile.length() > 0)
			bytes = getFileBlockBytes(requestedFile, request.blockNumber);

		String debugInfo = requestedFile.getName();
		FileContents fileContents = request.blockNumber == 0
			? new FileContentsFirstBlock(request.publisher, request.hashOfContents, requestedFile.length(), bytes, debugInfo)
			: new FileContents			(request.publisher, request.hashOfContents, request.blockNumber, 	bytes, debugInfo);
		log(fileContents);
		return fileContents;
	}


	private ImmutableByteArray getFileBlockBytes(File file, int blockNumber) throws IOException {
		try {
			byte[] result = my(IO.class).files().readBlock(file, blockNumber, Protocol.FILE_BLOCK_SIZE);
			return new ImmutableByteArray(result);
		} catch(IOException ioe) {
			throw new IOException("Error trying to read block " + blockNumber + " from requested file: " + file, ioe);
		}
	}


	private void log(FolderContents reply) {
		my(Logger.class).log("Sending Folder Contents:");
		for (FileOrFolder fileOrFolder : reply.contents)
			my(Logger.class).log("   FileOrFolder: {} date: {} hash: {}", fileOrFolder.name, fileOrFolder.lastModified, fileOrFolder.hashOfContents);
	}

	private void log(FileContents reply) {
		my(Logger.class).log("Sending File Contents --> File: {} block: {} hash: {} addressee: ", reply.debugInfo, reply.blockNumber, reply.hashOfFile, reply.addressee);
	}

}
