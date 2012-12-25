package com.wonderant.ssh;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;
import com.wonderant.gzip.TarAndGzip;

public class Rsync {

	private static final Logger log = Logger.getLogger(Rsync.class);

	public static void sync(String pSrcDir, String pRemoteUser,
			String pRemoteHost, String pRemotePath, List<String> pIgnores,
			File pPrivateKeyFile) throws IOException, JSchException {
		sync(pSrcDir, pRemoteUser, pRemoteHost, pRemotePath, pIgnores,
				pPrivateKeyFile, null);
	}

	public static void sync(String pSrcDir, String pRemoteUser,
			String pRemoteHost, String pRemotePath, List<String> pIgnores,
			String pPassword) throws IOException, JSchException {
		sync(pSrcDir, pRemoteUser, pRemoteHost, pRemotePath, pIgnores, null,
				pPassword);
	}

	private static void sync(String pSrcDir, String pRemoteUser,
			String pRemoteHost, String pRemotePath, List<String> pIgnores,
			File pPrivateKeyFile, String pPassword) throws IOException,
			JSchException {
		if (log.isDebugEnabled()) {
			log.debug(debugLine(pSrcDir, pRemoteUser, pRemoteHost, pRemotePath,
					pPrivateKeyFile));
			log.debug("ignoring: " + pIgnores);
		}

		SecureContext context = new SecureContext(pRemoteUser, pRemoteHost);
		context.setPassword(pPassword).setPrivateKeyFile(pPrivateKeyFile)
				.setTrustAllHosts(true);

		String filename = new File(pSrcDir).getName() + ".tar.gz";
		TarAndGzip.folder(new File(pSrcDir), pIgnores);
		if (log.isInfoEnabled()) {
			log.info("tar'ing: "
					+ new File(pSrcDir + "/../" + filename).getAbsolutePath());
		}
		Scp.exec(context, pSrcDir + "/../" + filename, pRemotePath + "/"
				+ filename);
		if (log.isInfoEnabled()) {
			log.info("scp'ing: " + pSrcDir + "/../" + filename + " "
					+ pRemoteUser + "@" + pRemoteHost + ":" + pRemotePath + "/"
					+ filename);
		}
		if (log.isDebugEnabled()) {
			log.debug("remote extraction: tar zxvf " + filename);
		}
		Exec.exec(context, "cd " + pRemotePath + "; tar zxvf " + filename
				+ "; rm " + filename);
		if (log.isDebugEnabled()) {
			log.debug("Succesfully copied");
		}
		FileUtils.deleteQuietly(new File(pSrcDir + "/../" + filename));
	}

	private static String debugLine(String pSrcDir, String pRemoteUser,
			String pRemoteHost, String pRemotePath, File pPrivateKeyFilePath) {
		return "scp "
				+ pSrcDir
				+ " "
				+ pRemoteUser
				+ "@"
				+ pRemoteHost
				+ ":"
				+ pRemotePath
				+ " using "
				+ (pPrivateKeyFilePath != null ? "private key file: "
						+ pPrivateKeyFilePath.getAbsolutePath() : "password");
	}
}
