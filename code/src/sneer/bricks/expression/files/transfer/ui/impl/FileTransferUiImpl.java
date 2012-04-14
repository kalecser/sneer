package sneer.bricks.expression.files.transfer.ui.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.files.transfer.FileTransferSugestion;
import sneer.bricks.expression.files.transfer.ui.FileTransferUi;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.skin.filechooser.FileChoosers;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import basis.lang.Consumer;

public class FileTransferUiImpl implements FileTransferUi, Consumer<File> {

	{
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
		my(TupleSpace.class).add(new FileTransferSugestion(fileOrFolder.getName(), selectedSeal()));
		try {
			my(FileMapper.class).mapFileOrFolder(fileOrFolder);
		} catch (MappingStopped mse) {
			//ignored
		} catch (IOException ioe) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error reading " + fileOrFolder, "This might indicate a problem with your harddrive", ioe);
		}
	}

	private Seal selectedSeal() {
		return my(ContactSeals.class).sealGiven(selectedContact()).currentValue();
	}

	private Contact selectedContact() {
		return my(ContactsGui.class).selectedContact().currentValue();
	}
}
