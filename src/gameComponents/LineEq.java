package gameComponents;
import java.awt.geom.Point2D;
import java.io.*;

/**
 * 
 * Virtual Optics
 * <p>
 * This class models the equation of a line in a 2D plane
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class LineEq implements Serializable {	

	private static final long serialVersionUID = 5560477265254498806L;
	/**
	 * The inclination of the line
	 */
	private double slope;
	/**
	 * Intercept of the line with the y-axis
	 */
	private double intercept;

	
	public LineEq() {
		
	}
	
	public LineEq(double slope, double intercept) {
		this.slope = slope;
		this.intercept = intercept;
	}
	
	public LineEq(double slope, double x, double y) {
		this.slope = slope;
		this.intercept = y - slope*x;
	}
	
	public LineEq(double x1, double y1, double x2, double y2) {
		slope = (y2-y1)/(x2-x1);
		intercept = y1-slope*x1;
	}
	
	public LineEq(Point2D.Double p1, Point2D.Double p2) {
		slope = (p2.getY()-p1.getY())/(p2.getX()-p1.getX());
		intercept = p1.getY()-slope*p1.getX();
	}

	
	public double getSlope() {
		return slope;
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}

	public double getIntercept() {
		return intercept;
	}

	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}
}
