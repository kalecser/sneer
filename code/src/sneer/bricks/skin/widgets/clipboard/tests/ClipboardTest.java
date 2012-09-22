package sneer.bricks.skin.widgets.clipboard.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.skin.widgets.clipboard.Clipboard;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class ClipboardTest extends BrickTestBase {

	private final Clipboard subject = my(Clipboard.class);
	
	@Test
	public void setAndGetContents() {
		String content = "someString";
		subject.setContent(content);
		assertEquals(content, subject.getContent());
	}
}
