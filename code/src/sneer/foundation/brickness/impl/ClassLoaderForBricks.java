package sneer.foundation.brickness.impl;

import java.net.URL;
import java.util.List;

import sneer.foundation.brickness.BrickClassLoader;
import sneer.foundation.brickness.Nature;

abstract class ClassLoaderForBricks extends ClassLoaderWithNatures implements BrickClassLoader {

	private final Class<?> _brick;

	ClassLoaderForBricks(Class<?> brick, URL[] urls, ClassLoader next, List<Nature> natures) {
		super(urls, next, natures);
		_brick = brick;
	}
	
	@Override
	public Class<?> brick() {
		return _brick;
	}

}
