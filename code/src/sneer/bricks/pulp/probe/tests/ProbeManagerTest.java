package sneer.bricks.pulp.probe.tests;

import static basis.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.connections.ConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.distribution.filtering.TupleFilterManager;
import sneer.bricks.pulp.probe.ProbeManager;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.serialization.Serializer;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.brickness.testsupport.Bind;
import basis.lang.Consumer;
import basis.lang.arrays.ImmutableByteArray;

public class ProbeManagerTest extends BrickTestBase {

	@Bind private final ConnectionManager _connectionManager = mock(ConnectionManager.class);

	@SuppressWarnings("unused")
	private final ProbeManager _subject = my(ProbeManager.class);
	private final TupleFilterManager _filter = my(TupleFilterManager.class);
	private final TupleSpace _tuples = my(TupleSpace.class);
	
	private final ByteConnection _connection = mock(ByteConnection.class);
	private PacketScheduler _scheduler;
	@SuppressWarnings("unused")
	private Consumer<byte[]> _packetReceiver;

	@Test (timeout = 1000)
	public void testTupleBlocking() throws Exception {
		checking(new Expectations(){{
			one(_connectionManager).connectionFor(with(aNonNull(Contact.class))); will(returnValue(_connection));
			one(_connection).isConnected(); will(returnValue(my(Signals.class).constant(true)));
			one(_connection).initCommunications(with(aNonNull(PacketScheduler.class)), with(aNonNull(Consumer.class)));
				will(new CustomAction("capturing scheduler") { @Override public Object invoke(Invocation invocation) throws Throwable {
					_scheduler = (PacketScheduler) invocation.getParameter(0);
					return null;
				}});
		}});

		my(Contacts.class).addContact("Neide");
		my(ContactSeals.class).put("Neide", newSeal(new byte[]{1}));

		_tuples.add(new TupleTypeA(1));
		assertPacketToSend(1);
		_tuples.add(new TupleTypeB(2));
		assertPacketToSend(2);

		_filter.block(TupleTypeB.class);
		
		_tuples.add(new TupleTypeA(3));
		_tuples.add(new TupleTypeB(4));
		assertPacketToSend(3);
		_tuples.add(new TupleTypeA(5));
		assertPacketToSend(5);
	}


	private void assertPacketToSend(int id) throws Exception {
		my(TupleDispatcher.class).waitForAllDispatchingToFinish();
		byte[] packet = _scheduler.highestPriorityPacketToSend();
		_scheduler.previousPacketWasSent();
		
		assertEquals(id, desserialize(packet).id);
	}


	private TupleWithId desserialize(byte[] packet) throws Exception {
		return (TupleWithId)my(Serializer.class).deserialize(packet);
	}

	
	private Seal newSeal(byte[] bytes) {
		return new Seal(new ImmutableByteArray(bytes));
	}

}
