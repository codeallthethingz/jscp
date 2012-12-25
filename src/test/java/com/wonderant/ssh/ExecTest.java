package com.wonderant.ssh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ExecTest {
	private SecureContext context;

	@Before
	public void Before() throws IOException {
		context = new SecureContext(System.getProperty("user.name"),
				"localhost").setTrustAllHosts(true).setPrivateKeyFile(
				new File("C:\\cygwin\\home\\will\\.ssh\\id_rsa"));
	}

	@Test
	public void testPwd() throws Exception {
		String result = Exec.exec(context, "pwd");
		assertEquals("/home/will\n", result);
	}

	@Test
	public void testBadCmd() throws Exception {
		try {
			Exec.exec(context, "aoeu");
			fail("should error");
		} catch (Exception e) {
			// expected
			assertEquals("bash: aoeu: command not found\n", e.getMessage());
		}
	}
}
