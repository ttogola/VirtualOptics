package userInterface;

import gameComponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.*;
import java.io.*;

import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * Virtual Optics
 * <p>
 * This class displays a panel in which users can experiment 
 * with optical objects
 * </p>
 *  @author Darrin Fong
 *  @author Tieme Togola
 */
public class Lab extends JPanel { 
	
	private static final long serialVersionUID = -7872169687505709389L;
	private final double BIG = 100000;
	private final int MARKERSIZE = 20;
	protected int scrollPaneSize = 127;
	/**
	 * width of the panel
	 */
	protected int w = 800-scrollPaneSize;
	/**
	 * height of the panel
	 */
	protected int h = 600;
	protected double scale = 1;
	protected boolean scaling;
	/**
	 * timer used for animation when the camera view changes
	 */
	private Timer timer = new Timer(1, new TimeListener());
	/**
	 * the index of the component's marker that was clicked
	 */
	private int componentIndex = -1;	//selected marker
	/**
	 * stores the previous x coordinate of the mouse pointer position on the panel
	 */
	private int prevMouseX = 0;
	/**
	 * stores the previous y coordinate of the mouse pointer position on the panel
	 */
	private int prevMouseY = 0;
	/**
	 * difference between the previous and current x coordinate of the mouse pointer
	 */
	private double diffX;
	/**
	 * difference between the previous and current y coordinate of the mouse pointer
	 */
	private double diffY;
	/**
	 * counts the number of times the camera view has been moved in x direction during an animation
	 */
	private int countX;
	/**
	 * counts the number of times the camera view has been moved in y direction during an animation
	 */
	private int countY;
	
	/**
	 * the rectangle enclosing an area to select components
	 */
	protected RectangularShape selectionRec = new Rectangle(-1, -1);
	/**
	 * the position where the user first clicked to make a selection rectangle
	 */
	private Point2D.Double originalPos;
	/**
	 * the position where the user released the mouse button to make a selection rectangle
	 */
	private Point2D.Double endPos;
	
	/**
	 * counts the number of iterations during which a key is held down
	 */
	private int keyHeldCount = 0;
	/**
	 * tells whether the mouse is over a component or not
	 */
	private boolean overComp = false;
	/**
	 * tells whether the user is dragging a component or not
	 */
	private boolean draggingComp = false;
	/**
	 * tells whether the user is resizing a rectangle or not
	 */
	private boolean resizingRectanle = false;
	/**
	 * stores the index of the component currently being resized
	 */
	private int beingResized = -1;
	/**
	 * tells whether the cursor is over a marker or not
	 */
	private boolean overMarker = false;
	/**
	 * tells whether the cursor is over the scroll pane or not
	 */
	private boolean overScrollPane = false;
	/**
	 * tells whether the user is currently changing the camera view or not
	 */
	private boolean changingView = false;
	
	/**
	 * contains the current state of the game
	 */
	private File backupFile;
	
//	protected boolean lvlEditionMode = false;	this feature is not available to the user

	/**
	 * for each active components, at the corresponding index,
	 * it tells whether that component is currently being dragged 
	 * or not
	 */
	ArrayList<Boolean> released = new ArrayList<>();	
	/**
	 * contains the positions of the markers
	 * markers can be clicked to move the camera view
	 * to the corresponding component in the plane
	 * a marker is only displayed if the corresponding
	 * component is out of the camera view
	 */
	ArrayList<Point> markers = new ArrayList<>();	
	/**
	 * contains all the game component objects
	 * that are active on the plane
	 */
	ArrayList<GameComponent> activeComponents = new ArrayList<>();
	/**
	 * contains lists of components that are available to the user
	 */
	protected ArrayList<GameComponent>[] availableComponents = new ArrayList[8];
	
	/**
	 * the panel displaying available components
	 * from this panel, the user can bring in new active components
	 */
	private ScrollPanel scrollPanel;
	private JScrollPane jSPComponents;
	public JPanel scrollContainer = new JPanel();
	
	/**
	 * menu icon from which the user can access the game menu
	 */
	GameMenu gm = new GameMenu("Lab");

	//constructor
	
	Lab() {
		
		setBackground(Color.GRAY);	

		activeComponents.clear();
		
		if (!(this instanceof Level)) {
			initializeAvailableComponents();
			setLayout(null);	//for set bounds to work
			makeScrollPanel();
		}
		
		makeGameMenu();
		
		//if there are already refractive zones on the panel, initialize their indices
		indices();
			
		//listeners
		
		addMouseWheelListener( new MouseAdapter() {
						
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				
				//when the user moves the mouse wheel while holding CTRL key down, scale the camera view
				if (e.isControlDown()) {
					scaling = true;
					if (e.getWheelRotation() > 0)	
						setScale(-0.1);
					else if (e.getWheelRotation() < 0)
						setScale(0.1);
				}
				else {
					
					//rotate components that are selected
					for (int i = 0; i < activeComponents.size(); i++) {
						if (activeComponents.get(i).isSelected() && activeComponents.get(i).isRotateable()) {
							if (e.getWheelRotation() > 0)
								activeComponents.get(i).rotate(1);
							else if (e.getWheelRotation() < 0)
								activeComponents.get(i).rotate(-1);
						}
					}
				}
				repositionAll();
				repaint();
			}
		});
		
