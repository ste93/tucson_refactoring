/**
 * This work by Danilo Pianini is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Italy License.
 * Permissions beyond the scope of this license may be available at www.danilopianini.org.
 */
package alice.casestudies.wanderaround;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Credits go to the author below.
 * 
 * @author Danilo Pianini
 */
public class Utils {
	
	public static String fileToString(String path) throws IOException {
		BufferedInputStream br = new BufferedInputStream(
				ClassLoader.getSystemClassLoader().getResourceAsStream(path)
			);
		byte[] res = new byte[br.available()];
		br.read(res);
		br.close();
		return new String(res);
	}
	
}
