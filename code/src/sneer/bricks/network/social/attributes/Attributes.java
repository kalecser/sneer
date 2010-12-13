package sneer.bricks.network.social.attributes;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.PickyConsumer;

@Brick
public interface Attributes {

	void registerAttribute(Attribute<?> newAttribute);
	SetSignal<Attribute<?>> myAttributes();

	<T> Consumer<T> myAttributeSetter(Class<? extends Attribute<T>> attribute);
	<T> Signal<T> myAttributeValue(Class<? extends Attribute<T>> attribute);

	<T> PickyConsumer<T> attributeSetterFor(Contact contact, Class<? extends Attribute<T>> attribute);
	<T> Signal<T> attributeValueFor(Contact contact, Class<? extends Attribute<T>> attribute, Class<T> valueType);

}
