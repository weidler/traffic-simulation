package graphical_interface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import datastructures.Intersection;
import datastructures.Road;
import datastructures.StreetMap;

public class Visuals extends JPanel{	
	
	StreetMap streetMap;
	ArrayList<Road> roads;
	public Visuals(StreetMap streetMap) {
		this.streetMap = streetMap;
		roads = streetMap.getRoads();
	}
	
	@Override
	public void paintComponent (Graphics g)
	{
		
		System.out.println("draw");
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		
		for(int i = 0 ; i< roads.size() ; i++ ) {
			g2.draw(new Line2D.Double(
					roads.get(i).getX1(), roads.get(i).getY1(), roads.get(i).getX2(), roads.get(i).getY2()));
			g2.fillOval(roads.get(i).getX1()-5, roads.get(i).getY1()-5, 10, 10);
			g2.fillOval(roads.get(i).getX2()-5, roads.get(i).getY2()-5, 10, 10);
		
		}
		
		
		
	}
}
