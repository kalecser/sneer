package spikes.rene.Krypton;

import java.util.Random;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class KryptonFile {

	public static void main (String args[]) throws IOException {
		Random random = new Random();
		String filepath=args[0], pass=args[1];
		if (filepath.length()==0) return;

		int seed=0, i=0;
		for (i=0; i<pass.length(); i++)
			seed+=pass.charAt(i)*(i+1);
		random.setSeed(seed);
		
		FileInputStream fr = new FileInputStream(filepath);

		System.out.print("Lendo arquivo... ");
		int max=fr.available();
		int num=(int) Math.floor(max/1000000);
		int resto=max-num*1000000;
		byte chars[][]=new byte[num][];
		
		
		for (i=0; i<=num; i++){
			chars[i]=new byte[1000000];	
			fr.read(chars[i],i*1000000,1000000);
		}
		chars[i]=new byte[resto];	
		fr.read(chars[i],i*1000000,resto);
		fr.close();
		
		System.out.print("completo.\nKriptografando arquivo... ");
		
		for (i=0; i<num; i++) {
			for (int i2=0; i2<1000000; i2++) 
				chars[i][i2]=(byte) (chars[i][i2]^random.nextInt(256));
		}
		for (int i2=0; i2<resto; i2++) 
			chars[i][i2]=(byte) (chars[i][i2]^random.nextInt(256));
		
		System.out.print("completo.\nSalvando arquivo... ");
		FileOutputStream fw = new FileOutputStream(filepath);
		
		for (i=0; i<num; i++) {
			for (int i2=0; i2<1000000; i2++) 
				fw.write(chars[i][i2]);
		}
		for (int i2=0; i2<resto; i2++) 
			fw.write(chars[i][i2]);
		
		
		fw.close();
		System.out.print("completo.");
	}
}
