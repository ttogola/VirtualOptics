package userInterface;

import gameComponents.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * <p>
 * this class extends the JLabel class and displays the available component that the 
 * user can click to add to the Lab panel. The label will be added to the ScrollPanel class
 * Virtual Optics
 * </p>
 * @author Darrin Fong
 * @author Tieme Togola
 */
public class ComponentLabel extends JLabel{
	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = -6166959944976884409L;
	/**
	 * the GameComponent that is represented by this label
	 */
	GameComponent component;
	
	/**
	 * Constructor initializes the component that is represented by this label
	 * @param component
	 */
	ComponentLabel(GameComponent component){
		// set the size of the label
		setPreferredSize(new Dimension(123, 123));
		// add a border to the label
		setBorder(BorderFactory.createLineBorder(Color.black));
		this.component = component;
	}

	/**
	 * paint the GameComponent according to which GameComponent this label represents
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g0){
		super.paintComponent(g0);
		// change Graphics so this label isnt influenced by the component that is created
		Graphics2D g = (Graphics2D) g0.create();
		g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		// add anti-aliasing to the paint job so that it looks better
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(rh);
        
        // paint light source
		if(component instanceof Ray){
			g.setColor(Color.BLACK);
			g.fillOval(55, 55, 10, 10);
		}
		
		// paint prism
		else if(component instanceof Prism){
			g.setColor(Color.BLUE);
			g.drawPolygon(new int[]{10,  60,  110}, new int[]{100, 10, 100}, 3);
		}
		
		// paint lens
		else if(component instanceof Lens){
			g.setColor(Color.BLUE);			
			g.drawArc(-50, 25, 150, 150, 0, 90);
			g.drawArc(25, -50, 150, 150, 180, 90);
		}
		
		// paint refractive zone
		else if(component instanceof RefractiveZone){
			g.setColor(Color.BLUE);
			g.drawRect(15, 15, 90, 90);
		}		
		
		// paint target
		else if(component instanceof Target){
			g.setColor(component.getColor());
			g.fillOval(55, 55, 20, 20);
		}
		
		// paint obstacle
		else if(component instanceof Obstacle){
			g.setColor(component.getColor());
			g.fillRect(50, 20, 25, 90);
		}
		
		// paint curved mirror
		else if(component instanceof CurvedMirror){
			g.setColor(Color.BLACK);
			g.drawArc(5, 8, 100, 100, 270, 180);
			g.setColor(component.getColor());
			g.drawArc(7, 11, 95, 95, 270, 180);
		}
		
		// paint mirror
		else if(component instanceof Mirror){
			g.setColor(component.getColor());
			g.drawLine(25, 25, 105, 105);			
			g.setColor(Color.BLACK);
			g.drawLine(23, 27, 103, 107);			
		}
		
	}
	
	/**
	 * @return returns the component this label represents
	 */
	GameComponent getComponentClicked(){
		return component;
	}
}
