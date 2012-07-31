package sneer.bricks.expression.files.server.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.protocol.FileContents;
import sneer.bricks.expression.files.protocol.FileContentsFirstBlock;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FileRequest;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.expression.files.protocol.Protocol;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import basis.lang.Consumer;
import basis.lang.arrays.ImmutableByteArray;

public class FileServerImpl implements FileServer, Consumer<FileRequest> {

	@SuppressWarnings("unused") private final WeakContract _fileRequestContract;


	{
		_fileRequestContract = my(RemoteTuples.class).addSubscription(FileRequest.class, this);
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
		Tuple response = createResponseFor(request);
		if (response == null) return;
		my(TupleSpace.class).add(response);
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
		
		try {
			return my(FileMapper.class).getExistingMappedFile(request.hashOfContents);
		} catch (FileNotFoundException e) {
			return null;
		}
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
		Integer blockNumber = request.blockNumbers.toArray()[0];
		if (requestedFile.length() > 0)
			bytes = getFileBlockBytes(requestedFile, blockNumber);

		String debugInfo = requestedFile.getName();
		assertExists(requestedFile);
		
		FileContents fileContents = blockNumber == 0
			? new FileContentsFirstBlock(request.publisher, request.hashOfContents, requestedFile.length(), bytes, debugInfo)
			: new FileContents			(request.publisher, request.hashOfContents, blockNumber, bytes, debugInfo);
		log(fileContents);
		return fileContents;
	}


	private void assertExists(File requestedFile) throws IOException {
		if (!requestedFile.exists())
			throw new IOException("File to be uploaded does not exist: " + requestedFile);
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
