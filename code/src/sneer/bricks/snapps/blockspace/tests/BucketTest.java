package sneer.bricks.snapps.blockspace.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.After;
import org.junit.Test;

import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.snapps.blockspace.BlockNumberOutOfRange;
import sneer.bricks.snapps.blockspace.BlockSpace;
import sneer.bricks.snapps.blockspace.Bucket;

public class BucketTest extends BrickTestWithTuples {

	private static final int BLOCK_SIZE = 8 * 1024;
	private static final byte[] BLANK_BLOCK = new byte[BLOCK_SIZE];

	
	private final Bucket _subject = my(BlockSpace.class).localBucket();

	
	@Test (expected = BlockNumberOutOfRange.class)
	public void readInAVacuum() throws Exception {
		_subject.read(42);
	}

	
	@Test (expected = BlockNumberOutOfRange.class)
	public void writeInAVacuum() throws Exception {
		_subject.write(42, new byte[]{ 0, 1, 2 });
	}

	
	@Test
	public void readWithoutWrite() throws Exception {
		_subject.setSize(1);
		assertArrayEquals(BLANK_BLOCK, _subject.read(0));

		_subject.setSize(42);
		assertArrayEquals(BLANK_BLOCK, _subject.read(41));
	}

	
	@Test
	public void write() throws Exception {
		_subject.setSize(10);
		_subject.write(7, new byte[] { 0, 1, 2 });
		
		byte[] block = _subject.read(7);
		assertStartsWith(new byte[] { 0, 1, 2 }, block);
		assertIsPaddedWithZeros(block);
	}


	private void assertIsPaddedWithZeros(byte[] block) {
		for (int i = 3; i < block.length; i++)
			assertEquals(0, block[i]);
	}

	
	@Test
	public void resizing() throws Exception {
		_subject.setSize(10);
		_subject.write(7, new byte[] { 42 });

		_subject.setSize(20);

		byte[] block = _subject.read(7);
		assertStartsWith(new byte[] { 42 }, block);
	}

	
	@After
	public void afterWackupTest() {
		_subject.crash();
	}

}
