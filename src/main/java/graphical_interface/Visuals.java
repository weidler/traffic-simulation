package graphical_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JPanel;


import core.Simulation;
import datastructures.CarType;
import datastructures.Intersection;
import datastructures.RoadType;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import road.Road;
import util.Geometry;

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
	private Stroke fat =new BasicStroke((float) (laneSize-2*getZoomMultiplier()), BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND);
	private int divider = 30;
	
	private double zoomMultiplier = 1.0;
	
	private int changeX = 0;
	private int changeY = 0;
	private final int GRAPH_MOVED_DISTANCE = 25;
	
	public Visuals(Simulation simulation) {
		this.simulation = simulation;
		this.streetMap = this.simulation.getStreetMap();
		roads = streetMap.getRoads();
	}
		
	public int getDivider() {
		return divider;
	}
	
	public void setLightDistanceFromIntersection(int length) {
		lightDistanceFromIntersection = (length/divider);
		if(lightDistanceFromIntersection <0.01) {
			lightDistanceFromIntersection = length;
		}
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
		this.changeX = changeX + (GRAPH_MOVED_DISTANCE * i);
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
		this.changeY = changeY + (GRAPH_MOVED_DISTANCE * i);
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
		
		// precalculate offsets
		HashMap<Road, double[]> precalculated_offsets = new HashMap<Road, double[]>();
		for(int i = 0 ; i< roads.size() ; i++ ) {				
			Road current_road = roads.get(i);
			double[] offsets = Geometry.offset(current_road, this.laneSize);
			precalculated_offsets.put(current_road, offsets);
		}
			
		g2.setColor(Color.black);
		for(int i = 0 ; i < roads.size() ; i++ ) {				
			Road current_road = roads.get(i);
			Intersection[] connected_intersections = current_road.getIntersections(this.streetMap);
			Intersection intersection_to = connected_intersections[0];
			Intersection intersection_from = connected_intersections[1];
			
			// store needed roads in variables
			Road[] neighbouring_roads_from = current_road.getNeighbouringRoadsAt(intersection_from);
			Road[] neighbouring_roads_to = current_road.getNeighbouringRoadsAt(intersection_to);
			
			Road next_clockwise_road_from = neighbouring_roads_from[0];
			Road next_counterclockwise_road_from = neighbouring_roads_from[1];
			Road next_clockwise_road_to = neighbouring_roads_to[0];
			Road next_counterclockwise_road_to = neighbouring_roads_to[1];			
			
			// get offset numbers
			int offset_x = (int) precalculated_offsets.get(current_road)[0];
			int offset_y = (int) precalculated_offsets.get(current_road)[1];
			int outer_offset_x = offset_x * current_road.getLanes();
			int outer_offset_y = offset_y * current_road.getLanes();
			double offsetAngle = precalculated_offsets.get(current_road)[2];
			
			// recalculate initial start and end of road outer lines based on angular offset
			double to_x_right = current_road.getX1() - outer_offset_x;
			double to_y_right = current_road.getY1() + outer_offset_y;
			double from_x_right = current_road.getX2() - outer_offset_x;
			double from_y_right = current_road.getY2() + outer_offset_y;
			
			double to_x_left = current_road.getX1() + outer_offset_x;
			double to_y_left = current_road.getY1() - outer_offset_y;
			double from_x_left = current_road.getX2() + outer_offset_x;
			double from_y_left = current_road.getY2() - outer_offset_y;
			
//			g2.setStroke(new BasicStroke(3));
//			g2.setColor(Color.BLUE);
//			g2.fillOval(
//					(int) ((to_x_right) * zoomMultiplier + changeX), 
//					(int) ((to_y_right) * zoomMultiplier + changeY),
//					5,5
//			);
//			
//			g2.setColor(Color.PINK);
//			g2.fillOval(
//					(int) ((from_x_left) * zoomMultiplier + changeX), 
//					(int) ((from_y_left) * zoomMultiplier + changeY),
//					5,5
//			);
			
			// ADJUST FROM end of the road
			if (next_clockwise_road_from != null) {
				
				// identify the coordinates of surrounding roads that are NOT at the focused intersection
				int ncr_origin_x = next_clockwise_road_from.getX1();
				int ncr_origin_y = next_clockwise_road_from.getY1();
				if (ncr_origin_x == intersection_from.getXCoord()) ncr_origin_x = next_clockwise_road_from.getX2();
				if (ncr_origin_y == intersection_from.getYCoord()) ncr_origin_y = next_clockwise_road_from.getY2();

				int nccr_origin_x = next_counterclockwise_road_from.getX1();
				int nccr_origin_y = next_counterclockwise_road_from.getY1();
				if (nccr_origin_x == intersection_from.getXCoord()) nccr_origin_x = next_counterclockwise_road_from.getX2();
				if (nccr_origin_y == intersection_from.getYCoord()) nccr_origin_y = next_counterclockwise_road_from.getY2();

				if(Geometry.liesLeft(ncr_origin_x, ncr_origin_y, intersection_from.getXCoord(), intersection_from.getYCoord(), // is clockwise road option a to the right?
							intersection_from.getXCoord() - (precalculated_offsets.get(next_clockwise_road_from)[0] * next_clockwise_road_from.getLanes()),
							intersection_from.getYCoord() + (precalculated_offsets.get(next_clockwise_road_from)[1] * next_clockwise_road_from.getLanes())) ==
						Geometry.liesLeft(ncr_origin_x, ncr_origin_y, intersection_from.getXCoord(), intersection_from.getYCoord(), // is clockwise road option a to the right?
								intersection_from.getXCoord() + (precalculated_offsets.get(next_clockwise_road_from)[0] * next_clockwise_road_from.getLanes()),
								intersection_from.getYCoord() - (precalculated_offsets.get(next_clockwise_road_from)[1] * next_clockwise_road_from.getLanes()))) {
					System.out.println("[WARNING] Geometry.liesLeft has issues!");					
				}				
				
				// right line needs to cross clockwise right
				double[] line_intersection_right_from;
				if (!Geometry.liesLeft(ncr_origin_x, ncr_origin_y, intersection_from.getXCoord(), intersection_from.getYCoord(), // is clockwise road option a to the right?
						intersection_from.getXCoord() - (precalculated_offsets.get(next_clockwise_road_from)[0] * next_clockwise_road_from.getLanes()),
						intersection_from.getYCoord() + (precalculated_offsets.get(next_clockwise_road_from)[1] * next_clockwise_road_from.getLanes()))) {
					
					line_intersection_right_from = Geometry.intersection(from_x_right, from_y_right, to_x_right, to_y_right, 
							ncr_origin_x - (precalculated_offsets.get(next_clockwise_road_from)[0] * next_clockwise_road_from.getLanes()),
							ncr_origin_y + (precalculated_offsets.get(next_clockwise_road_from)[1] * next_clockwise_road_from.getLanes()), 
							intersection_from.getXCoord() - (precalculated_offsets.get(next_clockwise_road_from)[0] * next_clockwise_road_from.getLanes()),
							intersection_from.getYCoord() + (precalculated_offsets.get(next_clockwise_road_from)[1] * next_clockwise_road_from.getLanes())
					);
				} else {
					line_intersection_right_from = Geometry.intersection(from_x_right, from_y_right, to_x_right, to_y_right, 
							ncr_origin_x + (precalculated_offsets.get(next_clockwise_road_from)[0] * next_clockwise_road_from.getLanes()),
							ncr_origin_y - (precalculated_offsets.get(next_clockwise_road_from)[1] * next_clockwise_road_from.getLanes()), 
							intersection_from.getXCoord() + (precalculated_offsets.get(next_clockwise_road_from)[0] * next_clockwise_road_from.getLanes()),
							intersection_from.getYCoord() - (precalculated_offsets.get(next_clockwise_road_from)[1] * next_clockwise_road_from.getLanes())
					);
				}
				
				// left line needs to cross counterclockwise right
				double[] line_intersection_left_from;
				if (Geometry.liesLeft(nccr_origin_x, nccr_origin_y, intersection_from.getXCoord(), intersection_from.getYCoord(), 
						intersection_from.getXCoord() + (precalculated_offsets.get(next_counterclockwise_road_from)[0] * next_counterclockwise_road_from.getLanes()), 
						intersection_from.getYCoord() - (precalculated_offsets.get(next_counterclockwise_road_from)[1] * next_counterclockwise_road_from.getLanes()))) {
				
					line_intersection_left_from = Geometry.intersection(from_x_left, from_y_left, to_x_left, to_y_left, 
						nccr_origin_x + (precalculated_offsets.get(next_counterclockwise_road_from)[0] * next_counterclockwise_road_from.getLanes()), 
						nccr_origin_y - (precalculated_offsets.get(next_counterclockwise_road_from)[1] * next_counterclockwise_road_from.getLanes()), 
						intersection_from.getXCoord() + (precalculated_offsets.get(next_counterclockwise_road_from)[0] * next_counterclockwise_road_from.getLanes()), 
						intersection_from.getYCoord() - (precalculated_offsets.get(next_counterclockwise_road_from)[1] * next_counterclockwise_road_from.getLanes())
					);
				} else {
					line_intersection_left_from = Geometry.intersection(from_x_left, from_y_left, to_x_left, to_y_left, 
						nccr_origin_x - (precalculated_offsets.get(next_counterclockwise_road_from)[0] * next_counterclockwise_road_from.getLanes()), 
						nccr_origin_y + (precalculated_offsets.get(next_counterclockwise_road_from)[1] * next_counterclockwise_road_from.getLanes()), 
						intersection_from.getXCoord() - (precalculated_offsets.get(next_counterclockwise_road_from)[0] * next_counterclockwise_road_from.getLanes()), 
						intersection_from.getYCoord() + (precalculated_offsets.get(next_counterclockwise_road_from)[1] * next_counterclockwise_road_from.getLanes())
					);
				}
				
				
			
				from_x_right = line_intersection_right_from[0];
				from_y_right = line_intersection_right_from[1];
			
				from_x_left = line_intersection_left_from[0];
				from_y_left = line_intersection_left_from[1];
			}			
			
			// ADJUST TO end of the road
			if (next_clockwise_road_to != null) {
				// identify the coordinates of surrounding roads that are NOT at the focused intersection
				int ncr_origin_x = next_clockwise_road_to.getX1();
				int ncr_origin_y = next_clockwise_road_to.getY1();
				if (ncr_origin_x == intersection_to.getXCoord()) ncr_origin_x = next_clockwise_road_to.getX2();
				if (ncr_origin_y == intersection_to.getYCoord()) ncr_origin_y = next_clockwise_road_to.getY2();

				int nccr_origin_x = next_counterclockwise_road_to.getX1();
				int nccr_origin_y = next_counterclockwise_road_to.getY1();
				if (nccr_origin_x == intersection_to.getXCoord()) nccr_origin_x = next_counterclockwise_road_to.getX2();
				if (nccr_origin_y == intersection_to.getYCoord()) nccr_origin_y = next_counterclockwise_road_to.getY2();

				// right line needs to cross counterclockwise left
				double[] line_intersection_right_to;
				if (Geometry.liesLeft(nccr_origin_x, nccr_origin_y, intersection_to.getXCoord(), intersection_to.getYCoord(), 
						intersection_to.getXCoord() - (precalculated_offsets.get(next_counterclockwise_road_to)[0] * next_counterclockwise_road_to.getLanes()),
						intersection_to.getYCoord() + (precalculated_offsets.get(next_counterclockwise_road_to)[1] * next_counterclockwise_road_to.getLanes()))) {
					
					line_intersection_right_to = Geometry.intersection(from_x_right, from_y_right, to_x_right, to_y_right, 
							nccr_origin_x - (precalculated_offsets.get(next_counterclockwise_road_to)[0] * next_counterclockwise_road_to.getLanes()),
							nccr_origin_y + (precalculated_offsets.get(next_counterclockwise_road_to)[1] * next_counterclockwise_road_to.getLanes()), 
							intersection_to.getXCoord() - (precalculated_offsets.get(next_counterclockwise_road_to)[0] * next_counterclockwise_road_to.getLanes()),
							intersection_to.getYCoord() + (precalculated_offsets.get(next_counterclockwise_road_to)[1] * next_counterclockwise_road_to.getLanes())
					);
				} else {
					line_intersection_right_to = Geometry.intersection(from_x_right, from_y_right, to_x_right, to_y_right, 
							nccr_origin_x + (precalculated_offsets.get(next_counterclockwise_road_to)[0] * next_counterclockwise_road_to.getLanes()),
							nccr_origin_y - (precalculated_offsets.get(next_counterclockwise_road_to)[1] * next_counterclockwise_road_to.getLanes()), 
							intersection_to.getXCoord() + (precalculated_offsets.get(next_counterclockwise_road_to)[0] * next_counterclockwise_road_to.getLanes()),
							intersection_to.getYCoord() - (precalculated_offsets.get(next_counterclockwise_road_to)[1] * next_counterclockwise_road_to.getLanes())
					);
				}
				
				// left line needs to cross clockwise right
				double[] line_intersection_left_to;
				if (!Geometry.liesLeft(ncr_origin_x, ncr_origin_y, intersection_to.getXCoord(), intersection_to.getYCoord(), 
						intersection_to.getXCoord() + (precalculated_offsets.get(next_clockwise_road_to)[0] * next_clockwise_road_to.getLanes()), 
						intersection_to.getYCoord() - (precalculated_offsets.get(next_clockwise_road_to)[1] * next_clockwise_road_to.getLanes()))) {
				
					line_intersection_left_to = Geometry.intersection(from_x_left, from_y_left, to_x_left, to_y_left, 
						ncr_origin_x + (precalculated_offsets.get(next_clockwise_road_to)[0] * next_clockwise_road_to.getLanes()), 
						ncr_origin_y - (precalculated_offsets.get(next_clockwise_road_to)[1] * next_clockwise_road_to.getLanes()), 
						intersection_to.getXCoord() + (precalculated_offsets.get(next_clockwise_road_to)[0] * next_clockwise_road_to.getLanes()), 
						intersection_to.getYCoord() - (precalculated_offsets.get(next_clockwise_road_to)[1] * next_clockwise_road_to.getLanes())
					);
				} else {
					line_intersection_left_to = Geometry.intersection(from_x_left, from_y_left, to_x_left, to_y_left, 
						ncr_origin_x - (precalculated_offsets.get(next_clockwise_road_to)[0] * next_clockwise_road_to.getLanes()), 
						ncr_origin_y + (precalculated_offsets.get(next_clockwise_road_to)[1] * next_clockwise_road_to.getLanes()), 
						intersection_to.getXCoord() - (precalculated_offsets.get(next_clockwise_road_to)[0] * next_clockwise_road_to.getLanes()), 
						intersection_to.getYCoord() + (precalculated_offsets.get(next_clockwise_road_to)[1] * next_clockwise_road_to.getLanes())
					);
				}
			
				to_x_right = line_intersection_right_to[0];
				to_y_right = line_intersection_right_to[1];
			
				to_x_left = line_intersection_left_to[0];
				to_y_left = line_intersection_left_to[1];
			}

			// draw road outer lines
			g2.setStroke(defaultStroke);
			g2.setColor(Color.BLACK);
			g2.draw(new Line2D.Double(
					(int) (to_x_right) * zoomMultiplier + changeX, 
					(int) (to_y_right) * zoomMultiplier + changeY,
					(int) (from_x_right) * zoomMultiplier + changeX, 
					(int) (from_y_right) * zoomMultiplier + changeY
			));
			
			g2.setStroke(defaultStroke);
			g2.setColor(Color.BLACK);
			g2.draw(new Line2D.Double(
					(int) (to_x_left) * zoomMultiplier + changeX, 
					(int) (to_y_left) * zoomMultiplier + changeY,
					(int) (from_x_left) * zoomMultiplier + changeX, 
					(int) (from_y_left) * zoomMultiplier + changeY
			));
			
			setLightDistanceFromIntersection((int) roads.get(i).getLength());
			if(roads.get(i).getType() == RoadType.ROAD) {
				g2.setColor(Color.black);
			} else if(roads.get(i).getType() == RoadType.DIRT_ROAD) {
				g2.setColor(Color.ORANGE);
			} else if(roads.get(i).getType() == RoadType.HIGHWAY)	{
				g2.setColor(Color.BLUE);
			}
						
			int midPointX1 = (int) ((roads.get(i).getX1()) +(((roads.get(i).getX2())-(roads.get(i).getX1()))/lightDistanceFromIntersection));
			int midPointY1 = (int) ((roads.get(i).getY1()) +(((roads.get(i).getY2())-(roads.get(i).getY1()))/lightDistanceFromIntersection));
			int midPointX2 = (int) ((roads.get(i).getX1()) +(((roads.get(i).getX2())-(roads.get(i).getX1()))/lightDistanceFromIntersection*(lightDistanceFromIntersection-1)));
			int midPointY2 = (int) ((roads.get(i).getY1()) +(((roads.get(i).getY2())-(roads.get(i).getY1()))/lightDistanceFromIntersection)*(lightDistanceFromIntersection-1));
					
			g2.setStroke(new BasicStroke(2));
			g2.draw(new Line2D.Double(
					(int) (roads.get(i).getX1()) * zoomMultiplier+changeX, 
					(int) (roads.get(i).getY1()) * zoomMultiplier+changeY, 
					(int) (roads.get(i).getX2()) * zoomMultiplier+changeX, 
					(int) (roads.get(i).getY2()) * zoomMultiplier+changeY
			));
			g2.setStroke(defaultStroke);
			
			// Draw lanes
			for(int j = 1; j <= current_road.getLanes(); j++) {	
				
				double lane_offset_x = offset_x * (j - 1);
				double lane_offset_y = offset_y * (j - 1);
								
				if (j > 1) {
					g2.setStroke(dashed);
					g2.setColor(Color.black);
					
					g2.draw(new Line2D.Double(
							(int) (current_road.getX1() - lane_offset_x) * zoomMultiplier + changeX, 
							(int) (current_road.getY1() + lane_offset_y) * zoomMultiplier + changeY, 
							(int) (current_road.getX2() - lane_offset_x) * zoomMultiplier + changeX, 
							(int) (current_road.getY2() + lane_offset_y) * zoomMultiplier + changeY
					));
					
					g2.draw(new Line2D.Double(
							(int) (current_road.getX1() + lane_offset_x) * zoomMultiplier+changeX, 
							(int) (current_road.getY1() - lane_offset_y) * zoomMultiplier+changeY, 
							(int) (current_road.getX2() + lane_offset_x) * zoomMultiplier+changeX, 
							(int) (current_road.getY2() - lane_offset_y) * zoomMultiplier+changeY
					));					
				}
				
				//trafficlights.
				g2.setStroke(new BasicStroke((int) (this.laneSize * zoomMultiplier / 2)));
			
				TrafficLight tl_from = intersection_from.getTrafficLightsApproachingFrom(intersection_to, j);
				TrafficLight tl_to = intersection_to.getTrafficLightsApproachingFrom(intersection_from, j);

				g2.setColor(Color.RED);
				if(tl_from.getStatus().equals("G")) {
					g2.setColor(Color.GREEN);
				}
				
				if (intersection_from.isAt(current_road.getX1(), current_road.getY1())) {
					g2.draw(new Line2D.Double(
							(int) (midPointX1 + (offset_x + lane_offset_x)) * zoomMultiplier + changeX, 
							(int) (midPointY1 - (offset_y + lane_offset_y)) * zoomMultiplier + changeY, 
							(int) (midPointX1 + (lane_offset_x)) * zoomMultiplier + changeX, 
							(int) (midPointY1 - (lane_offset_y)) * zoomMultiplier + changeY
					));					
				} else {
					g2.draw(new Line2D.Double(
							(int) (midPointX2 + (offset_x + lane_offset_x)) * zoomMultiplier + changeX, 
							(int) (midPointY2 - (offset_y + lane_offset_y)) * zoomMultiplier + changeY, 
							(int) (midPointX2 + (lane_offset_x)) * zoomMultiplier + changeX, 
							(int) (midPointY2 - (lane_offset_y)) * zoomMultiplier + changeY));
				}
				
				g2.setColor(Color.RED);
				if(tl_to.getStatus().equals("G")) {
					g2.setColor(Color.GREEN);
				}
				
				if (intersection_to.isAt(current_road.getX1(), current_road.getY1())) {
					g2.draw(new Line2D.Double(
						(int) (midPointX1 - (offset_x + lane_offset_x)) * zoomMultiplier + changeX, 
						(int) (midPointY1 + (offset_y + lane_offset_y))* zoomMultiplier + changeY, 
						(int) (midPointX1 - (lane_offset_x)) * zoomMultiplier + changeX, 
						(int) (midPointY1 + (lane_offset_y)) * zoomMultiplier + changeY
					));			
				} else {
					g2.draw(new Line2D.Double(
						(int) (midPointX2 - (offset_x + lane_offset_x)) * zoomMultiplier + changeX, 
						(int) (midPointY2 + (offset_y + lane_offset_y)) * zoomMultiplier + changeY, 
						(int) (midPointX2 - (lane_offset_x)) * zoomMultiplier + changeX, 
						(int) (midPointY2 + (lane_offset_y)) * zoomMultiplier + changeY
					));
				}
				
				g2.setStroke(defaultStroke);
			}
			
			g2.setStroke(defaultStroke);
			g2.setColor(Color.black);
			
			//draws the intersections
			intersectionSize = current_road.getLanes()*20;
//			if(intersectionSize>maxIntersectionSize)
//			{
//				maxIntersectionSize = intersectionSize;
//			}
//			
//			g2.fillOval(
//					(int) ((current_road.getX1()-intersectionSize/2)*zoomMultiplier + changeX), 
//					(int) ((current_road.getY1()-intersectionSize/2)*zoomMultiplier + changeY), 
//					(int) (intersectionSize*zoomMultiplier), 
//					(int) (intersectionSize*zoomMultiplier
//			));
//			
//			g2.fillOval(
//					(int) ((current_road.getX2()-intersectionSize/2)*zoomMultiplier + changeX), 
//					(int) ((current_road.getY2()-intersectionSize/2)*zoomMultiplier + changeY), 
//					(int) (intersectionSize*zoomMultiplier), 
//					(int) (intersectionSize*zoomMultiplier
//			));
		
		}
		
		// draws the cars
		for(int i = 0; i<simulation.getCars().size(); i ++)
		{
				simulation.getCars().get(i).calculateOffset(simulation.getCars().get(i).getCurrentOriginIntersection(), simulation.getCars().get(i).getCurrentDestinationIntersection());
				//simulation.getCars().get(i).setOffsetX(simulation.getCars().get(i).getCurrentRoad().getOffsetX().get(simulation.getCars().get(i).getLanes()-1));
				//simulation.getCars().get(i).setOffsetY(simulation.getCars().get(i).getCurrentRoad().getOffsetY().get(simulation.getCars().get(i).getLanes()-1));
				
				g2.setColor(simulation.getCars().get(i).getColor());
				if(simulation.getCars().get(i).getType() == CarType.CAR)
					g2.fillOval((int)((simulation.getCars().get(i).getPositionX())*zoomMultiplier + changeX + simulation.getCars().get(i).getOffsetX()), (int)((simulation.getCars().get(i).getPositionY())*zoomMultiplier + changeY + simulation.getCars().get(i).getOffsetY()), (int)(this.car_size*zoomMultiplier), (int)(this.car_size*zoomMultiplier));
				else {
					//g2.rotate(simulation.getCars().get(i).getAngle());
					
					g2.fillRect((int)((simulation.getCars().get(i).getPositionX())*zoomMultiplier + changeX + simulation.getCars().get(i).getOffsetX()), (int)((simulation.getCars().get(i).getPositionY())*zoomMultiplier + changeY + simulation.getCars().get(i).getOffsetY()), (int)(this.car_size*zoomMultiplier), (int)(this.car_size*zoomMultiplier));
					
					//g2.rotate(0);
				}
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
