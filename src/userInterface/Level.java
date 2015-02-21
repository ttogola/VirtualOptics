package userInterface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import java.net.URL;

import gameComponents.*;

/**
 * 
 * Virtual Optics
 * <p>
 * this class displays a level panel that will contain the level the user currently
 * selected to play on
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class Level extends Lab {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4177572802648463050L;
	
	/**
	 * the current level the user is in
	 */ 
	int currentLevel;
	/**
	 * the timer that indicates for how long the user has to hit the target for
	 */ 
	Timer pauseTimer = new Timer(200, new PauseTimeListener());
	/**
	 * indicates if the target is hit
	 */ 
	int targetHit;
	/**
	 * info panel in the form of a rounded panel that shows information at the beginning 
	 * of a level
	 */ 
	RoundedPanel infoPanel = new RoundedPanel();
	/**
	 * declare the background of the level, is only used for dynamic background
	 */ 
	BufferedImage background = null;
	
	/**
	 * 
	 * @param level indicates to the constructor which level the user selected to play
	 */
	Level(int level) {
		/**
		 * indicate the current level the user has selected
		 */ 
		currentLevel = level;
		/**
		 * set layout of the panel to null to be able to place labels on exact positions
		 */ 
		setLayout(null);
		getImages();
		
		/**
		 * loads the active components of the selected level
		 */ 
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("."+File.separator+"levels"+File.separator + level)));
			setActiveComponents((ArrayList<GameComponent>) in.readObject(), (ArrayList<Boolean>) in.readObject(), (ArrayList<Point>) in.readObject());
			in.close();
		}
		
		catch (Exception ex) {
		}
		
		/**
		 * remove available component panel
		 */ 
		scrollContainer.removeAll();
		scrollPaneSize = 0;
		
		/**
		 * normal level will be displayed if the level panel created does not
		 * represent a dynamic background
		 */ 
		if (level > 0)
		{
			infoPanel(currentLevel);
			
			/**
			 * add listeners to the panel so that a click would remove the infoPanel displayed
			 */
			addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	                remove(infoPanel);
	                repaint();
	            }
	        });
		}
		
		/**
		 * the dynamic background settings will engage if the level is 0, which is the level
		 * that indicates the dynamic background
		 */ 
		else{
			this.removeAll();
			setBackground(Color.LIGHT_GRAY);
		}
	}
	
	/**
	 * An information panel will be displayed in the form of a rounded panel which will
	 * indicate the predetermined information to the user according to the current
	 * level he is in, indicated by the @param tipNumber.
	 */
	void infoPanel(int tipNumber){

		// loads special tips when the level is under or equal to 5
		if(tipNumber <= 5)
		{
			infoPanel.setLayout(null);
			JTextArea infoLabel = new JTextArea(getTip(tipNumber));
			infoLabel.setFont(new Font("Verdana",1,12));
			infoLabel.setBackground(Color.LIGHT_GRAY);
			infoPanel.add(infoLabel);
			infoLabel.setBounds(7,7,135,180);
			infoPanel.repaint();
			add(infoPanel);
			infoPanel.setBounds(50,50,150,200);
		}
		
		// display the current level the user is playing the the level is over 5
		else {
			infoPanel.setLayout(null);
			JTextArea infoLabel = new JTextArea(tipNumber+"");
			infoLabel.setFont(new Font("Verdana",1,30));
			infoLabel.setBackground(Color.LIGHT_GRAY);
			infoPanel.add(infoLabel);
			infoLabel.setBounds(7,7,42,35);
			infoPanel.repaint();
			add(infoPanel);
			infoPanel.setBounds(50,50,60,50);
		}
	}
	
	/**
	 * imports the tip and return it in the form of a String
	 * @param tipNumber indicates what is the tip to be displayed
	 * @return method returns the tip to be displayed in the form of a string
	 */
	String getTip(int tipNumber){
		String tip = "";
		try {
			Scanner inputInfo = new Scanner(new File("."+File.separator+"learning"+File.separator+"tip"+tipNumber+".txt"));
			while(inputInfo.hasNext()){
				tip += inputInfo.nextLine()+"\n";
			}
			
			inputInfo.close();
		}
		
		catch (Exception ex) {
		}
		return tip;
	}
	
	/**
	 * Overrides the method from Lab so its functions are neglected
	 * @see userInterface.Lab#updateProperties(char)
	 */
	@Override
	public void updateProperties(char c) {
		//does nothing
	}
	
	/**
	 * @see userInterface.Lab#makeGameMenu()
	 */
	@Override
	void makeGameMenu() {
			GameMenu gm = new GameMenu("Level");
			add(gm);
			gm.setBounds(0,0,25,25);
	}
	
	/**
	 * starts the timer to count the amount of time that the target has been hit
	 * @see userInterface.Lab#levelCompleted(int)
	 */
	@Override
	void levelCompleted(int index) {
		//put rounded panel instead of dialog box
		targetHit = index;
		pauseTimer.start();
	}
	
	/**
	 * Listener that detects the amount of time the target has been hit
	 * and clears the level if the target has been hit for long enough
	 */
	class PauseTimeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (((Target)activeComponents.get(targetHit)).isHit()) {
				
				JOptionPane.showMessageDialog(null, "Level passed!");
				
				if(LevelSelection.progress == currentLevel) {
					LevelSelection.levelUP();
					new LevelSelection().displayNewLevel(LevelSelection.progress);
				}
				else
					new LevelSelection().displayNewLevel(currentLevel+1);
				}
			
			pauseTimer.stop();
		}
	}
	
	/**
	 * animates the background by rotating components in the activeComponents list
	 * according to their position in the list
	 */
	void animateDynamicBackground(){
		for (int i = 0; i < activeComponents.size(); i++) {
			if (activeComponents.get(i).isRotateable()) {
				if (i%2 == 0)
					activeComponents.get(i).rotate(1);
				else if (i%2 == 1)
					activeComponents.get(i).rotate(-1);
			}
		}
	}
	
	/**
	 * import the images needed
	 */
	void getImages(){
		try {
			background = ImageIO.read(new File("."+File.separator+"images"+File.separator+"Mainmenu.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * paints the background of the level if it has one (only at level 0), 
	 * set the width and height of the panel according to the size of the frame, 
	 * paint the ray, 
	 * paint the markers which indicate the positions of the gameComponents
	 * @see userInterface.Lab#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g0) {	
		super.paintComponent(g0);
		
		if (currentLevel == 0) {
			g0.drawImage(background, (getWidth()-background.getWidth())/2, (getHeight()+5-background.getHeight())/2, null);
		}
		
		w = (int)(getWidth()*1/scale)-scrollPaneSize;
		h = (int)(getHeight()*1/scale);
		
		for (int i = 0; i < activeComponents.size(); i++) {
			if (activeComponents.get(i) instanceof Ray) 
				((Ray)activeComponents.get(i)).impact(activeComponents);
		}
		
		Graphics2D g = (Graphics2D)g0.create();
		g.scale(scale, scale);

		g.setColor(Color.YELLOW.brighter());
    	g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.draw(selectionRec);
		
		drawComponents(g, activeComponents);	
		
		updateMarkers();
		if (scaling) markers.clear();
		drawMarkers(g);
		
		revalidate();
		requestFocus();
		g.dispose();
		
		checkWin();
	}
}
