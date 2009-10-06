package sneer.bricks.hardwaresharing.files.map.tests;

import org.junit.Ignore;

import sneer.bricks.software.folderconfig.tests.BrickTest;

@Ignore
public class FileReaderBigFilesTest extends BrickTest {

	/*
	private final FileReader _subject = my(FileReader.class);
	
	@Ignore
	@Test
	public void readBigFileToTheCache() throws IOException{
		testReadBigFileToTheCache(FileMap.FILE_BLOCK_SIZE + 1);
		testReadBigFileToTheCache(FileMap.FILE_BLOCK_SIZE * 200);
		testReadBigFileToTheCache(FileMap.FILE_BLOCK_SIZE * BigFileBlocks.NUMBER_OF_BLOCKS + 1); 
	}
	

	private void testReadBigFileToTheCache(int size) throws IOException {
		File originalFile = generateRandomFile(size);
		Sneer1024 read = _subject.readIntoTheFileCache(originalFile);
		
		BigFileBlocks blocks = (BigFileBlocks) my(FileMap.class).getContents(read);
		File reintegratedFromCache = new File(tmpFolder(), "file.reintegrated");
		unpackContentsToFile(blocks, reintegratedFromCache);
		
		my(IO.class).files().assertSameContents(originalFile, reintegratedFromCache);
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

	
	private void unpackContentsToFile(BigFileBlocks blocks, File file) throws IOException {
		FileOutputStream stream = new FileOutputStream(file);
		try{
			unpack(blocks._contents, stream);
		}finally{
			stream.close();			
		}
	}
	
	
	private void unpack(ImmutableArray<Sneer1024> immutableArray, FileOutputStream stream) throws IOException {
		for (Sneer1024 hash : immutableArray){
			Object contents = my(FileMap.class).getContents(hash);
			if (contents instanceof BigFileBlocks){
				unpack(((BigFileBlocks) contents)._contents, stream);
			} else {
				stream.write((byte[])contents);
			}
		}
	}
*/
}
