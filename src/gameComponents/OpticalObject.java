package gameComponents;

import java.awt.geom.Point2D;

/**
 * 
 * Virtual Optics
 * <p>
 * OpticalObject is the base class for game components that
 * can interact with Ray objects
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public abstract class OpticalObject extends GameComponent {
	
	private static final long serialVersionUID = -7410094189258732759L;
	/**
	 * The normal line is the line perpendicular to a surface
	 * hit by a ray of light
	 */
	private LineEq normalLine;
	/**
	 * The slope of the axis of this optical object
	 */
	private double inclination;

	
	protected OpticalObject() {
		
	}
	
	protected OpticalObject(Point2D.Double position) {
		super(position);
	}
	
	protected OpticalObject(double[] bounds) {
		super.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
	}
	
	
	public double getInclination() {
		return inclination;
	}
	
	public void setInclination(double inclination) {
		this.inclination = inclination;
	}
	
	public LineEq getNormalLine() {
		return normalLine;
	}
	
	public void setNormalLine(LineEq normalLine) {
		this.normalLine = normalLine;
	}
	/**
	 * Different optical objects have different ways of setting their normal lines
	 * @param intersection The point where a ray segment impacts this optical object
	 * @param p1 The source point of the ray segment intersecting this optical object
	 */
	protected abstract void setNormalLine(Point2D.Double intersection, Point2D.Double p1);
	
	/**
	 * Computes the angle between two slopes
	 * @param m1 Slope of line1
	 * @param m2 Slope of line2
	 * @return The angle between the two slopes specified
	 */
	protected double angleBetween(double m1, double m2) {
		
		//m1 needs to be greater than m2 for this formula to work, swap if necessary
		if (m1 > m2) {
			double temp = m1;
			m1 = m2;
			m2 = temp;
		}

		double angle = Math.atan((m2-m1)/(1 + m1*m2));

		if (angle < 0)
			angle += 180;

		 return angle;
	}
	/**
	 * Computes the slope of a ray reflected on this optical object
	 * @param m1 Slope of the incident ray segment 
	 * @param m2 Slope of the normal line of this optical object
	 * @return The slope of the reflected ray segment
	 */
	protected double reflection(double m1, double m2) {
		double incidentAngle;

		if (infiniteSlope(m1) && infiniteSlope(m2)) {
			return m2;
		}
		else if (infiniteSlope(m1)) {	
			incidentAngle = Math.PI/2 - (Math.PI-Math.atan(m2));
			return Math.tan(Math.PI-(Math.PI/2-2*incidentAngle));
		}
		else if (infiniteSlope(m2)) {
			return Math.tan(Math.PI-Math.atan(m1));
		}
		
		//m2 is the normal except if invert = true
		boolean invert = false;		
		
		if (m1 > m2) {
			double temp = m1;
			m1 = m2;
			m2 = temp;
			invert = true;
		}

		incidentAngle = Math.atan((m2-m1)/(1 + m1*m2));
		
		if (invert)
			return Math.tan(Math.atan(m1) - incidentAngle);
		else 
			return Math.tan(Math.atan(m2) + incidentAngle);
	}
	/**
	 * Determines the orientation of the ray segment that hit this optical object
	 * by returning a point that is far away in that direction 
	 * 
	 * @param source The source point of the ray segment that hit this optical object
	 * @param intersec The intersection point of the ray segment and this optical object
	 * @param segment The ray segment that hit this optical object
	 * @return The point at the end of the Ray object path that hit this optical object
	 */
	protected abstract Point2D.Double bend(Point2D.Double source, Point2D.Double intersec, LineEq segment);
	/**
	 * Checks if the reflected/refracted ray is on the correct side of the normal line.
	 * (Varies from one optical object to another)
	 * @param incidentLine
	 * @param resultLine
	 * @param source The source point of the ray segment that hit this optical object
	 * @param intersec The intersection point of the ray segment and this optical object
	 * @return False if the reflected/refracted ray is on the correct side, true otherwise
	 */
	protected abstract boolean checkSide(LineEq incidentLine, LineEq resultLine, Point2D.Double source, Point2D.Double intersec);
	/**
	 * Determines whether the incident ray segment will interact with the optical surface or not
	 * @param source The source point of the ray segment that hit this optical object
	 * @param intersection The intersection point of the ray segment and this optical object
	 * @param segment The ray segment that hit this optical object
	 * @return True if the ray can interact with this side of the optical object, false otherwise
	 */
	protected abstract boolean checkOrientation(Point2D.Double source, Point2D.Double intersection, LineEq segment);
}
