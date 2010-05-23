package sneer.bricks.software.bricks.interception;

import java.util.List;

import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.ClassDefinition;

@Brick
public interface InterceptionEnhancer {

	List<ClassDefinition> realize(Class<?> targetBrick, Class<? extends Interceptor> interceptorBrick, ClassDefinition classDef);

}