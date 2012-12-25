package com.wonderant.gzip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;

public class TarAndGzip {

	public static void folder(File pFolder) throws IOException {
		folder(pFolder, new ArrayList<String>());
	}

	public static void folder(File pFolder, List<String> pIgnores)
			throws IOException {
		TarOutputStream out = null;
		processIgnores(pFolder.getName(),pIgnores);
		try {
			out = new TarOutputStream(new BufferedOutputStream(
					new GZIPOutputStream(new FileOutputStream(new File(
							pFolder.getAbsolutePath() + "/../"
									+ pFolder.getName() + ".tar.gz")))));
			out.putNextEntry(new TarEntry(pFolder, pFolder.getName()));
			writeToStream(out, pFolder, pFolder.getName(), pIgnores);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	private static void processIgnores(String pName, List<String> pIgnores) {
		for (int i = 0; i < pIgnores.size(); i++) {
			String current = pIgnores.get(i);
			pIgnores.set(i, pName + (current.startsWith("/") ? "" : "/")
					+ current);
		}

	}

	private static void writeToStream(TarOutputStream pOut, File pFolder,
			String pParent, List<String> pIgnores) throws IOException {
		File[] filesToTar = pFolder.listFiles(new FileFilter() {
			public boolean accept(File pArg0) {
				return pArg0.isFile();
			}
		});

		if (filesToTar != null) {
			for (File f : filesToTar) {
				String path = pParent + "/" + f.getName();
				boolean skip = shouldSkip(pIgnores, path);
				if (!skip) {
					pOut.putNextEntry(new TarEntry(f, path));
					BufferedInputStream origin = new BufferedInputStream(
							new FileInputStream(f));
					int count;
					byte data[] = new byte[2048];

					while ((count = origin.read(data)) != -1) {
						pOut.write(data, 0, count);
					}

					pOut.flush();
					origin.close();
				}
			}
		}

		File[] dirsToTar = pFolder.listFiles(new FileFilter() {
			public boolean accept(File pArg0) {
				return pArg0.isDirectory();
			}
		});

		if (dirsToTar != null) {
			for (File dir : dirsToTar) {

				String path = pParent + "/" + dir.getName();
				boolean skip = shouldSkip(pIgnores, path);
				if (!skip) {
					pOut.putNextEntry(new TarEntry(dir, path));
					writeToStream(pOut, dir, path, pIgnores);
				}
			}
		}

	}

	private static boolean shouldSkip(List<String> pIgnores, String path) {
		boolean skip = false;
		for (String ignore : pIgnores) {
			if (path.matches(ignore)) {
				skip = true;
			}
		}
		return skip;
	}
}
