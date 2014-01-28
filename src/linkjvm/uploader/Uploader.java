package linkjvm.uploader;

import java.io.File;
import java.util.Properties;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class Uploader {

	private JSch jsch;
	private String host;
	private String user;
	private String password;
	
	public Uploader(String host,String user, String password) {
		this.jsch = new JSch();
		this.host = host;
		this.user = user;
		this.password = password;
		Properties prop = new Properties();
		prop.put("StrictHostKeyChecking", "no");
		JSch.setConfig(prop);
	}
	
	public boolean upload(File file) {
		String remotePath = "/kovan/lib/"+ file.getName().substring(0, file.getName().lastIndexOf('.'));
		Session session = null;
		try {
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setTimeout(2500);
			session.connect();
			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			channel.mkdir(remotePath);
			channel.put(file.getAbsolutePath(), remotePath);
			channel.disconnect();
			session.disconnect();
			return true;
		} catch (JSchException | SftpException e) {
			e.printStackTrace();
			return false;
		}
	}
}
