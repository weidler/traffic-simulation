package graphical_interface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import datastructures.Intersection;
import datastructures.StreetMap;

public class Visuals extends JPanel{	
	
	@Override
	public void paintComponent (Graphics g)
	{
		System.out.println("draw");
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.fillOval(400, 200, 50, 50);
		g2.drawLine(400, 400, 300, 300);
		
		/*for(int i = 0 ; i+1 < intersections.size() ; i++ ) {
			g2.draw(new Line2D.Double(
					intersections.get(i).getX_coord(), intersections.get(i).getY_coord(), intersections.get(i+1).getX_coord(), intersections.get(i+1).getY_coord()));
		
		}
		*/
		
		
	}
}
