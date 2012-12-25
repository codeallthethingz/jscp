package com.wonderant.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Scp {
	private static final int FATAL_ERROR = 2;
	private static final int ERROR = 1;
	private static final Logger log = Logger.getLogger(Scp.class);

	public static void exec(final SecureContext pContext, String pLocalFile,
			String pRemoteFile) throws JSchException, IOException {
		if (pContext.getPassword() == null
				&& pContext.getPrivateKeyFile() == null) {
			throw new IllegalStateException(
					"Must specifiy either a password or private key file");
		}

		FileInputStream fis = null;
		OutputStream out = null;
		InputStream in = null;
		try {
			Session session = pContext.createSession();
			session.connect();

			Channel channel = sendCommand(pRemoteFile, session);

			
			 out = channel.getOutputStream();
			 in = channel.getInputStream();

			channel.connect();

			if (checkAck(in) != 0) {
				System.exit(0);
			}

			sendFileSize(pLocalFile, out, in);

			fis = sendContent(pLocalFile, out, in);
			out.close();

			channel.disconnect();
			session.disconnect();
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
	}

	private static Channel sendCommand(String pRemoteFile, Session session)
			throws JSchException {
		// exec 'scp -t rfile' remotely
		String command = "scp " + " -t " + pRemoteFile;
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		return channel;
	}

	private static FileInputStream sendContent(String pLocalFile,
			OutputStream out, InputStream in) throws FileNotFoundException,
			IOException {
		FileInputStream fis;
		// send a content of lfile
		fis = new FileInputStream(pLocalFile);
		byte[] buf = new byte[1024];
		while (true) {
			int len = fis.read(buf, 0, buf.length);
			if (len <= 0)
				break;
			out.write(buf, 0, len); // out.flush();
		}
		fis.close();
		fis = null;
		// send '\0'
		buf[0] = 0;
		out.write(buf, 0, 1);
		out.flush();
		if (checkAck(in) != 0) {
			System.exit(0);
		}
		return fis;
	}

	private static void sendFileSize(String pLocalFile, OutputStream out,
			InputStream in) throws IOException {
		File _lfile = new File(pLocalFile);

		// send "C0644 filesize filename", where filename should not include
		// '/'
		long filesize = _lfile.length();
		String streamCommand = "C0644 " + filesize + " ";
		if (pLocalFile.lastIndexOf('/') > 0) {
			streamCommand += pLocalFile.substring(pLocalFile.lastIndexOf('/') + 1);
		} else {
			streamCommand += pLocalFile;
		}
		streamCommand += "\n";
		out.write(streamCommand.getBytes());
		out.flush();
		if (checkAck(in) != 0) {
			System.exit(0);
		}
	}

	static int checkAck(InputStream in) throws IOException {
		int b = in.read();

		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == ERROR || b == FATAL_ERROR) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == ERROR) {
				log.error(sb.toString());
			}
			if (b == FATAL_ERROR) {
				log.error(sb.toString());
			}
		}
		return b;
	}

}