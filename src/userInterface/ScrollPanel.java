package userInterface;

import gameComponents.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * 
 * Virtual Optics
 * <p>
 * This class is a scroll panel that will display all the GameComponents that are available 
 * to the user to add to the lab panel when he clicks on a ComponentLabel present in 
 * this scroll panel
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class ScrollPanel extends JPanel{
	
	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = 4043558362147707744L;
	/**
	 * Available components that the user can add to the laboratory panel
	 */
	ArrayList[] availableComponents = new ArrayList[8];
	/**
	 * Active components that will be displayed
	 */
	static ArrayList<GameComponent> activeComponents = new ArrayList<>();
	/**
	 * list of the components that will be displayed at the ScrollPanel
	 */
	ComponentLabel[] componentList;
	/**
	 * number of components that was previously available to the user (initialized at 0)
	 */
	int oldNumberOfComponents = 0;
	/**
	 * number of components that are currently available to the user to be put on the 
	 * laboratory panel
	 */
	int numberOfComponents = 0;
	/**
	 * the label that is clicked by the user
	 */
	public int labelClicked;
	
	/**
	 * constructor, initializes the availableComponents list and the activeComponents list, 
	 * requests focus on this scroll panel, create the ComponentLabel according to the 
	 * length of the availableComponents list, set the size of the ScrollPanel
	 * @param availableComponents
	 * @param activeComponents
	 */
	public ScrollPanel(ArrayList[] availableComponents, ArrayList<GameComponent> activeComponents){
		// copy the list that is in Lab
		this.availableComponents = availableComponents;
		ScrollPanel.activeComponents = activeComponents;
		// request focus
		this.setRequestFocusEnabled(true);
		this.setFocusable(true);
		
		for(int i = 0; i < 8; i++){
			numberOfComponents += this.availableComponents[i].size();
		}
		
		componentList = new ComponentLabel[numberOfComponents];
		
		// set size of the scrollpanel
		setPreferredSize(new Dimension(10, 128 * numberOfComponents));
		// add a border
		setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	/**
	 * check the number of components and add them to the scroll panel every time the frame 
	 * is repainted to ensure that the user can only add what is available to him.
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		
		//new labels only if the number of components changed
		if(numberOfComponents != oldNumberOfComponents){
		// check the number of components in the availableComponents list
			int componentNumber = 0;
			for(int i = 0; i < 8; i++){
				for(int j = 0; j < (availableComponents[i]).size(); j++){
					componentList[componentNumber] = new ComponentLabel((GameComponent) availableComponents[i].get(j));
					componentNumber++;
				}
			}
			
			for(int i = 0; i < numberOfComponents; i++)
				add(componentList[i]);
		}
		oldNumberOfComponents = numberOfComponents;
	}
	
	/**
	 * this method adds a component to the Lab panel every time a ComponentLabel has been clicked
	 * @param labelClicked the ComponentLabel that has been clicked
	 * @param parentWidth Width of the Lab Panel
	 * @param parentHeight Height of the Lab Panel
	 */
	public void addActiveComponent(int labelClicked, int parentWidth, int parentHeight){
		GameComponent componentToAdd = null;
		if (getParent().getMousePosition() == null)
			return;
		
		componentToAdd = componentList[labelClicked].getComponentClicked();
		
		// add a ray
		if (componentToAdd.getClass().getSimpleName().compareTo("Ray") == 0){
			componentToAdd = new Ray(new Point2D.Double(parentWidth - 140, (this.getParent().getMousePosition().y)));
		}
		
		// add a mirror
		else if (componentToAdd.getClass().getSimpleName().compareTo("Mirror") == 0){
			componentToAdd = new Mirror(new double[] {parentWidth - 200, (this.getParent().getMousePosition().y) - 35, parentWidth - 130, (this.getParent().getMousePosition().y) + 35});
		}
		
		// add a curved mirror
		else if (componentToAdd.getClass().getSimpleName().compareTo("CurvedMirror") == 0){
			componentToAdd = new CurvedMirror(new Point2D.Double(0, 0), parentWidth - 200, (this.getParent().getMousePosition().y));
		}
		
		// add a refractive zone
		else if (componentToAdd.getClass().getSimpleName().compareTo("RefractiveZone") == 0){
			componentToAdd = new RefractiveZone(new Point2D.Double(parentWidth - 250, (this.getParent().getMousePosition().y)));
		}
		
		// add a lens
		else if (componentToAdd.getClass().getSimpleName().compareTo("Lens") == 0){
			componentToAdd = new Lens(new Point2D.Double(parentWidth - 250, (this.getParent().getMousePosition().y) - 100));
		}
		
		// add an obstacle
		else if (componentToAdd.getClass().getSimpleName().compareTo("Obstacle") == 0){
			componentToAdd = new Obstacle(new Point2D.Double(parentWidth - 175, (this.getParent().getMousePosition().y)));
		}
		
		// add a prism
		else if (componentToAdd.getClass().getSimpleName().compareTo("Prism") == 0){
			componentToAdd = new Prism(new Point2D.Double(parentWidth - 200, this.getParent().getMousePosition().y));
		}
		
		// add a target
		else if (componentToAdd.getClass().getSimpleName().compareTo("Target") == 0){
			componentToAdd = new Target(new Point2D.Double(parentWidth - 140, (this.getParent().getMousePosition().y)));
		}
		// add the component that is clicked to the Lab panel
		activeComponents.add(componentToAdd);
	}
}
