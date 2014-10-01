package alice.tucson.persistency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PersistencyPerformance {

	private String fileName;
	private File FilePerformance;

	// Path /user/persistency
	// Filename log.log
	// p = /user/persistency/log.log
	public PersistencyPerformance(String p) {
		this.fileName = p;
		this.FilePerformance = new File(fileName);
	}

	public void write(String s) {
		PrintWriter pw = null;

		try {
			pw = new PrintWriter(new FileWriter(this.FilePerformance, true),
					true);

			pw.printf(s+"\n");
			pw.flush();
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

}
