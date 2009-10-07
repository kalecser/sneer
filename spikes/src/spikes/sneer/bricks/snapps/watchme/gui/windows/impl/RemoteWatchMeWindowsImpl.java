package spikes.sneer.bricks.snapps.watchme.gui.windows.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.HashMap;
import java.util.Map;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.ContactManager;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.foundation.lang.Consumer;
import spikes.sneer.bricks.snapps.watchme.gui.windows.RemoteWatchMeWindows;

class RemoteWatchMeWindowsImpl implements RemoteWatchMeWindows {

	private static final ContactManager ContactManager = my(ContactManager.class);

	
	private final Map<Contact, WatchMeReceiver> _remoteReceivers = new HashMap<Contact, WatchMeReceiver>();
	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;

	
	{
		_refToAvoidGc = ContactManager.contacts().addReceiver(new Consumer<CollectionChange<Contact>>(){ @Override public void consume(CollectionChange<Contact> change) {
			for (Contact contact : change.elementsAdded()  ) startReceivingScreensFrom(contact);			
			for (Contact contact : change.elementsRemoved())  stopReceivingScreensFrom(contact);
		}});
	}

	
	public void startReceivingScreensFrom(Contact contact) {
		WatchMeReceiver receiver = new WatchMeReceiver(contact);
		_remoteReceivers.put(contact, receiver);
	}

	
	public void stopReceivingScreensFrom(Contact contact) {
		WatchMeReceiver receiver = _remoteReceivers.get(contact);
		_remoteReceivers.remove(contact);
		receiver.dispose();
	}
}