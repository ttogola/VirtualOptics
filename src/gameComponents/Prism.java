package gameComponents;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * 
 * Virtual Optics
 * <p>
 * This class models a prism
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class Prism extends Lens {

	private static final long serialVersionUID = -8098103115950079854L;
	/**
	 * We use a polygon type, but the shape will always be triangular
	 */
	private Polygon shape = new Polygon();	
	/**
	 * The length of the sides of the equilateral triangle 
	 * representing this prism object
	 */
	private double side = 100;
	/**
	 * The first vertex of the triangular shape
	 */
	private Point2D.Double vertex1;
	/**
	 * The second vertex of the triangular shape
	 */
	private Point2D.Double vertex2;
	/**
	 * The third vertex of the triangular shape
	 */
	private Point2D.Double vertex3;
	/**
	 * The line segment between vertices 1 and 2
	 */
	private LineEq line1;
	/**
	 * The line segment between vertices 2 and 3
	 */
	private LineEq line2;	
	/**
	 * The line segment between vertices 3 and 1
	 */
	private LineEq line3;
	/**
	 * The angle of rotation of this prism
	 */
	private double angle = 90;
		
	
	public Prism() {
		setColor(Color.CYAN);
	}
	
	public Prism(Point2D.Double position) {
		super(position);
		setColor(Color.CYAN);
		initVertices();
		initPolygon();
		initBox();
	}
	/**
	 * Computes the initial values for the vertices
	 */
	private void initVertices() {
		super.setBounds(0, 0, 0, 0);
		double dis = (Math.sqrt(3)/2)*2/3*side;
		
		vertex1 = new Point2D.Double(getPosition().getX(), getPosition().getY()-dis);
		vertex2 = new Point2D.Double(getPosition().getX()+0.5*side, getPosition().getY()+0.5*dis);
		vertex3 = new Point2D.Double(getPosition().getX()-0.5*side, getPosition().getY()+0.5*dis);
		
		shape.addPoint((int)vertex1.getX(), (int)vertex1.getY());
		shape.addPoint((int)vertex2.getX(), (int)vertex2.getY());
		shape.addPoint((int)vertex3.getX(), (int)vertex3.getY());
		
		line1 = new LineEq(vertex1, vertex2);
		line2 = new LineEq(vertex2, vertex3);
		line3 = new LineEq(vertex3, vertex1);
	}
	/**
	 * Initializes the shape with the vertices
	 */
	private void initPolygon() {
		
		shape = new Polygon();
		
		shape.addPoint((int)vertex1.getX(), (int)vertex1.getY());
		shape.addPoint((int)vertex2.getX(), (int)vertex2.getY());
		shape.addPoint((int)vertex3.getX(), (int)vertex3.getY());
	}
	/**
	 * 
	 * @see gameComponents.Lens#setPosition(java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setPosition(Point2D.Double p) {
		
		double xDiff = (p.getX() - super.getPosition().getX());
		double yDiff = (p.getY() - super.getPosition().getY());
		setVertices(xDiff, yDiff, xDiff, yDiff, xDiff, yDiff);
		
		super.setPosition(p);
		initPolygon();
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.Lens#initBox()
	 */
	@Override
	public void initBox() {	
		if (shape != null) {
			Rectangle rec = shape.getBounds2D().getBounds();
			setBox(rec);
		}
	}
	/**
	 * 
	 * @see gameComponents.Lens#resize(int)
	 */
	@Override
	public void resize(int m) {
		
		double dx1 = vertex1.getX()-getPosition().getX();
		double dy1 = vertex1.getY()-getPosition().getY();
		double dx2 = vertex2.getX()-getPosition().getX();
		double dy2 = vertex2.getY()-getPosition().getY();	
		double dx3 = vertex3.getX()-getPosition().getX();
		double dy3 = vertex3.getY()-getPosition().getY();
		
		if (m > 0 && side < 200) 
			setVertices(dx1/4, dy1/4, dx2/4, dy2/4, dx3/4, dy3/4);
		else if (m < 0 && side > 15) 
			setVertices(-dx1/4, -dy1/4, -dx2/4, -dy2/4, -dx3/4, -dy3/4);

		side = vertex1.distance(vertex2);
		initPolygon();
		initBox();
	}
	/**
	 * 
	 * @see gameComponents.Lens#rotate(int)
	 */
	@Override
	public void rotate(int a) {
		angle += a;

		if (angle >= 360)
			angle -= 360;
		if (angle < 0)
			angle += 360;
		
		double angle2 = angle+120;
		if (angle2 >= 360)
			angle2 -= 360;

		double angle3 = angle+240;
		if (angle3 >= 360)
			angle3 -= 360;

		double dis = (Math.sqrt(3)/2)*2/3*side;

	        double x1 = getPosition().getX() + dis*Math.cos(Math.toRadians(angle)); 
	        double y1 = getPosition().getY() - dis*Math.sin(Math.toRadians(angle)); 
	        double x2 = getPosition().getX() + dis*Math.cos(Math.toRadians(angle3)); 
	        double y2 = getPosition().getY() - dis*Math.sin(Math.toRadians(angle3)); 
	        double x3 = getPosition().getX() + dis*Math.cos(Math.toRadians(angle2)); 
	        double y3 = getPosition().getY() - dis*Math.sin(Math.toRadians(angle2)); 
	          
	        vertex1 = new Point2D.Double(x1, y1);
	        vertex2 = new Point2D.Double(x2, y2);
	        vertex3 = new Point2D.Double(x3, y3);
	        initPolygon();
	        initBox();
	        side = vertex1.distance(vertex2);
	        
	        line1 = new LineEq(vertex1, vertex2);
			line2 = new LineEq(vertex2, vertex3);
			line3 = new LineEq(vertex3, vertex1);
	}
	
	
	public Polygon getShape() {
		return shape;
	}
	
	public void setVertices(double dx1, double dy1, double dx2, double dy2, double dx3, double dy3) {
		vertex1.setLocation(vertex1.getX()+dx1, vertex1.getY()+dy1);
		vertex2.setLocation(vertex2.getX()+dx2, vertex2.getY()+dy2);
		vertex3.setLocation(vertex3.getX()+dx3, vertex3.getY()+dy3);
		
		//update the lines between the vertices
		line1 = new LineEq(vertex1, vertex2);	
		line2 = new LineEq(vertex2, vertex3);
		line3 = new LineEq(vertex3, vertex1);
	}
	
	public Point2D.Double getVertex1() {
		return vertex1;
	}

	public Point2D.Double getVertex2() {
		return vertex2;
	}

	public Point2D.Double getVertex3() {
		return vertex3;
	}
	/**
	 * 
	 * @see gameComponents.Lens#setNormalLine(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setNormalLine(Point2D.Double intersection, Point2D.Double p1) {

		//picks the line to which the intersection point is the closest
		
		if (intersection.distance(vertex1) < intersection.distance(vertex3)
				&& intersection.distance(vertex2) < intersection.distance(vertex3))
			
			setNormalLine(new LineEq(-1/line1.getSlope(), intersection.getX(), intersection.getY()));
		
		else if (intersection.distance(vertex2) < intersection.distance(vertex1)
				&& intersection.distance(vertex3) < intersection.distance(vertex1))
			
			setNormalLine(new LineEq(-1/line2.getSlope(), intersection.getX(), intersection.getY()));
		
		else if (intersection.distance(vertex3) < intersection.distance(vertex2)
				&& intersection.distance(vertex1) < intersection.distance(vertex2))
			
			setNormalLine(new LineEq(-1/line3.getSlope(), intersection.getX(), intersection.getY()));
	}
	/**
	 * 
	 * @see gameComponents.Lens#intersection(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public Point2D.Double intersection(Point2D.Double p1, Point2D.Double p2, LineEq segment) {

		Point2D.Double inter;	//intersection point	
		double interx;	//x coordinate of the intersection point
		
		//potential intersection points
		Point2D.Double inter1;
		Point2D.Double inter2;
		Point2D.Double inter3;
		
		if (infiniteSlope(segment.getSlope())) {
			
			interx = p1.getX();
			inter1 = new Point2D.Double(interx, line1.getSlope()*interx+line1.getIntercept());
			inter2 = new Point2D.Double(interx, line2.getSlope()*interx+line2.getIntercept());
			inter3 = new Point2D.Double(interx, line3.getSlope()*interx+line3.getIntercept());
			
		}
		else {
			
			double tempx;
			
			//compute the potential intersection points of the ray segment with...
			
			//line1...
			if (infiniteSlope(line1.getSlope()))
				inter1 = new Point2D.Double(vertex1.getX(), segment.getSlope()*vertex1.getX() + segment.getIntercept());
			else {
				
				tempx = (segment.getIntercept()-line1.getIntercept())/(line1.getSlope()-segment.getSlope());
				inter1 = new Point2D.Double(tempx, segment.getSlope()*tempx + segment.getIntercept());
			}
			
			//line2...
			if (infiniteSlope(line2.getSlope()))
				inter2 = new Point2D.Double(vertex2.getX(), segment.getSlope()*vertex2.getX() + segment.getIntercept());
			else {
				
				tempx = (segment.getIntercept()-line2.getIntercept())/(line2.getSlope()-segment.getSlope());
				inter2 = new Point2D.Double(tempx, segment.getSlope()*tempx + segment.getIntercept());
			}
			
			//and line3
			if (infiniteSlope(line3.getSlope()))
				inter3 = new Point2D.Double(vertex3.getX(), segment.getSlope()*vertex3.getX() + segment.getIntercept());
			else {
				
				tempx = (segment.getIntercept()-line3.getIntercept())/(line3.getSlope()-segment.getSlope());
				inter3 = new Point2D.Double(tempx, segment.getSlope()*tempx + segment.getIntercept());
			}
		}
		
		/*for each potential intersection point, check if it is valid, i.e. if it is in the same quadrant as 
		 * the incident ray segment, if it is not the same as the source point p1, and if it is within the bounds
		 * of the side of the shape it intersects (+-1 to account for imprecisions)
		 * If the tested point is invalid, set it to (BIG, BIG) and the rest of the method will ignore it 
		 */
		//check inter1 with line1 from vertex1 to vertex2
		if (!sameQuadrant(p1, p2, inter1) || approx(p1, inter1) 
				|| !(inter1.getX() < Math.max(vertex1.getX(), vertex2.getX())+1 && inter1.getX() > Math.min(vertex1.getX(), vertex2.getX())-1 && 
				inter1.getY() <= Math.max(vertex1.getY(), vertex2.getY())+1 && inter1.getY() >= Math.min(vertex1.getY(), vertex2.getY())-1))
			
			inter1 = new Point2D.Double(BIG, BIG);
		
		//check inter2 with line2 from vertex2 to vertex3
		if (!sameQuadrant(p1, p2, inter2) || approx(p1, inter2) 
				|| !(inter2.getX() <= Math.max(vertex2.getX(), vertex3.getX())+1 && inter2.getX() >= Math.min(vertex2.getX(), vertex3.getX())-1 && 
				inter2.getY() <= Math.max(vertex2.getY(), vertex3.getY())+1 && inter2.getY() >= Math.min(vertex2.getY(), vertex3.getY())-1))
			
			inter2 = new Point2D.Double(BIG, BIG);
		
		//check inter3 with line3 from vertex3 to vertex1
		if (!sameQuadrant(p1, p2, inter3) || approx(p1, inter3) 
				|| !(inter3.getX() <= Math.max(vertex3.getX(), vertex1.getX())+1 && inter3.getX() >= Math.min(vertex3.getX(), vertex1.getX())-1 && 
				inter3.getY() <= Math.max(vertex3.getY(), vertex1.getY())+1 && inter3.getY() >= Math.min(vertex3.getY(), vertex1.getY())-1))
			
			inter3 = new Point2D.Double(BIG, BIG); 
		
		//keep the intersection point that is closest to the source point p1
		Point2D.Double closest;
		
		if (p1.distance(inter1) < p1.distance(inter2))
			closest = inter1;
		else closest = inter2;
		
		if (p1.distance(inter3) < p1.distance(closest))
			closest = inter3;
		
		inter = closest;
		
		if (inter.equals(new Point2D.Double(BIG, BIG))) {
			leaving = false;
			return null;
		}
		else return inter;

	}
	/**
	 * 
	 * @see gameComponents.Lens#draw(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	public void draw(Graphics2D g0, Rectangle viewRec) {
		//if the prism is not visible to the user, do not draw it
		if (!getBox().intersects(viewRec))
			return;
		
		Graphics2D g = (Graphics2D)g0.create();
		
		//if the user selected the prism, change its color
		if (isSelected()) 
			g.setColor(Color.YELLOW);
		else
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));	//set the color a bit transparent 
		
		//draw the triangular shape representing the prism object
		g.drawPolygon(shape);
	}
	/**
	 * 
	 * @see gameComponents.Lens#getType()
	 */
	@Override
	public String getType() {
		return "Prism";
	}
}
