/**
filechooser taken from http://www.java2s.com/Code/Java/Swing-JFC/SelectadirectorywithaJFileChooser.htm
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.*;

public class VideoSetter {

	boolean warning = false;
	boolean ifFileChosed = false;
	boolean invalid = false;
	boolean done = false;

	JFrame frame;
	JPanel panel;
	JFileChooser chooser;
	JButton fileChooserButton;
	JLabel imageSetPath;
	JTextField thresholdTextField;

	JButton confirmButton;

	VideoSetter() {
		// set system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();

		}
		frame = new JFrame("Video Setter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(500, 100));
		frame.setLocation(400, 400);
		frame.pack();
		frame.setVisible(true);
		panel = new JPanel();

		frame.getContentPane().add(panel);

		imageSetPath = new JLabel("No File Chosen");
		chooser = new JFileChooser();
		fileChooserButton = new JButton("...");
		fileChooserButton.addActionListener(new FileChooserButtonListener());

		confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(new ComfirmButtonListener());

		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(1, 2));
		innerPanel.add(imageSetPath);
		innerPanel.add(fileChooserButton);

		thresholdTextField = new JTextField("90");

		panel.setLayout(new GridLayout(3, 2));

		panel.add(new JLabel("Image Set Path: "));
		panel.add(innerPanel);

		panel.add(new JLabel("Threshold: "));
		panel.add(thresholdTextField);

		panel.add(confirmButton);

		panel.updateUI();

	}

	private class FileChooserButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg) {
			try {
				imageSetPath.setText(select());
				ifFileChosed = true;
			} catch (java.lang.NullPointerException e) {
				System.out.println("User Cancelled");
			}
		}
	}

	private class ComfirmButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg) {
			invalid = false;

			File f = new File(imageSetPath.getText());
			if (!ifFileChosed) {
				fileChooserButton.setBackground(Color.red);
				invalid = true;
			} 
			try {
				int i = Integer.valueOf(thresholdTextField.getText());
			} catch (NumberFormatException e) {
				thresholdTextField.setBackground(Color.red);
				invalid = true;
			}
			
			if(!invalid){
				HashMap<String, String> parameters = new HashMap<String, String>();
						
				parameters.put("imageSetPath", imageSetPath.getText());
				parameters.put("threshold", thresholdTextField.getText());
			
				PropertyFile.writeParams(parameters, "../../config.properties");
				
				
				done = true;
				frame.setVisible(false);
				frame.dispose();
			}
		}
	}

	String select() {
		if (warning == true) {
			JOptionPane.showMessageDialog(panel,
					"Folder is empty, please select an valid path");
			warning = false;
		}

		chooser.setCurrentDirectory(new java.io.File("../../"));
		chooser.setDialogTitle("Please Select the Image Set Folder");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			System.out.println("No Selection ");
		}

		if (chooser.getSelectedFile().listFiles().length > 0) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else {
			warning = true;
			return select();
		}

	}

}
