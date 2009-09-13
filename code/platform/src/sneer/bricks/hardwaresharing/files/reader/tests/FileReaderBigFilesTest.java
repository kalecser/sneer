package sneer.bricks.hardwaresharing.files.reader.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardwaresharing.files.cache.FileCache;
import sneer.bricks.hardwaresharing.files.protocol.BigFileBlocks;
import sneer.bricks.hardwaresharing.files.reader.FileReader;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class FileReaderBigFilesTest extends BrickTest {

	private final FileReader _subject = my(FileReader.class);
	
	@Ignore
	@Test
	public void readBigFileToTheCache() throws IOException{
		testReadBigFileToTheCache(FileReader.MAXIMUM_FILE_BLOCK_SIZE + 1);
		testReadBigFileToTheCache(FileReader.MAXIMUM_FILE_BLOCK_SIZE * 200);
		testReadBigFileToTheCache(FileReader.MAXIMUM_FILE_BLOCK_SIZE * BigFileBlocks.NUMBER_OF_BLOCKS + 1); 

	}
	

	private void testReadBigFileToTheCache(int size) throws IOException,
			FileNotFoundException {
		File originalFile = generateRandomFile(size);
		Sneer1024 read = _subject.readIntoTheFileCache(originalFile);
		
		BigFileBlocks blocks = (BigFileBlocks) my(FileCache.class).getContents(read);
		File reintegratedFromCache = new File(tmpFolder(), "file.reintegrated");
		unpackContentsToFile(blocks, reintegratedFromCache);
		
		assertEquals(FileUtils.checksumCRC32(originalFile), FileUtils.checksumCRC32(reintegratedFromCache));
	}

	private File generateRandomFile(int size) throws IOException {
		
		File file = new File(tmpFolder(), "randomfile.rnd");
		
		byte[] buffy = new byte[size];
		Random rnd = new Random();
		rnd.nextBytes(buffy);

		FileOutputStream output = new FileOutputStream (file);
		try{
			output.write(buffy);
		} finally {
			output.close();
		}
		
		return file;
	}

	private void unpackContentsToFile(BigFileBlocks blocks, File file)
			throws FileNotFoundException, IOException {
		FileOutputStream str = new FileOutputStream(file);
		try{
			unpack(blocks._contents, str);
		}finally{
			str.close();			
		}
	}
	
	private void unpack(ImmutableArray<Sneer1024> immutableArray, FileOutputStream str) throws IOException {
		for (Sneer1024 hash : immutableArray){
			Object contents = my(FileCache.class).getContents(hash);
			if (contents instanceof BigFileBlocks){
				unpack(((BigFileBlocks) contents)._contents, str);
			} else {
				str.write((byte[])contents);
			}
		}
	}

}
