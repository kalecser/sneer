package sneer.bricks.expression.files.map.visitors.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.visitors.FileMapGuide;
import sneer.bricks.expression.files.map.visitors.FolderStructureVisitor;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class FileMapGuideTest extends BrickTestBase {

	private final FileMapGuide _subject = my(FileMapGuide.class);
	private final FileMap _fileMap = my(FileMap.class);
	
	@Test
	public void sparseMappingTour() throws Exception {
		
		mapFolder("a1/b", 1);
		mapFolder("a1/b/c", 2);
		
		mapFolder("a2/b", 1);
		
		mapFolder("a3/b", 1);
		mapFolder("a3/b/d", 3);
		
		assertTour("a2/b", "c", "d");
	}

	private void assertTour(String path, String... expected) throws IOException {
		
		final List<String> tour = new ArrayList<String>();
		_subject.guide(new FolderStructureVisitor() {
			
			@Override
			public boolean visitFileOrFolder(String name, long lastModified, Hash hashOfContents) {
				tour.add(name);
				return true;
			}
			
			@Override
			public void visitFileContents(byte[] fileContents) {
				throw new IllegalStateException();
			}
			
			@Override
			public void leaveFolder() {
			}
			
			@Override
			public void enterFolder() {
			}
		}, _fileMap.getFolderContents(_fileMap.getHash(path)));
		
		assertContents(tour, expected);
	}

	private void mapFolder(String path, int hash) {
		_fileMap.putFolder(path, hash(hash));
	}

	private Hash hash(int b) {
		return my(Crypto.class).digest(new byte[] { (byte) b });
	}
}
