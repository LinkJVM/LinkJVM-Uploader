package linkjvm.uploader;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class JARtoLinkUploader {

	public static void main(String[] args) {
		new JARtoLinkUploader();
	}
	
	private Uploader uploader;
	
	public JARtoLinkUploader() {
		String host = JOptionPane.showInputDialog(null, "Please enter the address of the Link" + '\n' +
				"Example: 192.168.10.1", "Address of the Link", JOptionPane.QUESTION_MESSAGE);
		JPanel pwpanel = new JPanel(new GridLayout(2, 1));
		pwpanel.add(new JLabel("Please enter the password if there is one!"));
		JPasswordField pw = new JPasswordField();
		pwpanel.add(pw);
		JOptionPane.showConfirmDialog(null, pwpanel, "Password of the Link", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		String password = "";
		for(char c : pw.getPassword())
			password += c;
		uploader = new Uploader(host, "root", password);
		new GUIControler(uploader);
	}
}
