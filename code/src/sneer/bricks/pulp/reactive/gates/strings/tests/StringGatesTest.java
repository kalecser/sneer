package sneer.bricks.pulp.reactive.gates.strings.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.gates.strings.StringGates;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class StringGatesTest extends BrickTest {

	private final StringGates _subject =  my(StringGates.class);

	@Test
	public void concat() {
		Register<String> a = my(Signals.class).newRegister("");
		Register<String> b = my(Signals.class).newRegister("");
		Signal<String> a_concat_b = _subject.concat(a.output(), b.output());
		Signal<String> a_concat_comma_concat_b = _subject.concat(",", a.output(), b.output());

		assertEquals("", a_concat_b.currentValue());

		a.setter().consume("A");
		b.setter().consume("B");
		assertEquals("AB", a_concat_b.currentValue());
		assertEquals("A,B", a_concat_comma_concat_b.currentValue());

		b.setter().consume("");
		assertEquals("A", a_concat_b.currentValue());
		assertEquals("A,", a_concat_comma_concat_b.currentValue());

		a.setter().consume("A");
		b.setter().consume("B");
		assertEquals("AB", a_concat_b.currentValue());
		assertEquals("A,B", a_concat_comma_concat_b.currentValue());

		Signal<String> a_concat_b_concat_period_concat_a_concat_comma_concat_b = _subject.concat(".", a_concat_b, a_concat_comma_concat_b);
		assertEquals("AB.A,B", a_concat_b_concat_period_concat_a_concat_comma_concat_b.currentValue());

		Register<String> c = my(Signals.class).newRegister("");
		Register<String> d = my(Signals.class).newRegister("");
		Signal<String> a_concat_space_concat_b_concat_space_concat_c_concat_space_concat_d = _subject.concat(" ", a.output(), b.output(), c.output(), d.output());

		a.setter().consume("What");
		b.setter().consume("A");
		c.setter().consume("Wonderful");
		d.setter().consume("World");

		assertEquals("What A Wonderful World", a_concat_space_concat_b_concat_space_concat_c_concat_space_concat_d.currentValue());
	}

}
