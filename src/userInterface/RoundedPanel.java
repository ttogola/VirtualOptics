package userInterface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * 
 * Virtual Optics
 * <p>
 * Extends JPanel, displays a roundedPanel instead of a JPanel
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class RoundedPanel extends JPanel {
	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = -6987004120609095976L;
	/**
	 * set the size of the strokes, or size of the lines
	 */
    protected int strokeSize = 1;
    /**
     * set the color of the shadow created by the panel
     */
    protected Color shadowColor = Color.BLACK;
    /**
     * set the dimension of the corner in arcs
     */
    protected Dimension arcs = new Dimension(30, 30);
    /**
     * 
     */
    protected Color backgroundColor = Color.lightGray;
    /**
     * Constructor, set opacity
     */
    public RoundedPanel() {
        super();
        // set panel opacity
        setOpaque(false);
    }
    
    @Override
    public void setBackground(Color color){
    	this.backgroundColor = color;
    }

    /**
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // width and height of the panel
        int width = getWidth();
        int height = getHeight();
        
        // set the size of the shadow
        int shadowGap = 5;
        // set the color of the panel's shadow
        Color shadowColorApplied = new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 150);
        Graphics2D graphics = (Graphics2D) g;
        // apply anti-aliasing
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // draw and color the shadow
        graphics.setColor(shadowColorApplied);
        graphics.fillRoundRect(4, 4, width - strokeSize - 4,
        height - strokeSize - 4,arcs.width,arcs.height);

        RoundRectangle2D.Float roundRectangle = new RoundRectangle2D.Float(0, 0, (width - shadowGap), (height - shadowGap),arcs.width,arcs.height);
       
        graphics.setColor(backgroundColor);
        graphics.fill(roundRectangle);

        graphics.setColor(getForeground());
        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.draw(roundRectangle);
        graphics.setStroke(new BasicStroke());
    }
}