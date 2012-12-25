package com.wonderant.ssh;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class ScpTest {

	private File expectedFile;
	private File expectedFileRemote;
	private String dirRemote;
	private SecureContext context;

	@Before
	public void Before() throws IOException {
		dirRemote = "target/testFolderRemote";
		expectedFile = new File("target/temp.tar.gz");
		expectedFileRemote = new File("target/temp2.tar.gz");
		context = new SecureContext(System.getProperty("user.name"),
				"localhost").setTrustAllHosts(true);
		FileUtils.deleteQuietly(expectedFile);
		FileUtils.deleteQuietly(expectedFileRemote);
		try {
			FileUtils.deleteDirectory(new File(dirRemote));
		} catch (IOException e) {
		}
		FileUtils.writeStringToFile(expectedFile, "mydata");
	}

	@Test
	public void testScpWithKey() throws Exception {
		String linuxPath = new File("target").getCanonicalPath()
				.replaceAll("\\\\", "/").replace("C:", "/cygdrive/c")
				.replaceAll(" ", "\\ ")
				+ "/temp2.tar.gz";

		context.setPrivateKeyFile(new File(
				"C:\\cygwin\\home\\will\\.ssh\\id_rsa"));
		Scp.exec(context, expectedFile.getPath(), linuxPath);
		assertTrue(
				"file not found: "
						+ new File("target/temp2.tar.gz").getCanonicalPath(),
				new File("target/temp2.tar.gz").exists());

	}

	@Test
	public void testScpWithBadPassword() throws Exception {
		String linuxPath = new File("target").getCanonicalPath()
				.replaceAll("\\\\", "/").replace("C:", "/cygdrive/c")
				.replaceAll(" ", "\\ ")
				+ "/temp2.tar.gz";

		context.setPassword("");
		try {
			Scp.exec(context, expectedFile.getPath(), linuxPath);
			fail("should throw error");
		} catch (Exception e) {
			// expected
		}
	}

	@Test
	public void testScpWithNeitherSet() throws Exception {
		String linuxPath = new File("target").getCanonicalPath()
				.replaceAll("\\\\", "/").replace("C:", "/cygdrive/c")
				.replaceAll(" ", "\\ ")
				+ "/temp2.tar.gz";

		try {
			Scp.exec(context, expectedFile.getPath(), linuxPath);
			fail("should throw error");
		} catch (Exception e) {
			// expected
		}
	}

	@Test
	public void testScpWithMissingKey() throws Exception {
		String linuxPath = new File("target").getCanonicalPath()
				.replaceAll("\\\\", "/").replace("C:", "/cygdrive/c")
				.replaceAll(" ", "\\ ")
				+ "/temp2.tar.gz";
		context.setPrivateKeyFile(new File("missing"));
		try {
			Scp.exec(context, expectedFile.getPath(), linuxPath);
			fail("should throw error");
		} catch (Exception e) {
			// expected
		}
	}

	@Test
	public void testScpWithBadKey() throws Exception {
		String linuxPath = new File("target").getCanonicalPath()
				.replaceAll("\\\\", "/").replace("C:", "/cygdrive/c")
				.replaceAll(" ", "\\ ")
				+ "/temp2.tar.gz";

		context.setPrivateKeyFile(expectedFile);
		try {
			Scp.exec(context, expectedFile.getPath(), linuxPath);
			fail("should throw error");
		} catch (Exception e) {
			// expected
		}
	}
}
