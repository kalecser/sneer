package spikes.rene.krypton;

import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class KryptonSilent {

		KryptonSilent(String function, String filename, String password) throws IOException {
		String[] args={function,filename,password};
			main(args);
		}
	
	
	public static void main (String[] args) throws IOException {
	
		if (args.length==0) {answerGui("insufficient"); return;}
		
		final String function=args[0];
		if ((function.equals("krypt") & args.length<3) | ((function.equals("compress") | function.equals("decompress")) & args.length<2))
			{answerGui("insufficient"); return;}
			
		String destpath=args[1], srcpath=destpath.substring(0,destpath.lastIndexOf('.')) + ".tmp";
		File srcfile = new File(srcpath), destfile = new File(destpath);
		if (!(new File(destpath)).renameTo(srcfile)) {answerGui("rename error"); return;}
		
		if (function.equals("krypt")) enkryption(srcfile, destfile, args[2]);
		else if (function.equals("compr")) compress(srcfile, destfile);
		else if (function.equals("decom")) decompress(srcfile, destfile);
	}

	
	
	private static void enkryption(File srcfile, File destfile, String pass) throws IOException,
			FileNotFoundException {

		if (pass.length()==0) {answerGui("insufficient"); return;}
		
		FileInputStream reader = new FileInputStream(srcfile);
		FileOutputStream writer = new FileOutputStream(destfile);
		Random random = new Random();

		generateSeed(pass, random);

		int lidos = 0;
		byte[] chars = new byte[4096];
	    while ((lidos = reader.read(chars)) != -1) 
	    	writeKrypted(lidos, chars, random, writer);
		reader.close();
		srcfile.delete();
		writer.close();
		answerGui("success");
	}

	private static void compress(File srcfile, File destfile) throws IOException,
	FileNotFoundException {
	    FileInputStream reader = new FileInputStream(srcfile);
	    GZIPOutputStream writer = new GZIPOutputStream(new FileOutputStream(destfile));
	    int lidos;
	    byte[] chars = new byte[4096];
	    while ((lidos = reader.read(chars)) != -1)
	      writer.write(chars, 0, lidos);
	    reader.close();
		srcfile.delete();
	    writer.close();
		answerGui("success");
	}
	
	private static void decompress(File srcfile, File destfile) throws IOException,
	FileNotFoundException {
	    GZIPInputStream reader = new GZIPInputStream(new FileInputStream(srcfile));
	    FileOutputStream writer = new FileOutputStream(destfile);
	    int lidos;
	    byte[] chars = new byte[4096];
	    while ((lidos = reader.read(chars)) != -1)
	      writer.write(chars, 0, lidos);
	    reader.close();
		srcfile.delete();
	    writer.close();
		answerGui("success");
	}

	private static void generateSeed(String pass, Random rand) {
		int seed=0;
		for (int i=0; i<pass.length(); i++)
			seed += pass.charAt(i) * (i+1);
		rand.setSeed(seed);
	}

	private static void writeKrypted(int length, byte[] chars, Random random, FileOutputStream writer) throws IOException {
		for (int i=0; i<length; i++) 
			chars[i] = (byte)(chars[i] ^ random.nextInt(256));
		writer.write(chars, 0, length);
	}

	private static void answerGui(String result) throws IOException {
		FileWriter fw=new FileWriter("result.res");
		fw.write(result);
		fw.close();
	}
}
