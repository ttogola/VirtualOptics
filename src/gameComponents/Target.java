package gameComponents;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * 
 * Virtual Optics
 * <p>
 * This class models a target. It is a component that the user 
 * can try to hit with a ray of light to win the game. 
 * The incident ray must have the same color as the Target object
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class Target extends GameComponent {

	private static final long serialVersionUID = -1224218871538990900L;
	/**
	 * The radius of the circle representing the target component
	 */
	private double radius = 10;
	/**
	 * True if target has been hit by a valid ray
	 */
	private boolean hit;
	/**
	 * X coordinate of the center of the target,
	 * called h by convention
	 */
	private double h;
	/**
	 * Y coordinate of the center of the target,
	 * called k by convention
	 */
	private double k;
	
	
	public Target() {
		setColor(Color.RED);
	}
	
	public Target(Point2D.Double position) {
		super(position);
		setColor(Color.RED);
		h = position.getX();
		k = position.getY();
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
	public void setPosition(Point2D.Double p) {
		super.setPosition(p);
		h = p.getX();
		k = p.getY();
		initBox();
	}
	
	
	public boolean isHit() {
		return hit;
	}
	
	public void setHit(boolean hit) {
		this.hit = hit;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public double getH() {
		return h;
	}
	public double getK() {
		return k;
	}
	/**
	 * Notifies the user that this target was successfully hit by a ray of light
	 * @param incidentColor The color of the ray that hit this target
	 */
	public void react(Color incidentColor) {
		if (incidentColor.equals(getColor()))
			hit = true;		
	}
	
	/**
	 * 
	 * @see gameComponents.GameComponent#resize(int)
	 */
	@Override
	public void resize(int m) {
	
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#rotate(int)
	 */
	@Override
	public void rotate(int a) {
		
	}
	/**
	 * 
	 * @see gameComponents.GameComponent#intersection(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public Point2D.Double intersection(Point2D.Double p1, Point2D.Double p2, LineEq segment) {
	
		Point2D.Double inter;		
		double interx, intery;
		
		if (infiniteSlope(segment.getSlope())) {
			
			interx = p1.getX();
			
			double[] circleSols = circSolsX(p1.getX(), radius, h, k);
			double closeY = circleSols[0];
			double farY = circleSols[1];
			
			//take the solution that is closest to the source point
			if (distance(p1.getX(), p1.getY(), p1.getX(), farY) < distance(p1.getX(), p1.getY(), p1.getX(), closeY)) {
				double temp = closeY;
				closeY = farY;
				farY = temp;
			}
			
			intery = closeY;
			
		}
		else {	
			
			//compute the possible intersections of the ray segment with this target
			ArrayList<Point2D.Double> solutions = circSolsGeneral(segment, radius, h, k);

			if (solutions == null) {
				hit = false;
				return null;
			}
				
			Point2D.Double closeSol = solutions.get(0);
			Point2D.Double farSol = solutions.get(1);
			double closeDis = distance(closeSol.getX(), closeSol.getY(), p1.getX(), p1.getY());
			double farDis = distance(farSol.getX(), farSol.getY(), p1.getX(), p1.getY());
			
			//take the solution that is closest to the source point
			if (closeDis > farDis) {
				Point2D.Double temp = (Point2D.Double)closeSol.clone();	
				closeSol = (Point2D.Double)farSol.clone();
				farSol = temp;		
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
	public void draw(Graphics2D g0, Rectangle viewRec) {
		//if the component is not visible to the user, do not draw
		if (!getBox().intersects(viewRec))
			return;
		
		Graphics2D g = (Graphics2D)g0.create();
		g.fillOval((int)(h-radius), (int)(k-radius), (int)radius*2, (int)radius*2);
		
		//if the user selects the target
		if (isSelected() || isHit()) {
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
		return "Target";
	}
}
