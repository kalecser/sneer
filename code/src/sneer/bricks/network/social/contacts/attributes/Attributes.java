package sneer.bricks.network.social.contacts.attributes;

import sneer.bricks.network.social.contacts.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick
public interface Attributes {

	<T> Signal<T> myAttributeValue(Class<? extends Attribute<T>> attribute);

	<T> Consumer<T> myAttributeSetter(Class<? extends Attribute<T>> attribute);

	<T> Signal<T> attributeValueFor(Contact contact, Class<? extends Attribute<T>> attribute);

}
