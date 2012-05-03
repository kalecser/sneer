package sneer.bricks.expression.files.transfer.gui.impl;

import static basis.environments.Environments.my;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static javax.swing.JFileChooser.FILES_AND_DIRECTORIES;

import java.io.File;

import javax.swing.JOptionPane;

import sneer.bricks.expression.files.transfer.FileTransfer;
import sneer.bricks.expression.files.transfer.FileTransferSugestion;
import sneer.bricks.expression.files.transfer.downloadfolder.DownloadFolder;
import sneer.bricks.expression.files.transfer.gui.FileTransferGui;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.skin.filechooser.FileChoosers;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import basis.lang.Consumer;

public class FileTransferGuiImpl implements FileTransferGui {

	@SuppressWarnings("unused")
	private WeakContract ref;

	{
		ref = my(FileTransfer.class).registerHandler(new Consumer<FileTransferSugestion>() { @Override public void consume(FileTransferSugestion sugestion) {
			if (JOptionPane.showConfirmDialog(null, "Do you want to download " + sugestion.fileOrFolderName + "?", "Download",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
				download(sugestion);
		}});
		
		my(ContactActionManager.class).addContactAction(new ContactAction(){
			@Override public boolean isEnabled() { return true; }
			@Override public boolean isVisible() { return true; }
			@Override public String caption() { return "Send File or Folder";}
			@Override public void run() {
				chooseFileToSend();
			}
			
			@Override public int positionInMenu() { return 200; }
		});
	}

	private void chooseFileToSend() {
		my(FileChoosers.class).choose(FILES_AND_DIRECTORIES, null, new Consumer<File>() { @Override public void consume(File fileOrFolder) {
			my(FileTransfer.class).tryToSend(fileOrFolder, selectedSeal());
		}});
	}

	
	private void download(final FileTransferSugestion sugestion) {
		String downloadFolderPath = my(Attributes.class).myAttributeValue(DownloadFolder.class).currentValue();
		if (isDirectory(downloadFolderPath)) {
			my(FileTransfer.class).accept(sugestion);
			return;
		}
			
		my(FileChoosers.class).choose(DIRECTORIES_ONLY, null, new Consumer<File>() { @Override public void consume(File folder) {
			if (folder == null) return;
			my(Attributes.class).myAttributeSetter(DownloadFolder.class).consume(folder.getAbsolutePath());
			my(FileTransfer.class).accept(sugestion);
		}});
	}


	private boolean isDirectory(String path) {
		if (path == null) return false;
		return new File(path).isDirectory();
	}


	private Seal selectedSeal() {
		return my(ContactSeals.class).sealGiven(selectedContact()).currentValue();
	}

	private Contact selectedContact() {
		return my(ContactsGui.class).selectedContact().currentValue();
	}
}
