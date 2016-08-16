package com.wonderant.ssh;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;
import com.wonderant.gzip.TarAndGzip;

public class Jscp {

	private static final Logger log = Logger.getLogger(Jscp.class);

	public static void exec(SecureContext pContext, String pSrcDir,
			String pRemotePath, List<String> pIgnores) throws IOException,
			JSchException {
		if (log.isDebugEnabled()) {
			log.debug(debugLine(pSrcDir, pContext.getUsername(),
					pContext.getHost(), pRemotePath,
					pContext.getPrivateKeyFile()));
			log.debug("ignoring: " + pIgnores);
		}

		String filename = new File(pSrcDir).getName() + ".tar.gz";
		String filepath = pSrcDir + "/../" + filename;
		
		TarAndGzip.folder(new File(pSrcDir), pIgnores);
		if (log.isInfoEnabled()) {
			log.info("tar'ing: "
					+ new File(filepath).getAbsolutePath());
		}
		Scp.exec(pContext, filepath, "'" + pRemotePath + "/" + filename + "'");
		if (log.isInfoEnabled()) {
			log.info("scp'ing: " + filepath + " "
					+ pContext.getUsername() + "@" + pContext.getHost() + ":"
					+ pRemotePath + "/" + filename);
		}
		if (log.isDebugEnabled()) {
			log.debug("remote extraction: tar zxvf " + filename);
		}
		Exec.exec(pContext, "cd '" + pRemotePath + "'; tar zxvf '" + filename
                + "'; rm '" + filename + "'");
		if (log.isDebugEnabled()) {
			log.debug("Succesfully copied");
		}
		FileUtils.deleteQuietly(new File(filepath));
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
