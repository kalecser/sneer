package sneer.bricks.skin.widgets.clipboard.tests;

import static basis.environments.Environments.my;

import java.awt.GraphicsEnvironment;

import org.junit.Test;

import sneer.bricks.skin.widgets.clipboard.Clipboard;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class ClipboardTest extends BrickTestBase {

	@Test
	public void setAndGetContents() {
		if (GraphicsEnvironment.isHeadless()) return;
		Clipboard subject = my(Clipboard.class);
		String content = "someString";
		subject.setContent(content);
		assertEquals(content, subject.getContent());
	}
}
