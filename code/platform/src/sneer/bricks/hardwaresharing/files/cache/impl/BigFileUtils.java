package sneer.bricks.hardwaresharing.files.cache.impl;

import sneer.bricks.hardwaresharing.files.protocol.BigFileBlocks;
import sneer.bricks.pulp.crypto.Sneer1024;

class BigFileUtils {

	public static byte[] getBytes(BigFileBlocks bigFileBlocks) {
		byte[][] bigFileBlocksContentsHash = getContentsHashBytes(bigFileBlocks);
		byte[] mergedBytes = merge(bigFileBlocksContentsHash);
		return mergedBytes;
	}
	
	private static byte[] merge(byte[]... arrays) {
		int totalLength = 0;
		for (int i = 0; i < arrays.length; i++) {
			
			long length = arrays[i].length;
			if (totalLength > Integer.MAX_VALUE - length)
				throw new IllegalStateException("Integer overflow");
			
			totalLength += length;
		}
		
		byte[] merged = new byte[totalLength];
		int current  = 0;
		for (int i = 0; i < arrays.length; i++) {
			for (int j = 0; j < arrays[i].length; j++) {
				merged[current++] = arrays[i][j];
			}
		}
		
		return merged;
	}


	private static byte[][] getContentsHashBytes(BigFileBlocks bigFileBlocks) {
		Sneer1024[] bigFileBlocksContents = bigFileBlocks._contents.toArray();
		byte[][] bigFileBlocksContentsHash = new byte[bigFileBlocksContents.length][];
		
		int i = 0;
		for (Sneer1024 hash : bigFileBlocksContents){
			bigFileBlocksContentsHash[i++] = hash.bytes();
		}
		return bigFileBlocksContentsHash;
	}
}
