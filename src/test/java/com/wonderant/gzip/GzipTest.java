package com.wonderant.gzip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

public class GzipTest {
	private File folder;
	private File expectedFile;
	private String dir;

	@Before
	public void Before() {
		dir = "target/testFolder";
		folder = new File(dir);
		expectedFile = new File(folder.getAbsolutePath() + "/../"
				+ folder.getName() + ".tar.gz");
		FileUtils.deleteQuietly(expectedFile);
		try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
		}
		folder.mkdirs();

	}

	@Test
	public void testGZipEmptyFolder() throws Exception {
		TarAndGzip.folder(folder);
		assertTrue("not found zip: " + expectedFile.getCanonicalPath(),
				expectedFile.exists());
		assertEquals("testFolder/", listContents(expectedFile));
	}

	@Test
	public void testGZipWithFile() throws Exception {
		FileUtils.writeStringToFile(new File(dir + "/myfile.txt"),
				"hellö world", "UTF-8");
		TarAndGzip.folder(folder);
		assertTrue("not found zip: " + expectedFile.getCanonicalPath(),
				expectedFile.exists());
		assertEquals("testFolder/\n" + "testFolder/myfile.txt",
				listContents(expectedFile));
	}

	@Test
	public void testGZipWithNestedFile() throws Exception {
		new File(dir + "/newDir").mkdirs();
		FileUtils.writeStringToFile(new File(dir + "/newDir/myfile.txt"),
				"hellö world", "UTF-8");
		TarAndGzip.folder(folder);
		assertTrue("not found zip: " + expectedFile.getCanonicalPath(),
				expectedFile.exists());
		assertEquals("testFolder/\n" + "testFolder/newDir/\n"
				+ "testFolder/newDir/myfile.txt", listContents(expectedFile));
	}

	@Test
	public void testGZipWithNestedEmptyFolder() throws Exception {
		new File(dir + "/newDir").mkdirs();
		TarAndGzip.folder(folder);
		assertTrue("not found zip: " + expectedFile.getCanonicalPath(),
				expectedFile.exists());
		assertEquals("testFolder/\n" + "testFolder/newDir/",
				listContents(expectedFile));
	}

	@Test
	public void testWithIgnoreFile() throws Exception {
		createComplicated();
		TarAndGzip.folder(folder);
		assertEquals("testFolder/\n" + "testFolder/backup/\n"
				+ "testFolder/data/\n" + "testFolder/data/myfile.txt\n"
				+ "testFolder/logs/\n" + "testFolder/logs/log1.txt\n"
				+ "testFolder/logs/log2.txt", listContents(expectedFile));

		TarAndGzip.folder(folder, Arrays.asList("logs/log1.txt"));
		assertEquals("testFolder/\n" + "testFolder/backup/\n"
				+ "testFolder/data/\n" + "testFolder/data/myfile.txt\n"
				+ "testFolder/logs/\n" + "testFolder/logs/log2.txt",
				listContents(expectedFile));

		TarAndGzip.folder(folder, Arrays.asList("logs/log.*"));
		assertEquals("testFolder/\n" + "testFolder/backup/\n"
				+ "testFolder/data/\n" + "testFolder/data/myfile.txt\n"
				+ "testFolder/logs/", listContents(expectedFile));
		TarAndGzip.folder(folder,
				Arrays.asList("logs/log1.txt", "logs/log2.txt"));
		assertEquals("testFolder/\n" + "testFolder/backup/\n"
				+ "testFolder/data/\n" + "testFolder/data/myfile.txt\n"
				+ "testFolder/logs/", listContents(expectedFile));
	}

	@Test
	public void testWithIgnoreDirectory() throws Exception {
		createComplicated();
		TarAndGzip.folder(folder, Arrays.asList("logs", "backup"));
		assertEquals("testFolder/\n" + "testFolder/data/\n"
				+ "testFolder/data/myfile.txt", listContents(expectedFile));
	}

	@Test
	public void testExpectedAlreadyPresent() throws Exception {
		createComplicated();
		FileUtils.writeStringToFile(expectedFile, "somedata");
		TarAndGzip.folder(folder, Arrays.asList("logs", "backup"));
		assertEquals("testFolder/\n" + "testFolder/data/\n"
				+ "testFolder/data/myfile.txt", listContents(expectedFile));
	}

	private String listContents(File pExpectedFile) throws IOException {
		TarInputStream tis = new TarInputStream(new BufferedInputStream(
				new GZIPInputStream(new FileInputStream(pExpectedFile))));
		TarEntry entry;
		String result = "";
		while ((entry = tis.getNextEntry()) != null) {
			result += entry.getName() + "\n";
		}
		return result.trim();
	}

	private void createComplicated() throws IOException {
		new File(dir + "/data").mkdirs();
		new File(dir + "/logs").mkdirs();
		new File(dir + "/backup").mkdirs();
		FileUtils.writeStringToFile(new File(dir + "/data/myfile.txt"),
				"hellö world", "UTF-8");
		FileUtils.writeStringToFile(new File(dir + "/logs/log1.txt"),
				"hellö world", "UTF-8");
		FileUtils.writeStringToFile(new File(dir + "/logs/log2.txt"),
				"hellö world", "UTF-8");
	}
}
