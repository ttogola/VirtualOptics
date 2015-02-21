package gameComponents;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * 
 * Virtual Optics
 * <p>
 * This class models a lens.
 * A lens is composed of 2 circular surfaces, so 2 circle equations
 * are required to model it (hence the use of h1, h2, k1, k2 etc.)
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class Lens extends RefractiveZone {

	private static final long serialVersionUID = -7377177913826834933L;
	/**
	 * Radius for the circles of surface 1 and 
	 * surface 2
	 */
	private double radius = 100;
	/**
	 * The x coordinate of the circle equation of surface 1
	 */
	private double h1; 
	/**
	 * The x coordinate of the circle equation of surface 2
	 */
	private double h2; 
	/**
	 * The y coordinate of the circle equation of surface 1
	 */
	private double k1; 
	/**
	 * The y coordinate of the circle equation of surface 2
	 */
	private double k2; 
	
	//the values of the surface currently being interacted with
	private double h;
	private double k;
	private double arcAngle;
	private double arcAngleb;
	private double arcAngle1 = 180;
	private double arcAngleb1;
	private double arcAngleb2;	
	private double arcAngle2;
	/**
	 * The length of the arc representing the first surface of the lens
	 * specified in degrees, values between 30 and 180
	 */
	private double arcLength1 = 80; 
	/**
	 * The length of the arc representing the second surface of the lens
	 * specified in degrees, values between 30 and 180
	 */
	private double arcLength2;
	/**
	 * Keeps track of whether the incident ray is coming into the lens
	 * (false) or going out (true)
	 */
	protected boolean leaving = false;
	/**
	 * Horizontal distance between the 2 surfaces of the lens.
	 * Helps to fix the surfaces together
	 */
	private double dx;	
	/**
	 * Vertical distance between the 2 surfaces of the lens.
	 * Helps to fix the surfaces together
	 */
	private double dy;
		
	
	public Lens() {
		setColor(Color.CYAN);
	}
	
	//the position is the top left corner of the bounding rectangle
	public Lens(Point2D.Double position) {
		super(position);
		setColor(Color.CYAN);
		setRefractionIndex(1.3);	//default value
		this.h1 = position.getX()+radius;	
		this.k1 = position.getY()+radius;
		arcAngleb1 = arcAngle1+arcLength1;
		if (arcAngleb1 >= 360)
			arcAngleb1 -= 360;
		initBounds();
		initInclination();
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.RefractiveZone#initBox()
	 */
	@Override
	public void initBox() {
		Arc2D.Double arc1 = new Arc2D.Double((h1-radius), (k1-radius), radius*2, radius*2, arcAngle1, arcLength1, Arc2D.OPEN);
		Arc2D.Double arc2 = new Arc2D.Double((h2-radius), (k2-radius), radius*2, radius*2, arcAngle2, arcLength2, Arc2D.OPEN);

		Rectangle rec1 = (Rectangle)arc1.getBounds2D().getBounds();
		Rectangle rec2 = (Rectangle)arc2.getBounds2D().getBounds();
    	Rectangle rec = rec1.union(rec2);
		setBox(rec);
	}
	/**
	 * 
	 * @see gameComponents.RefractiveZone#initBounds()
	 */
	@Override
	protected void initBounds() {
	    
	    double a = arcAngle1; 
	    double b = arcAngle1+arcLength1; 
	    if (b >= 360) 
	        b -= 360; 
	          
	    double x1 = h1 + radius*Math.cos(Math.toRadians(a)); 
	    double y1 = k1 - radius*Math.sin(Math.toRadians(a)); 
	    double x2 = h1 + radius*Math.cos(Math.toRadians(b)); 
	    double y2 = k1 - radius*Math.sin(Math.toRadians(b)); 
	          
	    setBounds(x1, y1, x2, y2);
	    
		arcLength2 = arcLength1; 
		arcAngle2 = 180 + arcAngle1;
		if (arcAngle2 >= 360)
			arcAngle2 -= 360;
		
		arcAngleb2 = arcAngle2+arcLength2;
		if (arcAngleb2 >= 360)
			arcAngleb2 -= 360;
		
		double c = arcAngle2; 
	          
	    double x3 = h1 + radius*Math.cos(Math.toRadians(c)); //one other bound is enough to compute deltas
	    double y3 = k1 - radius*Math.sin(Math.toRadians(c)); 
	    
	    dx = x2-x3;
	    dy = y2-y3;
	    h2 = h1 + dx;
		k2 = k1 + dy;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#setPosition(java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setPosition(Point2D.Double p) {
		super.setPosition(p);
		h1 = p.getX()+radius;
		k1 = p.getY()+radius;
		h2 = h1 + dx;
		k2 = k1 + dy;
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.RefractiveZone#contact(java.awt.geom.Point2D.Double)
	 */
	@Override
	public boolean contact(Point2D.Double p) {		
		if (getBox().contains(p)) 
			return true;
		else return false;
	}
	/**
	 * Initializes the inclination of the axis of the lens	
	 */
	private void initInclination() {
		super.setInclination((getBounds()[1]-getBounds()[3])/(getBounds()[0]-getBounds()[2]));
	}
	/**
	 * 
	 * @see gameComponents.RefractiveZone#resize(int)
	 */
	@Override
	public void resize(int m) {
		if (m > 0 && arcLength1 < 180) 
			arcLength1++;
		else if (m < 0 && arcLength1 > 1) 
			arcLength1--;

		arcAngleb1 = arcAngle1+arcLength1;
		if (arcAngleb1 >= 180)
			arcAngleb1 -= 180;
		
		initBounds();
		initInclination();
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.RefractiveZone#rotate(int)
	 */
	@Override
	public void rotate(int a) {
		arcAngle1+= a;

		if (arcAngle1 >= 360)
			arcAngle1 -= 360;
		if (arcAngle1 < 0)
			arcAngle1 += 360;
		arcAngleb1 = arcAngle1+arcLength1;
		if (arcAngleb1 >= 360)
			arcAngleb1 -= 360;
		
		initBounds();
		initInclination();
		initBox();
	}

	
	public double getRadius() {
		return radius;
	}

	public void setRadiusDirectly(double radius) {
		this.radius = radius;
	}
	
	public void setRadius(double m) {
		if (m > 0 && radius < 150) 
			radius++;
		else if (m < 0 && radius > 15) 
			radius--;
		initBounds();
		initBox();
	}

	public double getH1() {
		return h1;
	}

	public void setH1(double h1) {
		this.h1 = h1;
	}

	public double getH2() {
		return h2;
	}

	public void setH2(double h2) {
		this.h2 = h2;
	}

	public double getK1() {
		return k1;
	}

	public void setK1(double k1) {
		this.k1 = k1;
	}

	public double getK2() {
		return k2;
	}

	public void setK2(double k2) {
		this.k2 = k2;
	}

	public double getArcAngle1() {
		return arcAngle1;
	}

	public void setArcAngle1(double arcAngle1) {
		this.arcAngle1 = arcAngle1;
	}

	public double getArcAngle2() {
		return arcAngle2;
	}

	public void setArcAngle2(double arcAngle2) {
		this.arcAngle2 = arcAngle2;
	}

	public double getArcLength1() {
		return arcLength1;
	}

	public void setArcLength1(double arcLength1) {
		this.arcLength1 = arcLength1;
	}

	public double getArcLength2() {
		return arcLength2;
	}

	public void setArcLength2(double arcLength2) {
		this.arcLength2 = arcLength2;
	}
	/**
	 * 
	 * @see gameComponents.RefractiveZone#setNormalLine(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setNormalLine(Point2D.Double intersection, Point2D.Double p1) {	
		setNormalLine(new LineEq(h, k, intersection.getX(), intersection.getY()));		
	}
	/**
	 *
	 * @see gameComponents.RefractiveZone#refraction(double, double, double, double)
	 */
	@Override
	public double refraction(double n1, double n2, double m1, double m2) {

		/*
		 * computing refraction in a lens or a prism is slightly different 
		 * than in a refractive zone
		 */
		leaving = (leaving) ? false : true;
		
		double incidentAngle;
		boolean invert = false;
		
		if (infiniteSlope(m1) && infiniteSlope(m2))
			return m1;
		else if (infiniteSlope(m1)) {
			if (m2 >= 0)
				incidentAngle = Math.PI/2 - Math.atan(m2);
			else
				incidentAngle = Math.PI/2 - (Math.PI-Math.atan(m2));
		}
		else if (infiniteSlope(m2)) {
			if (m1 >= 0)
				incidentAngle = Math.PI/2 - Math.atan(m1);
			else
				incidentAngle = Math.PI/2 - (Math.PI-Math.atan(m1));
		}
		else {
			//m2 is the normal except if invert = true
		
			if (m1 > m2) {
				double temp = m1;
				m1 = m2;
				m2 = temp;
				invert = true;
			}
			incidentAngle = Math.abs(Math.atan((m2-m1)/(1 + m1*m2)));
		}
		
		double criticalAngle = criticalAngle(n1, n2);	
		if (incidentAngle < -Math.PI/2) {
			incidentAngle += Math.PI;
		}
		if (incidentAngle > Math.PI/2) {
			incidentAngle -= Math.PI;
		}
		
		if (incidentAngle >= criticalAngle) {	

			leaving = (leaving) ? false : true;
			
			if (invert) {
				double temp = m1;
				m1 = m2;
				m2 = temp;
			}
			return reflection(m1, m2);
		}	
		else {
			double refractedAngle = Math.asin((n1*Math.sin(incidentAngle))/n2); //snell's law
			double refractedSlope;
			if (invert) {
				double temp = m1;
				m1 = m2;
				m2 = temp;
			}
			// computation here depends on the situation, check everywhere for redundancy/ clean code
			if (infiniteSlope(m1)) {
				if (m2 >= 0)
					refractedSlope = Math.tan(Math.atan(m2)+refractedAngle);
				else
					refractedSlope = Math.tan(Math.atan(m2)-refractedAngle);
			}
			else if (infiniteSlope(m2)) {
				if (m1 >= 0)
					refractedSlope = Math.tan(Math.PI/2-refractedAngle);
				else
					refractedSlope = Math.tan(Math.PI/2+refractedAngle);
			}
			else {
					if ((m1 <= 0 && m2 <= 0) || (m1 >= 0 && m2 >= 0)) {
						if (Math.atan(m2) > Math.atan(m1)) 
							refractedSlope = Math.tan(Math.atan(m2)-refractedAngle);
						else 
							refractedSlope = Math.tan(Math.atan(m2)+refractedAngle);
					}
					else {
						
						if (m1 <= 0) 
							refractedSlope = Math.tan(Math.atan(m1)-(incidentAngle-refractedAngle));
						else 
							refractedSlope = Math.tan(Math.atan(m1)+(incidentAngle-refractedAngle));
					}
			}
			return refractedSlope;
		}
	}
	/**
	 * 
	 * @see gameComponents.RefractiveZone#intersection(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public Point2D.Double intersection(Point2D.Double p1, Point2D.Double p2, LineEq segment) {
	
		//determines which surface the ray segment just hit
		if (which(p1, segment) == 1) {
			h = h1;
			k = k1;
			arcAngle = arcAngle1;
			arcAngleb = arcAngleb1;
		}
		else {
			h = h2;
			k = k2;
			arcAngle = arcAngle2;
			arcAngleb = arcAngleb2;
		}
		
		Point2D.Double inter;	//intersection point	
		double interx, intery;	//x and y coordinates of the intersection point
		
		if (infiniteSlope(segment.getSlope())) {
			
			interx = p1.getX();
			
			double[] circleSols = circSolsX(p1.getX(), radius, h, k);
			double closeY = circleSols[0];
			double farY = circleSols[1];
			
			if (p1.distance(p1.getX(), farY) < p1.distance(p1.getX(), closeY)) {
				double temp = closeY;
				closeY = farY;
				farY = temp;
			}
			
			if (onSurface(interx, closeY))
				intery = closeY;
			else
				intery = farY;
		}
		else {	
			
			ArrayList<Point2D.Double> solutions = circSolsGeneral(segment, radius, h, k);

			if (solutions == null)
				return null;
				
			Point2D.Double closeSol = solutions.get(0);
			Point2D.Double farSol = solutions.get(1);
					
			double closeDis = closeSol.distance(p1);
			double farDis = farSol.distance(p1);
					
			if (closeDis > farDis) {
				Point2D.Double temp = (Point2D.Double)closeSol.clone();	
				closeSol = (Point2D.Double)farSol.clone();
				farSol = temp;		
			}

			if (onSurface(closeSol.getX(), closeSol.getY()) && !approx(p1, closeSol)) {
				interx = closeSol.getX();
				intery = closeSol.getY();
			}
			else {
				interx = farSol.getX();
				intery = farSol.getY();
			}
		}

		inter = new Point2D.Double(interx, intery);	

		if (onSurface(interx, intery) && !approx(inter, p1)) 
			return inter;
		else {
			leaving = false;
			return null;	
		}
	}
	/**
	 * Checks if a given point is on the lens or not
	 * @param px X coordinate of the tested point
	 * @param py Y coordinate of the tested point
	 * @return True if the tested point is on the surface of the lens, false otherwise
	 */
	private boolean onSurface(double px, double py) {	
	
		//compute the angle (az) at which this point is on
		//the circle equation of this curved mirror
		double ax = px - h;	
		double ay = k - py;
		double az = Math.toDegrees(Math.atan(ay/ax));	
	
		if (ax < 0 && ay < 0)
			az += 180;
		else if (ax < 0 && ay >= 0)
			az += 180;
		else if (az < 0)
			az += 360;

		//now check if that angle is within the range 
		//of the arc representing this surface of the lens
		if (arcAngleb > arcAngle) {
			
			if (az <= arcAngleb && az >= arcAngle)	//+-2 to account for imprecisions
				return true;
			else 
				return false;
		}
		else {
			
			if (az <= arcAngleb || az >= arcAngle)
				return true;
			else 
				return false;
		}
	}
	/**
	 * 
	 * @see gameComponents.RefractiveZone#exiting(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
	 */
	@Override
	public boolean exiting(Point2D.Double source, Point2D.Double intersec) {
		return leaving;
	}
	/**
	 * Determines whether the current ray is incident on surface 1 or 2
	 * @param p1 Source end point of the incident ray
	 * @param segment Incident ray segment
	 * @return 1 if interacting with surface 1, 2 otherwise
	 */
	private int which(Point2D.Double p1, LineEq segment) {	
		
		//intercept of the axis through the lens with the y-axis
		double bodyinter = getBounds()[1] - getInclination()*getBounds()[0];
		
		//intercepts with the y-axis of lines perpendicular to the lens 
		//and passing through (bounds[0], bounds[1]) and (bounds[2], bounds[3]) respectively
		double boundsinter1 = getBounds()[1] - (-1/getInclination())*getBounds()[0];
		double boundsinter2 = getBounds()[3] - (-1/getInclination())*getBounds()[2];
		
		double max = Math.max(p1.getX()*(-1/getInclination())+boundsinter1, p1.getX()*(-1/getInclination())+boundsinter2);
		double min = Math.min(p1.getX()*(-1/getInclination())+boundsinter1, p1.getX()*(-1/getInclination())+boundsinter2);
		
		//coordinates of the intersection of the ray segment with the axis throught the lens
		double tempx = (segment.getIntercept()-bodyinter)/(getInclination()-segment.getSlope());	
		double tempy = getInclination()*tempx + bodyinter;
		
		double src = p1.getY();	
		
		//compute distance between the source point of the ray segment and the closest
		//bounding point of the lens
		Point2D.Double bound1 = new Point2D.Double(getBounds()[0], getBounds()[1]);
		Point2D.Double bound2 = new Point2D.Double(getBounds()[2], getBounds()[3]);
		double disClosestBound = Math.min(p1.distance(bound1), p1.distance(bound2));
		
		//using the values computed above...
		
		//check if the y value of the source point of the ray is
		//within the lines perpendicular to the axis through the lens
		//i.e. checks if ray segment is "facing" one surface or the other
		if (src >= min && src <= max) {
			
			//if the ray is leaving the lens, it interacts with the surface
			//whose coordinates are the closest to its source point
			if (leaving) {
				if (p1.distance(h1, k1) < p1.distance(h2, k2)) 
					return 1;
				else return 2;
			}
			else {
				
				if (p1.distance(h1, k1) > p1.distance(h2, k2)) 
					return 1;
				else return 2;
			}
		}
		else {	//if the ray is not "facing" one of the surfaces...
			
			//if it crosses the axis through the lens before it intersects with a surface
			//then it interacts with the surface whose coordinates are closest to its source point,
			if (p1.distance(h1, k1) < p1.distance(h2, k2)) {
				if (p1.distance(tempx, tempy) <= disClosestBound) 
					return 1;
				else return 2;
			}
			else {
				
				if (p1.distance(tempx, tempy) <= disClosestBound) 
					return 2;
				else return 1;
			}
		}
	}
	/**
	 * 
	 * @see gameComponents.RefractiveZone#draw(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	public void draw(Graphics2D g0, Rectangle viewRec) {
		//if the lens is not visible to the user, do not draw it
		if (!getBox().intersects(viewRec))
			return;
		
		Graphics2D g = (Graphics2D)g0.create();
		
		//if the user selected the lens, highlight it in a different color
		if (isSelected()) 
			g.setColor(Color.YELLOW);
		else
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

		//draw the arcs the represent the lens (one for surface 1 and one for surface 2)
		g.drawArc((int)(h1-radius), (int)(k1-radius), (int)radius*2, (int)radius*2, 
				(int)arcAngle1, (int)arcLength1);
		g.drawArc((int)(h2-radius), (int)(k2-radius), (int)radius*2, (int)radius*2, 
				(int)arcAngle2, (int)arcLength1);
	}
	/**
	 * 
	 * @see gameComponents.RefractiveZone#getType()
	 */
	@Override
	public String getType() {
		return "Lens";
	}
}
