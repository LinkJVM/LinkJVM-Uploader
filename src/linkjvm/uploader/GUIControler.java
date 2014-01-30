package linkjvm.uploader;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class GUIControler extends JFrame {
	
	private ContentPane pane;
	private Uploader uploader;
	
	public GUIControler(Uploader uploader) {
		super();
		this.setSize(400, 250);
		this.setTitle("JARtoLinkUploader");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.uploader = uploader;
		pane = new ContentPane();
		this.add(pane);
		this.setVisible(true);
	}
	
	private class ContentPane extends JPanel {
		
		private JTextArea output;
		private JButton uploadButton;
		private JButton changeHost;
		private JButton changePassword;
		private JButton reUpload;
		private JFileChooser explorer;
		private JScrollPane scrollpane;
		private JLabel link;
		
		public ContentPane() {
			this.setLayout(new GridLayout(3, 1));
			JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
			
			JPanel changeButtons = new JPanel(new GridLayout(1, 2));
			
			ButtonListener bl = new ButtonListener();
			
			uploadButton = new JButton("Upload JAR-File");
			uploadButton.addActionListener(bl);
			uploadButton.setActionCommand("up");
			
			changeHost = new JButton("Change Link address");
			changeHost.addActionListener(bl);
			changeHost.setActionCommand("ch");
			
			changePassword = new JButton("Change password");
			changePassword.addActionListener(bl);
			changePassword.setActionCommand("cp");
			
			changeButtons.add(changeHost);
			changeButtons.add(changePassword);
			
			reUpload = new JButton("Reupload: No File");
			reUpload.addActionListener(bl);
			reUpload.setActionCommand("ru");
			reUpload.setEnabled(false);
			
			link = new JLabel("Link address: "+uploader.getHost());
			link.setHorizontalAlignment(JLabel.CENTER);
			
			buttonPanel.add(uploadButton);
			buttonPanel.add(changeButtons);
			buttonPanel.add(reUpload);
			
			output = new JTextArea();
			explorer = new JFileChooser();
			explorer.setMultiSelectionEnabled(false);
			explorer.setAcceptAllFileFilterUsed(false);
			explorer.setFileFilter(new FileNameExtensionFilter("JAR-File .jar", "jar"));
			explorer.setFileHidingEnabled(true);
			
			output.setEditable(false);
			scrollpane = new JScrollPane(output);
					
			this.add(link);
			this.add(buttonPanel);
			this.add(scrollpane);
		}
		
		public void writeOutput(String text) {
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minutes = c.get(Calendar.MINUTE);
			int seconds = c.get(Calendar.SECOND);
			output.append(String.format("%02d", hour)+':'+String.format("%02d", minutes)+':'+String.format("%02d", seconds)+
					"> " + text + '\n');
		}
	}
	
	private class ButtonListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("up")) {
				int pressed = pane.explorer.showDialog(pane, "Upload");
				if(pressed == JFileChooser.APPROVE_OPTION) {
					File file = pane.explorer.getSelectedFile();
					try{
						if(uploader.upload(file)) {
							pane.writeOutput("Upload successful: " + file.getName());
							if(uploader.isFileUploaded())
								pane.reUpload.setText("Reupload: "+ file.getName());
								pane.reUpload.setEnabled(true);
						}else {
							pane.writeOutput("Error while uploading: " + file.getAbsolutePath());
							pane.reUpload.setText("Reupload: No File");
							pane.reUpload.setEnabled(false);
						}
					}catch (IllegalStateException ex) {
						pane.reUpload.setEnabled(false);
						pane.reUpload.setText("Reupload: No File");
						pane.writeOutput("Error while uploading: " + file.getAbsolutePath() + " | " + ex.getMessage());
					}
				}
			}else if(e.getActionCommand().equals("ch")) {
				String host = JOptionPane.showInputDialog(pane, "Please enter the address of the Link" + '\n' +
						"Example: 192.168.10.1", "Address of the Link", JOptionPane.QUESTION_MESSAGE);
				uploader.setHost(host);
				pane.link.setText("Link address: "+uploader.getHost());
			}else if(e.getActionCommand().equals("cp")){
				JPanel pwpanel = new JPanel(new GridLayout(2, 1));
				pwpanel.add(new JLabel("Please enter the password if there is one!"));
				JPasswordField pw = new JPasswordField();
				pwpanel.add(pw);
				JOptionPane.showConfirmDialog(pane, pwpanel, "Password of the Link", 
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				String password = "";
				for(char c : pw.getPassword())
					password += c;
				uploader.setPassword(password);
			}else {
				try {
					if(uploader.reupload())
						pane.writeOutput("Upload successful: " + uploader.getLastFile().getName());
					else {
						pane.writeOutput("Error while uploading: " + uploader.getLastFile().getAbsolutePath());
						pane.reUpload.setText("Reupload: No File");
						pane.reUpload.setEnabled(false);
					}
				}catch (IllegalStateException ex) {
					pane.reUpload.setEnabled(false);
					pane.reUpload.setText("Reupload: No File");
					pane.writeOutput("Error while uploading: " + uploader.getLastFile().getAbsolutePath() + " | " + ex.getMessage());
				}
			}
		}
	}
}
