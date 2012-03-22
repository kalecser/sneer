package sneer.bricks.software.bricks.interception;

import java.util.List;

import basis.brickness.Brick;
import basis.brickness.ClassDefinition;


@Brick
public interface InterceptionEnhancer {

	List<ClassDefinition> realize(Class<?> targetBrick, Class<? extends Interceptor> interceptorBrick, ClassDefinition classDef);

}