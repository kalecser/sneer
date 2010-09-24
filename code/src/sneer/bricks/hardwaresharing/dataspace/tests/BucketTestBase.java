package sneer.bricks.hardwaresharing.dataspace.tests;

import org.junit.After;
import org.junit.Test;

import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardwaresharing.dataspace.BlockNumberOutOfRange;
import sneer.bricks.hardwaresharing.dataspace.Bucket;

public abstract class BucketTestBase extends BrickTestWithTuples {

	private static final int BLOCK_SIZE = 8 * 1024;
	private static final byte[] BLANK_BLOCK = new byte[BLOCK_SIZE];

	
	private final Bucket _subject = subject();

	
	@Test (expected = BlockNumberOutOfRange.class, timeout = 2000)
	public void readInAVacuum() throws Exception {
		_subject.read(42);
	}

	
	abstract protected Bucket subject();

	@Test (expected = BlockNumberOutOfRange.class, timeout = 2000)
	public void writeInAVacuum() throws Exception {
		_subject.write(42, new byte[]{ 0, 1, 2 });
	}

	
	@Test (timeout = 2000)
	public void readWithoutWrite() throws Exception {
		_subject.setSize(1);
		assertArrayEquals(BLANK_BLOCK, _subject.read(0));

		_subject.setSize(42);
		assertArrayEquals(BLANK_BLOCK, _subject.read(41));
	}

	
	@Test (timeout = 2000)
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

	
	@Test (timeout = 2000)
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
