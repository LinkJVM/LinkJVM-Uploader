package linkjvm.uploader;

import javax.swing.JOptionPane;

public class JARtoLinkUploader {

	public static void main(String[] args) {
		new JARtoLinkUploader();
	}
	
	private Uploader uploader;
	
	public JARtoLinkUploader() {
		String host = JOptionPane.showInputDialog(null, "Please enter the address of the Link" + '\n' +
				"Example: 192.168.10.1", "Address of the Link", JOptionPane.QUESTION_MESSAGE);
		String password = JOptionPane.showInputDialog(null, "Please enter the password if there is one!",
				"Password of the Link", JOptionPane.QUESTION_MESSAGE);
		uploader = new Uploader(host, "root", password);
		new GUIControler(uploader);
	}
}