		addKeyListener( new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				//when the user lets go of CTRL key, the view snaps back to its original size
				if (scaling) {
					scaling = false;
					scale = 1;
					repaint();
				}
				
				//reset other variables
				resizingRectanle = false;
				beingResized = -1;
				changingView = false;
				refreshCursor();
				keyHeldCount = 0;
			}
			
			@Override
			public void keyTyped(KeyEvent e) {	
				
				updateProperties(e.getKeyChar());
				repaint();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
				createBackup();
				updateProperties(e);
				
				if (e.isControlDown()) {
					changingView = true;
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
				}
				
				if (!e.isShiftDown())
					repositionAll();
				
				repaint();
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				createBackup();
				
				//when the user clicks while holding ALT key down, a new selection rectangle is started
				if (e.isAltDown()) {
					((Rectangle)selectionRec).setLocation(e.getPoint());
					originalPos = new Point2D.Double(e.getX(), e.getY());
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {	
				
				//reset some values when the mouse is released
				
				for (int i = 0; i < released.size(); i++) {
					released.set(i, true);
					indices();
				}
				
				/*
				 * check to see if components are inside the selection rectangle
				 * if so make them selected, otherwise deselect them
				 */
				for (int i = 0; i < activeComponents.size(); i++) {
					if (selectionRec.intersects(activeComponents.get(i).getBox()))
							activeComponents.get(i).setSelected(true);
					else if (!selectionRec.equals(new Rectangle(-1, -1)))
						activeComponents.get(i).setSelected(false);
				}
				
				draggingComp = false;
				resizingRectanle = false;
				beingResized = -1;
				((Rectangle)selectionRec).setRect(new Rectangle(-1, -1));	//this is equivalent to a nonexisting rectangle
				indices();
				repositionAll();
				repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getClickCount() == 2) {	

					//if the user double-clicks on a component, select it
					for (int i = 0; i < activeComponents.size(); i++) {
						if (activeComponents.get(i).contact(new Point2D.Double(e.getPoint().getX(), e.getPoint().getY()))) {
							if (activeComponents.get(i).isSelected())
								activeComponents.get(i).setSelected(false);
							else activeComponents.get(i).setSelected(true);
						}
						else if (!e.isShiftDown())	//if the user was holding shift key, do not deselect other components
							activeComponents.get(i).setSelected(false);
					}
				}
				else if (e.getButton() == MouseEvent.BUTTON3) {	

					//the user has to right click on a marker to activate it
					for (int i = 0; i < markers.size(); i++) {
						if (markers.get(i) == null)
							continue;
						if (e.getPoint().distance(markers.get(i)) < MARKERSIZE) {
							componentIndex = i;
							break;
						}
					}
					
					if (componentIndex == -1) 
						return;
					
					countX = 0;
					countY = 0;
					diffX = (w/2-activeComponents.get(componentIndex).getPosition().getX());
					diffY = (h/2-activeComponents.get(componentIndex).getPosition().getY());
					
					//start the animation to move the camera view to the component corresponding to the marker that was clicked
					timer.start();	
				}
				
				repaint();
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				
				//reset values
				overScrollPane = false;
				overComp = false;
				overMarker = false;

				//check if the cursor is over a component and set overComp var accordingly
				for (int i = 0; i < activeComponents.size(); i++) {
					if (e.getPoint() == null)
						break;
					if (activeComponents.get(i).contact(new Point2D.Double(e.getX(), e.getY()))) 
						overComp = true;	
				}
				
				//check if the cursor is over a marker and set overMarker var accordingly
				for (int i = 0; i < markers.size(); i++) {
					if (e.getPoint() == null || markers.get(i) == null)
						break;
						if (e.getPoint().distance(markers.get(i).getX()+MARKERSIZE/2, markers.get(i).getY()+MARKERSIZE/2) < MARKERSIZE) 
							overMarker = true;
				}

				refreshCursor();
				prevMouseX = e.getX();
				prevMouseY = e.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				/*
				 * if the user holds CTRL key down while dragging the mouse, the camera view is changed in the
				 * direction of motion
				 */
				if (e.isControlDown()) {
					for (int i = 0; i < activeComponents.size(); i++) {
						Point2D.Double p = activeComponents.get(i).getPosition();
						activeComponents.get(i).setPosition(new Point2D.Double((p.getX()+(e.getX()-prevMouseX)), (p.getY()+(e.getY()-prevMouseY))));	
					}
				}
				else if (e.isAltDown()) {

					//update the size and position of the selection rectangle
					endPos = new Point2D.Double(e.getX(), e.getY());
					selectionRec.setFrameFromDiagonal(originalPos, endPos);
				}
				else {
					
					for (int i = 0; i < activeComponents.size(); i++) {

						Point2D.Double p = activeComponents.get(i).getPosition();
						boolean canMove = activeComponents.get(i).isMoveable();

						/*
						 * if the current component is released and another component is being dragged
						 * skip this one and continue through the loop
						 */
						if (released.get(i) && released.contains(false)) 
							continue;

						/*
						 * if the user drags the mouse on this component, move the component with the mouse pointer (drags the component)
						 */
						if ((canMove && !e.isShiftDown() && activeComponents.get(i).contact(new Point2D.Double(e.getX(), e.getY()))) 
								|| !released.get(i)) {
							
							//check that the move will not result in an overlap
							if (!overlap(activeComponents, e.getX()-prevMouseX, e.getY()-prevMouseY, i)) {
								
								activeComponents.get(i).setPosition(new Point2D.Double((p.getX()+(e.getX()-prevMouseX)), (p.getY()+(e.getY()-prevMouseY))));	
								updateReleased(i);
							}
						}

						/*
						 * if the user holds the shift key and drags the corner of a refractive zone
						 * that zone will be resized accordingly
						 */
						if (activeComponents.get(i).getType().equals("RefractiveZone") && e.isShiftDown()) {
							
							RefractiveZone refZone = (RefractiveZone)activeComponents.get(i);
							
							if (e.getPoint().distance(refZone.getBounds()[2], refZone.getBounds()[3]) < 50 && (beingResized == i || beingResized == -1)) { 
								
								refZone.scale(e.getX(), e.getY());	
								resizingRectanle = true;
								beingResized = i;
							}
						}
						/*
						 * if the user holds the shift key and drags the corner of an obstacle object
						 * that obstacle will be resized accordingly
						 */
						else if (activeComponents.get(i) instanceof Obstacle && e.isShiftDown()) {
							
							Obstacle ob = (Obstacle)activeComponents.get(i);
							
							if (e.getPoint().distance(ob.getBounds()[2], ob.getBounds()[3]) < 50 && (beingResized == i || beingResized == -1)) {
								
								//check that the move will not result in an overlap
								if (!overlap(activeComponents, e.getX()-prevMouseX, e.getY()-prevMouseY, i)) {
									
									ob.scale(e.getX(), e.getY());
									resizingRectanle = true;
									beingResized = i;
								}
							}
						}
					}
				
				}
				
				if (released.contains(false)) 
					draggingComp = true;
					
				prevMouseX = e.getX();
				prevMouseY = e.getY();
				repaint();
			}
		});
	}
	
	//reacts to the action events triggered by the timer to produce the camera view changing animation
	class TimeListener implements ActionListener {	
		
		@Override
		public void actionPerformed(ActionEvent e) {	
			
			/*
			 * the amounts by which the camera view will incrementally be moved
			 */
			int dx = 10;
			int dy = 10;
			
			if (Math.abs(countX) >= Math.abs(diffX)) dx = 0;
			else if (diffX < 0) dx = -10;
			
			if (Math.abs(countY) >= Math.abs(diffY)) dy = 0;
			else if (diffY < 0) dy = -10;
			
			
			/*
			 * reposition all components incrementally so that the selected one is
			 * in the middle of the view (center point is h/2,w/2)
			 */
			for (int i = 0; i < activeComponents.size(); i++) {
				Point2D.Double p = activeComponents.get(i).getPosition();
				activeComponents.get(i).setPosition(new Point2D.Double((p.getX()+dx), (p.getY()+dy)));	
			}
			
			countX += dx;
			countY += dy;
			
			if (dx == 0 && dy == 0)
				timer.stop();
			
			repaint();
			componentIndex = -1;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g0) {	
		super.paintComponent(g0);
		
		w = (int)(getWidth()*1/scale)-scrollPaneSize;
		h = (int)(getHeight()*1/scale);

		//edition mode, this feature is not available to the user
//		g0.setColor(Color.RED);
//		if (lvlEditionMode) 
//			g0.drawString("E", 5, 50);
		
		/*
		 * call the impact methods of each ray object in the list of 
		 * active components
		 */
		for (int i = 0; i < activeComponents.size(); i++) {
			
			if (activeComponents.get(i) instanceof Ray) 
				((Ray)activeComponents.get(i)).impact(activeComponents);
		}
		
		Graphics2D g = (Graphics2D)g0.create();
		g.scale(scale, scale);

		//draw the selection rectangle (if any)
		g.setColor(Color.YELLOW.brighter());
    	g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.draw(selectionRec);
		
		drawComponents(g, activeComponents);	
		
		updateMarkers();
		
		//remove all the markers if the user is zooming the camera view
		if (scaling) markers.clear();
		drawMarkers(g);
		
		scrollContainer.setBounds( getWidth() - 128, 0, 130, getHeight());
		revalidate();
		requestFocus();
		g.dispose();
		
		checkWin();	
	}
	/**
	 * adjusts the size and position of the active components
	 * to zoom in or out
	 * @param s amount by which to scale up or down
	 */
	public void setScale(double s) {	
		scale += s;
		if (scale < 0.1)
			scale = 0.1;
		if (scale > 10)
			scale = 10;
	}
	/**
	 * adjusts the look of the cursor depending 
	 * on the current situation
	 */
	public void refreshCursor() {
		
		if (overComp || overMarker || draggingComp || resizingRectanle || overScrollPane)
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		else if (!changingView)
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	/**
	 * this method is not useful here but inherited by the Level subclass
	 * @param index index of the target object that was hit
	 */
	void levelCompleted(int index){
		
	}
	/**
	 * this method is used by the subclass
	 * checks whether the level is won or not
	 */
	public void checkWin() {
		
		if (!(this instanceof Level))
			return;
		
		for (int i = 0; i < activeComponents.size(); i++) {
			
			if (activeComponents.get(i) instanceof Target) {
				
				Target target = (Target)activeComponents.get(i);
				
				//if a target has been hit, the level is won
				if (target.isHit()) {
					levelCompleted(i);
					break;
				}
			}
		}
	}
	/**
	 * draws all the active components on the plane according to their position
	 * @param g graphics component
	 * @param components list of all the active components
	 */
	public void drawComponents(Graphics2D g, ArrayList<GameComponent> components) {
																				
		for (int i = 0; i < components.size(); i++) {
			
			//set brush
			g.setColor(components.get(i).getColor());
			//width of the brush
	        int wid = 2;
	        if (components.get(i).isSelected())	//make the brush larger for selected components
	        	wid = 3;
	        
	        g.setStroke(new BasicStroke(wid, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	        
	        //makes the lines more smooth
	        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g.setRenderingHints(rh);
	        
	        //rectangle bounding the camera view
			Rectangle viewRec = new Rectangle(0, 0, w, h);
			
			//draw the current component using its draw method
	        components.get(i).draw(g, viewRec);	//pass rectangle object of the view to only redraw what is within view for more efficiency

	        /*
	         * when a component is being dragged, show the bounding boxes of all the active components
	         * to let the user know where he can move the objects
	         */
	        if (draggingComp) {	
	        	
	        	//the bounding box is shown with a yellow dashed line
	        	g.setColor(Color.YELLOW);
	 	        float dash1[] = {5.0f};
		        BasicStroke dashed = new BasicStroke(1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,5.0f, dash1, 0.0f);
	        	g.setStroke(dashed);
	        	
	        	g.draw(components.get(i).getBox());	
	        }
		}
	}
	/**
	 * draws all the markers (if any) on the edges of the camera view
	 * @param g graphics component
	 */
	public void drawMarkers(Graphics2D g) {
		
		//makes the markers a bit transparent
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		
		boolean markerUnder = false;	//reset value
		
		for (int i = 0; i < markers.size(); i++) {
			
			if (markers.get(i) != null) {
				
				g.setColor(activeComponents.get(i).getColor());
				g.fillRect((int)markers.get(i).getX(), (int)markers.get(i).getY(), 20, 20);
				
			if (markers.get(i).distance(new Point(0, 0)) < MARKERSIZE)	//checks if under game menu icon
				markerUnder = true;
			}
		}
		
		/*
		 * if a marker is under the menu icon make the menu
		 *  icon transparent so that the user can see markers under it
		 */
		if (markerUnder)
			gm.setTransparent(true);
		else gm.setTransparent(false);				
	}
	/**
	 * updates the positions of all the markers
	 * the position of a marker depends on the
	 * position of its corresponding game component
	 */
	public void updateMarkers() {
		
		if (activeComponents.size() > 0) {
			
			markers.clear();
			
			for (int i = 0; i < activeComponents.size(); i++) {
				
				if (activeComponents.get(i).getPosition().getY() < 0) {
					double x = activeComponents.get(i).getPosition().getX();
					if (x < 0)	x = 0;
					else if (x > w) x = w-MARKERSIZE;
					markers.add(new Point((int)x, 0));
				}
				else if (activeComponents.get(i).getPosition().getY() > h) {
					double x = activeComponents.get(i).getPosition().getX();
					if (x < 0)	x = 0;
					else if (x > w) x = w-MARKERSIZE;
					markers.add(new Point((int)x, h-MARKERSIZE));
				}
				else if (activeComponents.get(i).getPosition().getX() < 0) {
					double y = activeComponents.get(i).getPosition().getY();
					if (y < 0)	y = 0;
					else if (y > h) y = h;
					markers.add(new Point(0, (int)y));
				}
				else if (activeComponents.get(i).getPosition().getX() > w) {
					double y = activeComponents.get(i).getPosition().getY();
					if (y < 0)	y = 0;
					else if (y > h) y = h;
					markers.add(new Point(w-MARKERSIZE, (int)y));	
				}
				else
					/*
					 * if the corresponding component is within the camera view
					 * add null marker
					 */
					markers.add(null);	
			}
		}
	}
	/**
	 * updates the values in the released list
	 * @param i index of the component that is currently being dragged
	 */
	public void updateReleased(int i) {
		
		for (int r = 0; r < released.size(); r++) {	
			if (r != i)
				released.set(r,  true);
		}
		
		released.set(i, false);
	}
	/**
	 * checks if two components are about to overlap, using their bounding boxes
	 * @param components list of active components
	 * @param deltaX amount by which the component is about to be moved in the x direction
	 * @param deltaY amount by which the component is about to be moved in the y direction
	 * @param current index of the component that is currently being moved
	 * @return true if they are about to overlap, false otherwise
	 */
	public boolean overlap(ArrayList<GameComponent> components, int deltaX, int deltaY, int current) {
		
		//components can go through refractive zones, so do not check overlap in this case
		if (components.get(current).getType().equals("RefractiveZone"))
			return false;
		
		//check if rectangle +delta intersects, if yes stop
		for (int i = 0; i < components.size(); i++) {
			
			if (i == current)
				continue;
			
			Rectangle rec = components.get(i).getBox().getBounds();
			
			if (components.get(i).getType().equals("RefractiveZone")) {
				
				if (rec.contains(components.get(current).getBox()) || !rec.intersects(components.get(current).getBox()) || !released.get(current) || resizingRectanle)
					continue;
				else return true;
			}
			else if (components.get(current).contact(rec, deltaX, deltaY)) 
					return true;
		}
		
		return false;
	}
	/**
	 * only applies to refractive zones
	 * checks if any 2 zones intersect, and adjusts their positions consequently
	 * also updates the values of the outer refractive indices of each zone 
	 */
	public void indices() {	

		for (int i = 0; i < activeComponents.size(); i++) {
			
			if (activeComponents.get(i) instanceof RefractiveZone) {
				
				RefractiveZone zone1 = (RefractiveZone)activeComponents.get(i);
				
				//tells whether the current zone is inside another zone or not
				boolean outside = true;
				//distance between the current zone and its closest enclosing zone (initialized to a big value)
				double innerMostDis = BIG;
					
				for (int n = 0; n < activeComponents.size(); n++) {
					
					if (activeComponents.get(n).getType().equals("RefractiveZone") && activeComponents.get(n) != zone1) {
						
						RefractiveZone zone2 = (RefractiveZone)activeComponents.get(n);	
							
						//the center point of zone1
						Point2D.Double center1 = new Point2D.Double(zone1.getBox().getCenterX(), zone1.getBox().getCenterY());

						//if the 2 zones overlap...						
						if (zone1.getBox().intersects(zone2.getBox())) {

							if (zone1.getWidth() > zone2.getWidth() && zone1.getHeight() > zone2.getHeight()) {
								//switch, zone1 should be the smallest, to fit in zone2
								try {
									RefractiveZone temp = (RefractiveZone)zone1.clone();
									zone1 = (RefractiveZone)zone2.clone();
									zone2 = temp;
								}
								catch (Exception e) {
								}
							}
							
							//if zone1 fits into zone2, push it inside using the reposition method
							if (zone2.getBox().contains(center1) && zone1.getType().equals("RefractiveZone")) {	//if more inside..
								while (!zone2.getBox().contains(zone1.getBox())) //push in
									reposition(zone1, zone2, true);
							}
							else if (zone2.getBox().intersects(zone1.getBox()) && !zone2.getBox().contains(center1)) {	//if more outside
								while (zone1.getBox().intersects(zone2.getBox())) 	//otherwise, push out
									reposition(zone1, zone2, false);
							}
						}
						
						if (zone2.getBox().contains(zone1.getBox())) {
							outside = false;	//if zone is at least in one other zone, it is not outside 
								
							/*
							 * if zone1 is inside zone2
							 * set the outer index of the inner zone to that of the outer zone
							 */
							if (center1.distance(zone2.getPosition()) < innerMostDis) {
								
								zone1.setOuterIndex(zone2.getRefractionIndex());
								innerMostDis = center1.distance(zone2.getPosition());
							}
						}
					}
				}
				
				if (outside)
					zone1.setOuterIndex(1.0);
			}
		}
	}
	/**
	 * updates the position of one component relative to another
	 * such that its position is a valid one
	 * @param comp1 first game component
	 * @param comp2 second game component
	 * @param pushIn tells whether comp1 should be pushed in or out of comp2
	 */
	public void reposition(GameComponent comp1, GameComponent comp2, boolean pushIn) {
		
		//incremental amount by which to reposition comp1 in the x direction
		int dx = 1;
		//incremental amount by which to reposition comp1 in the y direction
		int dy = 1;
		//incremental amount by which to resize comp2
		int dz = 20;
		
		//if the 2 components are refractive zones, and one should be fitted into the other
		if (pushIn && comp1.getType().equals("RefractiveZone") && comp2.getType().equals("RefractiveZone")) {

			RefractiveZone zone1 = (RefractiveZone)comp1;
			RefractiveZone zone2 = (RefractiveZone)comp2;

			if (zone1.getWidth() >= zone2.getWidth())
				zone1.setWidth(zone1.getWidth()-dz);
			if (zone1.getHeight() >= zone2.getHeight())
				zone1.setHeight(zone1.getHeight()-dz);
			
			if (zone1.getPosition().getX() > zone2.getPosition().getX()+dz/2)
				dx *= -1;
			if (zone1.getPosition().getY() > zone2.getPosition().getY()+dz/2)
				dy *= -1;
			
			zone1.setPosition(new Point2D.Double((zone1.getPosition().getX()+dx), (zone1.getPosition().getY()+dy)));
		}
		else {
			//reposition comp1 away from comp2
			
			if (comp1.getBox().getMinX() < comp2.getBox().getMinX())
				dx *= -1;
			if (comp1.getBox().getMinY() < comp2.getBox().getMinY())
				dy *= -1;
			
			comp1.setPosition(new Point2D.Double((comp1.getPosition().getX()+dx), (comp1.getPosition().getY()+dy)));
		}
	}
	/**
	 * spreads out components on the plane to put them in valid positions
	 * that is, so that no 2 components overlap
	 */
	public void repositionAll() {
		
		for (int i = 0; i < activeComponents.size(); i++) {
			
			int safetyCount = 0;
			
			while (overlap(activeComponents, 0, 0, i) && safetyCount < BIG/10) {
				
				for (int j = 0; j < activeComponents.size(); j++) {
					
					if (j == i)
						continue;
					
					GameComponent comp1 = activeComponents.get(i);
					GameComponent comp2 = activeComponents.get(j);
					GameComponent zone;
					GameComponent comp;
					
					if (comp1.getType().equals("RefractiveZone") ^ comp2.getType().equals("RefractiveZone")) {
						
						zone = (comp1.getType().equals("RefractiveZone")) ? comp1 : comp2;
						comp = (comp1.getType().equals("RefractiveZone")) ? comp2 : comp1;
						
						while (zone.getBox().intersects(comp.getBox()) && !zone.getBox().contains(comp.getBox())) 
							reposition(comp, zone, false);
					}
					else {
						
						while(comp1.getBox().intersects(comp2.getBox())) 
							reposition(comp1, comp2, false);	
					}
				}
				
				safetyCount++;
			}

			/*
			 * if the algorithm has been trying for too long, put the application
			 * in its backup state
			 */
			if (safetyCount == BIG/10) {	
				load(backupFile);	
				backupFile.delete();
				repaint();
				return;
			}
		}
		
		/*
		 * if the backup file was not used, delete it
		 * another more recent one will be created elsewhere
		 */
		if (backupFile != null)
			backupFile.delete();
	}
	/**
	 * creates a backfile that saves the current
	 * state of the application
	 */
	public void createBackup() {
		
		backupFile = new File("."+File.separator+"user"+File.separator+"temp");	
		
		try {
			backupFile.createNewFile();	
			save(backupFile);
		}
		catch (Exception ex) {
		}
	}
	/**
	 * save the current state in the given file
	 * @param file the file in which the current state is saved
	 */
	public void save(File file) {
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(activeComponents);
			out.writeObject(released);
			out.writeObject(markers);
			out.close();
		}
		catch (Exception ex) {
		}
	}
	/**
	 * opens a dialog box that allows the user to save a project
	 */
	public void save() {
		try {
			String folder = "user";
			JFileChooser jfc = new JFileChooser();

//			if (lvlEditionMode) 	this feature is not available to the user
//				folder = "levels";
//			else {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Virtual Optics Projects", "op");
			    jfc.setFileFilter(filter);	//only files with the .op extension will be shown
//			}
			
			jfc.setCurrentDirectory(new File("."+File.separator+folder));
			jfc.setApproveButtonText("save");
			
			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				
				File project = jfc.getSelectedFile();
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(project));
				out.writeObject(activeComponents);
				out.writeObject(released);
				out.writeObject(markers);
				out.close();
				
				//add the extension to the file if it does not already have it
				if (/*!lvlEditionMode &&*/ !project.getName().contains(".op"))
					project.renameTo(new File(project.getCanonicalPath()+".op"));
			}
		}
		catch (Exception ex) {
		}
	}
	/**
	 * load the given file
	 * @param file
	 */
	public void load(File file) {
		
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			activeComponents = (ArrayList<GameComponent>)in.readObject();		//read in the same order it was saved
			released = (ArrayList<Boolean>)in.readObject();
			markers = (ArrayList<Point>)in.readObject();
			scrollContainer.removeAll();
			initializeAvailableComponents();
			makeScrollPanel();
			in.close();
		}
		catch (Exception ex) {
		}
	}
	/**
	 * opens a dialog box that allows the user to load a project
	 */
	public void load() {
		
		try {							
			JFileChooser jfc = new JFileChooser(new File("."+File.separator+"user"));
			
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Virtual Optics Projects", "op");
			jfc.setFileFilter(filter);
			
			jfc.setApproveButtonText("load");
			
			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				
				File project = jfc.getSelectedFile();
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(project));
				activeComponents = (ArrayList<GameComponent>)in.readObject();		//read in the same order it was saved
				released = (ArrayList<Boolean>)in.readObject();
				markers = (ArrayList<Point>)in.readObject();
				scrollContainer.removeAll();
				initializeAvailableComponents();
				makeScrollPanel();
				in.close();
				repaint();
			}
		}
		catch (Exception ex) {
		}
	}
	/**
	 * changes certain properties of selected components
	 * depending on the kind of character argument
	 * @param c character key that was pressed by the user
	 */
	public void updateProperties(char c) {
		
//		if (c == 'e')
//			lvlEditionMode = (lvlEditionMode) ? false : true;	this feature is not available to the user
		
		for (int i = 0; i < activeComponents.size(); i++) {
			
			//if the current component is not selected, skip to the next one
			if (activeComponents.get(i).isSelected() == false)
				continue;
			
			switch(c) {
			
			case 'c':
				//change the color
					Color newColor = JColorChooser.showDialog(null, "Choose a color", activeComponents.get(i).getColor());
					if (activeComponents.get(i).isSelected()) 
						activeComponents.get(i).setColor(newColor);
				break;
				
			case 'd':
				//change the convergence (curved mirrors only)
				if (activeComponents.get(i) instanceof CurvedMirror) {	
					CurvedMirror cm = (CurvedMirror)activeComponents.get(i);
					if (cm.isConvergent())
						cm.setConvergent(false);
					else cm.setConvergent(true);
				}
				break;
				
			case 'm' :
				//lock or unlock its position
				if (activeComponents.get(i).isMoveable())
					activeComponents.get(i).setMoveable(false);
				else activeComponents.get(i).setMoveable(true);
			break;
				
			case 'r':
				//enable or disable resizability
					if (activeComponents.get(i).isResizeable())
						activeComponents.get(i).setResizeable(false);
					else activeComponents.get(i).setResizeable(true);
				break;
				
			case 't':
				//enable or disable rotatability
					if (activeComponents.get(i).isRotateable())
						activeComponents.get(i).setRotateable(false);
					else activeComponents.get(i).setRotateable(true);
				break;
				
//			case 'x':
//				if (activeComponents.get(i).isSelecteable()) {		this feature is not available to the user
//					activeComponents.get(i).setSelecteable(false);
//					activeComponents.get(i).setSelected(false);
//				}
//			break;
			}
		}
	}
	/**
	 * changes certain properties of selected components
	 * depending on the keytype argument
	 * @param e KeyEvent user to know what kind of key was pressed by the user
	 */
	public void updateProperties(KeyEvent e) {
		
		for (int i = 0; i < activeComponents.size(); i++) {
			
			//if the current component is not selected, skip to the next one
			if (activeComponents.get(i).isSelected() == false)
				continue;
			
			switch(e.getKeyCode()) {
			
			case KeyEvent.VK_RIGHT:
				//rotate component one way
				if (activeComponents.get(i).isRotateable()) 
					activeComponents.get(i).rotate(10);	
				break;
				
			case KeyEvent.VK_LEFT:
				//rotate component the other way
				if (activeComponents.get(i).isRotateable()) 
					activeComponents.get(i).rotate(-10);
				break;
				
			case KeyEvent.VK_BACK_SPACE:
				//delete the component, not available in Level class
				if (this instanceof Level)
					return;
				
				activeComponents.remove(i);
				markers.remove(i);
				released.remove(i);
				indices();
				i--;
				break;
				
			case KeyEvent.VK_ENTER:
				//turn a light ray on or off
				if (activeComponents.get(i) instanceof Ray) {
					Ray ray = (Ray)activeComponents.get(i);
					
					if (ray.isOn())
						ray.setOn(false);
					else ray.setOn(true);
				}
				break;
				
			case KeyEvent.VK_UP:
				//when the shift key is down, increase the radius (curved mirror or lens only)
				if (e.isShiftDown() && activeComponents.get(i).isResizeable()) {
					if (activeComponents.get(i) instanceof CurvedMirror) {
						CurvedMirror cm = (CurvedMirror)activeComponents.get(i);
						cm.setRadius(1);
					}
					else if (activeComponents.get(i) instanceof Lens) {
						Lens lens = (Lens)activeComponents.get(i);
						lens.setRadius(1);
					}
				}
				//otherwise increase the arc length of the component
				else if (activeComponents.get(i).isResizeable()) {
					int a = 1;
					if (keyHeldCount >= 20)
						a = 5;
					activeComponents.get(i).resize(a);
					indices();
					keyHeldCount++;
				}
				break;
				
			case KeyEvent.VK_DOWN :
				//when the shift key is down, decrease the radius (curved mirror or lens only)
				if (e.isShiftDown() && activeComponents.get(i).isResizeable()) {
					if (activeComponents.get(i) instanceof CurvedMirror) {
						CurvedMirror cm = (CurvedMirror)activeComponents.get(i);
						cm.setRadius(-1);
					}
					else if (activeComponents.get(i) instanceof Lens) {
						Lens lens = (Lens)activeComponents.get(i);
						lens.setRadius(-1);
					}
				}
				//otherwise decrease the arc length of the component
				else if (activeComponents.get(i).isResizeable()) {
					int a = -1;
					if (keyHeldCount >= 20)
						a = -5;
					activeComponents.get(i).resize(a);
					indices();
					keyHeldCount++;
				}
				break;
			}
		}
	}
	/**
	 * creates a scroll panel and adds it to the right of the Lab panel
	 */
	void makeScrollPanel() {
		
		scrollPanel = new ScrollPanel(availableComponents, activeComponents);	
		
		//add listeners to the scrollPanel
		scrollPanel.addMouseListener(new MouseAdapter() {
			
            @Override
            public void mouseClicked(MouseEvent mouse) {
            	
            	//if an available component is clicked, add it to the list of active components
            	if (mouse.getClickCount() == 1) {
            		createBackup();
            		scrollPanel.labelClicked = mouse.getY() / 128;
            		scrollPanel.addActiveComponent(scrollPanel.labelClicked, getWidth(), getHeight());
            		released.add(true);
            		indices();
            		repositionAll();
            		repaint();
            	}
			}
		});
		
		scrollPanel.addMouseMotionListener(new MouseAdapter() {
			
            @Override
            public void mouseMoved(MouseEvent mouse) {	//change the look of the cursor
            	
            	overScrollPane = true;
            	setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		});

		jSPComponents = new JScrollPane(scrollPanel);
		jSPComponents.setPreferredSize(new Dimension(125, getHeight() - 5));
		jSPComponents.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollContainer.setLayout(new BorderLayout());
		scrollContainer.add(jSPComponents);
		add(scrollContainer);
	}
	/**
	 * Prepares the component scroll pane
	 */
	void initializeAvailableComponents() {
		ArrayList<GameComponent> rays = new ArrayList<>();
		ArrayList<GameComponent> obstacles = new ArrayList<>();
		ArrayList<GameComponent> mirrors = new ArrayList<>();
		ArrayList<GameComponent> curvedMirrors = new ArrayList<>();
		ArrayList<GameComponent> refractiveZones = new ArrayList<>();
		ArrayList<GameComponent> lenses = new ArrayList<>();
		ArrayList<GameComponent> prisms = new ArrayList<>();
		ArrayList<GameComponent> targets = new ArrayList<>();	
		
		availableComponents[0] = rays;
		availableComponents[1] = obstacles;
		availableComponents[2] = mirrors;
		availableComponents[3] = curvedMirrors;
		availableComponents[4] = refractiveZones;
		availableComponents[5] = lenses;
		availableComponents[6] = prisms;
		availableComponents[7] = targets;	
		
		rays.add(new Ray());	
		obstacles.add(new Obstacle());
		mirrors.add(new Mirror());
		curvedMirrors.add(new CurvedMirror());
		refractiveZones.add(new RefractiveZone());
		lenses.add(new Lens());
		prisms.add(new Prism());
		targets.add(new Target());
	}
	/**
	 * sets the list of active components to a new list
	 * @param activeComponents new list of active components
	 */
	void setActiveComponents(ArrayList<GameComponent> activeComponents) {
		this.activeComponents = (ArrayList<GameComponent>) activeComponents.clone();
	}
	/**
	 * sets the list of active components to a new list, with subordinate properties
	 * @param activeComponents new list of active components
	 * @param released list of booleans for released components
	 * @param markers list of marker positions
	 */
	void setActiveComponents(ArrayList<GameComponent> activeComponents, ArrayList<Boolean> released, ArrayList<Point> markers) {
		this.activeComponents = (ArrayList<GameComponent>) activeComponents.clone();
		this.released = (ArrayList<Boolean>)released.clone();
		this.markers = (ArrayList<Point>)markers.clone();
	}
	/**
	 * adds the game menu in the top left corner of this Lab panel 
	 */
	void makeGameMenu() {	
		add(gm);
		gm.setBounds(0,0,25,25);
	}
	/**
	 * 
	 * @return list of active components
	 */
	ArrayList<GameComponent> getActiveComponents() {
		return (ArrayList<GameComponent>) activeComponents.clone();
	}
	/**
	 * 
	 * @return list of booleans for released components
	 */
	ArrayList<Boolean> getReleased() {
		return (ArrayList<Boolean>) released.clone();
	}
	/**
	 * 
	 * @return list of marker positions
	 */
	ArrayList<Point> getMarkers() {
		return (ArrayList<Point>) markers.clone();
	}
}

