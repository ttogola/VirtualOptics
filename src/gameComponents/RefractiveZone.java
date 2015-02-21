package gameComponents;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * 
 * Virtual Optics
 * <p>
 * This class models a refractive zone.
 * It implements the Cloneable interface to enable objects to be duplicated in a method in the Lab class
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class RefractiveZone extends OpticalObject implements Cloneable {	
	
	private static final long serialVersionUID = -4747016761271858698L;
	/**
	 * This index determines how much a ray is bent
	 * when it enters the refractive zone;
	 * values between 1.0 and 5.0
	 */
	private double refractionIndex = 1;
	/**
	 * The refraction index of the zone surrounding this refractive zone.
	 * Value of 1.0 if it is not nested in another zone
	 */
	private double outerIndex = 1;
	/**
	 * Width of the rectangular shape of this refractive zone
	 */
	private int width = 100;
	/**
	 * Height of the rectangular shape of this refractive zone
	 */
	private int height = 100;
	
	
	
	public RefractiveZone() {
		setColor(Color.CYAN);
	}
	
	public RefractiveZone(Point2D.Double position) {
		super(position);
		setColor(Color.CYAN);
		initBounds();
		initBox();
	}
	
	/**
	 * Initializes the bounding points of the zone
	 */
	protected void initBounds() {
		super.setBounds(getPosition().getX(), getPosition().getY(), getPosition().getX()+width, getPosition().getY()+height);
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#initBox()
	 */
	@Override
	public void initBox() {
		int w = (int)Math.abs(getBounds()[2]-getBounds()[0]);
		if (w == 0)	w = 1;
		int h = (int)Math.abs(getBounds()[3]-getBounds()[1]);
		if (h == 0)	h = 1;
		
		Rectangle rec = new Rectangle((int)Math.min(getBounds()[0], getBounds()[2]), (int)Math.min(getBounds()[1], getBounds()[3]), w, h);	
		setBox(rec);
	}
	
	
	public double getRefractionIndex() {
		return refractionIndex;
	}

	public void setRefractionIndex(double refractionIndex) {
		this.refractionIndex = refractionIndex;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		super.setBounds(getPosition().getX(), getPosition().getY(), getPosition().getX()+width, getPosition().getY()+height);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		super.setBounds(getPosition().getX(), getPosition().getY(), getPosition().getX()+width, getPosition().getY()+height);
	}
	
	public double getOuterIndex() {
		return outerIndex;
	}
	
	public void setOuterIndex(double outerIndex) {
		this.outerIndex = outerIndex;
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
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/**
	 * Adjusts the refraction index, compatible with calls from lab listeners
	 * @see gameComponents.GameComponent#resize(int)
	 */
	@Override
	public void resize(int m) {
		if (m > 0 && refractionIndex < 4.9) 
			refractionIndex+=0.1;
		else if (m < 0 && refractionIndex > 1) 
			refractionIndex-=0.1;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#rotate(int)
	 */
	@Override
	public void rotate(int a) {
		
	}
	/**
	 * Adjusts the width and height of the refractive zone
	 * @param x X coordinate of the new lower right corner of the zone
	 * @param y Y coordinate of the new lower right corner of the zone
	 */
	public void scale(double x, double y) {
		if (isRotateable()) {
			
			if (width + x - getBounds()[2] > 25) {  
				width += x - getBounds()[2];
				setBounds(getBounds()[0], getBounds()[1], x, getBounds()[3]);
			}
		
			if (height + y - getBounds()[3] > 25) {
				height += y - getBounds()[3];
				setBounds(getBounds()[0], getBounds()[1], getBounds()[2], y);
			}
		}
		
		initBox();
	}
	
	/**
	 * Always returns true here because a ray of light 
	 * will always pass through this kind of optical object
	 * @see gameComponents.OpticalObject#checkOrientation(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public boolean checkOrientation(Point2D.Double p, Point2D.Double useless, LineEq segment) {
		return true;
	}
	/**
	 * 
	 * @see gameComponents.OpticalObject#setNormalLine(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setNormalLine(Point2D.Double intersection, Point2D.Double p1) {
		//if the intersection point is on an horizontal surface, set normal line with
		//a slope of zero, otherwise it hit a vertical side and set the slope to infinity
		if ((intersection.getX() == getBounds()[0] || intersection.getX() == getBounds()[2]) 
				&& (intersection.getY() != getBounds()[1] && intersection.getY() != getBounds()[3]))
			
			setNormalLine(new LineEq(0, intersection.getX(), intersection.getY()));
		else
			setNormalLine(new LineEq(1.0/0, intersection.getX(), intersection.getY()));
	}
	/**
	 * 
	 * @see gameComponents.OpticalObject#bend(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public Point2D.Double bend(Point2D.Double source, Point2D.Double intersec, LineEq incidentLine) {

		setNormalLine(intersec, source);

		double resultSlope;
		
		//if the ray that hit this optical object is going out of it n1 = refractionIndex and n2 = outerIndex,
		//see refraction(n1, n2, m1, m2) method, otherwise swap the two values
		//compute the slope of the refracted line and create a resultLine with that value
		if (exiting(source, intersec)) 
			resultSlope = refraction(refractionIndex, outerIndex, incidentLine.getSlope(), getNormalLine().getSlope());
		else 
			resultSlope = refraction(outerIndex, refractionIndex, incidentLine.getSlope(), getNormalLine().getSlope());	

		LineEq resultLine = new LineEq(resultSlope, intersec.getX(), intersec.getY());
		
		//these are the coordinates of the new endpoint of the ray path that hit this optical object
		//we use BIG to simulate a point located very far away
		double farX = BIG;
		double farY = BIG;
		
		if ((getType().equals("RefractiveZone") && infiniteSlope(incidentLine.getSlope()))
				|| (this instanceof Lens && (infiniteSlope(resultSlope) /*|| Math.abs(resultSlope) > 5000*/))) {	

			farX = intersec.getX();
			
			if (intersec.getY() < source.getY())
				farY *= -1;

			return new Point2D.Double(farX, farY);
		}	
		
		//checks that the resulting line is on the right side of the normal line
		//if not, change the far point
		if (checkSide(incidentLine, resultLine, source, intersec)) 
			farX *= -1;
			
		//compute farY by plug-in the value of farX in the equation of the resultLine
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
			if ((source.getX() < intersec.getX() && BIG < intersec.getX()) || (source.getX() > intersec.getX() && BIG > intersec.getX()))
				return true;
		}
		else if (infiniteSlope(incidentLine.getSlope())) {
			if (this instanceof Lens && getNormalLine().getSlope() < 0) 	
				return true;
		}
		else if (incidentLine.getSlope() == 0) {
			if (source.getX() > intersec.getX())
				return true;
		}
		else {
			double sourceOnNormal = getNormalLine().getSlope()*source.getX()+getNormalLine().getIntercept();
			double farOnResult = resultLine.getSlope()*BIG+resultLine.getIntercept();
			double farOnNormal = getNormalLine().getSlope()*BIG+getNormalLine().getIntercept();
			
			//if the slopes are approximately the same, the angle between them will be very small
			if (Math.toDegrees(angleBetween(incidentLine.getSlope(), resultLine.getSlope())) < 1) {
				if (source.getX() > intersec.getX())
					return true;
			}
			//if the source point and the far end point BIG are on the same side of the source point
			else if ((source.getY() < sourceOnNormal && farOnResult < farOnNormal)
						|| (source.getY() > sourceOnNormal && farOnResult > farOnNormal)) 
				return true;
		}
		
		return false;
	}
	/**
	 * 
	 * Computes the slope of a ray segment refracted by this optical object
	 * @param n1 The refraction index of the origin zone of the ray 
	 * @param n2 The refraction index of the current zone
	 * @param m1 Slope of the incident ray
	 * @param m2 Slope of the normal line of this optical object
	 * @return Slope of the refracted ray
	 */
	public double refraction(double n1, double n2, double m1, double m2) {
		
		double incidentAngle;	//angle between the incident ray and the normal line
		
		if (infiniteSlope(m1))
			return m1;
		
		else if (infiniteSlope(m2)) {
			
			if (m1 >= 0)
				incidentAngle = Math.PI/2 - Math.atan(m1);
			else
				incidentAngle = Math.PI/2 - (Math.PI-Math.atan(m1));
		}
		else {
			incidentAngle = angleBetween(m1, m2);
		}
		
		//compute the critical angle
		double criticalAngle = criticalAngle(n1, n2);
		
		//adjust 
		if (incidentAngle < 0)	
			criticalAngle -= Math.PI;
		
		//total internal reflection occurs when angle of 
		//incidence is bigger then critical angle
		if (incidentAngle >= criticalAngle) {
			return reflection(m1, m2);
		}	
		else {
			
			//compute the refracted angle using snell's law
			double refractedAngle = Math.asin((n1*Math.sin(incidentAngle))/n2);
			double refractedSlope;
			
			// computation here depends on the situation
			if (infiniteSlope(m2))
				refractedSlope = Math.tan(Math.PI/2-refractedAngle);
			
			else {
				
				if (m1 < 0)
					refractedSlope = Math.tan(Math.PI-refractedAngle);
				else 
					refractedSlope = Math.tan(refractedAngle);	
			}
			
			return refractedSlope;
		}
	}
	/**
	 * The critical angle is the angle at which a ray is reflected
	 * instead of refracted in a refractive zone. This occurs
	 * if its incidence angle is greater than the critical angle
	 * 
	 * @param n1 Refractive index of origin zone
	 * @param n2 Refractive index of destination zone
	 * @return Critical angle of this zone
	 */
	public double criticalAngle(double n1, double n2) {
		
		double criticalAngle = Math.asin(n2/n1);
		
		return criticalAngle;
	}
	/**
	 * Detects contact if the mouse pointer
	 * is near a side of the refractive zone
	 * @see gameComponents.GameComponent#contact(java.awt.geom.Point2D.Double)
	 */
	@Override
	public boolean contact(Point2D.Double p) {
		//argument p is the mouse point

		//buffer around the sides of the zone
		//the facilitate contact
		double pr = 10;
		
		if (p.getX() < getBounds()[2] && p.getX() > getBounds()[0]
				&& approx(p.getY(), getBounds()[1], pr)) {
			return true;
		}
		else if (p.getX() < getBounds()[2] && p.getX() > getBounds()[0]
				&& approx(p.getY(), getBounds()[3], pr)) {
			return true;

		}
		else if (p.getY() < getBounds()[3] && p.getY() > getBounds()[1]
				&& approx(p.getX(), getBounds()[0], pr)) {
			return true;

		}
		else if (p.getY() < getBounds()[3] && p.getY() > getBounds()[1]
				&& approx(p.getX(), getBounds()[2], pr)){
			return true;
		}
		else return false;
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#intersection(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public Point2D.Double intersection(Point2D.Double p1, Point2D.Double p2, LineEq segment) {

		Point2D.Double inter;	//intersection point
		double interx;	//x coordinate of intersection point
		double intery;	//y coordinate of intersection point
		
		double[] b = getBounds();

		if (infiniteSlope(segment.getSlope())) {
			
			interx = p1.getX();
			
			if (p1.getY() < p2.getY()) {
				if (p1.getY() < b[1])
					intery = b[1];
				else 
					intery = b[3];
			}
			else {
				if (p1.getY() > b[3])
					intery = b[3];
				else
					intery = b[1];
			}
		}
		else if (segment.getSlope() == 0) {
			
			intery = p1.getY();
			
			if (p1.getX() < p2.getX()) {
				if (p1.getX() < b[0])
					interx = b[0];
				else
					interx = b[2];
			}
			else {
				if (p1.getX() > b[2])
					interx = b[2];
				else
					interx = b[0];
			}
		}
		else {	
			
			//take all the intersections and keep the valid ones only
			Point2D.Double top = new Point2D.Double(((b[1]-segment.getIntercept())/segment.getSlope()), b[1]);
			Point2D.Double bottom = new Point2D.Double(((b[3]-segment.getIntercept())/segment.getSlope()), b[3]);
			Point2D.Double left = new Point2D.Double(b[0], segment.getSlope()*b[0]+segment.getIntercept());
			Point2D.Double right = new Point2D.Double(b[2], segment.getSlope()*b[2]+segment.getIntercept());
			ArrayList<Point2D.Double> points = new ArrayList<>();
			points.add(top);
			points.add(bottom);
			points.add(left);
			points.add(right);
			
			for (int i = 0; i < points.size(); i++) {
				if (!(points.get(i).getX() <= b[2] && points.get(i).getX() >= b[0] && points.get(i).getY() <= b[3] && points.get(i).getY() >= b[1]) 
						|| !sameQuadrant(p1, p2, points.get(i))) {
					points.remove(i);
					i--;
				}
			}
			
			if (points.size() > 0) {
				
				interx = points.get(0).getX();
				intery = points.get(0).getY();
				
				if (p1.distance(interx, intery) < 2 && points.size() > 1) {
					interx = points.get(1).getX();
					intery = points.get(1).getY();
				}
				
				//keep the solution that is closest to the source point p1
				for (int i = 1; i < points.size(); i++) {
					double d = p1.distance(points.get(i));	
					if (p1.distance(interx, intery) > d && !(d < 2)) {
						interx = points.get(i).getX();
						intery = points.get(i).getY();
					}
				}
			}
			else return null;
		}
			
		inter = new Point2D.Double(interx, intery);

		if (isWithinBounds(inter, b))
			return inter;
		else return null;
	}
	/**
	 * Tells if the ray interacting with this refractive zone 
	 * is coming in or out of the zone
	 * @param source Source end point of the ray segment
	 * @param intersec Intersection point of the ray with this zone
	 * @return True of going out, false if coming in
	 */
	public boolean exiting(Point2D.Double source, Point2D.Double intersec) {	
		
		if (intersec.getX() > source.getX() && intersec.getY() <= source.getY()) {
			if (intersec.getX()+1 > getBounds()[2] || intersec.getY()-1 < getBounds()[1])
				return true;
			else return false;
		}
		else if (intersec.getX() < source.getX() && intersec.getY() <= source.getY()) {
			if (intersec.getX()-1 < getBounds()[0] || intersec.getY()-1 < getBounds()[1])
				return true;
			else return false;
		}
		else if (intersec.getX() > source.getX() && intersec.getY() >= source.getY()) {
			if (intersec.getX()+1 > getBounds()[2] || intersec.getY()+1 > getBounds()[3])
				return true;
			else return false;
		}
		else if (intersec.getX() < source.getX() && intersec.getY() >= source.getY()) {
			if (intersec.getX()-1 < getBounds()[0] || intersec.getY()+1 > getBounds()[3])
				return true;
			else return false;
		}
		else return false;
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

		//change the object's color if the user selects it
		if (isSelected()) 
			g.setColor(Color.YELLOW);
		else 
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

		//draw the rectangle that represents the refractive zone
		g.drawRect((int)getBounds()[0], (int)getBounds()[1], width, height);
		
		String s = String.format("%.2f", refractionIndex);;
		
		g.drawString(s, (int)getBounds()[0]+3, (int)getBounds()[1]+15);
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#getType()
	 */
	@Override
	public String getType() {
		return "RefractiveZone";
	}
}
