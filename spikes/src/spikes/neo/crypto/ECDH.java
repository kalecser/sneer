package spikes.neo.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class ECDH {

	public static void main(String[] args) throws Exception {
		for(int i = 0; i < 1000; i++) getKeyPair();
		
		KeyPair alice = getKeyPair();
		KeyPair bob = getKeyPair();
		KeyPair carlos = getKeyPair();

		PublicKey alicePubKey = alice.getPublic();
		PrivateKey alicePrivKey = alice.getPrivate();

		PublicKey bobPubKey = bob.getPublic();
		PrivateKey bobPrivKey = bob.getPrivate();
		
		PublicKey carlosPubKey = carlos.getPublic();
		PrivateKey carlosPrivKey = carlos.getPrivate();

		SecretKey aliceSecret = getSecret(bobPubKey, alicePrivKey);
		SecretKey bobSecret = getSecret(alicePubKey, bobPrivKey);
		
		SecretKey carlosAliceSecret = getSecret(alicePubKey, carlosPrivKey);
		SecretKey aliceCarlosSecret = getSecret(carlosPubKey, alicePrivKey);
		
		System.out.println(Arrays.toString(aliceSecret.getEncoded()));
		System.out.println(Arrays.toString(bobSecret.getEncoded()));
		
		System.out.println(aliceSecret);
		System.out.println(bobSecret);
		
		System.out.println(carlosAliceSecret == aliceCarlosSecret);
		
		System.out.println(Arrays.toString(carlosAliceSecret.getEncoded()));
		System.out.println(Arrays.toString(aliceCarlosSecret.getEncoded()));
		
		//
		
		carlosAliceSecret = getSecret(alicePubKey, carlosPrivKey);
		aliceCarlosSecret = getSecret(carlosPubKey, alicePrivKey);
		
		System.out.println(carlosAliceSecret == aliceCarlosSecret);
		
		System.out.println(Arrays.toString(carlosAliceSecret.getEncoded()));
		System.out.println(Arrays.toString(aliceCarlosSecret.getEncoded()));
	}

	private static SecretKey getSecret(PublicKey publicKey,
			PrivateKey privateKey) throws Exception {
		KeyAgreement ka = KeyAgreement.getInstance("ECDH");
		ka.init(privateKey);
		ka.doPhase(publicKey, true);

		return ka.generateSecret("ECDH");
	}

	static KeyPair getKeyPair() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA", "BC");

		X9ECParameters paramsA = SECNamedCurves.getByName("secp160r1");
		ECParameterSpec params = new ECParameterSpec(paramsA.getCurve(),
				paramsA.getG(), paramsA.getN());

		kpg.initialize(params, new SecureRandom());
		KeyPair pair = kpg.generateKeyPair();
		
		int length = pair.getPublic().getEncoded().length;
		System.out.println("length:" + length);
		
		if (length != 214) throw new RuntimeException();
		
		return pair;
	}

}
