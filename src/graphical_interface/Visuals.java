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
	
	private StreetMap streetMap;
	public boolean isDrawLine() {
		return drawLine;
	}
	
	private ArrayList<Road> roads;
	private boolean drawLine = false;
	private int mousePosX=0;
	private int mousePosY=0;
	private int startPosX=0;
	private int startPosY=0;
	public Visuals(StreetMap streetMap) {
		this.streetMap = streetMap;
		roads = streetMap.getRoads();
	}
	
	@Override
	public void paintComponent (Graphics g)
	{
		
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.cyan);
				
		if (drawLine) 
		{			
			g2.drawLine(startPosX, startPosY, mousePosX, mousePosY);
		}
		g2.setColor(Color.black);
		for(int i = 0 ; i< roads.size() ; i++ ) {		
			
			g2.setColor(Color.black);
			if(roads.get(i).getX1() > roads.get(i).getX2()) 
			{
				if(roads.get(i).getY1() < roads.get(i).getY2()) 
				{	
					g2.draw(new Line2D.Double(
							roads.get(i).getX1()-2, roads.get(i).getY1()-2, roads.get(i).getX2()-2, roads.get(i).getY2()-2));
					g2.draw(new Line2D.Double(
							roads.get(i).getX1()+2, roads.get(i).getY1()+2, roads.get(i).getX2()+2, roads.get(i).getY2()+2));
				}
				else
				{
					g2.draw(new Line2D.Double(
							roads.get(i).getX1()+2, roads.get(i).getY1()-2, roads.get(i).getX2()+2, roads.get(i).getY2()-2));
					g2.draw(new Line2D.Double(
							roads.get(i).getX1()-2, roads.get(i).getY1()+2, roads.get(i).getX2()-2, roads.get(i).getY2()+2));
				}
			}
			else
			{
				if(roads.get(i).getY1() < roads.get(i).getY2())
				{	
					g2.draw(new Line2D.Double(
						roads.get(i).getX1()+2, roads.get(i).getY1()-2, roads.get(i).getX2()+2, roads.get(i).getY2()-2));
					g2.draw(new Line2D.Double(
						roads.get(i).getX1()-2, roads.get(i).getY1()+2, roads.get(i).getX2()-2, roads.get(i).getY2()+2));
				}
				else
				{
					g2.draw(new Line2D.Double(
							roads.get(i).getX1()-2, roads.get(i).getY1()-2, roads.get(i).getX2()-2, roads.get(i).getY2()-2));
					g2.draw(new Line2D.Double(
							roads.get(i).getX1()+2, roads.get(i).getY1()+2, roads.get(i).getX2()+2, roads.get(i).getY2()+2));

				}
			}

			g2.fillOval(roads.get(i).getX1()-5, roads.get(i).getY1()-5, 10, 10);
			g2.fillOval(roads.get(i).getX2()-5, roads.get(i).getY2()-5, 10, 10);
		
		}

		g2.setColor(Color.MAGENTA);
		for(int i = 0; i<streetMap.getCarsList().size(); i ++)
		{
			g2.fillOval(streetMap.getCarsList().get(i).getPositionX()-3, streetMap.getCarsList().get(i).getPositionY()-3, 7, 7);
		}
		
		
		
	}
	public int getStartPosX() {
		return startPosX;
	}

	public void setStartPosX(int startPosX) {
		this.startPosX = startPosX;
	}

	public int getStartPosY() {
		return startPosY;
	}

	public void setStartPosY(int startPosY) {
		this.startPosY = startPosY;
	}

	public void setDrawLine(boolean drawLine) {
		this.drawLine = drawLine;
	}

	public int getMousePosX() {
		return mousePosX;
	}

	public void setMousePosX(int mousePosX) {
		this.mousePosX = mousePosX;
	}

	public int getMousePosY() {
		return mousePosY;
	}

	public void setMousePosY(int mousePosY) {
		this.mousePosY = mousePosY;
	}
}
