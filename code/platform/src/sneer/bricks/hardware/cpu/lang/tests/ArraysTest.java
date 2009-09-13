package sneer.bricks.hardware.cpu.lang.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class ArraysTest extends BrickTest{

	
	@Test
	public void testMerge(){
		assertMerge(new byte[]{0,1,2},new byte[][]{{0},{1,2}});
		assertMerge(new byte[]{0,1,2},new byte[][]{{0},{}, {1}, {2}});
		assertMerge(new byte[]{0,1,2},new byte[][]{{0, 1,2}});
	}

	private void assertMerge(byte[] expected, byte[][] subject) {
		assertArrayEquals(expected, my(Lang.class).arrays().merge(subject));
	}
	
}
