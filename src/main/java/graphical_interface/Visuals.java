package graphical_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

import core.Simulation;
import datastructures.Intersection;
import datastructures.Road;
import datastructures.StreetMap;
import datastructures.TrafficLight;

public class Visuals extends JPanel{	
	private Simulation simulation;
	private StreetMap streetMap;
	private ArrayList<Road> roads;
	private ArrayList<TrafficLight> greenLights;
	
	public boolean isDrawLine() {
		return drawLine;
	}
	private Intersection drawRed;
	
	final int car_size = 10;
	
	private boolean drawLine = false;
	private int mousePosX = 0;
	private int mousePosY = 0;
	private int startPosX = 0;
	private int startPosY = 0;
	
	private double zoomMultiplier = 1.0;
	
	private int changeX = 0;
	private int changeY = 0;
	private final int GRAPH_MOVED_DISTANCE = 25;
	public int getChangeX() {
		return changeX;
	}

	public boolean setChangeX(int i) {
		this.changeX = changeX+(GRAPH_MOVED_DISTANCE*i);
		return true;
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
		this.changeY = changeY+(GRAPH_MOVED_DISTANCE*i);
	}

	public Visuals(Simulation simulation) {
		this.simulation = simulation;
		this.streetMap = this.simulation.getStreetMap();
		roads = streetMap.getRoads();
	}
	
	@Override
	public void paintComponent (Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		// draws red pointed
		g2.setColor(Color.red);
		if(drawRed!=null)
		{
			g2.fillOval((int)((drawRed.getXCoord()-8)*zoomMultiplier + changeX), (int)((drawRed.getYCoord()-8)*zoomMultiplier + changeY), (int)(15*zoomMultiplier), (int)(15*zoomMultiplier ));

		}
		g2.setColor(Color.cyan);
		
		
		// draws guid line		
		if (drawLine) 
		{	
			g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));		
			g2.drawLine((int)(startPosX*zoomMultiplier), (int)(startPosY*zoomMultiplier), (int)(mousePosX*zoomMultiplier), (int)(mousePosY*zoomMultiplier));
			g2.setStroke(new BasicStroke());
		}
		// draws the roads
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
							(int)(roads.get(i).getX1()-2)*zoomMultiplier + changeX, (int)(roads.get(i).getY1()+2)*zoomMultiplier+ changeY, (int)(roads.get(i).getX2()-2)*zoomMultiplier+ changeX, (int)(roads.get(i).getY2()+2)*zoomMultiplier+ changeY));
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()+2)*zoomMultiplier + changeX, (int)(roads.get(i).getY1()-2)*zoomMultiplier+ changeY, (int)(roads.get(i).getX2()+2)*zoomMultiplier+ changeX, (int)(roads.get(i).getY2()-2)*zoomMultiplier+ changeY));
				}
				else
				{
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()+2)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+2)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+2)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+2)*zoomMultiplier+changeY));
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()-2)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-2)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-2)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-2)*zoomMultiplier+changeY));					
				}
			}
			//draws the intersections
			g2.fillOval((int)((roads.get(i).getX1()-5)*zoomMultiplier + changeX), (int)((roads.get(i).getY1()-5)*zoomMultiplier + changeY), (int)(10*zoomMultiplier), (int)(10*zoomMultiplier ));
			g2.fillOval((int)((roads.get(i).getX2()-5)*zoomMultiplier + changeX), (int)((roads.get(i).getY2()-5)*zoomMultiplier + changeY), (int)(10*zoomMultiplier), (int)(10*zoomMultiplier ));
		
		}
		
		// draws the lights
		for (int i = 0; i < streetMap.getTrafficLights().size(); i++) {
			Road road= streetMap.getTrafficLights().get(i).getRoad();
			int midPointX = (int) ((road.getX1()*zoomMultiplier+changeX) +(((road.getX2()*zoomMultiplier+changeX)-(road.getX1()*zoomMultiplier+changeX))/2));
			int midPointY = (int) ((road.getY1()*zoomMultiplier+changeY) +(((road.getY2()*zoomMultiplier+changeY)-(road.getY1()*zoomMultiplier+changeY))/2));
			g2.setColor(Color.RED);
			if(streetMap.getTrafficLights().get(i).getStatus().equals("G"))
			{
				g2.setColor(Color.GREEN);
			}
			
			if(road.getX1() == streetMap.getTrafficLights().get(i).getIntersection().getXCoord() && road.getY1() == streetMap.getTrafficLights().get(i).getIntersection().getYCoord())
			{
				g2.draw(new Line2D.Double(midPointX, midPointY, road.getX1()*zoomMultiplier+changeX, road.getY1()*zoomMultiplier+changeY));
			}
			else
			{
				g2.draw(new Line2D.Double(midPointX, midPointY, road.getX2()*zoomMultiplier+changeX, road.getY2()*zoomMultiplier+changeY));
			}			
			
		}

		// draws the cars
		for(int i = 0; i<simulation.getCars().size(); i ++)
		{
			simulation.getCars().get(i).calculateOffset(simulation.getCars().get(i).getCurrentOriginIntersection(), simulation.getCars().get(i).getCurrentDestinationIntersection());
			if(i%2 == 0) {
				g2.setColor(Color.RED);
				g2.fillOval((int)((simulation.getCars().get(i).getPositionX()-3)*zoomMultiplier + changeX + simulation.getCars().get(i).getOffsetX()), (int)((simulation.getCars().get(i).getPositionY()-3)*zoomMultiplier + changeY + simulation.getCars().get(i).getOffsetY()), (int)(this.car_size*zoomMultiplier), (int)(this.car_size*zoomMultiplier));
			}
			else {
				g2.setColor(Color.BLUE);
				g2.fillOval((int)((simulation.getCars().get(i).getPositionX()-3)*zoomMultiplier + changeX) + simulation.getCars().get(i).getOffsetX(), (int)((simulation.getCars().get(i).getPositionY()-3)*zoomMultiplier + changeY + simulation.getCars().get(i).getOffsetY()), (int)(this.car_size*zoomMultiplier), (int)(this.car_size*zoomMultiplier));

			}
		}		
	}
	
	public void IncreaseZoomMultiplier() {
		zoomMultiplier = zoomMultiplier + 0.2;
	}
	
	public void DecreaseZoomMultiplier() {
		zoomMultiplier = zoomMultiplier - 0.2;
	}
	
	public void resetZoomMultiplier() {
		zoomMultiplier = 1.0;
	}
	
	public double getZoomMultiplier() {
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

	public void setDrawRed(Intersection drawRed) {
		this.drawRed = drawRed;
	}
}
