package gameComponents;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * 
 * Virtual Optics
 * <p>
 * This class models a plane mirror
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class Mirror extends OpticalObject { 

	private static final long serialVersionUID = 2980938889237079064L;
	/**
	 * The angle from the horizontal to the bounding
	 * point (b[0], b[1])
	 */
	private double angle;	
	/**
	 * Length of the plane mirror
	 */
	private double length;	
	/**
	 * The orientation can take the values:
	 * NORTH, EAST, SOUTH, WEST, Q1, Q2, Q3 or Q4.
	 * It is the direction that the reflective side of
	 * the mirror is facing
	 */
	private int orientation;  
	/**
	 * Orientation facing up
	 */
	public final int NORTH = 1;
	/**
	 * Orientation facing right
	 */
	public final int EAST = 2;
	/**
	 * Orientation facing down
	 */
	public final int SOUTH = 3;
	/**
	 * Orientation facing left
	 */
	public final int WEST = 4;
	/**
	 * Orientation facing first quadrant 
	 * of the Cartesian plane
	 */
	public final int Q1 = -1;	
	/**
	 * Orientation facing second quadrant 
	 * of the Cartesian plane
	 */
	public final int Q2 = -2;
	/**
	 * Orientation facing third quadrant 
	 * of the Cartesian plane
	 */
	public final int Q3 = -3;
	/**
	 * Orientation facing forth quadrant 
	 * of the Cartesian plane
	 */
	public final int Q4 = -4;

		
	public Mirror() { 
		setColor(Color.CYAN);
	}
	
	public Mirror(Point2D.Double position) {
		super(position);
	}
	
	public Mirror(double[] bounds) { 
		super(bounds);
		setColor(Color.CYAN);
		setInclination((bounds[1]-bounds[3])/(bounds[0]-bounds[2]));
		length = distance(bounds[0], bounds[1], bounds[2], bounds[3]);
		initOrientation();
		Point2D.Double mid = midPoint(bounds[0], bounds[1], bounds[2], bounds[3]);
		super.setPosition(mid);
		
		double ax = bounds[0] - mid.getX();	
		double ay = mid.getY() - bounds[1];
		angle = Math.toDegrees(Math.atan(ay/ax));	
		
		if (ax < 0 && ay < 0)
			angle += 180;
		else if (ax < 0 && ay >= 0)
			angle += 180;
		else if (angle < 0)
			angle += 360;
		
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#initBox()
	 */
	@Override
	public void initBox() {
		int w = (int)Math.abs(getBounds()[2]-getBounds()[0]);
		if (w == 0)	w = 5;
		int h = (int)Math.abs(getBounds()[3]-getBounds()[1]);
		if (h == 0)	h = 5;
		
		Rectangle rec = new Rectangle((int)Math.min(getBounds()[0], getBounds()[2]), (int)Math.min(getBounds()[1], getBounds()[3]), w, h);	
		setBox(rec);
	}
	
	
	public double getLength() {
		return length;
	}
	
	public void setLength(double length) {
		this.length = length;
	}
	
	public int getOrientation() {
		return orientation;
	}
	
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#setPosition(java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setPosition(Point2D.Double p) {

		double[] bounds = super.getBounds();
		double xDiff = (p.getX() - super.getPosition().getX());
		double yDiff = (p.getY() - super.getPosition().getY());
		super.setBounds(bounds[0]+xDiff, bounds[1]+yDiff, bounds[2]+xDiff, bounds[3]+yDiff);

		super.setPosition(p);
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#resize(int)
	 */
	@Override
	public void resize(int m) {
		if (Math.abs(m) < 5)
			m *= 10;
		else m *= 2;
		if (length > 0 && length <= 400)
			length += m;
		if (length <= 0) length = 1;
		if (length > 400) length = 400;
		
		double angle2 = angle+180;
		if (angle2 >= 360)
			angle2 -= 360;
	          
	    double x1 = getPosition().getX() + (length/2)*Math.cos(Math.toRadians(angle)); 
	    double y1 = getPosition().getY() - (length/2)*Math.sin(Math.toRadians(angle)); 
	    double x2 = getPosition().getX() + (length/2)*Math.cos(Math.toRadians(angle2)); 
	    double y2 = getPosition().getY() - (length/2)*Math.sin(Math.toRadians(angle2)); 
	        
	    setBounds(x1, y1, x2, y2);
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#rotate(int)
	 */
	@Override
	public void rotate(int a) {	
		angle += a;

		if (angle >= 360)
			angle -= 360;
		if (angle < 0)
			angle += 360;
		
		double angle2 = angle+180;
		if (angle2 >= 360)
			angle2 -= 360;
	          
	    double x1 = getPosition().getX() + (length/2)*Math.cos(Math.toRadians(angle)); 
	    double y1 = getPosition().getY() - (length/2)*Math.sin(Math.toRadians(angle)); 
	    double x2 = getPosition().getX() + (length/2)*Math.cos(Math.toRadians(angle2)); 
	    double y2 = getPosition().getY() - (length/2)*Math.sin(Math.toRadians(angle2)); 
	        
	    setBounds(x1, y1, x2, y2);
	        
	    setInclination((y1-y2)/(x1-x2));
	    initOrientation();
	    initBox();
	}
	/**
	 * Initializes the orientation variable depending on the bounding points
	 */
	private void initOrientation() {	
		double[] b = getBounds();
		
		if (b[1]==b[3] && b[0] < b[2])
			orientation = NORTH;
		
		else if (b[1]==b[3] && b[0] > b[2])
			orientation = SOUTH;
		
		else if (b[0]==b[2] && b[1] < b[3])
			orientation = EAST;
		
		else if (b[0]==b[2] && b[1] > b[3])
			orientation = WEST;
		
		else if (b[0]<b[2] && b[1]<b[3])
			orientation = Q1;
		
		else if (b[0]<b[2] && b[1]>b[3])
			orientation = Q2;
		
		else if (b[0]>b[2] && b[1]>b[3])
			orientation = Q3;
		
		else if (b[0]>b[2] && b[1]<b[3])
			orientation = Q4;
	}
	/**
	 * The arguments intersection and segment are not used here but they are in the subclass
	 * @see gameComponents.OpticalObject#checkOrientation(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public boolean checkOrientation(Point2D.Double p, Point2D.Double intersection, LineEq segment) {
		//uses orientation value to see if incident ray segment is hitting the
		//the reflective side of the mirror or its non-reflective side 
		double[] b = getBounds();
		double intercept = b[1]-getInclination()*b[0];
		
		switch (orientation) {
		
		case NORTH: if (p.getY() < getPosition().getY())
						return true;
					else return false;

		case SOUTH: if (p.getY() > getPosition().getY())
						return true;
					else return false;
		
		case EAST: if (p.getX() > getPosition().getX())
						return true;
					else return false;

		case WEST: if (p.getX() < getPosition().getX())
						return true;
					else return false;
		
		case Q1: if (p.getY() < getInclination()*p.getX()+intercept)
					return true;
				 else return false;
					
		case Q2: if (p.getY() < getInclination()*p.getX()+intercept)
					return true;
				 else return false;
		
		case Q3: if (p.getY() > getInclination()*p.getX()+intercept)
					return true;
				 else return false;
		
		case Q4: if (p.getY() > getInclination()*p.getX()+intercept)
					return true;
				 else return false;
		
		}

		return false;
	}
	/**
	 *
	 * @see gameComponents.OpticalObject#setNormalLine(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setNormalLine(Point2D.Double intersection, Point2D.Double p1) {
		/*
		 * The normal line is perpendicular to the incident surface,
		 * this is why the slope argument when constructing a LineEq object is
		 * 1/inclination
		 */
		setNormalLine(new LineEq(-1/getInclination(), intersection.getX(), intersection.getY()));
	}
	/**
	 * 
	 * @see gameComponents.OpticalObject#bend(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public Point2D.Double bend(Point2D.Double source, Point2D.Double intersec, LineEq incidentLine) {
		
		setNormalLine(intersec, source);
		
		//compute the slope of the reflected line and create a resultLine with that value
		double resultSlope = reflection(incidentLine.getSlope(), getNormalLine().getSlope());
		LineEq resultLine = new LineEq(resultSlope, intersec.getX(), intersec.getY());
		
		//these are the coordinates of the new end-point of the ray path that hit this optical object
		//we use BIG to simulate a point located very far away
		double farX = BIG;
		double farY = BIG;
		
		//if both the resultLine and incidentLine are vertical, the ray bounces back in the opposite direction
		if (infiniteSlope(resultSlope) && infiniteSlope(incidentLine.getSlope())){

			farX = intersec.getX();
			
			if (intersec.getY() > source.getY())
				farY *= -1;

			return new Point2D.Double(farX, farY);
		}	
					
		//if the resulting line is on the same side as the incident line, change the far point to adjust the line
		if (checkSide(incidentLine, resultLine, source, intersec)) 
			farX *= -1;
			
		//compute farY by plugging-in the value of farX in the equation of the resultLine
		farY = resultLine.getSlope()*farX + resultLine.getIntercept();

		return new Point2D.Double(farX, farY);
	}
	/**
	 * 
	 * @see gameComponents.OpticalObject#checkSide(gameComponents.LineEq, gameComponents.LineEq, java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
	 */
	@Override
	public boolean checkSide(LineEq incidentLine, LineEq resultLine, Point2D.Double source, Point2D.Double intersec) { 
		
		if (infiniteSlope(getNormalLine().getSlope())) {
			//if the source point and the far endpoint BIG are on the same side of the intersection
			if ((source.getX() < intersec.getX() && BIG < intersec.getX()) || (source.getX() > intersec.getX() && BIG > intersec.getX()))
				return true;
		}
		else if (infiniteSlope(incidentLine.getSlope()) && !(this instanceof CurvedMirror)) {	
			if (orientation == Q3 || orientation == Q2)
				return true;
		}
		else if (infiniteSlope(getInclination()) && orientation == WEST) {
				return true;
		}
		else if (infiniteSlope(incidentLine.getSlope()) && infiniteSlope(getNormalLine().getSlope())) {
			return false;
		}
		else {
			
			double sourceOnNormal = getNormalLine().getSlope()*source.getX()+getNormalLine().getIntercept();
			double farOnResult = resultLine.getSlope()*BIG+resultLine.getIntercept();
			double farOnNormal = getNormalLine().getSlope()*BIG+getNormalLine().getIntercept();

			//if the slopes are approximately the same, the angle between them will be very small
			if (Math.toDegrees(angleBetween(incidentLine.getSlope(), resultLine.getSlope())) < 1) {
				if (source.getX() < intersec.getX())
					return true;
			}
			//if the source point and the far endpoint BIG are on the same side of the source point
			else if ((source.getY() < sourceOnNormal && farOnResult < farOnNormal)
				|| (source.getY() > sourceOnNormal && farOnResult > farOnNormal)) {
				return true;
			}
			
		}
		
		return false;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#intersection(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public Point2D.Double intersection(Point2D.Double p1, Point2D.Double p2, LineEq segment) {

		Point2D.Double inter;	//the intersection point
		double[] b = getBounds();
		
		//the intercept of the line that goes through the axis of this mirror with the y-axis
		double interceptMirror = b[1] - getInclination()*b[0];	

		double interx;	//x coordinate of the intersection point
		double intery;  //y coordinate of the intersection point
		
		if (infiniteSlope(getInclination()) && infiniteSlope(segment.getSlope())) 
			return null;
	
		else if (infiniteSlope(segment.getSlope())) {	
			interx = p1.getX();
			intery = getInclination()*interx + interceptMirror;
		}
		else if (infiniteSlope(getInclination())) {
			interx = getPosition().getX();
			intery = segment.getSlope()*interx + segment.getIntercept();
		}
		else {				
			interx = (segment.getIntercept()-interceptMirror)/(getInclination()-segment.getSlope());
			intery = getInclination()*interx + interceptMirror;
		}
			
		inter = new Point2D.Double(interx, intery);

		//check that intersection is within the bounds of the mirror, and that it is not the same point as the source point (p1)
		if (isWithinBounds(inter, b) && !approx(p1, inter))
			return inter;
		else return null;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#draw(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	public void draw(Graphics2D g0, Rectangle viewRec) {
		//if the optical object is not visible to the user, do not draw it
		if (!getBox().intersects(viewRec))
			return;

		Graphics2D g = (Graphics2D)g0.create();
		
		//change the color when the user selects it
		if (isSelected()) 
			g.setColor(Color.YELLOW);
		
		double[] b = getBounds();
		g.drawLine((int)b[0], (int)b[1], (int)b[2], (int)b[3]);	//draws the reflective side
		
		//now draw the non-reflective side, depends on the orientation
		g.setColor(Color.BLACK);
		int dis = 2;	//distance between reflective and non-reflective sides
		
		if (orientation == NORTH) {
			g.drawLine((int)b[0], (int)b[1]+dis, (int)b[2], (int)b[3]+dis);
		}
		else if (orientation == SOUTH) {
			g.drawLine((int)b[0], (int)b[1]-dis, (int)b[2], (int)b[3]-dis);
		}
		else if (orientation == EAST) {
			g.drawLine((int)b[0]-dis, (int)b[1], (int)b[2]-dis, (int)b[3]);
		}
		else if (orientation == WEST){
			g.drawLine((int)b[0]+dis, (int)b[1], (int)b[2]+dis, (int)b[3]);
		}
		else if (orientation == Q1) { 
			g.drawLine((int)b[0]-dis, (int)b[1]+dis, (int)b[2]-dis, (int)b[3]+dis);
		}
		else if (orientation == Q2) {
			g.drawLine((int)b[0]+dis, (int)b[1]+dis, (int)b[2]+dis, (int)b[3]+dis);
		}
		else if (orientation == Q3) {
			g.drawLine((int)b[0]+dis, (int)b[1]-dis, (int)b[2]+dis, (int)b[3]-dis);
		}
		else if (orientation == Q4) {
			g.drawLine((int)b[0]-dis, (int)b[1]-dis, (int)b[2]-dis, (int)b[3]-dis);
		}
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#getType()
	 */
	@Override
	public String getType() {
		return "Mirror";
	}
}
