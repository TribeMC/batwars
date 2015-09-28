package de.tribemc.batwars.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.WorldCreator;

import de.tribemc.batwars.main.BatWarsManager;

public class CopyMapManager {

	private File dir;
	private BatWarsManager bwm;

	public CopyMapManager(BatWarsManager bwm) {
		this.dir = new File("plugins/BatWars/Maps/");
		if (!dir.exists())
			this.dir.mkdir();
		this.bwm = bwm;
	}

	public File getDir() {
		return this.dir;
	}

	public void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public void deleteFiles(File file) {
		if (!file.exists())
			return;
		if (file.isDirectory())
			for (File f : file.listFiles())
				deleteFiles(f);
		if (!file.delete())
			file.deleteOnExit();

	}

	public void copyMapAsync(String name) {
		try {
			File target = new File(name);
			target.mkdir();
			copyDirectory(new File(dir, name), target);
			bwm.getServer().createWorld(new WorldCreator(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean mapExist(String name) {
		return new File(dir, name).isDirectory();
	}

	public void copyMap(String name) {
		try {
			File target = new File(name);
			if (target.exists())
				deleteFiles(target);
			target.mkdir();
			copyDirectory(new File(dir, name), target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
