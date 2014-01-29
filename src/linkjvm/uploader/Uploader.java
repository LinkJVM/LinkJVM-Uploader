package linkjvm.uploader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
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
	
	public boolean upload(File file) throws IllegalStateException {
		if(!file.getName().substring(file.getName().lastIndexOf('.')).equals(".jar"))
			throw new IllegalStateException("You can only upload .jar files");
		String fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
		String libPath = "/kovan/lib/" + fileName;
		String binPath = "/kovan/bin/" + fileName;
		String tmpPath = "/tmp/linkjvm-uploader-" + System.currentTimeMillis();
		Session session = null;
		try {
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setTimeout(7000);
			session.connect();
			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			try {
				channel.rm(libPath + "/*");
				channel.rm(binPath + "/*");
				channel.rmdir(binPath);
				channel.rmdir(libPath);
				channel.rm("/kovan/archives/" + fileName);
			}catch (SftpException e) {}
			channel.mkdir(libPath);
			channel.mkdir(binPath);
			channel.mkdir(tmpPath);
			channel.put(file.getAbsolutePath(), libPath);
			OutputStream out = channel.put(tmpPath + "/"+fileName+".c");
			out.write("#include <stdlib.h>\n".getBytes());
			out.write(("int main() {return system(\"export BOOTCLASSPATH=\\\"/usr/share/jamvm/classes.zip:/usr/share/classpath/glibj.zip:/usr/share/classpath/tools.zip:/usr/lib/linkjvmjava.jar\\\"; "
					+ "export CLASSPATH=\\\"/usr/share/jamvm/classes.zip:/usr/share/classpath/glibj.zip:/usr/share/classpath/tools.zip:/usr/lib/linkjvmjava.jar:.\\\""
					+ "export LD_LIBRARY_PATH=\\\"/usr/lib/classpath:/usr/lib\\\"; java -jar " + libPath + "/" + file.getName() + "\");}").getBytes());
			out.flush();
			out.close();
			channel.disconnect();
			runCommand(session, "gcc " + tmpPath + "/" + fileName + ".c -o " + binPath + "/" + fileName);
			runCommand(session, "/usr/bin/kar-gen /kovan/archives/" + fileName);
			session.disconnect();
			return true;
		} catch (SftpException e) {
			e.printStackTrace();
			return false;
		} catch (JSchException e) {
				e.printStackTrace();
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void runCommand(Session session, String command) throws JSchException, IOException{
		 Channel channel = session.openChannel( "exec" );
		    channel.setInputStream( null );
		    channel.setOutputStream( System.out );

		    ( (ChannelExec) channel ).setCommand( command );

		    channel.connect();

		    InputStream in = channel.getInputStream();

		    byte[] tmp = new byte[1024];
		    while ( true )
		    {
		        while ( in.available() > 0 )
		        {
		            int i = in.read( tmp, 0, 1024 );
		            if ( i < 0 )
		            {
		                break;
		            }
		            System.out.print( new String( tmp, 0, i ) );
		       }
		       if ( channel.isClosed() )
		       {
		           break;
		       }
		       try
		       {
		           Thread.sleep( 1000 );
		       }
		       catch ( Exception ee )
		       {
		       }
		   }
		   channel.disconnect();
	}
}
