package sneer.bricks.hardware.ram.collections;

import java.util.Collection;

import basis.brickness.Brick;
import basis.lang.Functor;
import basis.lang.Predicate;


@Brick
public interface CollectionUtils {
	
    <I,O> Collection<O> map(Collection<I> inputCollection, Functor<? super I, ? extends O> functor);
    <T> Collection<T> filter(Collection<T> inputCollection, Predicate<T> predicate);
    
}
