package graphical_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;


import javax.swing.JPanel;


import core.Simulation;
import datastructures.Intersection;
import datastructures.RoadType;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import road.Road;

public class Visuals extends JPanel{	
	private Simulation simulation;
	private StreetMap streetMap;
	private ArrayList<Road> roads;
	
	
	public boolean isDrawLine() {
		return drawLine;
	}
	private Intersection drawRed;
	
	final int car_size = 8;

	private int intersectionSize = 0;
	private int maxIntersectionSize = 0;

	private int laneSize = 7;
	private int lightDistanceFromIntersection = 7;
	private boolean drawLine = false;
	private int mousePosX = 0;
	private int mousePosY = 0;
	private int startPosX = 0;
	private int startPosY = 0;
	private Stroke defaultStroke;
	private Stroke dashed = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND, 0, new float[]{9}, 0);
	private Stroke fat =new BasicStroke(laneSize-2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND);
	private int divider = 30;
	
	private double zoomMultiplier = 1.0;
	
	private int changeX = 0;
	private int changeY = 0;
	private final int GRAPH_MOVED_DISTANCE = 25;
	public int getDivider()
	{
		return divider;
	}
	public void setLightDistanceFromIntersection(int length)
	{
		lightDistanceFromIntersection = length/divider;
	}
	public void setIntersectionSize(int is)	{
		intersectionSize = is;
	}
	
	public int getIntersectionSize() {
		return intersectionSize;
	}
	public void setMaxIntersectionSize(int is) {
		intersectionSize = is;
	}
	
	public int getMaxIntersectionSize() {
		return intersectionSize;
	}
	public void setLaneSize(int l) {
		laneSize = l;
	}
	
	public int getLaneSize() {
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

	public void resetPosition() {
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
	public void paintComponent (Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		defaultStroke = g2.getStroke();
		// draws red pointer
		g2.setColor(Color.red);
		if(drawRed != null)
		{
			String text = "X: "+drawRed.getXCoord()+" Y: "+ drawRed.getYCoord()+"\n"+"test";
			g2.fillOval((int)((drawRed.getXCoord()-(maxIntersectionSize/2)-3)*zoomMultiplier + changeX), (int)((drawRed.getYCoord()-(maxIntersectionSize/2)-3)*zoomMultiplier + changeY), (int)((maxIntersectionSize+5)*zoomMultiplier), (int)((maxIntersectionSize+5)*zoomMultiplier ));
			drawToolTip(g2, text, drawRed.getXCoord(), drawRed.getYCoord());
		}
		g2.setColor(Color.cyan);
		
		
		// draws guide line		
		/*if (drawLine) 
		{	
			g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));		
			g2.drawLine((int)(startPosX*zoomMultiplier)+changeX, (int)(startPosY*zoomMultiplier)+changeY, (int)(mousePosX*zoomMultiplier)+changeX, (int)(mousePosY*zoomMultiplier)+changeY);
			g2.setStroke(new BasicStroke());
		}*/
				
		g2.setColor(Color.black);
		for(int i = 0 ; i< roads.size() ; i++ ) {	
			
			setLightDistanceFromIntersection(roads.get(i).getLength());
			if(roads.get(i).getType() == RoadType.ROAD)
			{
				g2.setColor(Color.black);
			}
			else if(roads.get(i).getType() == RoadType.DIRT_ROAD)
			{
				g2.setColor(Color.ORANGE);
			}
			else if(roads.get(i).getType() == RoadType.HIGHWAY)
			{
				g2.setColor(Color.BLUE);
			}
			
			
			
			int k = roads.get(i).getLanes();
			double angle = Math.atan2(roads.get(i).getY2()-roads.get(i).getY1(), roads.get(i).getX1()-roads.get(i).getX2());
			if (angle<0)
				angle+=Math.PI*2;
			double offsetAngle = angle+(Math.PI/2);	
			if (offsetAngle > Math.PI*2)
				offsetAngle-= Math.PI*2;
			
			int midPointX = (int) ((roads.get(i).getX1()*zoomMultiplier+changeX) +(((roads.get(i).getX2()*zoomMultiplier+changeX)-(roads.get(i).getX1()*zoomMultiplier+changeX))/lightDistanceFromIntersection));
			int midPointY = (int) ((roads.get(i).getY1()*zoomMultiplier+changeY) +(((roads.get(i).getY2()*zoomMultiplier+changeY)-(roads.get(i).getY1()*zoomMultiplier+changeY))/lightDistanceFromIntersection));
			int midPointX2 = (int) ((roads.get(i).getX1()*zoomMultiplier+changeX) +(((roads.get(i).getX2()*zoomMultiplier+changeX)-(roads.get(i).getX1()*zoomMultiplier+changeX))/lightDistanceFromIntersection*(lightDistanceFromIntersection-1)));
			int midPointY2 = (int) ((roads.get(i).getY1()*zoomMultiplier+changeY) +(((roads.get(i).getY2()*zoomMultiplier+changeY)-(roads.get(i).getY1()*zoomMultiplier+changeY))/lightDistanceFromIntersection)*(lightDistanceFromIntersection-1));
			
			int offsetX = (int) (Math.round(Math.cos(offsetAngle)*k*laneSize));
			int offsetY = (int) (Math.round(Math.sin(offsetAngle)*k*laneSize));
			int offsetX2 = (int) (Math.round(Math.cos(offsetAngle)*laneSize));
			int offsetY2 = (int) (Math.round(Math.sin(offsetAngle)*laneSize));
					
			g2.draw(new Line2D.Double(
					(int)(roads.get(i).getX1())*zoomMultiplier+changeX, (int)(roads.get(i).getY1())*zoomMultiplier+changeY, (int)(roads.get(i).getX2())*zoomMultiplier+changeX, (int)(roads.get(i).getY2())*zoomMultiplier+changeY));
			
			//trafficlight
			g2.setColor(Color.RED);
			g2.setStroke(fat);
			if(roads.get(i).getTrafficLightsRight().get(0).getStatus().equals("G"))
			{
				g2.setColor(Color.GREEN);
			}
			
			g2.draw(new Line2D.Double(
					(int)(roads.get(i).getX1()-(offsetX2/2))*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+(offsetY2/2 ))*zoomMultiplier+changeY, (int)(midPointX-(offsetX2/2 ))*zoomMultiplier+changeX, (int)(midPointY+(offsetY2/2))*zoomMultiplier+changeY));

			//end trafficlight
			
			g2.setStroke(defaultStroke);
			g2.setColor(Color.BLACK);
			g2.draw(new Line2D.Double(
					(int)(roads.get(i).getX1()-offsetX)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+offsetY)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-offsetX)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+offsetY)*zoomMultiplier+changeY));

			//trafficlight
			g2.setColor(Color.RED);
			g2.setStroke(fat);
			if(roads.get(i).getTrafficLightsLeft().get(0).getStatus().equals("G"))
			{
				g2.setColor(Color.GREEN);
			}
			
			g2.draw(new Line2D.Double(
					(int)(roads.get(i).getX2()+(offsetX2/2 ))*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-(offsetY2/2 ))*zoomMultiplier+changeY, (int)(midPointX2+(offsetX2/2 ))*zoomMultiplier+changeX, (int)(midPointY2-(offsetY2/2 ))*zoomMultiplier+changeY));
			//end trafficlight
			
			g2.setStroke(defaultStroke);
			g2.setColor(Color.BLACK);
			g2.draw(new Line2D.Double(
					(int)(roads.get(i).getX1()+offsetX)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-offsetY)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+offsetX)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-offsetY)*zoomMultiplier+changeY));
						
			for(int j = 1; j < roads.get(i).getLanes();j++)
			{	
			offsetX = (int) (Math.round(Math.cos(offsetAngle)*j*laneSize));
			offsetY = (int) (Math.round(Math.sin(offsetAngle)*j*laneSize));
			
			g2.setStroke(dashed);
			g2.setColor(Color.black);
			g2.draw(new Line2D.Double(
					(int)(roads.get(i).getX1()-offsetX)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+offsetY)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()-offsetX)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()+offsetY)*zoomMultiplier+changeY));
			g2.draw(new Line2D.Double(
					(int)(roads.get(i).getX1()+offsetX)*zoomMultiplier+changeX, (int)(roads.get(i).getY1()-offsetY)*zoomMultiplier+changeY, (int)(roads.get(i).getX2()+offsetX)*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-offsetY)*zoomMultiplier+changeY));	
		
			//trafficlights.
			g2.setStroke(fat);
			g2.setColor(Color.RED);
			
			if(roads.get(i).getTrafficLightsRight().get(j).getStatus().equals("G"))
			{
				g2.setColor(Color.GREEN);
			}
			
			if(roads.get(i).getTrafficLightsRight().get(j).getIntersection().getXCoord() == roads.get(i).getX1() && roads.get(i).getTrafficLightsRight().get(j).getIntersection().getYCoord() == roads.get(i).getY1())
			{
				g2.draw(new Line2D.Double(
						(int)(roads.get(i).getX1()-(offsetX2/2 + offsetX))*zoomMultiplier+changeX, (int)(roads.get(i).getY1()+(offsetY2/2 + offsetY))*zoomMultiplier+changeY, (int)(midPointX-(offsetX2/2 + offsetX))*zoomMultiplier+changeX, (int)(midPointY+(offsetY2/2 + offsetY))*zoomMultiplier+changeY));
			
			}
			
			g2.setColor(Color.RED);
			
			if(roads.get(i).getTrafficLightsLeft().get(0).getStatus().equals("G"))
			{
				g2.setColor(Color.GREEN);
			}
			
			if(roads.get(i).getTrafficLightsLeft().get(j).getIntersection().getXCoord() == roads.get(i).getX2() && roads.get(i).getTrafficLightsLeft().get(j).getIntersection().getYCoord() == roads.get(i).getY2())
			{
				g2.draw(new Line2D.Double(
						(int)(roads.get(i).getX2()+(offsetX2/2 + offsetX))*zoomMultiplier+changeX, (int)(roads.get(i).getY2()-(offsetY2/2 + offsetY))*zoomMultiplier+changeY, (int)(midPointX2+(offsetX2/2 + offsetX))*zoomMultiplier+changeX, (int)(midPointY2-(offsetY2/2 + offsetY))*zoomMultiplier+changeY));
			
			}
			g2.setStroke(defaultStroke);
			}
			g2.setStroke(defaultStroke);
			g2.setColor(Color.black);
			
			
			
			
			//draws the intersections
			intersectionSize = roads.get(i).getLanes()*20;
			if(intersectionSize>maxIntersectionSize)
			{
				maxIntersectionSize = intersectionSize;
			}
			g2.fillOval((int)((roads.get(i).getX1()-intersectionSize/2)*zoomMultiplier + changeX), (int)((roads.get(i).getY1()-intersectionSize/2)*zoomMultiplier + changeY), (int)(intersectionSize*zoomMultiplier), (int)(intersectionSize*zoomMultiplier ));
			g2.fillOval((int)((roads.get(i).getX2()-intersectionSize/2)*zoomMultiplier + changeX), (int)((roads.get(i).getY2()-intersectionSize/2)*zoomMultiplier + changeY), (int)(intersectionSize*zoomMultiplier), (int)(intersectionSize*zoomMultiplier ));
		
		}
		
		// draws the cars
		for(int i = 0; i<simulation.getCars().size(); i ++)
		{
				simulation.getCars().get(i).calculateOffset(simulation.getCars().get(i).getCurrentOriginIntersection(), simulation.getCars().get(i).getCurrentDestinationIntersection());
				//simulation.getCars().get(i).setOffsetX(simulation.getCars().get(i).getCurrentRoad().getOffsetX().get(simulation.getCars().get(i).getLanes()-1));
				//simulation.getCars().get(i).setOffsetY(simulation.getCars().get(i).getCurrentRoad().getOffsetY().get(simulation.getCars().get(i).getLanes()-1));
				
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
		int lastX = x+(int)(maxIntersectionSize/2);
		int lastY = y-(int)(maxIntersectionSize/2);
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
