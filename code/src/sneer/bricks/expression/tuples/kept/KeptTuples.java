package sneer.bricks.expression.tuples.kept;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface KeptTuples extends ListRegister<Tuple> {}
