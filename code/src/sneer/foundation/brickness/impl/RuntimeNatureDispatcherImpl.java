package sneer.foundation.brickness.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.Nature;
import sneer.foundation.brickness.RuntimeNature;
import sneer.foundation.brickness.RuntimeNatureDispatcher;
import sneer.foundation.brickness.RuntimeNature.Continuation;
import sneer.foundation.lang.exceptions.NotImplementedYet;

public class RuntimeNatureDispatcherImpl implements RuntimeNatureDispatcher {

	private final ConcurrentHashMap<Class<?>, List<RuntimeNature>> _runtimeNaturesPerBrick = new ConcurrentHashMap<Class<?>, List<RuntimeNature>>();

	@Override
	public Object dispatch(Class<?> brick, Object instance, String methodName,
			Object[] args, Continuation continuation) {
		
		List<RuntimeNature> natures = produceRuntimeNaturesFor(brick);
		if (natures.size() > 1)
			throw new NotImplementedYet();
		return natures.get(0).invoke(brick, instance, methodName, args, continuation);
	}

	private List<RuntimeNature> produceRuntimeNaturesFor(Class<?> brick) {
		
		List<RuntimeNature> cached = _runtimeNaturesPerBrick.get(brick);
		if (null != cached)
			return cached;
		
		List<RuntimeNature> result = runtimeNaturesFor(brick);
		_runtimeNaturesPerBrick.putIfAbsent(brick, result);
		return result;
	}

	private List<RuntimeNature> runtimeNaturesFor(Class<?> brick) {
		ArrayList<RuntimeNature> result = new ArrayList<RuntimeNature>();
		for (Class<? extends Nature> nature : brick.getAnnotation(Brick.class).value()) {
			Nature natureBrick = my(nature);
			if (natureBrick instanceof RuntimeNature)
				result.add((RuntimeNature) natureBrick);
		}
		return result;
	}

}
