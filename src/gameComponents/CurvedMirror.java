package gameComponents;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * 
 * Virtual Optics
 * <p>
 * This class models a curved mirror
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class CurvedMirror extends Mirror {

	private static final long serialVersionUID = 4657868968094799031L;
	/**
	 * Radius of the circle equation representing 
	 * this curved mirror
	 */
	private int radius = 50;
	/**
	 * X coordinate of the circle equation
	 * called h by convention
	 */
	private double h; 
	/**
	 * Y coordinate of the circle equation
	 * called k by convention
	 */
	private double k; 
	/**
	 * Tells whether the mirror is convergent (true) or divergent (false).
	 * A convergent mirror has its reflective side towards its center,
	 * while a divergent mirror has its reflective side pointing outwards
	 */
	private boolean convergent = true;
	/**
	 * The angle from the horizontal to the
	 * first end point of the arc representing the mirror
	 */
	private double arcAngle =  270;
	/**
	 * The angle from the horizontal to the
	 * second end point of the arc representing the mirror
	 */
	private double arcAngleb;
	/**
	 * The length of the arc representing the curved mirror,
	 * specified in degrees
	 */
	private double arcLength = 180;
		
	
	public CurvedMirror() {
		setColor(Color.CYAN);
	}
	
	public CurvedMirror(Point2D.Double position) {
		super(position);
		setColor(Color.CYAN);
	}
	
	public CurvedMirror(Point2D.Double position, double h, double k) {
		super(position);
		setColor(Color.CYAN);
		this.h = h;
		this.k = k;
		arcAngleb = arcAngle+arcLength;
		if (arcAngleb >= 360)
			arcAngleb -= 360;
		initBounds();
		initBox();
		
		super.setPosition(new Point2D.Double(h, k));
	}
	/**
	 * Initializes the bounds of this curved mirror.
	 * The bounds are the end points of the arc representing
	 * the mirror
	 */
    private void initBounds() { 
        double a1 = arcAngle; 
        double a2 = arcAngle+arcLength; 
        double a3 = (a1+a2)/2;
        if (a2 >= 360) 
            a2 -= 360; 
        if (a3 >= 360) 
            a3 -= 360; 
          
        double x1 = h + radius*Math.cos(Math.toRadians(a1)); 
        double y1 = k - radius*Math.sin(Math.toRadians(a1)); 
        double x2 = h + radius*Math.cos(Math.toRadians(a2)); 
        double y2 = k - radius*Math.sin(Math.toRadians(a2)); 
        
        setBounds(x1, y1, x2, y2); 
    }
    /**
     * 
     * @see gameComponents.Mirror#initBox()
     */
    @Override
    public void initBox() {
		Arc2D.Double arc = new Arc2D.Double((h-radius), (k-radius), radius*2, radius*2, arcAngle, arcLength, Arc2D.OPEN);
    	Rectangle rec = (Rectangle)arc.getBounds2D().getBounds();
		setBox(rec);
	}
	
	
	public double getRadius() {
		return radius;
	}

	public void setRadius(int m) {
		if (m > 0 && radius < 300) 
			radius++;
		else if (m < 0 && radius > 5) 
			radius--;
		initBounds();
		initBox();
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getK() {
		return k;
	}

	public void setK(double k) {
		this.k = k;
	}

	public boolean isConvergent() {
		return convergent;
	}

	public void setConvergent(boolean convergent) {
		this.convergent = convergent;
	}

	public double getArcAngle() {
		return arcAngle;
	}

	public void setArcAngle(double arcAngle) {
		this.arcAngle = arcAngle;
	}

	public double getArcLength() {
		return arcLength;
	}

	public void setArcLength(double arcLength) {
		this.arcLength = arcLength;
	}
    /**
     * 
     * @see gameComponents.Mirror#resize(int)
     */
	@Override
	public void resize(int m) {
		
		if (arcLength < 360 && arcLength > 1) 
			arcLength += m;
		if (arcLength >= 360) arcLength = 359;
		if (arcLength <= 1) arcLength = 2;

		arcAngleb = arcAngle+arcLength;
		if (arcAngleb >= 360)
			arcAngleb -= 360;
		
		initBounds();
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.Mirror#rotate(int)
	 */
	@Override
	public void rotate(int a) {
		arcAngle+= a;

		if (arcAngle >= 360)
			arcAngle -= 360;
		if (arcAngle < 0)
			arcAngle += 360;
		arcAngleb = arcAngle+arcLength;
		if (arcAngleb >= 360)
			arcAngleb -= 360;
		
		initBounds();
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.Mirror#setPosition(java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setPosition(Point2D.Double p) {
		super.setPosition(p);
		h = p.getX();
		k = p.getY();
		initBounds();
		initBox();
	}
	
	/**
	 * The argument p1 is not used here, but is necessary to allow this method to override the one in the superclass
	 * @see gameComponents.Mirror#setNormalLine(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setNormalLine(Point2D.Double intersection, Point2D.Double p1) {
		setNormalLine(new LineEq(h, k, intersection.getX(), intersection.getY()));
	}
	/**
	 * 
	 * @see gameComponents.Mirror#checkOrientation(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public boolean checkOrientation(Point2D.Double source, Point2D.Double intersec, LineEq segment) {

		if (source.distance(getPosition()) > radius+2) {	//the +2 accounts for some potential imprecision
			
			//if the source of the ray is not within the radius of this curved mirror
			//then the ray hits a reflective surface if the mirror is convergent 
			//and if the intersection is not the closest solution...
			if (convergent && !closestSol(source, intersec, segment)) 
				return true;
			
			//...or if it is divergent and the intersection is the closest solution
			else if (!convergent && closestSol(source, intersec, segment)) 
				return true;
			
			else return false;
		}
		else {	
			//if the source of the ray segment is within the radius of this curved mirror
			//then the ray will inevitably hit a reflective surface
			if (convergent) 
				return true;
			else return false;
		}
	}
	/**
	 * Tells if the intersection point of an incident ray with 
	 * this curved mirror is the closest of two possible solutions 
	 * @param source Source end point of the incident ray segment
	 * @param intersec Intersection point to check
	 * @param segment Incident ray segment
	 * @return True if intersec is the closest solution, false otherwise
	 */
	private boolean closestSol(Point2D.Double source, Point2D.Double intersec, LineEq segment) {
		
		ArrayList<Point2D.Double> solutions = new ArrayList<>();
			
		if (infiniteSlope((source.getY()-intersec.getY())/(source.getX()-intersec.getX()))) {
				
			//if the line from source to intersec is vertical, use circSolsX to compute the intersections
			double[] c = circSolsX(source.getX(), radius, h, k);
			solutions.add(new Point2D.Double(source.getX(), c[0]));
			solutions.add(new Point2D.Double(source.getX(), c[1]));
		}
		else solutions = circSolsGeneral(segment, radius, h, k);	

		//determine which solution of the intersection of the line with this curved mirror 
		//is closest to the current intersection
		if (intersec.distance(solutions.get(0)) < intersec.distance(solutions.get(1)) ) {
			
			if (source.distance(intersec) <= source.distance(solutions.get(1))) 
				return true;
			else return false;
		}
		else {
			
			if (source.distance(intersec) <= source.distance(solutions.get(0)))
				return true;
			else return false;
		}
	}
	/**
	 * 
	 * @see gameComponents.Mirror#intersection(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public Point2D.Double intersection(Point2D.Double p1, Point2D.Double p2, LineEq segment) {
			
			Point2D.Double inter;	//the intersection point	
			double interx, intery;	//the x and y coordinates of the intersection point
			
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
				
				if (onSurface(interx, closeY) && sameQuadrant(p1, p2, new Point2D.Double(interx, closeY)))
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
				double closeDis = p1.distance(closeSol);
				double farDis = p1.distance(farSol);
					
				if (closeDis > farDis) {
					Point2D.Double temp = (Point2D.Double)closeSol.clone();	
					closeSol = (Point2D.Double)farSol.clone();
					farSol = temp;		
				}

				if (onSurface(closeSol.getX(), closeSol.getY()) && !approx(closeSol, p1) && sameQuadrant(p1, p2, closeSol)) {
					interx = closeSol.getX();
					intery = closeSol.getY();
				}
				else {
					interx = farSol.getX();
					intery = farSol.getY();
				}
				
			}

			inter = new Point2D.Double(interx, intery);

			//the intersection point is valid if it is on the surface 
			//of this curved mirror and if it is not equal to the source point 
			//of the ray segment intersecting with the mirror
			if (onSurface(interx, intery) && !approx(p1, inter))	
				return inter;
			else return null;
	}
	/**
	 * Checks if a given point is on the curved mirror or not
	 * @param px X coordinate of the tested point
	 * @param py Y coordinate of the tested point
	 * @return True if the tested point is on the surface of the curved mirror, false otherwise
	 */
	private boolean onSurface(double px, double py) {
		
		//compute the angle (az) at which this point is on
		// the circle equation of this curved mirror
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
		//of the arc representing this curved mirror
		if (arcAngleb > arcAngle) {
			
			if (az <= arcAngleb+1 && az >= arcAngle-1)
				return true;
			else 
				return false;
		}
		else {
			
			if (az <= arcAngleb+1 || az >= arcAngle-1)
				return true;
			else 
				return false;
		}
	}
	/**
	 * 
	 * @see gameComponents.Mirror#draw(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	public void draw(Graphics2D g0, Rectangle viewRec) {
		//if curvedMirror is not visible to the user, do not draw it
		if (!getBox().intersects(viewRec))
			return;
		
		Graphics2D g = (Graphics2D)g0.create();
		
		//if the user selects this curved mirror, change its color
		if (isSelected()) 
			g.setColor(Color.YELLOW);
		
		//draws the arc that represents the reflective side of the curved mirror
		//depends on whether the curved mirror is convergent or not
		g.drawArc((int)(h-radius), (int)(k-radius), (int)radius*2, (int)radius*2, (int)arcAngle, (int)arcLength);
		
		//now draw the non-reflective side
		g.setColor(Color.BLACK);
		int dis = 2; 	//distance between reflective and non-reflective sides
		
		if (convergent)
			g.drawArc((int)(h-radius-dis), (int)(k-radius-dis), (int)(radius+dis)*dis, (int)(radius+dis)*dis, (int)arcAngle, (int)arcLength);
		else
			g.drawArc((int)(h-radius+dis), (int)(k-radius+dis), (int)(radius-dis)*dis, (int)(radius-dis)*dis, (int)arcAngle, (int)arcLength);
	}
	/**
	 * 
	 * @see gameComponents.Mirror#getType()
	 */
	@Override
	public String getType() {
		return "CurvedMirror";
	}
}
