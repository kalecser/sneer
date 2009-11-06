package sneer.foundation.brickness;

import java.util.List;

public interface LoadTimeNature extends Nature {

	List<ClassDefinition> realize(ClassDefinition classDef);

}
