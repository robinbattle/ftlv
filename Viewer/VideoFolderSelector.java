/**
filechooser taken from http://www.java2s.com/Code/Java/Swing-JFC/SelectadirectorywithaJFileChooser.htm
 */

import java.io.File;

import javax.swing.*;

public class VideoFolderSelector {

	boolean warning = false;
	
	VideoFolderSelector() {
		// set system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	String select() {
		if(warning == true){
			JOptionPane.showMessageDialog(null, "Please select an valid path");
			warning = false;
		}
		
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("../../"));
		chooser.setDialogTitle("Please Select the Video Folder");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			System.out.println("No Selection ");
			System.exit(0);
		}

		File[] files = chooser.getSelectedFile().listFiles();
		int valid = 0;
		for (File f : files) {
			if (f.getName().equals("fixedImage")) {
				valid++;
			}
			if (f.getName().equals("matrix")) {
				valid++;
			}
			if (f.getName().equals("shadow")) {
				valid++;
			}
		}
		if (valid == 3) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else {
			warning = true;
			return select();
		}

	}

}
