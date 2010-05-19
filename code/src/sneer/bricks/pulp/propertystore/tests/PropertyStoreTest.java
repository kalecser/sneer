package sneer.bricks.pulp.propertystore.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.pulp.propertystore.PropertyStore;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class PropertyStoreTest extends BrickTestWithFiles {

	@Test
	public void testPropertyStore() {
		runInNewEnvironment(new Closure() { @Override public void run() {
			PropertyStore subject1 = my(PropertyStore.class);
			assertNull(subject1.get("Height"));
			subject1.set("Height", "1,80m");
			subject1.set("Weight", "85kg");
			assertEquals("1,80m", subject1.get("Height"));
			assertEquals("85kg", subject1.get("Weight"));
		}});

		runInNewEnvironment(new Closure() { @Override public void run() {
			PropertyStore subject2 = my(PropertyStore.class);
			assertEquals("1,80m", subject2.get("Height"));
			assertEquals("85kg", subject2.get("Weight"));
		}});
	}

	private void runInNewEnvironment(Closure closure) {
		Environment newTestEnvironment = newTestEnvironment(my(FolderConfig.class));
		Environments.runWith(newTestEnvironment, closure);
		crash(newTestEnvironment);
	}

}
