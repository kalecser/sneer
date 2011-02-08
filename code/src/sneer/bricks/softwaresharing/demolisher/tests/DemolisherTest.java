package sneer.bricks.softwaresharing.demolisher.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriter;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriters;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;
import sneer.bricks.softwaresharing.demolisher.Demolisher;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.CacheMap;

public class DemolisherTest extends BrickTestBase {

	private Demolisher _subject = my(Demolisher.class);

	@Test
	public void test() throws Exception {
		
		File buildingA = newTmpFile("buildinga");
		writeBrick(buildingA);
		createTmpFile("buildinga/brick1/MISSING.txt");
		createTmpFile("buildinga/brick1/CURRENT.txt", "current");
		createTmpFile("buildinga/brick1/DIFFERENT.txt", "current");
		
		File buildingB = newTmpFile("buildingb");
		writeBrick(buildingB);
		createTmpFile("buildingb/brick1/EXTRA.txt");
		createTmpFile("buildingb/brick1/CURRENT.txt", "current");
		createTmpFile("buildingb/brick1/DIFFERENT.txt", "different");
		
		CacheMap<String, BrickHistory> result = CacheMap.newInstance();
		demolishBuildingInto(result, buildingA, true);
		demolishBuildingInto(result, buildingB, false);
		
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
	
	private void writeBrick(File buildingA) throws IOException {
		JavaSourceWriter aWriter = my(JavaSourceWriters.class).newInstance(buildingA);
		aWriter.write("brick1.Brick1", "@" + Brick.class.getName() + " public interface Brick1 {}");
	}

	private void demolishBuildingInto(CacheMap<String, BrickHistory> result,
			File building, boolean isMyOwn) throws MappingStopped, IOException {
		Hash buildingAHash = my(FileMapper.class).mapFileOrFolder(building);
		_subject.demolishBuildingInto(result, buildingAHash, isMyOwn);
	}
}
