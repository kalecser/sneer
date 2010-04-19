package sneer.bricks.hardware.ram.arrays.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.testsupport.AssertUtils;

public class ImmutableArrayTest extends BrickTest {

	@Test
	public void iterable() {
		final ImmutableArray<Integer> array = my(ImmutableArrays.class).newImmutableArray(Arrays.asList(1, 2, 3));
		AssertUtils.assertSameContents(array, 1, 2, 3);
	}

	@Test
	public void immutable() {		
		final List<Integer> original = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
		final ImmutableArray<Integer> array = my(ImmutableArrays.class).newImmutableArray(original);

		original.add(4);
		AssertUtils.assertSameContents(array, 1, 2, 3);
	}

	@Test
	public void immutableUsingAlternateConstructor() {		
		Integer[] original = new Integer[] {1,2,3};
		final ImmutableArray<Integer> array = my(ImmutableArrays.class).newImmutableArray(original);

		original[2] = 4;
		AssertUtils.assertSameContents(array, 1, 2, 3);
	}

	@Test
	public void toArray() {
		Integer[] original = new Integer[] {1,2,3};
		final ImmutableArray<Integer> array = my(ImmutableArrays.class).newImmutableArray(original);
		Assert.assertArrayEquals(original, array.toArray());

		array.toArray()[2] = 4;
		Assert.assertArrayEquals(original, array.toArray());
	}

}
