package sneer.bricks.hardware.cpu.utils.consumers.parsers.integer;

import basis.brickness.Brick;
import basis.lang.PickyConsumer;

@Brick
public interface IntegerParsers {

	PickyConsumer<String> newIntegerParser(PickyConsumer<Integer> delegate);

}
