package spikes.klaus.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("java.runtime.name"));
		System.out.println(System.getProperty("java.runtime.version"));
		
		Security.addProvider(new BouncyCastleProvider());
		
		printProvidersAndServices();
		
		byte[] message = "Any silly thing...".getBytes();
		
		testHash(message);
		testPK(message);
		
	}

	
	
	private static void printProvidersAndServices() {
		HashSet<String> serviceTypes = new HashSet<String>();

		Provider[] provs = Security.getProviders();
		for (Provider prov : provs) {
			System.out.println();
			System.out.println("Provider: " + prov);
			Set<Service> services = prov.getServices();
			for (Service service : services) {
				String type = service.getType();
				serviceTypes.add(type);
				System.out.println("\n\tType: " + type);
				System.out.println("\tAlgo: " + service.getAlgorithm());
			}
		}

		for (String type : serviceTypes) {
			System.out.println("\nService Type: " + type);
			for (String alg : Security.getAlgorithms(type)) {
				System.out.println(alg);
			}
		}
		}

	private static void testHash(byte[] message) throws Exception {
		//MessageDigest digester = MessageDigest.getInstance("SHA-512", "SUN");
		//MessageDigest digester = MessageDigest.getInstance("WHIRLPOOL", "BC");
		MessageDigest digester = MessageDigest.getInstance("SHA-512", "BC");
		byte[] digest = digester.digest(message);
		System.out.println("Digest length: " + digest.length * 8 + " bits");
	}

	
	private static void testPK(byte[] bytecodeDummy) throws Exception {
		KeyPair keys = generateKeyPair();
				
		byte[] signature1 = generateSignature(keys.getPrivate(), bytecodeDummy);
		byte[] signature2 = generateSignature(keys.getPrivate(), bytecodeDummy);
		avoidSonyPS3EpicFail(signature1,signature2);
		
		boolean ok = verifySignature(keys.getPublic(), bytecodeDummy, signature1);
		System.out.println("PK Signature ok: " + ok);
	}


	//They were using the same random number for all ECDSA signatures, thus producing the same signature given the same message.
	private static void avoidSonyPS3EpicFail(byte[] signature1, byte[] signature2) { 
		Assert.assertFalse(Arrays.areEqual(signature1, signature2));
	}

		
	public static KeyPair generateKeyPair() throws Exception {
		System.out.println(">>>>>>>> Starting to generate key.");
		
//		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
//		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "BC");
		SecureRandom random = new CountingSecureRandom();
		
//		byte[] seed = "SENHA SECRETA COMPRIDA".getBytes("UTF-8");
//		random.setSeed(seed);

//		KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
//		KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA", "BC");
//		keypairgenerator.initialize(4096, random);
		
		KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
		keypairgenerator.initialize(256, random);

		try {
			return keypairgenerator.generateKeyPair();
		} finally {
			System.out.println(">>>>>>>> Finished generating key with " + ((CountingSecureRandom)random)._bytesRequested + " random bytes.");
		}
	}

	public static byte[] generateSignature(PrivateKey privatekey, byte[] message) throws Exception {
//		Signature signer = Signature.getInstance("SHA512WITHRSA", "SunRsaSign");
//		Signature signer = Signature.getInstance("SHA512WITHRSA", "BC");
//		Signature signer = Signature.getInstance("SHA512WITHECDSA", "BC");
		Signature signer = Signature.getInstance("ECDSA", "BC");

		signer.initSign(privatekey);
		signer.update("The same signer instance can be used over again.".getBytes());

		signer.initSign(privatekey);
		signer.update(message);
	
		return signer.sign();
	}

	public static boolean verifySignature(PublicKey publickey, byte[] message, byte[] signature) throws Exception {
//		Signature verifier = Signature.getInstance("SHA512WITHRSA", "SunRsaSign");
//		Signature verifier = Signature.getInstance("SHA512WITHRSA", "BC");
//		Signature verifier = Signature.getInstance("SHA512WITHECDSA", "BC");
		Signature verifier = Signature.getInstance("ECDSA", "BC");

		KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publickey.getEncoded());
		PublicKey publicKeyDecoded = keyFactory.generatePublic(publicKeySpec);

		verifier.initVerify(publicKeyDecoded);
		verifier.update("The same verifier instance can be used over again.".getBytes());

		verifier.initVerify(publicKeyDecoded);
		verifier.update(message);
	
		return verifier.verify(signature);
	}

}
