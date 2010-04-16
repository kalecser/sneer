package sneer.foundation.brickness.impl;

import java.net.URL;
import java.util.List;

import sneer.foundation.brickness.BrickClassLoader;
import sneer.foundation.brickness.Nature;

abstract class ClassLoaderForBricks extends ClassLoaderWithNatures implements BrickClassLoader {

	private final String _brickName;

	ClassLoaderForBricks(String brickName, URL[] urls, ClassLoader next, List<Nature> natures) {
		super(urls, next, natures);
		_brickName = brickName;
	}
	
	@Override
	public String brickName() {
		return _brickName;
	}

}
