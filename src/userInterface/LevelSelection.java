package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * Virtual Optics
 * <p>
 * Class extends JPanel class from java and represents a panel where the user can 
 * select a level according to his current progress and will send him to the level he 
 * has chosen. 
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class LevelSelection extends JPanel{

	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = -2000834623025421169L;
	/**
	 *  the first level that is displayed
	 */
	int level = 1;
	/**
	 *  initialize this panel's number
	 */
	int panelNumber = 0;
	/**
	 *  the maximum level the user can play at
	 */
	int maxLevel = 15;
	/**
	 *  initialize the user's progress
	 */
	static int progress = 0;
	
	/**
	 *  declare the levelIcon image
	 */
	BufferedImage levelIcon;
	/**
	 * declare the leftIcon image
	 */
	BufferedImage leftIcon;
	/**
	 * declare the rightIcon image
	 */
	BufferedImage rightIcon;
	/**
	 * declare the lockIcon image
	 */
	BufferedImage lockIcon;
	/**
	 * declare the background image
	 */
	BufferedImage background;
	/**
	 * declare the backIcon image
	 */
	BufferedImage backIcon;
	
	/**
	 * declare the leftLabel JLabel
	 */	
	JLabel leftLabel;
	/**
	 * declare the rightLabel JLabel
	 */
	JLabel rightLabel;
	/**
	 * declare the backLabel JLabel
	 */
	JLabel backLabel;
	
	/**
	 * declare the firstLabel JLabel that will be displayed as a level that the user can select
	 */	
	JLabel firstLabel;
	/**
	 * declare the secondLabel JLabel that will be displayed as a level that the user can select
	 */
	JLabel secondLabel;
	/**
	 * declare the thirdLabel JLabel that will be displayed as a level that the user can select
	 */
	JLabel thirdLabel;
	/**
	 * declare the fourthLabel JLabel that will be displayed as a level that the user can select
	 */
	JLabel fourthLabel;
	/**
	 * declare the fifthLabel JLabel that will be displayed as a level that the user can select
	 */
	JLabel fifthLabel;
	
	/**
	 * declare the labelNumber that represents the number of the labels on screen
	 */
	int labelNumber;
	
	/**
	 * Constructor set the background to white
	 */
	LevelSelection() {
        setBackground(Color.white);
        
		getProgress();
		
		setLayout(null);
		
		loadImages();
		
		setLabels();
		
		addListeners();
		
		repaint();
	}
	
	/**
	 * paint all the graphical components that will be visible to the user
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		
		// draw backgrounds
		g.drawImage(background, (getWidth()-background.getWidth())/2, (getHeight()+5-background.getHeight())/2, null);
		
		// set the position of the back, left and right labels
		backLabel.setBounds(5,5,40,40);
		leftLabel.setBounds(15, getHeight() - 50, 67, 38);
		rightLabel.setBounds(getWidth() - 80, getHeight() - 50, 67, 38);
		
		// set the position of the levelLabels according to their number
		for(int labelNumber = 1, x = (int)(getWidth()/5), y = (int)(getHeight()/3); labelNumber <= 5; labelNumber++){
			getLabel(labelNumber).setBounds(x, y, 166, 114);
			
			x = setLabelPosition(labelNumber, x, y)[0];
			y = setLabelPosition(labelNumber, x, y)[1];
			
			nameLabel(labelNumber);
			
			applyLocks(labelNumber);
		}
	}
	
	/**
	 * set the position of the levelLabels according to their number and changes
	 * row when the label is greater than 3
	 * @param labelNumber
	 * @param x X position of the levelLabel
	 * @param y Y position of the levelLabel
	 * @return Returns the position of the levelLabel in the form of coordinates
	 */
	int[] setLabelPosition(int labelNumber, int x, int y){
		if(labelNumber <= 2){
			x += (int)(getWidth()/5);
		}
		
		if(labelNumber == 3){
			x = (int)(getWidth()/4);
			y += (int)(getHeight()/3);
		}
		
		if(labelNumber == 4){
			x += (int)(getWidth()/4);
		}
		
		return new int[]{x,y};
	}
	
	/**
	 * display a lock on the levelLabel if the level number exceeds the number of the user's 
	 * current progress.
	 * @param labelNumber
	 */
	void applyLocks(int labelNumber){
		JLabel currentLabel = getLabel(labelNumber);
		// set layout of the levelLabel to null
		currentLabel.setLayout(null);
		// remove every thing in the label
		currentLabel.removeAll();
		// get the label's number
		JLabel numberLabel = new JLabel(currentLabel.getDisplayedMnemonic()+"");
		// display the label's number on the label
		numberLabel.setFont(new Font("Jokerman", 1, 50));
		currentLabel.add(numberLabel);
		numberLabel.setBounds(45,25,75,75);
		
		// check if the user can select this level, if not, display a lock on the label
		if(currentLabel.getDisplayedMnemonic() > progress){
			JLabel lockLabel = new JLabel(new ImageIcon(lockIcon));
			currentLabel.add(lockLabel,0);
			lockLabel.setBounds(100,20,50,75);
		}
	}
	
	/**
	 * add an invisible title to the level labels in the form of mnemonics 
	 * @param labelNumber is the number of the level the label is currently representing
	 */
	void nameLabel(int labelNumber){
		switch(labelNumber){
		case 1:
			firstLabel.setDisplayedMnemonic(level + (labelNumber - 1));
			break;
		case 2:
			secondLabel.setDisplayedMnemonic(level + (labelNumber - 1));
			break;
		case 3:
			thirdLabel.setDisplayedMnemonic(level + (labelNumber - 1));
			break;
		case 4:
			fourthLabel.setDisplayedMnemonic(level + (labelNumber - 1));
			break;
		case 5:
			fifthLabel.setDisplayedMnemonic(level + (labelNumber - 1));
			break;
		}
	}

	/**
	 * import the required images
	 */
	void loadImages(){
		try {
			background = ImageIO.read(new File("./images/levelselection.png"));
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
	 * add listeners to the all the labels
	 */
	void addListeners(){
		// add listeners to the level labels according to their mnemonics
		for(labelNumber = 1; labelNumber <= 5; labelNumber++){
			final JLabel levelLabel = getLabel(labelNumber);
			levelLabel.setLayout(new BorderLayout());
			levelLabel.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	            	if(checkLevelLock(levelLabel.getDisplayedMnemonic())){
	            		displayNewLevel(levelLabel.getDisplayedMnemonic());
	            	}
	            }
	        });
			add(levelLabel);
		}
		
		// add listener to the left label to turn pages to the left
		leftLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
            	pageChange("left");
            	repaint();
            }
        });
		add(leftLabel);
		
		// add listener to the right label to turn pages to the right
		rightLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
            	pageChange("right");
            	repaint();
            }
        });
		add(rightLabel);
		
		// add listener to the back label to go back to the main menu
		backLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
            	MainFrame.changePanel(0, 0);
            }
        });
		add(backLabel);
	}
	
	/**
	 * change page according to the direction
	 * @param direction Left or Right determined by the label pressed
	 */
	void pageChange(String direction){
		if(direction == "right" && level < maxLevel - 5){
			level += 5;
		}
		else if (direction == "left" && level > 5){
			level -= 5;
		}
	}
	
	/**
	 * check if the user can play this level
	 * @param levelToCheck the level to check
	 * @return true if the user can play this level, false if he can't
	 */
	Boolean checkLevelLock(int levelToCheck){
		if(levelToCheck <= progress){
			return true;
		}
		
		else{
			return false;
		}
	}
	
	/**
	 * get the current progress of the user according to the progress file
	 */
	static void getProgress(){
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("./user/progress")));
			progress = (int) in.readObject();
			in.close();
		}
		
		catch (Exception ex) {
		}
	}
	
	/**
	 * make a new Level panel and displays it on the frame if the user clicks on one of the 
	 * level label
	 * @param progress represents the level he clicked
	 */
	void displayNewLevel(int progress){
		MainFrame.changePanel(4, progress);
	}
	
	/**
	 * When the user finishes the last level that is available to him, his progress will change 
	 * which enables him to play the next level.
	 */
	static void levelUP(){	
		progress++;
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("./user/progress")));
			out.writeObject(progress);
			out.close();
		}
		catch (Exception ex) {
		}
	}
	
	/**
	 * get the label that is requested
	 * @param labelNumber label that is requested
	 * @return Return the label that is requested
	 */
	JLabel getLabel(int labelNumber){
		switch (labelNumber){
		case 1:
			return firstLabel;
		case 2:
			return secondLabel;
		case 3:
			return thirdLabel;
		case 4:
			return fourthLabel;
		case 5:
			return fifthLabel;
		}
		return null;
	}
	
	/**
	 * add the images to their respective Label
	 */
	void setLabels(){
		firstLabel = new JLabel(new ImageIcon(levelIcon));
		secondLabel = new JLabel(new ImageIcon(levelIcon));
		thirdLabel = new JLabel(new ImageIcon(levelIcon));
		fourthLabel = new JLabel(new ImageIcon(levelIcon));
		fifthLabel = new JLabel(new ImageIcon(levelIcon));
		leftLabel = new JLabel(new ImageIcon(leftIcon));
		rightLabel = new JLabel(new ImageIcon(rightIcon));
		backLabel = new JLabel(new ImageIcon(backIcon));
	}
}