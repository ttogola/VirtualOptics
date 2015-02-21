package userInterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Learn extends LevelSelection {

	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = -6906997243557015372L;

	/**
	 * constructor, set the maxlevel to the number of information boxes available
	 */
	Learn(){
		maxLevel = 5;
	}
	
	/**
	 * Display the information the user requests by clicking on a level label in the 
	 * form of a rounded panel. Information will be displayed in a textArea and 
	 * the image that is linked to the information will be displayed in an informationPanel 
	 * @see userInterface.LevelSelection#displayNewLevel(int)
	 */
	void displayNewLevel(int infoNumber){
		// rounded panel that will be displayed
		final RoundedPanel p = new RoundedPanel();
		// set layout of the rounded panel
		p.setLayout(null);
		add(p, 0);
		// set dimension and position of the rounded panel
		p.setBounds((int) (getWidth()/3) - 50, (int) (getHeight()/4) - 125, 500, 600);
		// set the background's color
		p.setBackground(new Color(255, 255, 255, 250));
		
		// info panel is the panel where the information will be present
		RoundedPanel infoPanel = new RoundedPanel();
		infoPanel.setLayout(null);
		JTextArea infoLabel = new JTextArea(getInfo(infoNumber));
		// set font and other parameters of the textArea
		infoLabel.setFont(new Font("Verdana",1,12));
		infoLabel.setBackground(Color.LIGHT_GRAY);
		infoPanel.add(infoLabel);
		infoLabel.setBounds(7,7,435,268);
		infoPanel.repaint();
		
		// set size and position of the image panel in the form of a rounded panel
		RoundedPanel infoPicturePanel = new RoundedPanel();
		JLabel infoPictureLabel = getInfoPicture(infoNumber);
		infoPicturePanel.add(infoPictureLabel);
		infoPictureLabel.setBounds(7,7,435,205);
		
		// add information and picture panel to the main rounded panel
		p.add(infoPanel);
		p.add(infoPicturePanel);
		infoPicturePanel.setBounds(25, 25, 450, 225);
		infoPanel.setBounds(25, 250, 450, 325);
		
		// add mouse listener to the Learn panel so a click will remove the current
		// visible roudned panel
		addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent e) {
	        	remove(p);
	        	repaint();
	        }
		});
		
		repaint();
	}
	
	/**
	 * get the information to be displayed according to the infoNumber
	 * @param infoNumber Number that represents the information to be displayed
	 * @return Return the information to be displayed in the form of a string
	 */
	String getInfo(int infoNumber){
		String info = "";
		// get the information from text file
		try {
			Scanner inputInfo = new Scanner(new File("."+File.separator+"learning"+File.separator+"info"+infoNumber+".txt"));
			while(inputInfo.hasNext()){
				info += inputInfo.nextLine()+"\n";
			}
		}
		
		catch (Exception ex) {
		}
		return info;
	}
	
	/**
	 *  get the information picture to be displayed according to the infoNumber
	 * @param infoNumber infoNumber Number that represents the information picture to be displayed
	 * @return Return the information to be displayed in the form of a JLabel
	 */
	JLabel getInfoPicture(int infoNumber){
		JLabel infoLabel = null;
		BufferedImage infoPicture = null;
		
		try {
			infoPicture = ImageIO.read(new File("."+File.separator+"learning"+File.separator+"infoPicture"+infoNumber+".PNG"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				infoPicture = ImageIO.read(new File("."+File.separator+"learning"+File.separator+"infoPicture"+infoNumber+".JPG"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		infoLabel = new JLabel(new ImageIcon(infoPicture));
		return infoLabel;
	}
	
	/**
	 * load the images that are needed for this panel
	 * @see userInterface.LevelSelection#loadImages()
	 */
	@Override
	void loadImages(){
		try {
			background = ImageIO.read(new File("./images/learningcenter.png"));
			levelIcon = ImageIO.read(new File("./images/selectionframe.png"));
			lockIcon = ImageIO.read(new File("./images/lock.png"));
			leftIcon = ImageIO.read(new File("./images/leftarrow.png"));
			rightIcon = ImageIO.read(new File("./images/rightarrow.png"));
			backIcon = ImageIO.read(new File("./images/backicon.png"));
		}
				
		catch(IOException ioEx){
		}
	}
	
	/**
	 * Overrides the method in LevelSelection so that nothing is locked but the information 
	 * number is still displayed
	 * @see userInterface.LevelSelection#applyLocks(int)
	 */
	@Override
	void applyLocks(int labelNumber){
		  getLabel(labelNumber).setLayout(null);
		  getLabel(labelNumber).removeAll();
		  JLabel numberLabel = new JLabel(getLabel(labelNumber).getDisplayedMnemonic()+"");
		  numberLabel.setFont(new Font("Jokerman", 1, 50));
		  getLabel(labelNumber).add(numberLabel);
		  numberLabel.setBounds(65,25,75,75);
	}
	
	/**
	 * Overrides the method in LevelSelection so that nothing is locked 
	 * @see userInterface.LevelSelection#checkLevelLock(int)
	 */
	@Override
	Boolean checkLevelLock(int levelToCheck){
		return true;
	}
}
