package sneer.bricks.softwaresharing.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriter;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriters;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.testsupport.BrickTest;

public class BrickPublishingTest extends BrickTest {

//	private final BrickSpace _spaceSubject = my(BrickSpace.class);
//	private final BrickPublisher _publisherSubject = my(BrickPublisher.class);

	@Ignore
	@Test
	public void brickPublishing() throws IOException {
		generateY(null);
//		
//		neide = newSession(neide);
//		
//		neide.waitForAvailableBrick("freedom7.y.Y", "CURRENT");

		fail();
	}

	private void generateY(File srcFolder) throws IOException {
		JavaSourceWriter writer = my(JavaSourceWriters.class).newInstance(srcFolder);
		writer.write("freedom7.y.Y",
				"@" + Brick.class.getName() + " " +
				"@" + Snapp.class.getName() + " " +
				"public interface Y {}");
		writer.write("freedom7.y.impl.YImpl",
				"class YImpl implements freedom7.y.Y {\n" +
					"public YImpl() {\n" +
						"System.setProperty(\"freedom7.y.Y.installed\", \"true\");\n" +
					"}" +
				"}");
	}
	
	
	

}