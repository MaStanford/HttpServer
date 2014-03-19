package com.stanford.httpserver.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Contains the utilities for parsing strings and files
 * @author Mark Stanford
 *
 */
public class Util {

	/**
	 * Returns a file or null
	 * @param path
	 * @return
	 */
	public static File getFile(String path){
		File file = new File(path);
		if(file.exists())
			return file;
		return null;
	}

	/**
	 * Converts file to byte array
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static byte[] getByteFromFile(File file) throws Exception{
		byte []buffer = new byte[(int) file.length()];
		InputStream ios = null;
		try {
			ios = new FileInputStream(file);
			if ( ios.read(buffer) == -1 ) {
				throw new IOException("EOF reached while trying to read the whole file");
			}        
		} finally { 
			try {
				if ( ios != null ) 
					ios.close();
			} catch ( IOException e) {
			}
		}
		return buffer;
	}
}
