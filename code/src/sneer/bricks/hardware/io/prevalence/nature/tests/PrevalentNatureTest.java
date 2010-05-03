package sneer.bricks.hardware.io.prevalence.nature.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.io.prevalence.map.ExportMap;
import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.Item;
import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.SomePrevalentBrick;
import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.brick2.PrevalentBrick2;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class PrevalentNatureTest extends BrickTest {
	
	
	@Test (timeout = 3000)
	public void stateIsPreserved() {
		my(SomePrevalentBrick.class).set("foo");
		assertEquals("foo", my(SomePrevalentBrick.class).get());

		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertEquals("foo", my(SomePrevalentBrick.class).get());
		}});
	}

	
	@Test (timeout = 2000)
	public void nullParameter() {
		my(SomePrevalentBrick.class).set("foo");
		my(SomePrevalentBrick.class).set(null);

		assertNull(my(SomePrevalentBrick.class).get());
	}

	
	@Test (timeout = 3000)
	public void multipleBricks() {
		my(SomePrevalentBrick.class).addItem("Foo");
		my(PrevalentBrick2.class).rememberItemCount();
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertEquals(1, my(PrevalentBrick2.class).recallItemCount());
		}});
	}
	
	
	@Test (timeout = 3000)
	public void brickCommandCausesAnotherBrickInstantiation() {
		my(PrevalentBrick2.class).rememberItemCount();
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertEquals(0, my(PrevalentBrick2.class).recallItemCount());
		}});
	}
	
	
	@Test (timeout = 3000)
	public void baptismProblem() {
		SomePrevalentBrick brick = my(SomePrevalentBrick.class);
		brick.addItem("Foo");
		assertEquals(1, brick.itemCount());
		brick.addItem("Bar");
		assertEquals(2, brick.itemCount());
		
		Item item = brick.getItem("Foo");
		brick.removeItem(item);
		assertEquals(1, brick.itemCount());
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertEquals(1, my(SomePrevalentBrick.class).itemCount());
		}});
	}
	
	
	@Test (timeout = 3000)
	public void bubbleExpandsToQueriedValues() {
		SomePrevalentBrick brick = my(SomePrevalentBrick.class);
		brick.addItem("Foo");
		
		Item item = brick.getItem("Foo");
		item.name("Bar");
			
		assertNull(brick.getItem("Foo"));
		assertSame(item, brick.getItem("Bar"));
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertNotNull(my(SomePrevalentBrick.class).getItem("Bar"));
		}});
	}

	
	@Test (timeout = 3000)
	public void queriesThatReturnUnregisteredObjectsAreAssumedToBeIdempotent() {
		SomePrevalentBrick brick = my(SomePrevalentBrick.class);
		brick.itemAdder_Idempotent().consume("Foo");
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertNotNull(my(SomePrevalentBrick.class).getItem("Foo"));
		}});
	}
	
	@Test (timeout = 3000)
	public void idempotencyIsTransitive() {
		SomePrevalentBrick brick = my(SomePrevalentBrick.class);
		brick.itemAdder_Idempotent_Transitive().setter().consume("Foo");
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertNotNull(my(SomePrevalentBrick.class).getItem("Foo"));
		}});
	}

	
	@Test (timeout = 3000)
	public void transactionAnnotation() {
		SomePrevalentBrick brick = my(SomePrevalentBrick.class);

		Item item = brick.addItem_AnnotatedAsTransaction("Foo");
		item.name("Bar");
			
		assertNull(brick.getItem("Foo"));
		assertSame(item, brick.getItem("Bar"));
		
		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertNotNull(my(SomePrevalentBrick.class).getItem("Bar"));
		}});
	}

	
	@Test (timeout = 3000)
	public void objectsReturnedByTransactionsAreRegistered() {
		Item item = my(SomePrevalentBrick.class).addItem_AnnotatedAsTransaction("Foo");
		assertTrue("Item should be registered.", my(ExportMap.class).isRegistered(item));
	}

	
	@Test (timeout = 3000)
	public void invocationPathWithArgs() {
		my(SomePrevalentBrick.class).addItem("foo");
		my(SomePrevalentBrick.class).addItem("bar");
		Item item = my(SomePrevalentBrick.class).getItem("foo");
		Closure remover = my(SomePrevalentBrick.class).removerFor(item);
		remover.run();

		runInNewTestEnvironment(new Closure() { @Override public void run() {
			assertEquals(1, my(SomePrevalentBrick.class).itemCount());
			assertNotNull(my(SomePrevalentBrick.class).getItem("bar"));
		}});
	}


	private void runInNewTestEnvironment(Closure closure) {
		Environments.runWith(newTestEnvironment(my(FolderConfig.class)), closure);
	}

}
