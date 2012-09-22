package sneer.bricks.skin.widgets.clipboard.impl;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import sneer.bricks.skin.widgets.clipboard.Clipboard;

public class ClipboardImpl implements Clipboard {

	java.awt.datatransfer.Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	@Override
	public void setContent(String content) {
		StringSelection stringSelection = new StringSelection(content);
		ClipboardOwner owner = null;
		systemClipboard.setContents(stringSelection, owner);
	}

	@Override
	public String getContent() {
		Transferable contents = systemClipboard.getContents(null);
		return readStringContent(contents);
	}

	private String readStringContent(Transferable contents) {
		if (contents == null)
			return "";
		
		try {
			return (String) contents.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception ex) {
			return "";
		}
		
	}

}
