package sneer.bricks.softwaresharing.demolisher.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.IO.Files;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriter;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriters;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;
import sneer.bricks.softwaresharing.demolisher.Demolisher;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.ConsumerX;

public class DemolisherTest extends BrickTestBase {

	private Demolisher _subject = my(Demolisher.class);
	
	@Test
	public void testUsers() throws Exception {
		
		File buildingA = newTmpFile("buildinga");
		writeBrick("Brick1", buildingA);
		writeBrick("Brick2", buildingA);
		writeBrick("Brick4", buildingA);
		
		File buildingB = newTmpFile("buildingb");
		writeBrick("Brick2", buildingB);
		writeBrick("Brick4", buildingB);
		
		File buildingC = newTmpFile("buildingc");
		writeBrick("Brick3", buildingC);
		writeBrick("Brick4", buildingC);
		
		File buildingD = newTmpFile("buildingd");
		writeBrick("Brick3", buildingD);
		writeBrick("Brick4", buildingD);
		
		Contact userB = my(Contacts.class).produceContact("Bruno");
		Contact userC = my(Contacts.class).produceContact("Carlos");
		Contact userD = my(Contacts.class).produceContact("Daniel");
		
		CacheMap<String, BrickHistory> result =  demolishOwnBuilding(buildingA);
		demolishBuildingInto(result, buildingB, userB);
		demolishBuildingInto(result, buildingC, userC);
		demolishBuildingInto(result, buildingD, userD);
		
		assertBrickUsers(result, "Brick1");
		assertBrickUsers(result, "Brick2", userB);
		assertBrickUsers(result, "Brick3", userC, userD);
		assertBrickUsers(result, "Brick4", userB, userC, userD);
	}

	private void assertBrickUsers(CacheMap<String, BrickHistory> result, String brickName, Contact...users) {
		BrickHistory brickHistory = result.get(qualifiedBrickName(brickName));
		assertNotNull(brickHistory);
		
		assertContents(brickHistory.versions().get(0).users(), users);
	}

	@Test
	public void testStatus() throws Exception {
		
		runStatusTest(null);
	}
	
	@Test
	public void testStatusWithSparseMapping() throws Exception {
		
		runStatusTest(new ConsumerX<File, Exception>() {  @Override public void consume(File value) throws Exception {
			File brick = new File(value, "brick1/Brick1.java");
			
			File foo = createTmpFile("foo");
			files().copyFile(brick, foo);
			mapFileOrFolder(foo);
			
			assertTrue(brick.delete());
		}});
	}

	private void runStatusTest(ConsumerX<File, Exception> mess) throws Exception {
		File buildingA = newTmpFile("buildinga");
		writeBrick("Brick1", buildingA);
		createTmpFile("buildinga/brick1/MISSING.txt");
		createTmpFile("buildinga/brick1/CURRENT.txt", "current");
		createTmpFile("buildinga/brick1/DIFFERENT.txt", "current");
		
		File buildingB = newTmpFile("buildingb");
		writeBrick("Brick1", buildingB);
		createTmpFile("buildingb/brick1/EXTRA.txt");
		createTmpFile("buildingb/brick1/CURRENT.txt", "current");
		createTmpFile("buildingb/brick1/DIFFERENT.txt", "different");
		
		Contact userB = my(Contacts.class).produceContact("Bruno");
		
		Hash buildingAHash = mapFileOrFolder(buildingA);
		if (mess != null) mess.consume(buildingA);		
		CacheMap<String, BrickHistory> result =  _subject.demolishOwnBuilding(buildingAHash);
		demolishBuildingInto(result, buildingB, userB);
		
		assertEquals(1, result.size());
		
		List<BrickVersion> versions = result.get("brick1.Brick1").versions();
		assertEquals(2, versions.size());
		
		BrickVersion versionA = versions.get(0);
		BrickVersion versionB = versions.get(1);
		
		assertSame(BrickVersion.Status.CURRENT, versionA.status().currentValue());
		assertSame(BrickVersion.Status.DIFFERENT, versionB.status().currentValue());
		
		assertSame(FileVersion.Status.CURRENT, versionA.file("MISSING.txt").status());
		assertSame(FileVersion.Status.CURRENT, versionA.file("CURRENT.txt").status());
		assertSame(FileVersion.Status.CURRENT, versionA.file("DIFFERENT.txt").status());
		
		assertSame(FileVersion.Status.EXTRA, versionB.file("EXTRA.txt").status());
		assertSame(FileVersion.Status.MISSING, versionB.file("MISSING.txt").status());
		assertSame(FileVersion.Status.CURRENT, versionB.file("CURRENT.txt").status());
		assertSame(FileVersion.Status.DIFFERENT, versionB.file("DIFFERENT.txt").status());
	}
	
	private void writeBrick(String brickName, File srcFolder)
			throws IOException {
		JavaSourceWriter aWriter = my(JavaSourceWriters.class).newInstance(srcFolder);
		aWriter.write(qualifiedBrickName(brickName), "@" + Brick.class.getName() + " public interface " + brickName + " {}");
	}

	private String qualifiedBrickName(String brickName) {
		return brickName.toLowerCase() + "." + brickName;
	}

	private void demolishBuildingInto(CacheMap<String, BrickHistory> result, File building, Contact owner) throws Exception {
		Hash buildingAHash = mapFileOrFolder(building);
		_subject.demolishBuildingInto(result, buildingAHash, owner);
	}
	
	private CacheMap<String, BrickHistory> demolishOwnBuilding(File building) throws Exception {
		Hash buildingAHash = mapFileOrFolder(building);
		return _subject.demolishOwnBuilding(buildingAHash);
	}

	private Hash mapFileOrFolder(File fileOrFolder) throws Exception {
		return my(FileMapper.class).mapFileOrFolder(fileOrFolder);
	}

	private Files files() {
		return my(IO.class).files();
	}
}
