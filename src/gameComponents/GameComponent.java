package gameComponents;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.ArrayList;

/**
 * 
 * Virtual Optics
 * <p>
 * GameComponent is the base class for all the components
 * that the user can interact with in the application
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public abstract class GameComponent implements Serializable {
		
	private static final long serialVersionUID = -3553956612400520952L;

	/**
	 * Locates the component on the 2D plane
	 */
	private Point2D.Double position;	
	
	private Color color;
	
	private boolean moveable = true;	
	private boolean resizeable = true;
	private boolean rotateable = true;
	private boolean selecteable = true;	
	private boolean selected;
	
	/**
	 * Delimits the contours of a component; 
	 * (bounds[0], bounds[1]) is the top left corner and 
	 * (bounds[2], bounds[3]) is the lower right corner
	 */
	private double[] bounds;
	/**
	 * The bounds of the component in the form of a rectangle
	 */
	private Rectangle box;				
	/**
	 * A useful constant, to simulate points/slopes of infinite values
	 */
	protected final int BIG = 100000;	
	
	
	protected GameComponent() {
		
	}
	
	protected GameComponent(Point2D.Double position) {
		this.position = position;
	}
	
	
	public double[] getBounds() {
		return bounds;
	}
	
	public void setBounds(double x1, double y1, double x2, double y2) {
		bounds = new double[] {x1, y1, x2, y2};
	}
	
	public Rectangle getBox() {
		return box;
	}
	
	public void setBox(Rectangle rec) {
		box = rec;
	}

	public Point2D.Double getPosition() {
		return position;
	}

	public void setPosition(Point2D.Double position) {
		this.position = position;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}
	
	public boolean isResizeable() {
		return resizeable;
	}

	public void setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
	}
	
	public boolean isRotateable() {
		return rotateable;
	}

	public void setRotateable(boolean rotateable) {
		this.rotateable = rotateable;
	}
	
	public boolean isSelecteable() {
		return selecteable;
	}

	public void setSelecteable(boolean selecteable) {
		this.selecteable = selecteable;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (selected && !selecteable)
			return;
		
		this.selected = selected;
	}
	
	
	/** 
	 * Detects when the user clicks on the component
	 * @param cursorPos The position of the mouse on the panel
	 * @return True if the component was clicked, false otherwise
	 */
	public boolean contact(Point2D.Double cursorPos) {		
		if (getBox().contains(cursorPos)) 
			return true;
		else return false;
	}
	
	/**
	 * Checks if a game component displaced by the amount specified 
	 * by deltaX and deltaY would collide with this component
	 * @param rec The bounding box to test for collision with this component
	 * @param deltaX Displacement in the x direction
	 * @param deltaY Displacement in the y direction
	 * @return True if there is contact, false otherwise
	 */
	public boolean contact(Rectangle rec, int deltaX, int deltaY) {	
		Rectangle thisRec = (Rectangle)getBox().clone();
		thisRec.setLocation((int)(thisRec.getX()+deltaX), (int)(thisRec.getY()+deltaY));

		if (thisRec.intersects(rec))
			return true;
		else return false;
	}
	
	/**
	 * Checks that the intersection point is in the same quadrant of the 2D plane 
	 * as the origin point and end point of a ray segment
	 * @param origin The origin point of the ray segment
	 * @param end The end point of the ray segment
	 * @param intersec The intersection of the ray segment with this component
	 * @return True if the intersection is in the same quadrant, false otherwise
	 */
	protected boolean sameQuadrant(Point2D.Double origin, Point2D.Double end, Point2D.Double intersec) {	
		
		Point2D.Double p1 = new Point2D.Double(end.getX()-origin.getX(), end.getY()-origin.getY());
		Point2D.Double p2 = new Point2D.Double(intersec.getX()-origin.getX(), intersec.getY()-origin.getY());
		
		int x1 = (p1.getX() >= 0) ? 1 : -1;
		int x2 = (p2.getX() >= 0) ? 1 : -1;
		int y1 = (p1.getY() >= 0) ? 1 : -1;
		int y2 = (p2.getY() >= 0) ? 1 : -1;

		if (x1 == x2 && y1 == y2)
			return true;
		else 
			return false;
	}
	
	/**
	 * Checks if the given point p is within the rectangular bounds specified by b
	 * @param p A point
	 * @param b Rectangular bounds
	 * @return True if point p is within the bounds, false otherwise
	 */
	protected boolean isWithinBounds(Point2D.Double p, double[] b) {
		
		double minX = Math.min(b[0], b[2]);
		double maxX = Math.max(b[0], b[2]);
		
		double minY = Math.min(b[1], b[3]);
		double maxY = Math.max(b[1], b[3]);

		if (p.getX() >= minX && p.getX() <= maxX && p.getY() >= minY && p.getY() <= maxY)
			return true;
		else return false;
	}
	
	/**
	 * Checks if the point p is within the rectangular bounds delimited by points p1 and p2
	 * @param p A point
	 * @param p1 Bounding point 1
	 * @param p2 Bounding point 2
	 * @return True, if point p is within the bounds of p1 and p2, false otherwise
	 */
	protected boolean isWithinBounds(Point2D.Double p, Point2D.Double p1, Point2D.Double p2) {

		double minX = Math.min(p1.getX(), p2.getX());
		double maxX = Math.max(p1.getX(), p2.getX());
		
		double minY = Math.min(p1.getY(), p2.getY());
		double maxY = Math.max(p1.getY(), p2.getY());
		
		if (p.getX() <= maxX && p.getX() >= minX && p.getY() <= maxY && p.getY() >= minY)
			return true;
		else return false;
	}
	
	/**
	 * Computes the distance between points (x1, y1) and (x2, y2)
	 * @param x1 X coordinate of point 1
	 * @param y1 Y coordinate of point 1
	 * @param x2 X coordinate of point 2
	 * @param y2 Y coordinate of point 2
	 * @return Distance 
	 */
	protected double distance(double x1, double y1, double x2, double y2) {
		
		return Math.sqrt( Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2) );
	}
	
	/**
	 * Computes the midpoint of the line from point (x1, y2) and (x2, y2)
	 * @param x1 X coordinate of point 1
	 * @param y1 Y coordinate of point 1
	 * @param x2 X coordinate of point 2
	 * @param y2 Y coordinate of point 2
	 * @return Midpoint
	 */
	protected Point2D.Double midPoint(double x1, double y1, double x2, double y2) {
		return new Point2D.Double((x1+x2)/2, (y1+y2)/2);
	}
	/**
	 * Solves a quadratic equation
	 * @param a Parameter of the equation
	 * @param b Parameter of the equation
	 * @param discr Discriminant of the equation
	 * @return The solutions of the equation
	 */
	protected ArrayList<Double> quadSolver(double a, double b, double discr) {
		
		ArrayList<Double> solutions = new ArrayList<>();
		
			solutions.add((-b + Math.sqrt(discr))/(2*a));
			solutions.add((-b - Math.sqrt(discr))/(2*a));
		
		return solutions;	
	}
	/**
	 * Finds the intersections of a vertical line with a circle
	 * @param x X coordinate of the vertical line
	 * @return Y coordinates of intersection points
	 */
	protected double[] circSolsX(double x, double radius, double h, double k) {
		
		double[] solutions = new double[2];
		
		solutions[0] = Math.sqrt(Math.pow(radius, 2) - Math.pow(x-h, 2)) + k;
		solutions[1] = -Math.sqrt(Math.pow(radius, 2) - Math.pow(x-h, 2)) + k;
		
		return solutions;	
	}
	/**
	 * Finds the intersections of any line with a circle
	 * @param segment Line segment to test for intersection with this circle
	 * @param radius Radius of this circle
	 * @param h X coordinate of the center of this circle
	 * @param k Y coordinate of the center of this circle
	 * @return The intersection points
	 */
	protected ArrayList<Point2D.Double> circSolsGeneral(LineEq segment, double radius, double h, double k) {
		
		ArrayList<Point2D.Double> solutions = new ArrayList<>();
		
		double a = Math.pow(segment.getSlope(), 2)+1;
		double b = 2*segment.getSlope()*segment.getIntercept() -2*h -2*segment.getSlope()*k;
		double c = Math.pow(h, 2) -2*segment.getIntercept()*k + Math.pow(segment.getIntercept(), 2) + Math.pow(k, 2) - Math.pow(radius, 2);
		double d = Math.pow(b, 2) -4*a*c;	//discriminant
		
		if (d < 0) 
			return null;
		
		ArrayList<Double> xSol = quadSolver(a, b, d);

		solutions.add(new Point2D.Double(xSol.get(0), segment.getSlope()*xSol.get(0) + segment.getIntercept()));
		solutions.add(new Point2D.Double(xSol.get(1), segment.getSlope()*xSol.get(1) + segment.getIntercept()));

		return solutions;	
	}
	
	/**
	 * Checks if the given slope is vertical (infinite value).
	 * This works because java returns "infinity" or "-Infinity" when a double variable has value 1/0.0 or -1/0.0
	 * @param slope Slope of the line to test
	 * @return True if the slope is infinite, false otherwise
	 */
	protected boolean infiniteSlope(double slope) {
		if ((slope+ "").equals("Infinity") || (slope+ "").equals("-Infinity"))
			return true;
		else 
			return false;
	}
	
	/**
	 * Checks if the given points are approximately equal
	 * @param p1 Point 1
	 * @param p2 Point 2
	 * @return True if they are approximately equal, false otherwise
	 */
	protected boolean approx(Point2D.Double p1, Point2D.Double p2) {
		double pr = 3;
		if ((p1.getX() >= p2.getX()-pr && p1.getX() <= p2.getX()+pr && p1.getY() >= p2.getY()-pr && p1.getY() <= p2.getY()+pr)
				&& (p2.getX() >= p1.getX()-pr && p2.getX() <= p1.getX()+pr && p2.getY() >= p1.getY()-pr && p2.getY() <= p1.getY()+pr)) 
			return true;
		else
			return false;
	}
	/**
	 * Checks if the given points are approximately equal
	 * @param p1 Point 1
	 * @param p2 Point 2
	 * @param pr The amount of precision desired in the approximation
	 * @return True if they are approximately equal, false otherwise
	 */
	protected boolean approx(Point2D.Double p1, Point2D.Double p2, double pr) {
		if ((p1.getX() >= p2.getX()-pr && p1.getX() <= p2.getX()+pr && p1.getY() >= p2.getY()-pr && p1.getY() <= p2.getY()+pr)
				&& (p2.getX() >= p1.getX()-pr && p2.getX() <= p1.getX()+pr && p2.getY() >= p1.getY()-pr && p2.getY() <= p1.getY()+pr)) 
			return true;
		else
			return false;
	}
	/**
	 * Checks if the given values are approximately equal
	 * @param v1 Value 1
	 * @param v2 Value 2
	 * @return True if they are approximately equal, false otherwise
	 */
	protected boolean approx(double v1, double v2) {
		double pr = 3;
		if ((v1 >= v2-pr && v1 <= v2+pr) && (v2 >= v1-pr && v2 <= v1+pr))	
			return true;
		else
			return false;
	}
	/**
	 * Checks if the given values are approximately equal
	 * @param v1 Value 1
	 * @param v2 Value 2
	 * @param pr The amount of precision desired in the approximation
	 * @return True if they are approximately equal, false otherwise
	 */
	protected boolean approx(double v1, double v2, double pr) {
		if ((v1 >= v2-pr && v1 <= v2+pr) && (v2 >= v1-pr && v2 <= v1+pr))	
			return true;
		else
			return false;
	}
		
	/**
	 * 
	 * @return The type of this game component
	 */
	public abstract String getType();
	
	/**
	 * Initializes the bounding box of this component
	 */
	protected abstract void initBox();
	
	/**
	 * Computes the intersection of the given ray segment with this component
	 * @param p1 The source point of the ray segment
	 * @param p2 The end point of the ray segment
	 * @param segment The ray segment intersecting this component
	 * @return The intersection point
	 */
	public abstract Point2D.Double intersection(Point2D.Double p1, Point2D.Double p2, LineEq segment);
	
	/**
	 * Rotates the component in the plane
	 * @param a The angular amount of rotation, in degrees
	 */
	public abstract void rotate(int a);
	
	/**
	 * Scale the size of the component in the plane
	 * @param m The length amount to add if m > 0, to remove if m < 0
	 */
	public abstract void resize(int m);
	
	/**
	 * Draws the component on the plane
	 * @param g Graphics component
	 * @param viewRec The rectangle that delimits what the user sees in the panel
	 */
	public abstract void draw(Graphics2D g, Rectangle viewRec);
	
}
