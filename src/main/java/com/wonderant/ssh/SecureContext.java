package com.wonderant.ssh;

import java.io.File;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SecureContext {
	private String password;
	private File privateKeyFile;
	private boolean trustAllHosts = false;
	private String username;
	private String host;

	public SecureContext(String pUsername, String pHost) {
		super();
		username = pUsername;
		host = pHost;
	}

	public String getPassword() {
		return password;
	}

	public SecureContext setPassword(String pPassword) {
		password = pPassword;
		return this;
	}

	public File getPrivateKeyFile() {
		return privateKeyFile;
	}

	public SecureContext setPrivateKeyFile(File pPrivateKeyFile) {
		privateKeyFile = pPrivateKeyFile;
		return this;
	}

	public boolean isTrustAllHosts() {
		return trustAllHosts;
	}

	public SecureContext setTrustAllHosts(boolean pTrustAllHosts) {
		trustAllHosts = pTrustAllHosts;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public SecureContext setUsername(String pUsername) {
		username = pUsername;
		return this;
	}

	public String getHost() {
		return host;
	}

	public SecureContext setHost(String pHost) {
		host = pHost;
		return this;
	}

	private UserInfo getUserInfo() {
		return new UserInfo() {
			public void showMessage(String pMessage) {
			}

			public boolean promptYesNo(String pMessage) {
				return false;
			}

			public boolean promptPassword(String pMessage) {
				return password != null;
			}

			public boolean promptPassphrase(String pMessage) {
				return false;
			}

			public String getPassword() {
				return password;
			}

			public String getPassphrase() {
				return null;
			}
		};
	}

	private Properties getConfig() {
		Properties config = new java.util.Properties();
		if (isTrustAllHosts()) {
			config.put("StrictHostKeyChecking", "no");
		}
		return config;
	}

	public Session createSession() throws JSchException {
		JSch jsch = new JSch();
		if (getPrivateKeyFile() != null) {
			jsch.addIdentity(getPrivateKeyFile().getAbsolutePath());
		}
		Session session = jsch.getSession(getUsername(), getHost(), 22);
		session.setConfig(getConfig());
		session.setUserInfo(getUserInfo());
		return session;
	}
}
