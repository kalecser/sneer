package sneer.bricks.hardware.cpu.lang.contracts;

import basis.brickness.Brick;

@Brick
public interface Contracts {

	WeakContract weakContractFor(Disposable service);

}
