package userInterface;

import gameComponents.GameComponent;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * Virtual Optics
 * <p>
 * This class represents the in game menu that is displayed when the user clicks on the 
 * icon that is on the top left corner of the Lab or Level panel
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class GameMenu extends JLabel{
	
	/**
	 * declares a rounded panel that represents the in game menu
	 */
	RoundedPanel p = new RoundedPanel();
	/**
	 * declares a menuPanel in the form of a RoundedPanel
	 */
	RoundedPanel menuPanel = new RoundedPanel();
	
	/**
	 * insure that the leave alert box is only displayed once
	 */
	boolean once = false;
	/**
	 * insure that the leave alert box is only displayed once
	 */
	static String responseClicked;
	/**
	 * insure that the leave alert box is only displayed once
	 */
	static String secondResponseClicked;
	
	/**
	 * constant representing the answer of the user to the alert dialog
	 */
	final int yes = 0;
	/**
	 * constant representing the answer of the user to the alert dialog
	 */
	final int no = 1;
	/**
	 * constant representing the answer of the user to the alert dialog
	 */
	final int cancel = 2;
	
	/**
	 * declares the menuIcon that will be present in the Level or Lab panel
	 */
	BufferedImage menuIcon;
	/**
	 * declares the homeIcon that will be present in the GameMenu panel
	 */
	BufferedImage homeIcon;
	/**
	 * declares the ladderIcon that will be present in the GameMenu panel
	 */
	BufferedImage ladderIcon;
	/**
	 * declares the loadIcon that will be present in the GameMenu panel
	 */
	BufferedImage loadIcon;
	/**
	 * declares the newIcon that will be present in the GameMenu panel
	 */
	BufferedImage newIcon;
	/**
	 * declares the resetIcon that will be present in the GameMenu panel
	 */
	BufferedImage resetIcon;
	/**
	 * declares the saveIcon that will be present in the GameMenu panel
	 */
	BufferedImage saveIcon;
	/**
	 * declares the controlsIcon that will be present in the GameMenu panel
	 */
	BufferedImage controlsIcon;
	
	/**
	 * declares the transparency of the menuIcon
	 */
	private boolean transparent;
	/**
	 * declares the gameMode it is currently displayed
	 */
	String gameMode;
	
	/**
	 * Constructor, GameMenu will depend on the gameMode it is currently displayed
	 * @param gameMode String represents the gameMode it is currently displayed
	 */
	public GameMenu(final String gameMode){
		this.gameMode = gameMode;
		
		loadImages();
		
		p.setLayout(null);
    	
		// addMouseListerners according to the gameMode it is currently displayed
		if(gameMode == "Lab"){
			addMouseListener("labMenu", this);
		}
		
		else if (gameMode == "Level"){
			addMouseListener("levelMenu", this);
		}
	}
	
	/**
	 * paint the GameMenu icon to the Level or Lab panel and check the transparency and apply it
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g.create();
		if (transparent){
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		}
		g2.drawImage(menuIcon, 0, 0, null);
	}
	
	/**
	 * set the transparency of the menuIcon
	 * @param transparent
	 */
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}
	
	/**
	 * add all the mouseListeners
	 * @param componentName The name of the component to receive a mouseListener
	 * @param component The component to receive a mouseListener
	 */
	void addMouseListener(final String componentName, Component component){
		// add listener according to the name of the component
		component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	
            	responseClicked = null;
            	getParent().add(p, 0);
        		p.setBounds((int) (getParent().getWidth()/2) - 125, (int) (getParent().getHeight()/2) - 125, 250, 250);
        		p.setBackground(new Color(255, 255, 255, 250));
        		getParent().repaint();
        		
        		// when the menuIcon is clicked in Lab, display all the following labels
        		if(componentName == "labMenu"){
            		if(!once){
	            		once = true;
	            		JLabel homeLabel = new JLabel(new ImageIcon(homeIcon));
	            		addMouseListener("goMainMenu", homeLabel);
	            		p.add(homeLabel);
	            		homeLabel.setBounds(90, 5, 75, 75);
	            		
	            		JLabel clearLabel = new JLabel(new ImageIcon(newIcon));
	            		addMouseListener("clearAll", clearLabel);
	            		p.add(clearLabel);
	            		clearLabel.setBounds(15, 85, 75, 75);
	            		
	            		JLabel saveLabel = new JLabel(new ImageIcon(saveIcon));
	            		addMouseListener("save", saveLabel);
	            		p.add(saveLabel);
	            		saveLabel.setBounds(160, 85, 75, 75);
	            		
	            		JLabel loadLabel = new JLabel(new ImageIcon(loadIcon));
	            		addMouseListener("load", loadLabel);
	            		p.add(loadLabel);
	            		loadLabel.setBounds(90, 170, 75, 75);
	            		
	            		JLabel controlsLabel = new JLabel(new ImageIcon(controlsIcon));
	            		addMouseListener("controls", controlsLabel);
	            		p.add(controlsLabel);
	            		controlsLabel.setBounds(90, 88, 75, 75);
	            		
	        			addMouseListener("Lab", getParent());
            		}
            		
            	}
            	
        		// when the menuIcon is clicked in Level, display all the following labels
            	if(componentName == "levelMenu"){
            		if(!once){
                		once = true;
                		JLabel homeLabel = new JLabel(new ImageIcon(homeIcon));
                		addMouseListener("goMainMenu", homeLabel);
                		p.add(homeLabel);
                		homeLabel.setBounds(90, 5, 75, 75);
                		
                		JLabel ladderLabel = new JLabel(new ImageIcon(ladderIcon));
                		addMouseListener("selectLevel", ladderLabel);
                		p.add(ladderLabel);
                		ladderLabel.setBounds(160, 85, 75, 75);
                		
                		JLabel resetLabel = new JLabel(new ImageIcon(resetIcon));
                		addMouseListener("resetLevel", resetLabel);
                		p.add(resetLabel);
                		resetLabel.setBounds(90, 170, 75, 75);
                		
                		JLabel controlsLabel = new JLabel(new ImageIcon(controlsIcon));
	            		addMouseListener("controls", controlsLabel);
	            		p.add(controlsLabel);
	            		controlsLabel.setBounds(15, 85, 75, 75);
                		
                		addMouseListener("Level", getParent());
            		}
            	}
            	
            	//add listener to respective JLabel
            	if(componentName == "goMainMenu"){
            		goMainMenu(gameMode);
            	}
            	//add listener to respective JLabel
				if(componentName == "clearAll"){
					clearAll(gameMode);
				}
				//add listener to respective JLabel
				if(componentName == "save"){
					save(gameMode);
				}
				//add listener to respective JLabel
				if(componentName == "load"){
					load(gameMode);
				}
				//add listener to respective JLabel
				if(componentName == "selectLevel"){
					selectLevel(gameMode);
            	}
				//add listener to respective JLabel
				if(componentName == "resetLevel"){
					resetLevel(gameMode);
            	}
				//add listener to respective JLabel
				if(componentName == "controls"){
					getControls(gameMode);
				}
				//add listener to Lab or Level to remove in game menu when the panel is clicked
				if(componentName == "Lab" || componentName == "Level"){
				   	getParent().remove(p);
				   	getParent().remove(menuPanel);
				   	getParent().repaint();
                }
            }
        });
	}
	
	/**
	 * import all the images needed in this panel
	 */
	void loadImages(){
		try {
			menuIcon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"gameMenuIcon.png"));
			homeIcon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"homeicon.png"));
			ladderIcon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"laddericon.png"));
			loadIcon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"loadicon.png"));
			newIcon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"newicon.png"));
			resetIcon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"reseticon.png"));
			saveIcon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"saveicon.png"));
			controlsIcon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"controlsicon.png"));
		} catch (IOException e) {
		}
	}
	
	/**
	 * Alert the user he is about to lose his progress
	 * @param gameMode Alert changes according to the gameMode he is in
	 * @return Return the answer of the alert
	 */
	int leaveAlert(String gameMode){
		JFrame frame = new JFrame();
		
		//0 = yes, 1 = no, 2 = cancel
		if(gameMode == "Lab"){
			Object[] options = {"Yes", "No", "Cancel"};
			return JOptionPane.showOptionDialog(frame, "Would you like to save your progress before you leave?", "Leave Alert", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,	null, options, options[2]);
		}
		
		//0 = yes, 1 = no
		else{
			Object[] options = {"Yes", "No"};
			return JOptionPane.showOptionDialog(frame, "All unsaved progress will be lost.\nAre you sure you want to leave?", "Leave Alert", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,	null, options, options[1]);
		}
	}
	
	/**
	 * Display the controls of the game according to the game mode the user is currently in
	 * @param gameMode The gameMode the user is currently in
	 */
	void getControls(String gameMode){
		// display the controls according to the game mode
		if(gameMode == "Level"){
			menuPanel.setLayout(null);
			JTextArea infoLabel = new JTextArea(getControlsString(gameMode));
			infoLabel.setFont(new Font("Verdana",1,12));
			infoLabel.setBackground(Color.LIGHT_GRAY);
			menuPanel.add(infoLabel);
			infoLabel.setBounds(7,7,500,600);
			menuPanel.repaint();
		}
		// display the controls according to the game mode
		else if(gameMode == "Lab"){
			menuPanel.setLayout(null);
			JTextArea infoLabel = new JTextArea(getControlsString(gameMode));
			infoLabel.setFont(new Font("Verdana",1,12));
			infoLabel.setBackground(Color.LIGHT_GRAY);
			menuPanel.add(infoLabel);
			infoLabel.setBounds(7,7,500,600);
			menuPanel.repaint();
		}
		
		getParent().add(menuPanel, 0);
		menuPanel.setBounds((int) (getParent().getWidth()/2) - 325, (int) (getParent().getHeight()/2) - 350, 525, 625);
		repaint();
	}
	
	/**
	 * get the controls in the form of a string according to the game mode 
	 * @param gameMode The gameMode the user is currently in
	 * @return Return the controls in the form of a string
	 */
	String getControlsString(String gameMode){
		
		String controls = "";
		// get controls from its text file
		try {
			Scanner inputInfo = new Scanner(new File("."+File.separator+"learning"+File.separator+gameMode+"controls.txt"));
			while(inputInfo.hasNext()){
				controls += inputInfo.nextLine()+"\n";
			}
		}
		
		catch (Exception ex) {
		}
		return controls;
	}
	
	/**
	 * Available in Lab & Level, goes to main menu when pressed
	 * @param gameMode The gameMode the user is currently in
	 */
	void goMainMenu(String gameMode){
		int alert = leaveAlert(gameMode);
		// goes to main menu if alert passes in Lab
		if(gameMode == "Lab" && alert == yes){
			MainFrame.changePanel(0, 0);			
		}
		
		//does pass if user cancels save in Lab
		else if(gameMode == "Lab" && alert == no){
			save(gameMode);
			if(secondResponseClicked.equals("cancel")){
				return;
			}
			MainFrame.changePanel(0, 0);
		}
		
		// goes to main menu if alert passes in Level
		else if(gameMode == "Level" && alert == yes){
			MainFrame.changePanel(0, 0);		
		}
	}
	
	/**
	 * Available in Level, resets all the components in the Level according to their default position
	 * @param gameMode The gameMode the user is currently in
	 */
	void resetLevel(String gameMode){
		// alert the user
		int alert = leaveAlert(gameMode);
		
		// execute if the user passes the alert
		if(alert == yes){
		
			int level = ((Level) getParent()).currentLevel;
			
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("."+File.separator+"levels"+File.separator + level)));
				((Level) getParent()).setActiveComponents((ArrayList<GameComponent>) in.readObject(), (ArrayList<Boolean>) in.readObject(), (ArrayList<Point>) in.readObject());
				in.close();
			}
			
			catch (Exception ex) {
			}
			
			((Level) getParent()).repaint();
		}
	}
	
	/**
	 * Available in Level, return to the level selection panel
	 * @param gameMode The gameMode the user is currently in
	 */
	void selectLevel(String gameMode){
		
		int alert = leaveAlert(gameMode);
		
		// execute if the user passes the alert
		if(alert == 0){
			MainFrame.changePanel(1, 0);
		}
	}
	
	/**
	 * Available in Lab, save the current progress
	 * @param gameMode The gameMode the user is currently in
	 */
	void save(String gameMode){
		// open JFileChooser to let user set name of the save and save to the designated folder
		try {
			JFileChooser jfc = new JFileChooser(new File("."+File.separator+"user"));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Virtual Optics Projects", "op");
		    jfc.setFileFilter(filter);
			jfc.setName("Save");
			jfc.setApproveButtonText("save");
			int option = jfc.showOpenDialog(null);
			if (option == JFileChooser.APPROVE_OPTION) {
				File project = jfc.getSelectedFile();
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(project));
				// write all the necessary objects to the .op file
				out.writeObject(((Lab)getParent()).getActiveComponents());
				out.writeObject(((Lab)getParent()).getReleased());
				out.writeObject(((Lab)getParent()).getMarkers());
				out.close();
				if (!project.getName().contains(".op"))
					project.renameTo(new File(project.getCanonicalPath()+".op"));
			}
			else if (option == JFileChooser.CANCEL_OPTION){
				secondResponseClicked = "cancel";
				responseClicked = "cancel";
			}
		}
		catch (Exception ex) {
		}
	}
	
	/**
	 * Available in Lab, loads the save the user chose.
	 * @param gameMode The gameMode the user is currently in
	 */
	void load(String gameMode){
		int alert = leaveAlert(gameMode);
		
		// alert user
		if(alert == yes){
			save(gameMode);
		}
		
		else if(alert == cancel){	
			return;
		}
		
		((Lab) getParent()).load();
		
		secondResponseClicked = null;
	}
	
	/**
	 * Available in Lab, clears all the active components in the Lab panel
	 * @param gameMode The gameMode the user is currently in
	 */
	void clearAll(String gameMode){
		int alert = leaveAlert(gameMode);
		
		// alert user
		if(alert == yes){
			save(gameMode);
		}
		
		else if(alert == cancel){
			return;
		}
		
		//  execute if user passes alert and doesn't cancel
		if(responseClicked != "cancel"){
			((Lab) getParent()).activeComponents.clear();
			((Lab) getParent()).released.clear();
			((Lab) getParent()).markers.clear();
			((Lab) getParent()).scrollContainer.removeAll();
			((Lab) getParent()).initializeAvailableComponents();
			((Lab) getParent()).makeScrollPanel();
			getParent().repaint();
		}		
	}
}