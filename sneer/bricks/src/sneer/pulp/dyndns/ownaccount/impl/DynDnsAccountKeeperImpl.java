package sneer.pulp.dyndns.ownaccount.impl;

import sneer.pulp.dyndns.ownaccount.DynDnsAccount;
import sneer.pulp.dyndns.ownaccount.DynDnsAccountKeeper;
import wheel.lang.Consumer;
import wheel.reactive.Register;
import wheel.reactive.Signal;
import wheel.reactive.impl.RegisterImpl;

class DynDnsAccountKeeperImpl implements DynDnsAccountKeeper {

	private Register<DynDnsAccount> _ownAccount = new RegisterImpl<DynDnsAccount>(null);

	@Override
	public Signal<DynDnsAccount> ownAccount() {
		return _ownAccount.output();
	}

	@Override
	public Consumer<DynDnsAccount> accountSetter() {
		return _ownAccount.setter();
	}

}
