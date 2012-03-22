package sneer.bricks.hardware.io.prevalence.nature;

import basis.brickness.Brick;
import basis.brickness.Nature;

/** Nature that makes bricks transparently prevalent by wrapping them in a prevalent Proxy:
 *		- void methods are run as Transactions.
 *		- non-void methods are run as Queries.
 *		- non-void methods annotated with @Transaction are run as Transactions.
 *
 *		IMPORTANT: Prevalent brick instantiation is NOT run as a transaction. It must not have stateful side-effects.
 * */
@Brick
public interface Prevalent extends Nature {}
