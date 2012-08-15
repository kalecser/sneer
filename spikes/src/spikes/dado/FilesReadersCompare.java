package spikes.dado;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesReadersCompare {

	public static void main(String[] args) {
		String path = "src//spikes//dado";
		readFileWithJavaIo(path);
		readFileWithJavaNio(path);
	}

	private static void readFileWithJavaIo(String path) {
		System.out.println("====With Java IO===");
		File[] files = new File(path).listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				System.out.println("\n"+ file.getName());
				System.out.println("File exists = "+ file.exists());				
				try {
					new FileInputStream(file);
				} catch (FileNotFoundException e) {
					System.out.println(e);
				}
			}
		}
	}
	
	private static void readFileWithJavaNio(String path) {
		System.out.println("====With Java NIO====");
		Path dir = FileSystems.getDefault().getPath(path);
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path file : stream) {
				if (!Files.isDirectory(file)) {
					System.out.println("\n"+ file.getFileName());
					System.out.println("File exists = "+ Files.exists(file));
					Files.newInputStream(file);
				}
			}
		} catch (IOException | DirectoryIteratorException e) {
			System.err.println(e);
		}
	}
	
}
