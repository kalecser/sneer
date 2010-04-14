package sneer.bricks.software.bricks.statestore.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.software.bricks.statestore.BrickStateStore;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class BrickStateStoreTest extends BrickTest {
	
	private final BrickStateStore _subject = my(BrickStateStore.class);

	@Ignore
	@Test
	public void writeAndReadBrickState() throws Exception {
		List<Object> toStore = new ArrayList<Object>();
		toStore.add("0");
		toStore.add("1");
		toStore.add("2");
		toStore.add(null);

		_subject.writeObjectFor(BrickStateStore.class, toStore);
		List<Object> restored = (List<Object>) _subject.readObjectFor(BrickStateStore.class, _subject.getClass().getClassLoader());
		
		assertEquals(toStore.size(), restored.size());
		assertTrue(toStore != restored);
		
		assertEquals("2", restored.get(2));
		System.out.println(restored.get(3));
		assertNull(restored.get(3));
	}
}

