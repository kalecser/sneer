package sneer.bricks.hardware.cpu.crypto.impl;

import java.security.Provider;

import javax.crypto.KeyAgreement;
import javax.crypto.KeyAgreementSpi;

class KeyAgreementWrapper extends KeyAgreement {
	
	public KeyAgreementWrapper(KeyAgreementSpi keyAgreeSpi, Provider provider, String algorithm) {
		super(keyAgreeSpi, provider, algorithm);
	}

}
