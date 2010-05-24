package spikes.rene.Krypton;

//ancient

import java.util.Random;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class KryptonConsole {

	public static void main (String args[]) throws IOException {
	
		System.out.print("Krypton, criptografador de arquivos enormes\nby -=ReNeX=-\n\nInicializando... ");
		
		String filepath=args[0], pass=args[1];
		if (filepath.length()==0) {System.out.print("Erro: Nenhum arquivo."); return;}
		String filetemp=filepath.substring(0,filepath.indexOf('.'))+".tmp";

		int seed=0, i=0, i2=0;
		Random rand = new Random();
		for (i=0; i<pass.length(); i++)
			seed+=pass.charAt(i)*(i+1);
		rand.setSeed(seed);
		
		FileInputStream fr = new FileInputStream(filepath);
		FileOutputStream fw = new FileOutputStream(filetemp);
		
		int len=10000000, max=fr.available(), iter=(int) Math.floor(max/len), resto=max-iter*len;
		byte[] chars=new byte[len];
		
		String sizetag;
		if (max<1024) sizetag=max+" bytes";
		else if (max<1048576) sizetag=(max/1024f)+" KB";
		else sizetag=(max/1048576f)+" MB";
		
		System.out.print("pronto.\nArquivo: "+filepath.substring(filepath.indexOf('\\')+1,filepath.length())+", Tamanho total: "+sizetag+".\nKrypton-grafando arquivo... ");
		
		for (i=0; i<iter; i++) {
			fr.read(chars);
			for (i2=0; i2<len; i2++) 
				chars[i2]=(byte) (chars[i2]^rand.nextInt(256));
			fw.write(chars);
		}
		fr.read(chars,0,resto);
		for (i=0; i<resto; i++) 
			chars[i]=(byte) (chars[i]^rand.nextInt(256));
		fw.write(chars, 0, resto);

		System.out.print("pronto.\nFinalizando... ");
	
		fr.close();
		fw.close();
		File oldfile=new File(filepath);
		File tmpfile=new File(filetemp);
		oldfile.renameTo(new File(filepath.substring(0,filepath.indexOf('.'))+".bkp")); 
		oldfile.delete();
		tmpfile.renameTo(new File(filepath));

		System.out.print("pronto.");
	}
}
