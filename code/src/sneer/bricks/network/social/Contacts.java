package sneer.bricks.network.social;

import basis.brickness.Brick;
import basis.lang.PickyConsumer;
import basis.lang.exceptions.Refusal;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.hardware.io.prevalence.nature.Transaction;
import sneer.bricks.pulp.reactive.collections.SetSignal;

@Brick(Prevalent.class)
public interface Contacts {
    
	SetSignal<Contact> contacts();

	Contact contactGiven(String nickname);
	boolean isNicknameAlreadyUsed(String nickname);
	
	/** @throws Refusal if there already is a Contact with that nickname.*/
	@Transaction
	Contact addContact(String nickname) throws Refusal;
	
	/** Returns a contact with the given nickname. Creates a new one if there was no contact with that nickname before. */
	@Transaction
	Contact produceContact(String nickname);
	
	PickyConsumer<String> nicknameSetterFor(Contact contact);
	
	void removeContact(Contact contact);

}