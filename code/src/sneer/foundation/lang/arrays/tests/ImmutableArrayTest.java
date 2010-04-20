package sneer.foundation.lang.arrays.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.arrays.ImmutableArray;
import sneer.foundation.testsupport.AssertUtils;

public class ImmutableArrayTest extends BrickTest {

	@Test
	public void iterable() {
		final ImmutableArray<Integer> array = new ImmutableArray<Integer>(Arrays.asList(1, 2, 3));
		AssertUtils.assertSameContents(array, 1, 2, 3);
	}

	@Test
	public void immutable() {		
		final List<Integer> original = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
		final ImmutableArray<Integer> array = new ImmutableArray<Integer>(original);

		original.add(4);
		AssertUtils.assertSameContents(array, 1, 2, 3);
	}

	@Test
	public void immutableUsingAlternateConstructor() {		
		Integer[] original = new Integer[] {1,2,3};
		final ImmutableArray<Integer> array = new ImmutableArray<Integer>(original);

		original[2] = 4;
		AssertUtils.assertSameContents(array, 1, 2, 3);
	}

	@Test
	public void toArray() {
		Integer[] original = new Integer[] {1,2,3};
		final ImmutableArray<Integer> array = new ImmutableArray<Integer>(original);
		Assert.assertArrayEquals(original, array.toArray());

		array.toArray()[2] = 4;
		Assert.assertArrayEquals(original, array.toArray());
	}

}
