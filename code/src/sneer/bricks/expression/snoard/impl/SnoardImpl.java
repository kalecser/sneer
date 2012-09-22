package sneer.bricks.expression.snoard.impl;

import static basis.environments.Environments.my;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import sneer.bricks.expression.snoard.Snoard;
import sneer.bricks.expression.snoard.SnoardTuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import basis.lang.Consumer;

public class SnoardImpl implements Snoard, ClipboardOwner {
	public SnoardImpl() {
		my(ContactActionManager.class).addContactAction(new ContactAction() {

			@Override
			public String caption() {
				return "Send clipboard";
			}

			@Override
			public void run() {
				sendClipboardContents(my(ContactsGui.class).selectedContact()
						.currentValue());
			}

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public int positionInMenu() {
				return 1;
			}
		});

		my(TupleSpace.class).addSubscription(SnoardTuple.class,
				new Consumer<SnoardTuple>() {

					@Override
					public void consume(SnoardTuple value) {
						//System.out.println(value.clipValue);
						setClipboardContents((String) value.clipValue);
					}
				});
	}

	protected void sendClipboardContents(Contact contact) {
		SnoardTuple t = new SnoardTuple(getClipboardContents(), my(ContactSeals.class)
				.sealGiven(contact).currentValue());
		my(TupleSpace.class).add(t);
	}

	public String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
			
		boolean hasTransferableText = (contents != null)
				&& contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex) {
				// highly unlikely since we are using a standard DataFlavor
				System.out.println(ex);
				ex.printStackTrace();
			} catch (IOException ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return result;
	}

	public void setClipboardContents(String aString) {
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// ignoring...
	}

}
