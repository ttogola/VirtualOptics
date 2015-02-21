package gameComponents;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * 
 * Virtual Optics
 * <p>
 * This class models a ray of light
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class Ray extends GameComponent {

	private static final long serialVersionUID = -7964894323816186059L;
	/**
	 * Contains the points that trace the trajectory of this ray
	 */
	private ArrayList<Point2D.Double> path = new ArrayList<>();
	/**
	 * Contains the indices of the components, in the list
	 * passed to the impact method, that were hit by this Ray object;
	 * i.e. hitComponent.get(i) contains the index of the component hit by the ith point 
	 * in the path
	 */
	private ArrayList<Integer> hitComponent = new ArrayList<>();
	/**
	 * Tells if the light ray is on or off
	 */
	private boolean on = false;
	/**
	 * The orientation of the first segment of the ray
	 */
	private double angle = 180;
	/**
	 * The radius of the light source, represented by a circular shape
	 */
	private double radius = 5;
	/**
	 * The x coordinate of the center of the 
	 * circular shape of this light source,
	 * called h by convention
	 */
	private double h;
	/**
	 * The y coordinate of the center of the
	 * circular shape of this light source,
	 * called k by convention
	 */
	private double k;
	
	
	public Ray() {
		setColor(Color.WHITE);	
	}
	
	public Ray(Point2D.Double position) {
		super(position);
		setColor(Color.WHITE);
		path.add(position);
		path.add(position);		//twice, because impact method needs a segment
		h = position.getX();
		k = position.getY();
		hitComponent.add(0);
		hitComponent.add(-1);	//assign -1 when no component is hit yet
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#initBox()
	 */
	@Override
	public void initBox() {
		Rectangle rec = new Rectangle((int)(h-radius), (int)(k-radius), (int)radius*2, (int)radius*2);
		setBox(rec);
	}
	
	/**
	 * 
	 * @see gameComponents.GameComponent#setPosition(java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setPosition(Point2D.Double p ) {
		super.setPosition(p);
		path.set(0, p);
		if (on) rotate(0);
		else setOn(false);
		h = p.getX();
		k = p.getY();
		initBox();
	}
	
	public Point2D.Double getPoint(int index) {
		return path.get(index);
	}
	
	/**
	 * Adds a new point to the path
	 * @param p
	 */
	public void addPoint(Point2D.Double p) {
		path.add(p);
	}
	
	/**
	 * @return The size of the path 
	 */
	public int size() {
		return path.size();
	}
	
	public boolean isOn() {
		return on;
	}
	
	public void setOn(boolean on) {
		if (!on) {
			path.clear();
			path.add(getPosition());
			path.add(getPosition());
		}
		this.on = on;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		if (selected) on = true;
	}
	
	public double getX(int index) {
		return path.get(index).getX();
	}
	
	public double getY(int index) {
		return path.get(index).getY();
	}
	
	public double getRadius() {
		return radius;
	}
	
	public double getAngle() {
		return angle;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#rotate(int)
	 */
	@Override
	public void rotate(int delta) {
		if (!on) return;
		
		angle += delta;

		if (angle > 360)
			angle -= 360;
		else if (angle < 0)
			angle += 360;
		else if (angle == 360)
			angle = 0;
				
		int xEnd = (int)(getX(0) + (BIG * Math.sin(Math.toRadians(angle))));	//the x coordinate of the end point of the ray
		int yEnd = (int)(getY(0) - (BIG * Math.cos(Math.toRadians(angle))));	//the y coordinate of the end point of the ray
		path.set(1, new Point2D.Double(xEnd, yEnd));	//setting source endpoint position
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#resize(int)
	 */
	@Override
	public void resize(int m) {
		
	}
	/**
	 * Goes through the path and checks whether each segment intersects 
	 * a component in the list of active components on the plane.
	 * If so, apply the appropriate optical formula to change the direction of the ray.
	 * @param components List of active components
	 */
	public void impact(ArrayList<GameComponent> components) {
		if (!on)
			return;
		
		rotate(0);			//refresh orientation of the ray
		int targetHit = -1;	//index of the Target object that has been hit by this ray

		//refresh the list of hit components
		hitComponent.clear();	
		hitComponent.add(0);
		hitComponent.add(-1);
		hitComponent.set(0, findIndex(components));	//assign correct index of this ray in the list of components

		//go through the path
		for (int i = 1, limit = 0; i < path.size(); i++, limit++) {	

			//set a limit to prevent infinite loops
			if (limit == 1001) {
				path.remove(i);
				hitComponent.remove(i);
				break;
			}

			//remove points after the latest intersection
			while (i+1 < path.size()) 
				path.remove(i+1);
			
			//this is the segment that is passed to the intersection method of each component to check if there is an impact
			LineEq currentSegment = new LineEq(path.get(i-1), path.get(i));	

			//go through the list of game components
			for (int j = 0; j < components.size(); j++) {
				
				//compute and save the intersection
				Point2D.Double intersec = components.get(j).intersection(path.get(i-1), path.get(i), currentSegment);
				
				if (intersec == null) 
					continue;
									
				//if the intersection point is not the same as the source point and is in the direction of the current segment
				//then proceed
				if (!approx(intersec, path.get(i-1)) && sameQuadrant(path.get(i-1), path.get(i), intersec))	{ 

					//set the last point to the intersection point
					path.set(i, intersec);
					hitComponent.set(i, j);	//ith point in path hit jth component

					//remove points after the latest intersection
					while (i+1 < path.size()) {
						path.remove(i+1);
						hitComponent.remove(i+1);
					}

					//the game component that was hit is an optical object, bend the path accordingly
					if (components.get(j) instanceof OpticalObject) {
						
						OpticalObject currentOb = (OpticalObject)components.get(j);
								
						//check orientation, if nonreflective or nonrefractive, do not call bend
						if (currentOb.checkOrientation(path.get(i-1), intersec, currentSegment)) {
							path.add(currentOb.bend(path.get(i-1), intersec, currentSegment)); 
							hitComponent.add(-1);
						}
					}
				}	
			}
			
			if (hitComponent.get(i) != -1)
				if (components.get(hitComponent.get(i)) instanceof Target) 
					targetHit = hitComponent.get(i);
		}

		if (targetHit >= 0)
			((Target)components.get(targetHit)).react(getColor());
		
		//set collisions for the obstacles lighting
		if (hitComponent.get(hitComponent.size()-1) != -1) {
			for (int i = 0; i < path.size(); i++) {	
				if (components.get(hitComponent.get(i)) instanceof Obstacle)	
					((Obstacle)components.get(hitComponent.get(i))).collision(path.get(i), this); 
			}
		}
	}
	/**
	 * Finds the index of this Ray object in the list of active components
	 * @param components List of active game components
	 * @return The index of this ray
	 */
	private int findIndex(ArrayList<GameComponent> components) {
		int index = -1;
		for (int i = 0; i < components.size(); i++) {
			if (this == components.get(i))	//checking for a reference match
				index = i;
		}
		
		return index;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#sameQuadrant(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
	 */
	@Override
	public boolean sameQuadrant(Point2D.Double origin, Point2D.Double end, Point2D.Double intersec) {	
		
		Point.Double p1 = new Point.Double((end.getX()-origin.getX()), (end.getY()-origin.getY()));
		Point.Double p2 = new Point.Double((intersec.getX()-origin.getX()), (intersec.getY()-origin.getY()));
		
		int x1 = (p1.getX() >= 0) ? 1 : -1;
		int x2 = (p2.getX() >= 0) ? 1 : -1;
		int y1 = (p1.getY() >= 0) ? 1 : -1;
		int y2 = (p2.getY() >= 0) ? 1 : -1;

		if (!isWithinBounds(intersec, origin, end)) 
			return false;
		else if (x1 == x2 && y1 == y2) 
			return true;
		else 
			return false;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#intersection(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public Point2D.Double intersection(Point2D.Double p1, Point2D.Double p2, LineEq segment) {
	
		Point2D.Double inter;	//intersection point
		double interx, intery;	//coordinates of the intersection point
		
		if (p1.distance(h, k) < radius)
			return null;
		
		if (infiniteSlope(segment.getSlope())) {
			
			interx = p1.getX();
			
			//compute solutions for vertical line 
			double[] circleSols = circSolsX(p1.getX(), radius, h, k);

			if ((circleSols[0]+"").equals("NaN"))
				return null;
			
			double closeY = circleSols[0];
			double farY = circleSols[1];
			
			//keep the solution closest to source point
			if (p1.distance(p1.getX(), farY) < p1.distance(p1.getX(), closeY)) {
				double temp = closeY;
				closeY = farY;
				farY = temp;
			}
			
			intery = closeY;
			
		}
		else {	
				
			ArrayList<Point2D.Double> solutions = circSolsGeneral(segment, radius, h, k);

			if (solutions == null)
				return null;
				
			Point2D.Double closeSol = solutions.get(0);
			Point2D.Double farSol = solutions.get(1);
			double closeDis = closeSol.distance(p1);
			double farDis = farSol.distance(p1);
					
			//keep the closest solution
			if (closeDis > farDis) {
				Point2D.Double temp = (Point2D.Double)closeSol.clone();	
				closeSol = (Point2D.Double)farSol.clone();
				farSol = (Point2D.Double)temp;		
			}

			interx = closeSol.getX();
			intery = closeSol.getY();
		}

		inter = new Point2D.Double(interx, intery);
		return inter;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#draw(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	public void draw(Graphics2D g0, Rectangle viewRec) {	//always draw rays, dont need to check viewRec
		Graphics2D g = (Graphics2D)g0.create();

		int[] x = new int[size()];	//x coordinates of the path's points
		int[] y = new int[size()];  //y coordinates of the path's points

		//fill in the arrays with the values in path
		for (int j = 0; j < size(); j++) {
			x[j] = (int)getX(j);
			y[j] = (int)getY(j);
		}

		g.drawPolyline(x,  y, size());	//draws all the segments of the ray path

		g.setColor(Color.BLACK);
		g.fillOval((int)(x[0]-radius), (int)(y[0]-radius), (int)radius*2, (int)radius*2);
		
		//draw a border around the ray source circle when the user selects it
		if (isSelected()) {
			g.setColor(Color.YELLOW);
			g.drawOval((int)(h-radius), (int)(k-radius), (int)radius*2, (int)radius*2);
		}
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#getType()
	 */
	@Override
	public String getType() {
		return "Ray";
	}
}
