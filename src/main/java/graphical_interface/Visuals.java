package graphical_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import core.Simulation;
import datastructures.Intersection;
import datastructures.Road;
import datastructures.RoadTypes;
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

	final int car_size = 8;

	private int intersectionSize = 60;
	private int laneSize = 7;
	private boolean drawLine = false;
	private int mousePosX = 0;
	private int mousePosY = 0;
	private int startPosX = 0;
	private int startPosY = 0;
	private Stroke defaultStroke;
	private Stroke dashed = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND, 0, new float[]{9}, 0);


	private double zoomMultiplier = 1.0;

	private int changeX = 0;
	private int changeY = 0;
	private final int GRAPH_MOVED_DISTANCE = 25;

	public void setIntersectionSize(int is)
	{
		intersectionSize = is;
	}

	public int getIntersectionSize()
	{
		return intersectionSize;
	}
	public void setLaneSize(int l)
	{
		laneSize = l;
	}

	public int getLaneSize()
	{
		return laneSize;
	}

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
		defaultStroke = g2.getStroke();
		// draws red point
		g2.setColor(Color.red);
		if(drawRed!=null)
		{
			String text = "X: "+drawRed.getXCoord()+" Y: "+ drawRed.getYCoord()+"\n"+"test";
			g2.fillOval((int)((drawRed.getXCoord()-(intersectionSize/2)-3)*zoomMultiplier + changeX), (int)((drawRed.getYCoord()-(intersectionSize/2)-3)*zoomMultiplier + changeY), (int)((intersectionSize+5)*zoomMultiplier), (int)((intersectionSize+5)*zoomMultiplier ));
			drawToolTip(g2, text, drawRed.getXCoord(), drawRed.getYCoord());
		}
		g2.setColor(Color.cyan);


		// draws guideline		
		if (drawLine) 
		{	
			g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));		
			g2.drawLine((int)(startPosX*zoomMultiplier)+changeX, (int)(startPosY*zoomMultiplier)+changeY, (int)(mousePosX*zoomMultiplier), (int)(mousePosY*zoomMultiplier));
			g2.setStroke(new BasicStroke());
		}
		
		// draws the lights
		for (int i = 0; i < streetMap.getTrafficLights().size(); i++) {
			Road road= streetMap.getTrafficLights().get(i).getRoad();
			double offset = 3;
			
			g2.setColor(Color.RED);

			if(streetMap.getTrafficLights().get(i).getStatus().equals("G")){
				g2.setColor(Color.GREEN);
			}

			if(road.getX1() > road.getX2()){
				if(road.getY1() < road.getY2()){

					for(int j = 1; j <= road.getLanes();j++){
						g2.draw(new Line2D.Double(
								(int)(road.getX1()-j*laneSize+offset)*zoomMultiplier+changeX, (int)(road.getY1()-j*laneSize+offset)*zoomMultiplier+changeY, (int)(road.getX2()-j*laneSize+offset)*zoomMultiplier+changeX, (int)(road.getY2()-j*laneSize+offset)*zoomMultiplier+changeY));
						g2.draw(new Line2D.Double(
								(int)(road.getX1()+j*laneSize-offset)*zoomMultiplier+changeX, (int)(road.getY1()+j*laneSize-offset)*zoomMultiplier+changeY, (int)(road.getX2()+j*laneSize-offset)*zoomMultiplier+changeX, (int)(road.getY2()+j*laneSize-offset)*zoomMultiplier+changeY));
					}

				}
				else{

					for(int j = 1; j <= road.getLanes();j++){
						g2.draw(new Line2D.Double(
								(int)(road.getX1()+j*laneSize-offset)*zoomMultiplier+changeX, (int)(road.getY1()-j*laneSize-offset)*zoomMultiplier+changeY, (int)(road.getX2()+j*laneSize-offset)*zoomMultiplier+changeX, (int)(road.getY2()-j*laneSize-offset)*zoomMultiplier+changeY));
						g2.draw(new Line2D.Double(
								(int)(road.getX1()-j*laneSize+offset)*zoomMultiplier+changeX, (int)(road.getY1()+j*laneSize+offset)*zoomMultiplier+changeY, (int)(road.getX2()-j*laneSize+offset)*zoomMultiplier+changeX, (int)(road.getY2()+j*laneSize+offset)*zoomMultiplier+changeY));
					}
				}
			}
			else{

				if(road.getY1() < road.getY2()){
					
					for(int j = 1; j <= road.getLanes();j++){
						g2.draw(new Line2D.Double(
								(int)(road.getX1()-j*laneSize+offset)*zoomMultiplier+changeX, (int)(road.getY1()+j*laneSize-offset)*zoomMultiplier+changeY, (int)(road.getX2()-j*laneSize+offset)*zoomMultiplier+changeX, (int)(road.getY2()+j*laneSize-offset)*zoomMultiplier+changeY));
						g2.draw(new Line2D.Double(
								(int)(road.getX1()+j*laneSize-offset)*zoomMultiplier+changeX, (int)(road.getY1()-j*laneSize+offset)*zoomMultiplier+changeY, (int)(road.getX2()+j*laneSize-offset)*zoomMultiplier+changeX, (int)(road.getY2()-j*laneSize+offset)*zoomMultiplier+changeY));
					}
				}

				else{		

					for(int j = 1; j <= road.getLanes();j++){
						g2.draw(new Line2D.Double(
								(int)(road.getX1()+j*laneSize-offset)*zoomMultiplier+changeX, (int)(road.getY1()+j*laneSize-offset)*zoomMultiplier+changeY, (int)(road.getX2()+j*laneSize-offset)*zoomMultiplier+changeX, (int)(road.getY2()+j*laneSize-offset)*zoomMultiplier+changeY));
						g2.draw(new Line2D.Double(
								(int)(road.getX1()-j*laneSize+offset)*zoomMultiplier+changeX, (int)(road.getY1()-j*laneSize+offset)*zoomMultiplier+changeY, (int)(road.getX2()-j*laneSize+offset)*zoomMultiplier+changeX, (int)(road.getY2()-j*laneSize+offset)*zoomMultiplier+changeY));
					}
				}
			}
		}		
		
		// draws the roads
		g2.setColor(Color.black);
		for(int i = 0 ; i< roads.size() ; i++ ) {		

			if(roads.get(i).getType() == RoadTypes.ROAD)
			{
				g2.setColor(Color.black);
			}
			else if(roads.get(i).getType() == RoadTypes.DIRT_ROAD)
			{
				g2.setColor(Color.ORANGE);
			}
			else if(roads.get(i).getType() == RoadTypes.HIGHWAY)
			{
				g2.setColor(Color.BLUE);
			}

			if(roads.get(i).getX1() > roads.get(i).getX2()) 
			{
				if(roads.get(i).getY1() < roads.get(i).getY2()) 
				{	
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1())*zoomMultiplier+changeX, (int)(roads.get(i).getY1())*zoomMultiplier+changeY, (int)(roads.get(i).getX2())*zoomMultiplier+changeX, (int)(roads.get(i).getY2())*zoomMultiplier+changeY));

					int k = roads.get(i).getLanes();
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()-k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-k*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-k*laneSize)*zoomMultiplier+changeY));
					g2.setStroke(dashed);
					for(int j = 1; j < roads.get(i).getLanes();j++)
					{

						g2.draw(new Line2D.Double(
								(int)(roads.get(i).getX1()-j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-j*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-j*laneSize)*zoomMultiplier+changeY));
						g2.draw(new Line2D.Double(
								(int)(roads.get(i).getX1()+j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+j*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+j*laneSize)*zoomMultiplier+changeY));


					}
					g2.setStroke(defaultStroke);
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()+k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+k*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+k*laneSize)*zoomMultiplier+changeY));

				}
				else
				{
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1())*zoomMultiplier+changeX, (int)(roads.get(i).getY1())*zoomMultiplier+changeY, (int)(roads.get(i).getX2())*zoomMultiplier+changeX, (int)(roads.get(i).getY2())*zoomMultiplier+changeY));

					int k = roads.get(i).getLanes();
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()+k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-k*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-k*laneSize)*zoomMultiplier+changeY));
					g2.setStroke(dashed);
					for(int j = 1; j < roads.get(i).getLanes();j++)
					{

						g2.draw(new Line2D.Double(
								(int)(roads.get(i).getX1()+j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-j*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-j*laneSize)*zoomMultiplier+changeY));
						g2.draw(new Line2D.Double(
								(int)(roads.get(i).getX1()-j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+j*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+j*laneSize)*zoomMultiplier+changeY));


					}
					g2.setStroke(defaultStroke);
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()-k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+k*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+k*laneSize)*zoomMultiplier+changeY));

				}
			}
			else
			{
				if(roads.get(i).getY1() < roads.get(i).getY2())
				{	
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1())*zoomMultiplier+changeX, (int)(roads.get(i).getY1())*zoomMultiplier+changeY, (int)(roads.get(i).getX2())*zoomMultiplier+changeX, (int)(roads.get(i).getY2())*zoomMultiplier+changeY));

					int k = roads.get(i).getLanes();
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()+k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-k*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-k*laneSize)*zoomMultiplier+changeY));
					g2.setStroke(dashed);
					for(int j = 1; j < roads.get(i).getLanes();j++)
					{

						g2.draw(new Line2D.Double(
								(int)(roads.get(i).getX1()-j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+j*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+j*laneSize)*zoomMultiplier+changeY));
						g2.draw(new Line2D.Double(
								(int)(roads.get(i).getX1()+j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-j*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-j*laneSize)*zoomMultiplier+changeY));


					}
					g2.setStroke(defaultStroke);
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()-k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+k*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+k*laneSize)*zoomMultiplier+changeY));

				}
				else
				{					
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1())*zoomMultiplier+changeX, (int)(roads.get(i).getY1())*zoomMultiplier+changeY, (int)(roads.get(i).getX2())*zoomMultiplier+changeX, (int)(roads.get(i).getY2())*zoomMultiplier+changeY));

					int k = roads.get(i).getLanes();
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()-k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-k*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-k*laneSize)*zoomMultiplier+changeY));
					g2.setStroke(dashed);
					for(int j = 1; j < roads.get(i).getLanes();j++)
					{
						g2.draw(new Line2D.Double(
								(int)(roads.get(i).getX1()+j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+j*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+j*laneSize)*zoomMultiplier+changeY));
						g2.draw(new Line2D.Double(
								(int)(roads.get(i).getX1()-j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-j*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-j*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-j*laneSize)*zoomMultiplier+changeY));
					}
					g2.setStroke(defaultStroke);
					g2.draw(new Line2D.Double(
							(int)(roads.get(i).getX1()+k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+k*laneSize)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+k*laneSize)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+k*laneSize)*zoomMultiplier+changeY));
				}
			}
			//draws the intersections
			Color color = g2.getColor();
			g2.setColor(Color.GRAY);
			g2.fillOval((int)((roads.get(i).getX1()-intersectionSize/2)*zoomMultiplier + changeX), (int)((roads.get(i).getY1()-intersectionSize/2)*zoomMultiplier + changeY), (int)(intersectionSize*zoomMultiplier), (int)(intersectionSize*zoomMultiplier ));
			g2.fillOval((int)((roads.get(i).getX2()-intersectionSize/2)*zoomMultiplier + changeX), (int)((roads.get(i).getY2()-intersectionSize/2)*zoomMultiplier + changeY), (int)(intersectionSize*zoomMultiplier), (int)(intersectionSize*zoomMultiplier ));
			g2.setColor(color);
		}


		/*
		for (int i = 0; i < streetMap.getIntersections().size(); i++)
		{
			int counter = 0;

			for(int j = 0; j < streetMap.getIntersections().get(i).getTrafficLights().size(); j++)
			{		
				Road road= streetMap.getIntersections().get(i).getTrafficLights().get(j).getRoad();
				int midPointX = (int) ((road.getX1()*zoomMultiplier+changeX) +(((road.getX2()*zoomMultiplier+changeX)-(road.getX1()*zoomMultiplier+changeX))/2));
				int midPointY = (int) ((road.getY1()*zoomMultiplier+changeY) +(((road.getY2()*zoomMultiplier+changeY)-(road.getY1()*zoomMultiplier+changeY))/2));

				if (streetMap.getIntersections().get(i).getTrafficLights().get(j).getStatus() == "R")
					g2.setColor(Color.RED);
				else
					g2.setColor(Color.GREEN);

				if(counter == 0)
				{
					g2.draw(new Line2D.Double(midPointX, midPointY, road.getX1()*zoomMultiplier+changeX, road.getY1()*zoomMultiplier+changeY));				
					counter++;
				}
				else
				{
					g2.draw(new Line2D.Double(midPointX, midPointY, road.getX2()*zoomMultiplier+changeX, road.getY2()*zoomMultiplier+changeY));
					counter = 0;
				}
			}
		}

		 */

		// draws the cars
		for(int i = 0; i<simulation.getCars().size(); i ++)
		{
			simulation.getCars().get(i).calculateOffset(simulation.getCars().get(i).getCurrentOriginIntersection(), simulation.getCars().get(i).getCurrentDestinationIntersection());
			g2.setColor(simulation.getCars().get(i).getColor());
			g2.fillOval((int)((simulation.getCars().get(i).getPositionX())*zoomMultiplier + changeX + simulation.getCars().get(i).getOffsetX()), (int)((simulation.getCars().get(i).getPositionY())*zoomMultiplier + changeY + simulation.getCars().get(i).getOffsetY()), (int)(this.car_size*zoomMultiplier), (int)(this.car_size*zoomMultiplier));

		}		
	}
	// draws tooltip
	public void drawToolTip(Graphics2D graphics, String text, int x,int y) {
		int i = 0;
		if(i==0)
		{
			i=1;
		}
		String[] list = text.split("\n");
		int max = 0;
		for(String s : list) 
		{
			if(s.length()>max)
			{
				max = s.length();
			}
		}
		i = list.length;
		int lastX = x+(int)(intersectionSize/2);
		int lastY = y-(int)(intersectionSize/2);
		Rectangle r = new Rectangle();
		r.setBounds((int)(lastX*zoomMultiplier + changeX), (int)(lastY*zoomMultiplier + changeY), max*6, i*13);
		graphics.fill(r);
		graphics.draw(r);
		graphics.setColor(Color.WHITE);
		lastY = lastY + 10;
		for(String s : list)
		{
			graphics.drawString(s, (int)(lastX*zoomMultiplier + changeX), (int)(lastY*zoomMultiplier + changeY));
			lastY = lastY + 10;
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
