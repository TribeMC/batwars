package de.tribemc.batwars.objects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;

public class BatWarsLogger {

	private FileWriter fw;
	private PrintWriter pw;

	public BatWarsLogger(File f) {
		try {
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		pw = new PrintWriter(fw);

	}

	public void save() {
		pw.flush();

		pw.close();
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void log(String message) {
		pw.println(message);
	}

	
	@SuppressWarnings("deprecation")
	public String getPrefix() {
		return "[" + new Time(System.currentTimeMillis()).toGMTString() + "] ";
	}
}
