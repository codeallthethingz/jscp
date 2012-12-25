package com.wonderant.ssh;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Doesn't actually do rsync yet, but I'm hoping it will in future.
 * 
 * @author will
 * 
 */
public class JscpTest {

	private static final String SRC_DIR = "target/scpDir";
	private static final String BACKUP_DIR = SRC_DIR + "/backup";
	private static final String LOG_DIR = SRC_DIR + "/logs";
	private static final String DATA_DIR = SRC_DIR + "/data";

	@Before
	public void setup() throws IOException {
		deleteAll();
		new File(DATA_DIR).mkdirs();
		new File(LOG_DIR).mkdirs();
		new File(BACKUP_DIR).mkdirs();
		new File(BACKUP_DIR + "/temp1234").mkdirs();
		new File(SRC_DIR + "Remote").mkdirs();
		FileUtils.writeStringToFile(new File(DATA_DIR + "/test.txt"), "hellö");
		FileUtils.writeStringToFile(new File(LOG_DIR + "/log.txt"), "logging");
		FileUtils.writeStringToFile(new File(SRC_DIR + "/route.json"), "route");
		FileUtils.writeStringToFile(new File(SRC_DIR + "/javascript.js"), "js");
		FileUtils.writeStringToFile(new File(BACKUP_DIR + "/temp1234/backup"),
				"js");
	}

	public void deleteAll() {
		try {
			FileUtils.deleteDirectory(new File(SRC_DIR));
			FileUtils.deleteQuietly(new File(SRC_DIR + "/../scpDir.tar.gz"));
			FileUtils.deleteDirectory(new File(SRC_DIR + "Remote"));
		} catch (IOException e) {
		}
	}

	@Test
	public void testRemoteCopy() throws Exception {
		String linuxPath = new File(SRC_DIR).getCanonicalPath()
				.replaceAll("\\\\", "/").replace("C:", "/cygdrive/c")
				.replaceAll(" ", "\\ ")
				+ "Remote";
		SecureContext context = new SecureContext(System.getProperty("user.name"), "localhost");
		context.setTrustAllHosts(true);
		context.setPrivateKeyFile(new File(
		"C:\\cygwin\\home\\will\\.ssh\\id_rsa"));
		Jscp.exec(context, SRC_DIR,
				linuxPath, Arrays.asList("logs", "backup"));
		assertEquals(3, new File(SRC_DIR + "Remote/scpDir").listFiles().length);
		assertEquals(
				"hellö",
				FileUtils.readFileToString(new File(SRC_DIR
						+ "Remote/scpDir/data/test.txt")));
	}

}
