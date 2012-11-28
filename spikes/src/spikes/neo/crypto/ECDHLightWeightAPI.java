package spikes.neo.crypto;

import static org.bouncycastle.jce.provider.asymmetric.ec.ECUtil.generatePrivateKeyParameter;
import static org.bouncycastle.jce.provider.asymmetric.ec.ECUtil.generatePublicKeyParameter;

import java.math.BigInteger;
import java.security.KeyPair;

import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator;

public class ECDHLightWeightAPI {

	public static void main(String[] args) throws Exception {
		runTest();
	}
	
	static private void runTest() throws Exception {
		KeyPairGenerator.EC generator = new KeyPairGenerator.ECDSA();
		generator.initialize(256);
		
		KeyPair neide = generator.generateKeyPair();
		KeyPair jhon = generator.generateKeyPair();
		
		System.out.println("Neide: \n " + neide.getPublic() + " " + neide.getPrivate());
		System.out.println("Jhon: \n " + jhon.getPublic() + " " + jhon.getPrivate());
		
		ECDHBasicAgreement agreement = new ECDHBasicAgreement();
		agreement.init(generatePrivateKeyParameter(neide.getPrivate()));
		BigInteger neideSecret = agreement.calculateAgreement(generatePublicKeyParameter(jhon.getPublic()));
		
		System.out.println("SECRET FROM NEIDE'S SIDE: " + neideSecret);
		
		agreement = new ECDHBasicAgreement();
		agreement.init(generatePrivateKeyParameter(jhon.getPrivate()));
		BigInteger jhonSecret = agreement.calculateAgreement(generatePublicKeyParameter(neide.getPublic()));
		
		System.out.println("SECRET FROM JHON'S SIDE: " + jhonSecret);
		
		System.out.println("SAME SECRET: " + neideSecret.equals(jhonSecret));
	}

}
