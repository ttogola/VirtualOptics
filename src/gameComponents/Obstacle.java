package gameComponents;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * 
 * Virtual Optics
 * <p>
 * This class models an obstacle (a wall)
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class Obstacle extends GameComponent {
	
	private static final long serialVersionUID = -7997547483202487011L;
	
	/**
	 * The width of this obstale
	 */
	private int width = 25;
	/**
	 * The height of this obstacle
	 */
	private int height = 100;
	
	/**
	 * Contains the points where rays hit this obstacle
	 */
	private ArrayList<Point2D.Double> collisions = new ArrayList<>(); 
	/**
	 * Contains all the ray objects that hit this obstacle
	 */
	private ArrayList<Ray> rays = new ArrayList<>(); 
	
	
	public Obstacle() {
		setColor(Color.BLACK);
	}
	
	public Obstacle(Point2D.Double position) {
		super(position);
		setColor(Color.BLACK);
		initBounds();
		initBox();
	}
	
	/**
	 *Initialize the bounding coordinates of this obstacle 
	 */
	private void initBounds() {
		super.setBounds(getPosition().getX(), getPosition().getY(), getPosition().getX()+width, getPosition().getY()+height);
	}
	/**
	 * @see gameComponents.GameComponent#initBox()
	 */
	@Override
	public void initBox() {
		int w = (int)Math.abs(getBounds()[2]-getBounds()[0]);
		if (w == 0)	w = 1;
		int h = (int)Math.abs(getBounds()[3]-getBounds()[1]);
		if (h == 0)	h = 1;
		
		Rectangle rec = new Rectangle((int)Math.min(getBounds()[0], (int)getBounds()[2]), (int)Math.min(getBounds()[1], (int)getBounds()[3]), w, h);	
		setBox(rec);
	}
	/**
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
	
	
	public double getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * Adds a new collision point to the collisions list
	 * @param intersec The new point of collision
	 * @param ray The ray that collided with this obstacle
	 */
	public void collision(Point2D.Double intersec, Ray ray) {

		if (rays.size() == 0) {
			rays.add(ray);
			collisions.add(intersec);
			return;
		}

		boolean newCol = true;
		for (int i = 0; i < rays.size(); i++) {
			if (rays.get(i) == ray) {	//checking if same reference
				collisions.set(i, intersec);
				newCol = false;
			}
		}
		
		if (newCol) {
			collisions.add(intersec);
			rays.add(ray);
		}
	}
	
	private void resetCollisions() {
		collisions.clear();
		rays.clear();
	}
	
	//rotate and resize functions are not available for this component
	
	@Override
	public void rotate(int a) {
		
	}
	@Override
	public void resize(int m) {
		
	}
	
	/**
	 * Adjusts the width and height of this obstacle
	 * @param x X coordinate of the new lower right corner of the wall
	 * @param y Y coordinate of the new lower right corner of the wall
	 */
	public void scale(double x, double y) {
		if (isRotateable()) {
			if (width + x - getBounds()[2] > 25){  
				width += x - getBounds()[2];
				super.setBounds(getBounds()[0], getBounds()[1], x, getBounds()[3]);
			}
		
			if (height + y - getBounds()[3] > 25) {
				height += y - getBounds()[3];
				super.setBounds(getBounds()[0], getBounds()[1], getBounds()[2], y);
			}
		}
		initBox();
	}
	
	/**
	 * @see gameComponents.GameComponent#intersection(java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double, gameComponents.LineEq)
	 */
	@Override
	public Point2D.Double intersection(Point2D.Double p1, Point2D.Double p2, LineEq segment) {
						
		Point2D.Double inter;
		double[] b = getBounds();

		double interx;
		double intery;

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
			
			//all the possible intersection points
			Point2D.Double top = new Point2D.Double(((b[1]-segment.getIntercept())/segment.getSlope()), b[1]);
			Point2D.Double bottom = new Point2D.Double(((b[3]-segment.getIntercept())/segment.getSlope()), b[3]);
			Point2D.Double left = new Point2D.Double(b[0], (segment.getSlope()*b[0]+segment.getIntercept()));
			Point2D.Double right = new Point2D.Double(b[2], (segment.getSlope()*b[2]+segment.getIntercept()));
			ArrayList<Point2D.Double> points = new ArrayList<>();
			points.add(top);
			points.add(bottom);
			points.add(left);
			points.add(right);
			
			//ignore the points that are not valid
			for (int i = 0; i < points.size(); i++) {
				if (!(points.get(i).getX() <= b[2] && points.get(i).getX() >= b[0] && points.get(i).getY() <= b[3] && points.get(i).getY() >= b[1]) 
						|| !sameQuadrant(p1, p2, points.get(i))) {
					points.remove(i);
					i--;
				}
			}
			
			//keep the point that is closest to source point
			if (points.size() > 0) {
				
				interx = points.get(0).getX();
				intery = points.get(0).getY();
				if (distance(p1.getX(), p1.getY(), interx, intery) < 2 && points.size() > 1) {
					interx = points.get(1).getX();
					intery = points.get(1).getY();
				}
				
				for (int i = 1; i < points.size(); i++) {
					double d = distance(p1.getX(), p1.getY(), points.get(i).getX(), points.get(i).getY());	
					if (distance(p1.getX(), p1.getY(), interx, intery) > d && !(d < 2)) {
						interx = points.get(i).getX();
						intery = points.get(i).getY();
					}
				}
				
			}
			else 
				return null;			
		}
		
		inter = new Point2D.Double(interx, intery);
		
		if (isWithinBounds(inter, b)) {
			return inter;
		}
		else return null;
	}
	
	/**
	 * @see gameComponents.GameComponent#draw(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	public void draw(Graphics2D g0, Rectangle viewRec) {
		//if the component is not visible to the user, do not draw it
		if (!getBox().intersects(viewRec))
			return;

		Graphics2D g = (Graphics2D)g0.create();

		//this buffered image is used to draw color gradients
	    BufferedImage buffImg = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);	
	    
	    Graphics2D gbi = buffImg.createGraphics();
	    gbi.setColor(getColor());
	    gbi.fillRect(0, 0, width, height);
	    gbi.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN));

	    float radius = 30;
	    float[] dist = {0.0f, 1.0f};
	    int[] rgbArray = buffImg.getRGB(0, 0, width, height, null, 0, width);	//store the pixels of the image

	    for (int i = 0; i < collisions.size(); i++) {
	    	
		    Color[] colors = {rays.get(i).getColor(), getColor()};
		    BufferedImage tempBuffImg = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);	//temporary layer to count pixels
		    Graphics2D tempGbi = tempBuffImg.createGraphics();

	    	RadialGradientPaint rgp = new RadialGradientPaint((int)(collisions.get(i).getX()-getPosition().getX()), 
	    			(int)(collisions.get(i).getY()-getPosition().getY()), radius, dist, colors);
	    	tempGbi.setPaint(rgp);
	    	
	    	//draw a circle with the colour gradient to simulate light fading 
	    	//the gradient is placed at the position where the light collides the obstacle
	    	tempGbi.fillOval((int)(collisions.get(i).getX()-getPosition().getX()-radius), 
	    			(int)(collisions.get(i).getY()-getPosition().getY()-radius), (int)(2*radius), (int)(2*radius));

        	int[] tempRgbArray = tempBuffImg.getRGB(0, 0, width, height, null, 0, width);
        	
        	//go through the array of pixels and add the values of the layers
        	//this simulates the intensity of light increasing when more than on ray hits the same spot
	    	for (int j = 0; j < rgbArray.length; j++) {
	    		
		    	Color c1 = new Color(tempRgbArray[j]);
		    	Color c2 = new Color(rgbArray[j]);
		    	
		    	int red = c1.getRed()+c2.getRed();
		    	int green = c1.getGreen()+c2.getGreen();
		    	int blue = c1.getBlue()+c2.getBlue();
		    	int alpha = c1.getAlpha()+c2.getAlpha();
		    	
		    	if (red > 255)
		    		red = 255;
		    	if (green > 255)
		    		green = 255;
		    	if (blue > 255)
		    		blue = 255;
		    	if (alpha > 255)
		    		alpha = 255;
		    	
		    	Color c3 = new Color(red, green, blue, alpha);
		    	rgbArray[j] = c3.getRGB();
		    	
	    	}	
	    	
	    }
	    
    	buffImg.setRGB(0, 0, width, height, rgbArray, 0, width);
	    g.drawImage(buffImg, (int)(getPosition().getX()), (int)(getPosition().getY()), null);
	    resetCollisions();
	    
	    //when the user selects the component, it is surrounded by a yellow frame
	    if (isSelected()) {
			g.setColor(Color.YELLOW);
			g.drawRect((int)(getPosition().getX()), (int)(getPosition().getY()), width, height);
		}
	}
	
	/**
	 * @see gameComponents.GameComponent#getType()
	 */
	@Override
	public String getType() {
		return "Obstacle";
	}
}
