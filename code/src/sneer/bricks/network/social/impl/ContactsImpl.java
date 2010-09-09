package sneer.bricks.network.social.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.io.log.stacktrace.StackTraceLogger;
import sneer.bricks.hardware.io.prevalence.map.PrevalenceMap;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.lang.PickyConsumer;
import sneer.foundation.lang.exceptions.Refusal;

class ContactsImpl implements Contacts {
    
    private final SetRegister<Contact> _contacts = my(CollectionSignals.class).newSetRegister();
    
	
	@Override
	synchronized
	public Contact addContact(String nickname) throws Refusal {
		nickname.toString();
		checkAvailability(nickname);
		return doAddContact(nickname);
	}

	
	private Contact doAddContact(String nickname) {
		if (nickname.equals("Dummy")) my(StackTraceLogger.class).logStack();
		
		final Contact result = new ContactImpl(nickname);
		my(PrevalenceMap.class).register(result);
		_contacts.add(result);
		return result;
	}
	
	private void checkAvailability(String nickname) throws Refusal {
		if (isNicknameAlreadyUsed(nickname))
			throw new Refusal("Nickname " + nickname + " is already being used.");
	}
	
	@Override
	public SetSignal<Contact> contacts() {
		return _contacts.output();
	}

	
	@Override
	synchronized
	public boolean isNicknameAlreadyUsed(String nickname) {
		return contactGiven(nickname) != null;
	}

	
	@Override
	synchronized
	public Contact contactGiven(String nickname) {
		for (Contact candidate : contacts())
			if (candidate.nickname().currentValue().equals(nickname))
				return candidate;

		return null;
	}

	
	@Override
	synchronized
	public void removeContact(Contact contact) {
		_contacts.remove(contact);
	}
	
	
	@Override
	public PickyConsumer<String> nicknameSetterFor(final Contact contact) {
		return new PickyConsumer<String>() { @Override public void consume(String newNickname) throws Refusal {
			checkAvailability(newNickname);
			((ContactImpl)contact).setNickname(newNickname);
		}};
	}

	
	@Override
	synchronized
	public Contact produceContact(String nickname) {
		Contact result = contactGiven(nickname);
		if (result == null)
			result = doAddContact(nickname);

		return result;
	}

}
