package linkjvm.uploader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
		String fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
		String remotePath = "/kovan/lib/" + fileName;
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
			OutputStream out = channel.put(remotePath + "/"+fileName+".c");
			out.write("#include <stdlib.h>".getBytes());
			out.write(("#define JAR_LOCATION "+remotePath).getBytes());
			out.write(("#define JAR_NAME "+file.getName()).getBytes());
			out.write(("int main() {return system(\"export BOOTCLASSPATH=\"/usr/share/jamvm/classes.zip:/usr/share/classpath/glibj.zip:/usr/share/classpath/tools.zip:/usr/lib/linkjvmjava.jar\"; "
					+ "export CLASSPATH=\"/usr/share/jamvm/classes.zip:/usr/share/classpath/glibj.zip:/usr/share/classpath/tools.zip:/usr/lib/linkjvmjava.jar:.\""
					+ "export LD_LIBRARY_PATH=\"/usr/lib/classpath:/usr/lib\"; java -jar CLASS_LOCATION/JAR_NAME\");}").getBytes());
			out.flush();
			out.close();
			channel.disconnect();
			session.disconnect();
			return true;
		} catch (JSchException | SftpException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
}
