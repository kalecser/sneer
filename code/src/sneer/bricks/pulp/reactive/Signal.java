package sneer.bricks.pulp.reactive;

import sneer.bricks.pulp.notifiers.Source;

/** @invariant this.toString().equals("" + this.currentValue()) */
public interface Signal<VO> extends Source<VO> {
	
	VO currentValue();
	
}

