/**
 * 
 */
package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.foundation.lang.Producer;

final class BrickProvision implements Producer<Object> {
	
	public BrickProvision(Class<?> prevalentBrick, Object brickInstance) {
		_prevalentBrick = prevalentBrick;
		_brickInstance = brickInstance;
	}


	private final Class<?> _prevalentBrick;
	private transient Object _brickInstance;

	
	@Override public Object produce() {
		if (_brickInstance == null)
			_brickInstance = my(_prevalentBrick);
		
		return _brickInstance;
	}
}