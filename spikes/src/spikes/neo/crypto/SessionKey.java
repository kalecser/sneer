package spikes.neo.crypto;

import static basis.brickness.Brickness.newBrickContainer;
import static basis.environments.Environments.my;
import static basis.environments.Environments.runWith;

import java.math.BigInteger;
import java.security.SecureRandom;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import basis.lang.ClosureX;

public class SessionKey {

	public static void main(String[] args) throws Exception {
		runWith(newBrickContainer(), new ClosureX<Exception>() {  @Override public void run() throws Exception {
			runSpike();
		}});
	}

	public static void runSpike() {
		BigInteger secret = new BigInteger("11187681314623280161338908177921774308688744234917591442980124796871021941046");
		
		BigInteger sessionKeyFromNeide = new BigInteger(256, new SecureRandom());
		BigInteger sessionKeyFromJohn = new BigInteger(256, new SecureRandom());
		
		BigInteger finalSessionKey = sessionKeyFromJohn.add(sessionKeyFromNeide);
		
		System.out.println("                Secret: " + secret + " Size: " + secret.toByteArray().length);
		System.out.println("Session Key From Neide: " + sessionKeyFromNeide + " Size: " + sessionKeyFromNeide.toByteArray().length);
		System.out.println(" Session Key From John: " + sessionKeyFromJohn + " Size: " + sessionKeyFromJohn.toByteArray().length);
		System.out.println("     Final session key: " + finalSessionKey + " Size " + finalSessionKey.toByteArray().length);
		
		byte[] neideSecret = generateSecret(secret, finalSessionKey);
		byte[] johnSecret = generateSecret(secret, finalSessionKey);
		
		Hash neideHash = my(Crypto.class).digest(neideSecret);
		Hash johnHash = my(Crypto.class).digest(johnSecret);
		
		System.out.println("Neide: " + neideHash);
		System.out.println(" John: " + johnHash);
	}

	private static byte[] generateSecret(BigInteger secret, BigInteger sessionKey) {
		return sessionKey.compareTo(secret) < 1 ? 
				my(Lang.class).arrays().concat(sessionKey.toByteArray(), secret.toByteArray()) :
				my(Lang.class).arrays().concat(secret.toByteArray(), sessionKey.toByteArray());
	}
	
}
