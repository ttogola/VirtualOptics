package userInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * 
 * Virtual Optics
 * <p>
 * The main menu that will be displayed when the program launches or when 
 * the user decides to go back to from another panel.
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
class MainMenu extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3997444270865524297L;
	/**
	 * background image that will be used in the panel
	 */
	BufferedImage background;
	/**
	 * Storyline image that will be used in the panel
	 */
	BufferedImage SLicon;
	/**
	 * Learning center image that will be used in the panel
	 */
	BufferedImage LCicon;
	/**
	 * Laboratory image that will be used in the panel
	 */
	BufferedImage LBicon;
	/**
	 * Quit image that will be used in the panel
	 */
	BufferedImage QTicon;
	
	/**
	 * the label of the Storyline that will be used in the panel
	 */
	JLabel SLLabel;
	/**
	 * the label of the Learning center that will be used in the panel
	 */
	JLabel LCLabel;
	/**
	 * the label of the Laboratory that will be used in the panel
	 */
	JLabel LBLabel;
	/**
	 * the label of the Quit that will be used in the panel
	 */
	JLabel QTLabel;
	
	/**
	 * initialize a level to be displayed as the dynamic background
	 */
	Level dynamicBackground = new Level(0);
	/**
	 * set the timer to animate the components in dynamic background
	 */
	Timer dynamicBackgroundTimer = new Timer(25, new dynamicBackgroundListener());
	
	/**
	 * Set the background, buttons, layout of the panel.
	 * Also starts the timer of the dynamic background
	 */
	MainMenu(){
		setBackground(Color.LIGHT_GRAY);
		setButtons();
		setLayout(null);
		setDynamicBackground();
		repaint();
		revalidate();
		dynamicBackgroundTimer.start();
	}
	
	/**
	 * set import images, add images in labels, set listeners to labels and add labels to the panel
	 * the positions of the labels are set according to he size of the panel
	 */
	void setButtons(){
		try {
			// import image for background
			background = ImageIO.read(new File("."+File.separator+"images"+File.separator+"Mainmenu.png"));
			
			//set and import for the storyline label
			SLicon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"SLicon.png"));
			SLLabel = new JLabel(new ImageIcon(SLicon));			
			SLLabel.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mousePressed(MouseEvent e){
	            	SLLabel.setBorder(BorderFactory.createLoweredSoftBevelBorder());
				}
				
				@Override
				public void mouseReleased(MouseEvent e){
					SLLabel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
	            	MainFrame.changePanel(1, 0);
				}
	        });
			add(SLLabel,0);
			SLLabel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
			
			//set and import for the learningcenter label
			LCicon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"LCicon.png"));
			LCLabel = new JLabel(new ImageIcon(LCicon));
			LCLabel.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e){
	            	LCLabel.setBorder(BorderFactory.createLoweredSoftBevelBorder());
				}
				
				@Override
				public void mouseReleased(MouseEvent e){
					LCLabel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
	            	MainFrame.changePanel(2, 0);
				}
	        });
			add(LCLabel,0);
			LCLabel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
			
			//set and import for the laboratory label
			LBicon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"LBicon.png"));
			LBLabel = new JLabel(new ImageIcon(LBicon));
			LBLabel.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e){
	            	LBLabel.setBorder(BorderFactory.createLoweredSoftBevelBorder());
				}
				
				@Override
				public void mouseReleased(MouseEvent e){
					LBLabel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
	            	MainFrame.changePanel(3, 0);
				}
	        });
			add(LBLabel,0);
			LBLabel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
			
			//set and import for the quit label
			QTicon = ImageIO.read(new File("."+File.separator+"images"+File.separator+"QTicon.png"));
			QTLabel = new JLabel(new ImageIcon(QTicon));
			QTLabel.addMouseListener(new MouseAdapter() {
	            
	            @Override
				public void mousePressed(MouseEvent e){
	            	QTLabel.setBorder(BorderFactory.createLoweredSoftBevelBorder());
				}
				
				@Override
				public void mouseReleased(MouseEvent e){
					QTLabel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
	            	System.exit(0);
				}
	        });
			add(QTLabel,0);
			QTLabel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
			
		}
		
		//catch exceptions if the images are not found
		catch (IOException e) {
		}
	}
	
	/**
	 *  paint component so that it will display
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		//set the dimension in case the user resizes the frame
		dynamicBackground.setBounds(0,0,dim.width-30, dim.height-50);
		SLLabel.setBounds(25, getHeight()/5*2, 174, 30);
		LCLabel.setBounds(getWidth()/2 - 78, getHeight()/5*2 - 10, 156, 56);
		LBLabel.setBounds(getWidth() - 217, getHeight()/5*2, 192, 28);
		QTLabel.setBounds(getWidth()/2 - 40, getHeight() - 50, 79, 40);
	    getParent().setSize(dim.width, dim.height-50);
	}
	
	/**
	 * add dynamic background to the panel
	 */
	void setDynamicBackground(){
		add(dynamicBackground);
	}
	
	/**
	 * animate the background according to the timer
	 */
	class dynamicBackgroundListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dynamicBackground.animateDynamicBackground();
		}
	}
}