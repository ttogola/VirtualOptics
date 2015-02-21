package userInterface;

import java.awt.*;
import javax.swing.*;

/**
 * Virtual Optics
 * <p>
 * This is the main class of the application, it contains the main method of the program 
 * and when run, will display a frame which contains the application.
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class MainFrame extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5136660416044311436L;
	/** 
	 * The application
	 */
	static MainFrame frame = new MainFrame();
	/**
	 * first panel of the application that will be created
	 */
	static JPanel currentPanel = new MainMenu();
	
	/**
	 * final constant that represent the state (MainMenu) of the frame
	 * as in which panel it is currently showing
	 */
	final static int MAINMENU = 0;
	/**
	 * final constant that represent the state (LEVELSELECTION) of the frame
	 * as in which panel it is currently showing
	 */
	final static int LEVELSELECTION = 1;
	/**
	 * final constant that represent the state (LEARN) of the frame
	 * as in which panel it is currently showing
	 */
	final static int LEARN = 2;
	/**
	 * final constant that represent the state (LAB) of the frame
	 * as in which panel it is currently showing
	 */
	final static int LAB = 3;
	/**
	 * final constant that represent the state (LEVEL) of the frame
	 * as in which panel it is currently showing
	 */
	final static int LEVEL = 4;
	
	/**
	 * main method of the program crates a frame that contains the program
	 * @param args
	 */
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				//create jframe
				JFrame mainFrame = new JFrame("RGB Relic");
				//put application in jframe to run in form of application
				mainFrame.add(frame);
				//configure dimensions, visibility of frame
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();				
			    mainFrame.setSize(dim.width, dim.height-50);
			    mainFrame.setLocationRelativeTo(null);
				mainFrame.setVisible(true);
				mainFrame.setBackground(Color.white);
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//initialize panel in the frame
				frame.add(currentPanel);
				currentPanel.repaint();
			}
		});
	}
	
	/**
	 * @param panel represents the panel the program has to switch to
	 * @param progress current progress of the user, i.e. the level he wants to go to
	 */
	static void changePanel(int panel, int progress) {
		// changes to the panel
		switch(panel){
		// remove the current panel from the frame
		// replace the current panel 
		// add the current panel back to the frame
			case MAINMENU:
				
				frame.getContentPane().remove(currentPanel);
				currentPanel = new MainMenu();
				frame.getContentPane().add(currentPanel);
				frame.revalidate();
				break;
			
			case LAB:
				frame.getContentPane().remove(currentPanel);
				currentPanel = new Lab();
				frame.getContentPane().add(currentPanel);
				frame.revalidate();
				break;
				
			case LEVELSELECTION: 
				frame.getContentPane().remove(currentPanel);
				currentPanel = new LevelSelection();
				frame.getContentPane().add(currentPanel);
				frame.revalidate();
				break;
			
			case LEARN:
				frame.getContentPane().remove(currentPanel);
				currentPanel = new Learn();
				frame.getContentPane().add(currentPanel);
				frame.revalidate();
				break;
				
			case LEVEL: 
				frame.getContentPane().remove(currentPanel);
				currentPanel = new Level(progress);
				frame.getContentPane().add(currentPanel);
				frame.revalidate();
				break;
		}
	}
}