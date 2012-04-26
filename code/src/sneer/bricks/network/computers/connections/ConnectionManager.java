package sneer.bricks.network.computers.connections;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.notifiers.Source;
import basis.brickness.Brick;

@Brick
public interface ConnectionManager {

	public interface Delegate {
		ByteConnection connectionFor(Contact contact);
		void closeConnectionFor(Contact contact);
		Source<Call> unknownCallers();
	}

	ByteConnection connectionFor(Contact contact);
	void closeConnectionFor(Contact contact);
	Source<Call> unknownCallers();

}
