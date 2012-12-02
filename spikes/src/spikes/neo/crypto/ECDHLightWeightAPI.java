package spikes.neo.crypto;

import static org.bouncycastle.jce.provider.asymmetric.ec.ECUtil.generatePrivateKeyParameter;
import static org.bouncycastle.jce.provider.asymmetric.ec.ECUtil.generatePublicKeyParameter;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator;

import basis.lang.exceptions.NotImplementedYet;

public class ECDHLightWeightAPI {

	public static void main(String[] args) throws Exception {
		runTest();
	}
	
	static private void runTest() throws Exception {
		KeyPairGenerator.EC generator = new KeyPairGenerator.ECDSA();
		generator.initialize(256);
		
		KeyPair neide = generator.generateKeyPair();
		KeyPair john = generator.generateKeyPair();
		
		System.out.println("Neide: \n " + neide.getPublic() + " " + neide.getPrivate());
		System.out.println("John: \n " + john.getPublic() + " " + john.getPrivate());
		
		BigInteger neideSecret = secretGiven(neide.getPrivate(), john.getPublic());
		BigInteger johnSecret = secretGiven(john.getPrivate(), neide.getPublic());
		
		System.out.println("SECRET FROM NEIDE'S SIDE: " + neideSecret + " Size: " + neideSecret.toByteArray().length);
		System.out.println("SECRET FROM  JOHN'S SIDE: " + johnSecret + " Size: " + johnSecret.toByteArray().length);
		System.out.println("SAME SECRET: " + neideSecret.equals(johnSecret));
		
		throw new NotImplementedYet("1) Generate keys given seed, like Sneer does. 2) Use a hash of the BigInteger in the end to minimize information leak about the private keys.");
	}

	private static BigInteger secretGiven(PrivateKey privateKey, PublicKey publicKey) throws InvalidKeyException {
		ECDHBasicAgreement ret = new ECDHBasicAgreement();
		ret.init(generatePrivateKeyParameter(privateKey));
		return ret.calculateAgreement(generatePublicKeyParameter(publicKey));
	}

}
