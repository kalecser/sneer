package sneer.bricks.network.social.attributes;

import basis.brickness.Brick;
import basis.lang.Consumer;
import basis.lang.PickyConsumer;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.SetSignal;

@Brick
public interface Attributes {

	void registerAttribute(Class<? extends Attribute<?>> newAttribute);
	SetSignal<Class<? extends Attribute<?>>> all();

	<T> Consumer<T> myAttributeSetter(Class<? extends Attribute<T>> attribute);
	<T> Signal<T> myAttributeValue(Class<? extends Attribute<T>> attribute);

	<T> PickyConsumer<T> attributeSetterFor(Contact contact, Class<? extends Attribute<T>> attribute);
	<T> Signal<T> attributeValueFor(Contact contact, Class<? extends Attribute<T>> attribute, Class<T> valueType);

}
