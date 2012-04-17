package sneer.bricks.expression.files.transfer.ui.impl;

import static basis.environments.Environments.my;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import sneer.bricks.expression.files.transfer.FileTransfer;
import sneer.bricks.expression.files.transfer.FileTransferSugestion;
import sneer.bricks.expression.files.transfer.ui.FileTransferUi;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.skin.filechooser.FileChoosers;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import basis.lang.Consumer;

public class FileTransferUiImpl implements FileTransferUi, Consumer<File> {

	@SuppressWarnings("unused")
	private WeakContract ref;

	{
		ref = my(FileTransfer.class).registerHandler(new Consumer<FileTransferSugestion>() {
			@Override
			public void consume(FileTransferSugestion sugestion) {
				if (JOptionPane.showConfirmDialog(null, "Do you want to download " + sugestion.fileOrFolderName + "?", "Download",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
					my(FileTransfer.class).accept(sugestion);
			}
		});
		my(ContactActionManager.class).addContactAction(new ContactAction(){
			@Override public boolean isEnabled() { return true; }
			@Override public boolean isVisible() { return true; }
			@Override public String caption() { return "Send Files...";}
			@Override public void run() {
				openFileChooser();
			}
			
			@Override public int positionInMenu() { return 200; }
		});
	}

	private void openFileChooser() {
		my(FileChoosers.class).choose(this, JFileChooser.FILES_AND_DIRECTORIES, null);
	}

	@Override
	public void consume(File fileOrFolder) {
		my(FileTransfer.class).tryToSend(fileOrFolder, selectedSeal());
	}

	private Seal selectedSeal() {
		return my(ContactSeals.class).sealGiven(selectedContact()).currentValue();
	}

	private Contact selectedContact() {
		return my(ContactsGui.class).selectedContact().currentValue();
	}
}
