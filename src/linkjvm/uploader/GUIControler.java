package linkjvm.uploader;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class GUIControler extends JFrame {
	
	private ContentPane pane;
	private Uploader uploader;
	
	public GUIControler(Uploader uploader) {
		super();
		this.setSize(400, 200);
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
		private JFileChooser explorer;
		private JScrollPane scrollpane;
		
		public ContentPane() {
			this.setLayout(new GridLayout(2, 1));
			uploadButton = new JButton("Upload JAR-File");
			uploadButton.addActionListener(new ButtonListener());
			output = new JTextArea();
			explorer = new JFileChooser();
			explorer.setMultiSelectionEnabled(false);
			explorer.setAcceptAllFileFilterUsed(false);
			explorer.setFileFilter(new FileNameExtensionFilter("JAR-File .jar", "jar"));
			explorer.setFileHidingEnabled(true);
			output.setEditable(false);
			scrollpane = new JScrollPane(output);
			this.add(uploadButton);
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
			int pressed = pane.explorer.showDialog(pane, "Upload");
			if(pressed == JFileChooser.APPROVE_OPTION) {
				File file = pane.explorer.getSelectedFile();
				try{
					if(uploader.upload(file))
						pane.writeOutput("Upload successful: " + file.getName());
					else
						pane.writeOutput("Error while uploading: " + file.getAbsolutePath());
				}catch (IllegalStateException ex) {
					pane.writeOutput(ex.getMessage());
				}
			}
		}
	}
}
