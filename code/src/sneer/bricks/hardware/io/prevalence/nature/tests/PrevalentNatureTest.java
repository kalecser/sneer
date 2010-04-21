package sneer.bricks.hardware.io.prevalence.nature.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.Item;
import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.SomePrevalentBrick;
import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.brick2.PrevalentBrick2;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class PrevalentNatureTest extends BrickTest {
	
	Environment subject = Brickness.newBrickContainer();
	
	@Test (timeout = 3000)
	public void stateIsPreserved() {
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			my(SomePrevalentBrick.class).set("foo");
			assertEquals("foo", my(SomePrevalentBrick.class).get());
		}});

		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertEquals("foo", my(SomePrevalentBrick.class).get());
		}});
	}
	
	@Test (timeout = 3000)
	public void multipleBricks() {
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			my(SomePrevalentBrick.class).addItem("Foo");
			my(PrevalentBrick2.class).rememberItemCount();
		}});
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertEquals(1, my(PrevalentBrick2.class).recallItemCount());
		}});
	}
	
	@Test (timeout = 3000)
	public void brickCommandCausesAnotherBrickInstantiation() {
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			my(PrevalentBrick2.class).rememberItemCount();
		}});
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertEquals(0, my(PrevalentBrick2.class).recallItemCount());
		}});
	}
	
	@Ignore
	@Test (timeout = 2000)
	public void baptismProblem() {
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			SomePrevalentBrick brick = my(SomePrevalentBrick.class);
			brick.addItem("Foo");
			assertEquals(1, brick.itemCount());
			
			Item item = brick.getItem("Foo");
			brick.removeItem(item);
			assertEquals(0, brick.itemCount());
		}});
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertEquals(0, my(SomePrevalentBrick.class).itemCount());
		}});
	}

	private void runInNewTestEnvironment(Closure closure) {
		Environments.runWith(newTestEnvironment(my(FolderConfig.class)), closure);
	}

}
