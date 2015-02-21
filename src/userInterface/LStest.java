package userInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;

public class LStest {
	public static void main(String[] args){
		
		int progress = 1;
		
		try {
			JFileChooser jfc = new JFileChooser(new File("./user"));
			jfc.setApproveButtonText("save");
			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File project = jfc.getSelectedFile();
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(project));
				out.writeObject(progress);
				out.close();
			}
		}
		catch (Exception ex) {

		}
	}
}
