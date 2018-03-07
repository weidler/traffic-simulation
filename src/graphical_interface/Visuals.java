package graphical_interface;

import java.awt.BasicStroke;
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
	private int mousePosX = 0;
	private int mousePosY = 0;
	private int startPosX = 0;
	private int startPosY = 0;
	
	private double zoomMultiplier = 1.0;
	
	private int changeX = 0;
	private int changeY = 0;
	
	public int getChangeX() {
		return changeX;
	}

	public void setChangeX(int i) {
		this.changeX = changeX+(10*i);
	}

	public int getChangeY() {
		return changeY;
	}
	public void resetPosition()
	{
		changeX = 0;
		changeY = 0;
	}

	public void setChangeY(int i) {
		this.changeY = changeY+(10*i);
	}

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
			g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));		
			g2.drawLine((int)(startPosX*zoomMultiplier), (int)(startPosY*zoomMultiplier), (int)(mousePosX*zoomMultiplier), (int)(mousePosY*zoomMultiplier));
			g2.setStroke(new BasicStroke());
		}
		g2.setColor(Color.black);
		for(int i = 0 ; i< roads.size() ; i++ ) {		
			
			g2.setColor(Color.black);
			if(roads.get(i).getX1() > roads.get(i).getX2()) 
			{
				if(roads.get(i).getY1() < roads.get(i).getY2()) 
				{	
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()-2)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-2)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-2)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-2)*zoomMultiplier+changeY));
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()+2)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+2)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+2)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+2)*zoomMultiplier+changeY));
				}
				else
				{
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()+2)*zoomMultiplier + changeX, (int)(roads.get(i).getY1()-2)*zoomMultiplier+ changeY, (int)(roads.get(i).getX2()+2)*zoomMultiplier+ changeX, (int)(roads.get(i).getY2()-2)*zoomMultiplier+ changeY));
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()-2)*zoomMultiplier + changeX, (int)(roads.get(i).getY1()+2)*zoomMultiplier+ changeY, (int)(roads.get(i).getX2()-2)*zoomMultiplier+ changeX, (int)(roads.get(i).getY2()+2)*zoomMultiplier+ changeY));
				}
			}
			else
			{
				if(roads.get(i).getY1() < roads.get(i).getY2())
				{	
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()+2)*zoomMultiplier + changeX, (int)(roads.get(i).getY1()-2)*zoomMultiplier+ changeY, (int)(roads.get(i).getX2()+2)*zoomMultiplier+ changeX, (int)(roads.get(i).getY2()-2)*zoomMultiplier+ changeY));
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()-2)*zoomMultiplier + changeX, (int)(roads.get(i).getY1()+2)*zoomMultiplier+ changeY, (int)(roads.get(i).getX2()-2)*zoomMultiplier+ changeX, (int)(roads.get(i).getY2()+2)*zoomMultiplier+ changeY));
				}
				else
				{
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()-2)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-2)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-2)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-2)*zoomMultiplier+changeY));
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()+2)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+2)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+2)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+2)*zoomMultiplier+changeY));
				
				}
			}

			g2.fillOval((int)((roads.get(i).getX1()-5)*zoomMultiplier), (int)((roads.get(i).getY1()-5)*zoomMultiplier), (int)(10*zoomMultiplier), (int)(10*zoomMultiplier));
			g2.fillOval((int)((roads.get(i).getX2()-5)*zoomMultiplier), (int)((roads.get(i).getY2()-5)*zoomMultiplier), (int)(10*zoomMultiplier), (int)(10*zoomMultiplier));
		
		}
 
		g2.setColor(Color.MAGENTA);
		for(int i = 0; i<streetMap.getCarsList().size(); i ++)
		{
			g2.fillOval((int)((streetMap.getCarsList().get(i).getPositionX()-3)*zoomMultiplier), (int)((streetMap.getCarsList().get(i).getPositionY()-3)*zoomMultiplier), (int)(7*zoomMultiplier), (int)(7*zoomMultiplier));
		}
		
		
	
		
	}
	public void IncreaseZoomMultiplier()
	{
		zoomMultiplier = zoomMultiplier + 0.2;
	}
	public void DecreaseZoomMultiplier()
	{
		zoomMultiplier = zoomMultiplier - 0.2;
	}
	public void resetZoomMultiplier()
	{
		zoomMultiplier = 1.0;
	}
	public double getZoomMultiplier()
	{
		return zoomMultiplier;
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
