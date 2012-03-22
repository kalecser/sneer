package sneer.bricks.pulp.dyndns.ownaccount.impl;

import static basis.environments.Environments.my;
import basis.lang.Consumer;
import sneer.bricks.pulp.dyndns.ownaccount.DynDnsAccount;
import sneer.bricks.pulp.dyndns.ownaccount.DynDnsAccountKeeper;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.bricks.statestore.BrickStateStore;

class DynDnsAccountKeeperImpl implements DynDnsAccountKeeper {

	private final BrickStateStore _store = my(BrickStateStore.class);
	private final Register<DynDnsAccount> _ownAccount = my(Signals.class).newRegister(null);
	
	@SuppressWarnings("unused")
	private Object _refToAvoidGc;
	
	DynDnsAccountKeeperImpl(){
		restore();
		initReceivers();
	}

	@Override
	public Signal<DynDnsAccount> ownAccount() {
		return _ownAccount.output();
	}

	@Override
	public Consumer<DynDnsAccount> accountSetter() {
		return _ownAccount.setter();
	}
	
	private void initReceivers() {
		_refToAvoidGc = ownAccount().addReceiver(new Consumer<DynDnsAccount>(){ @Override public void consume(DynDnsAccount dynDnsAccount) {
			save(dynDnsAccount);
		}});
	}
	
	private void restore() {
		String[] data = (String[]) _store.readObjectFor(DynDnsAccountKeeper.class);
		
		if (data != null) accountSetter().consume(new DynDnsAccount(data[0], data[1], data[2]));
	}
	
	private void save(DynDnsAccount dynDnsAccount) {
		if (dynDnsAccount == null) return;
		_store.writeObjectFor(DynDnsAccountKeeper.class,  new String[]{dynDnsAccount.host, dynDnsAccount.user, dynDnsAccount.password});
	}
}