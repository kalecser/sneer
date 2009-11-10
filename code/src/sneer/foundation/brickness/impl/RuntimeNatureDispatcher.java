package sneer.foundation.brickness.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;

import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.Nature;
import sneer.foundation.brickness.RuntimeNature;
import sneer.foundation.brickness.RuntimeNature.Continuation;
import sneer.foundation.lang.exceptions.NotImplementedYet;

public class RuntimeNatureDispatcher {

	public static Object dispatch(Class<?> brick,
			RuntimeNature[] natures,
			Object instance,
			String methodName,
			Object[] args, Continuation continuation) {
		
		if (natures.length > 1)
			throw new NotImplementedYet();
		return natures[0].invoke(brick, instance, methodName, args, continuation);
	}
	

	public static RuntimeNature[] runtimeNaturesFor(Class<?> brick) {
		ArrayList<RuntimeNature> result = new ArrayList<RuntimeNature>();
		for (Class<? extends Nature> nature : brick.getAnnotation(Brick.class).value()) {
			Nature natureBrick = my(nature);
			if (natureBrick instanceof RuntimeNature)
				result.add((RuntimeNature) natureBrick);
		}
		return result.toArray(new RuntimeNature[result.size()]);
	}

}
