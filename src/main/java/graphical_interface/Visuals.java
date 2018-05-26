package graphical_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import car.Car;
import core.Simulation;
import datastructures.Intersection;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import road.Road;
import type.CarType;
import type.RoadType;
import util.Geometry;
import datatype.Point;

public class Visuals extends JPanel {
	private Simulation simulation;
	private StreetMap streetMap;
	private ArrayList<Road> roads;

	public boolean isDrawLine() {
		return drawLine;
	}

	private Intersection drawRed;

	private int intersectionSize = 0;
	private int maxIntersectionSize = 0;

	private int laneSize = 8;
	final int car_width = laneSize/2;
	private int lightDistanceFromIntersection = 20;
	private boolean drawLine = false;
	private int mousePosX = 0;
	private int mousePosY = 0;
	private int startPosX = 0;
	private int startPosY = 0;
	private Stroke defaultStroke;
	private Stroke dashed = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND, 0, new float[] { 9 }, 0);
	private Stroke fat = new BasicStroke((float) (laneSize - 2 * getZoomMultiplier()), BasicStroke.CAP_ROUND,
			BasicStroke.CAP_ROUND);
	private int divider = 30;

	private double zoomMultiplier = 1.0;

	private int changeX = 0;
	private int changeY = 0;
	private final int GRAPH_MOVED_DISTANCE = 25;
	private int indicator_size = 15;

	private String road_color = "#596270";
	private String highway_color = "#5c85d6";
	private String dirtroad_color = "#cc9900";

	public Visuals(Simulation simulation) {
		this.simulation = simulation;
		this.streetMap = this.simulation.getStreetMap();
		roads = streetMap.getRoads();
		
		setOpaque(true);
		this.setBackground(Color.decode("#57af6b"));
		this.setBorder(BorderFactory.createEmptyBorder());
	}

	public int getDivider() {
		return divider;
	}

	public void setLightDistanceFromIntersection(int length) {
		lightDistanceFromIntersection = (length / divider);
		if (lightDistanceFromIntersection < 0.01) {
			lightDistanceFromIntersection = length;
		}
	}

	public void setIntersectionSize(int is) {
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
		this.changeX = changeX - (GRAPH_MOVED_DISTANCE * i);
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
		this.changeY = changeY - (GRAPH_MOVED_DISTANCE * i);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		defaultStroke = g2.getStroke();

		// draws guide line
		g2.setColor(Color.cyan);
		if (drawLine) {
			g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0));
			g2.drawLine((int) (startPosX * zoomMultiplier) + changeX, (int) (startPosY * zoomMultiplier) + changeY,
					(int) (mousePosX * zoomMultiplier) + changeX, (int) (mousePosY * zoomMultiplier) + changeY);
			g2.setStroke(new BasicStroke());
		}

		// precalculate offsets
		HashMap<Road, double[]> precalculated_offsets = new HashMap<Road, double[]>();
		for (int i = 0; i < roads.size(); i++) {
			Road current_road = roads.get(i);
			double[] offsets = Geometry.offset(current_road, this.laneSize);
			precalculated_offsets.put(current_road, offsets);
		}

		g2.setColor(Color.black);

		HashMap<Intersection, ArrayList<Point>> chosen_line_intersections = new HashMap<Intersection, ArrayList<Point>>();
		for (int i = 0; i < roads.size(); i++) {
			Road current_road = roads.get(i);
			Intersection[] connected_intersections = current_road.getIntersections(this.streetMap);
			Intersection intersection_to = connected_intersections[0];
			Intersection intersection_from = connected_intersections[1];
			
			if (!chosen_line_intersections.containsKey(intersection_from)) chosen_line_intersections.put(intersection_from, new ArrayList<Point>());
			if (!chosen_line_intersections.containsKey(intersection_to)) chosen_line_intersections.put(intersection_to, new ArrayList<Point>());

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

			// ADJUST FROM end of the road
			if (next_clockwise_road_from != null) {

				// identify the coordinates of surrounding roads that are NOT at the focused
				// intersection
				int ncr_origin_x = next_clockwise_road_from.getX1();
				int ncr_origin_y = next_clockwise_road_from.getY1();
				if (ncr_origin_x == intersection_from.getXCoord()) ncr_origin_x = next_clockwise_road_from.getX2();
				if (ncr_origin_y == intersection_from.getYCoord()) ncr_origin_y = next_clockwise_road_from.getY2();

				int nccr_origin_x = next_counterclockwise_road_from.getX1();
				int nccr_origin_y = next_counterclockwise_road_from.getY1();
				if (nccr_origin_x == intersection_from.getXCoord())
					nccr_origin_x = next_counterclockwise_road_from.getX2();
				if (nccr_origin_y == intersection_from.getYCoord())
					nccr_origin_y = next_counterclockwise_road_from.getY2();

				if (Geometry.liesLeft(ncr_origin_x, ncr_origin_y, intersection_from.getXCoord(),
						intersection_from.getYCoord(), // is clockwise road option a to the right?
						intersection_from.getXCoord() - (precalculated_offsets.get(next_clockwise_road_from)[0]
								* next_clockwise_road_from.getLanes()),
						intersection_from.getYCoord() + (precalculated_offsets.get(next_clockwise_road_from)[1]
								* next_clockwise_road_from.getLanes())) == Geometry.liesLeft(ncr_origin_x, ncr_origin_y,
										intersection_from.getXCoord(), intersection_from.getYCoord(), // is clockwise
																										// road option a
																										// to the right?
										intersection_from.getXCoord()
												+ (precalculated_offsets.get(next_clockwise_road_from)[0]
														* next_clockwise_road_from.getLanes()),
										intersection_from.getYCoord()
												- (precalculated_offsets.get(next_clockwise_road_from)[1]
														* next_clockwise_road_from.getLanes()))) {
					System.out.println("[WARNING] Geometry.liesLeft has issues!");
				}

				// right line needs to cross clockwise right
				double[] line_intersection_right_from;
				if (!Geometry.liesLeft(ncr_origin_x, ncr_origin_y, intersection_from.getXCoord(),
						intersection_from.getYCoord(), // is clockwise road option a to the right?
						intersection_from.getXCoord() - (precalculated_offsets.get(next_clockwise_road_from)[0]
								* next_clockwise_road_from.getLanes()),
						intersection_from.getYCoord() + (precalculated_offsets.get(next_clockwise_road_from)[1]
								* next_clockwise_road_from.getLanes()))) {

					line_intersection_right_from = Geometry.intersection(from_x_right, from_y_right, to_x_right,
							to_y_right,
							ncr_origin_x - (precalculated_offsets.get(next_clockwise_road_from)[0]
									* next_clockwise_road_from.getLanes()),
							ncr_origin_y + (precalculated_offsets.get(next_clockwise_road_from)[1]
									* next_clockwise_road_from.getLanes()),
							intersection_from.getXCoord() - (precalculated_offsets.get(next_clockwise_road_from)[0]
									* next_clockwise_road_from.getLanes()),
							intersection_from.getYCoord() + (precalculated_offsets.get(next_clockwise_road_from)[1]
									* next_clockwise_road_from.getLanes()));
				} else {
					line_intersection_right_from = Geometry.intersection(from_x_right, from_y_right, to_x_right,
							to_y_right,
							ncr_origin_x + (precalculated_offsets.get(next_clockwise_road_from)[0]
									* next_clockwise_road_from.getLanes()),
							ncr_origin_y - (precalculated_offsets.get(next_clockwise_road_from)[1]
									* next_clockwise_road_from.getLanes()),
							intersection_from.getXCoord() + (precalculated_offsets.get(next_clockwise_road_from)[0]
									* next_clockwise_road_from.getLanes()),
							intersection_from.getYCoord() - (precalculated_offsets.get(next_clockwise_road_from)[1]
									* next_clockwise_road_from.getLanes()));
				}

				// left line needs to cross counterclockwise right
				double[] line_intersection_left_from;
				if (Geometry.liesLeft(nccr_origin_x, nccr_origin_y, intersection_from.getXCoord(),
						intersection_from.getYCoord(),
						intersection_from.getXCoord() + (precalculated_offsets.get(next_counterclockwise_road_from)[0]
								* next_counterclockwise_road_from.getLanes()),
						intersection_from.getYCoord() - (precalculated_offsets.get(next_counterclockwise_road_from)[1]
								* next_counterclockwise_road_from.getLanes()))) {

					line_intersection_left_from = Geometry.intersection(from_x_left, from_y_left, to_x_left, to_y_left,
							nccr_origin_x + (precalculated_offsets.get(next_counterclockwise_road_from)[0]
									* next_counterclockwise_road_from.getLanes()),
							nccr_origin_y - (precalculated_offsets.get(next_counterclockwise_road_from)[1]
									* next_counterclockwise_road_from.getLanes()),
							intersection_from.getXCoord()
									+ (precalculated_offsets.get(next_counterclockwise_road_from)[0]
											* next_counterclockwise_road_from.getLanes()),
							intersection_from.getYCoord()
									- (precalculated_offsets.get(next_counterclockwise_road_from)[1]
											* next_counterclockwise_road_from.getLanes()));
				} else {
					line_intersection_left_from = Geometry.intersection(from_x_left, from_y_left, to_x_left, to_y_left,
							nccr_origin_x - (precalculated_offsets.get(next_counterclockwise_road_from)[0]
									* next_counterclockwise_road_from.getLanes()),
							nccr_origin_y + (precalculated_offsets.get(next_counterclockwise_road_from)[1]
									* next_counterclockwise_road_from.getLanes()),
							intersection_from.getXCoord()
									- (precalculated_offsets.get(next_counterclockwise_road_from)[0]
											* next_counterclockwise_road_from.getLanes()),
							intersection_from.getYCoord()
									+ (precalculated_offsets.get(next_counterclockwise_road_from)[1]
											* next_counterclockwise_road_from.getLanes()));
				}

				from_x_right = line_intersection_right_from[0];
				from_y_right = line_intersection_right_from[1];

				from_x_left = line_intersection_left_from[0];
				from_y_left = line_intersection_left_from[1];
				
				chosen_line_intersections.get(intersection_from).add(new Point(line_intersection_left_from));
				chosen_line_intersections.get(intersection_from).add(new Point(line_intersection_right_from));
			}

			// ADJUST TO end of the road
			if (next_clockwise_road_to != null) {
				// identify the coordinates of surrounding roads that are NOT at the focused
				// intersection
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
				if (Geometry.liesLeft(nccr_origin_x, nccr_origin_y, intersection_to.getXCoord(),
						intersection_to.getYCoord(),
						intersection_to.getXCoord() - (precalculated_offsets.get(next_counterclockwise_road_to)[0]
								* next_counterclockwise_road_to.getLanes()),
						intersection_to.getYCoord() + (precalculated_offsets.get(next_counterclockwise_road_to)[1]
								* next_counterclockwise_road_to.getLanes()))) {

					line_intersection_right_to = Geometry.intersection(from_x_right, from_y_right, to_x_right,
							to_y_right,
							nccr_origin_x - (precalculated_offsets.get(next_counterclockwise_road_to)[0]
									* next_counterclockwise_road_to.getLanes()),
							nccr_origin_y + (precalculated_offsets.get(next_counterclockwise_road_to)[1]
									* next_counterclockwise_road_to.getLanes()),
							intersection_to.getXCoord() - (precalculated_offsets.get(next_counterclockwise_road_to)[0]
									* next_counterclockwise_road_to.getLanes()),
							intersection_to.getYCoord() + (precalculated_offsets.get(next_counterclockwise_road_to)[1]
									* next_counterclockwise_road_to.getLanes()));
				} else {
					line_intersection_right_to = Geometry.intersection(from_x_right, from_y_right, to_x_right,
							to_y_right,
							nccr_origin_x + (precalculated_offsets.get(next_counterclockwise_road_to)[0]
									* next_counterclockwise_road_to.getLanes()),
							nccr_origin_y - (precalculated_offsets.get(next_counterclockwise_road_to)[1]
									* next_counterclockwise_road_to.getLanes()),
							intersection_to.getXCoord() + (precalculated_offsets.get(next_counterclockwise_road_to)[0]
									* next_counterclockwise_road_to.getLanes()),
							intersection_to.getYCoord() - (precalculated_offsets.get(next_counterclockwise_road_to)[1]
									* next_counterclockwise_road_to.getLanes()));
				}

				// left line needs to cross clockwise right
				double[] line_intersection_left_to;
				if (!Geometry.liesLeft(ncr_origin_x, ncr_origin_y, intersection_to.getXCoord(),
						intersection_to.getYCoord(),
						intersection_to.getXCoord() + (precalculated_offsets.get(next_clockwise_road_to)[0]
								* next_clockwise_road_to.getLanes()),
						intersection_to.getYCoord() - (precalculated_offsets.get(next_clockwise_road_to)[1]
								* next_clockwise_road_to.getLanes()))) {

					line_intersection_left_to = Geometry.intersection(from_x_left, from_y_left, to_x_left, to_y_left,
							ncr_origin_x + (precalculated_offsets.get(next_clockwise_road_to)[0]
									* next_clockwise_road_to.getLanes()),
							ncr_origin_y - (precalculated_offsets.get(next_clockwise_road_to)[1]
									* next_clockwise_road_to.getLanes()),
							intersection_to.getXCoord() + (precalculated_offsets.get(next_clockwise_road_to)[0]
									* next_clockwise_road_to.getLanes()),
							intersection_to.getYCoord() - (precalculated_offsets.get(next_clockwise_road_to)[1]
									* next_clockwise_road_to.getLanes()));
				} else {
					line_intersection_left_to = Geometry.intersection(from_x_left, from_y_left, to_x_left, to_y_left,
							ncr_origin_x - (precalculated_offsets.get(next_clockwise_road_to)[0]
									* next_clockwise_road_to.getLanes()),
							ncr_origin_y + (precalculated_offsets.get(next_clockwise_road_to)[1]
									* next_clockwise_road_to.getLanes()),
							intersection_to.getXCoord() - (precalculated_offsets.get(next_clockwise_road_to)[0]
									* next_clockwise_road_to.getLanes()),
							intersection_to.getYCoord() + (precalculated_offsets.get(next_clockwise_road_to)[1]
									* next_clockwise_road_to.getLanes()));
				}

				to_x_right = line_intersection_right_to[0];
				to_y_right = line_intersection_right_to[1];

				to_x_left = line_intersection_left_to[0];
				to_y_left = line_intersection_left_to[1];
				
				chosen_line_intersections.get(intersection_to).add(new Point(line_intersection_left_to));
				chosen_line_intersections.get(intersection_to).add(new Point(line_intersection_right_to));
			}

			// draw road background
			Polygon bg_polygon = new Polygon();
			bg_polygon.addPoint((int) (to_x_right * zoomMultiplier + changeX), (int) (to_y_right * zoomMultiplier + changeY));
			bg_polygon.addPoint((int) (from_x_right * zoomMultiplier + changeX), (int) (from_y_right * zoomMultiplier + changeY));
			bg_polygon.addPoint((int) (from_x_left * zoomMultiplier + changeX), (int) (from_y_left * zoomMultiplier + changeY));
			bg_polygon.addPoint((int) (to_x_left * zoomMultiplier + changeX), (int) (to_y_left * zoomMultiplier + changeY));
			if (roads.get(i).getType() == RoadType.ROAD) g2.setColor(Color.decode(road_color));
			else if (roads.get(i).getType() == RoadType.DIRT_ROAD) g2.setColor(Color.decode(dirtroad_color));
			else if (roads.get(i).getType() == RoadType.HIGHWAY) g2.setColor(Color.decode(highway_color));
			g2.fill(bg_polygon);

			// draw road outer lines
			g2.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke((float) 1.5));
			g2.draw(new Line2D.Double((int) (to_x_right) * zoomMultiplier + changeX,
					(int) (to_y_right) * zoomMultiplier + changeY, (int) (from_x_right) * zoomMultiplier + changeX,
					(int) (from_y_right) * zoomMultiplier + changeY));
			g2.draw(new Line2D.Double((int) (to_x_left) * zoomMultiplier + changeX,
					(int) (to_y_left) * zoomMultiplier + changeY, (int) (from_x_left) * zoomMultiplier + changeX,
					(int) (from_y_left) * zoomMultiplier + changeY));
			
			// draw mid line
			g2.setColor(Color.WHITE);
			g2.setStroke(dashed);
			if (current_road.getLanes() > 1) g2.setStroke(new BasicStroke(2));
			g2.draw(new Line2D.Double((int) (roads.get(i).getX1()) * zoomMultiplier + changeX,
					(int) (roads.get(i).getY1()) * zoomMultiplier + changeY,
					(int) (roads.get(i).getX2()) * zoomMultiplier + changeX,
					(int) (roads.get(i).getY2()) * zoomMultiplier + changeY));
			g2.setStroke(defaultStroke);

			// Draw lanes
			for (int j = 1; j <= current_road.getLanes(); j++) {

				double lane_offset_x = offset_x * (j - 1);
				double lane_offset_y = offset_y * (j - 1);
				
				// draw traffic lights
				double stroke_width = this.laneSize * zoomMultiplier / 2;
				g2.setStroke(new BasicStroke((int) (stroke_width)));

				TrafficLight tl_from = intersection_from.getTrafficLightsApproachingFrom(intersection_to, j);
				TrafficLight tl_to = intersection_to.getTrafficLightsApproachingFrom(intersection_from, j);

				double[] tl_position_from = Geometry.getPointBetween(
						lightDistanceFromIntersection, 
						current_road.getX1(), 
						current_road.getY1(), 
						current_road.getX2(), 
						current_road.getY2()
				);

				double[] tl_position_to = Geometry.getPointBetween(
						lightDistanceFromIntersection, 
						current_road.getX2(), 
						current_road.getY2(), 
						current_road.getX1(), 
						current_road.getY1()
				);

				g2.setColor(Color.RED);
				if (tl_from.getStatus().equals("G")) {
					g2.setColor(Color.GREEN);
				}

				if (intersection_from.isAt(current_road.getX1(), current_road.getY1())) {
					g2.drawLine(
							(int) ((tl_position_from[0] + (offset_x + lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_from[1] - (offset_y + lane_offset_y)) * zoomMultiplier + changeY),
							(int) ((tl_position_from[0] + (lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_from[1] - (lane_offset_y)) * zoomMultiplier + changeY));
				} else {
					g2.drawLine(
							(int) ((tl_position_to[0] + (offset_x + lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_to[1] - (offset_y + lane_offset_y)) * zoomMultiplier + changeY),
							(int) ((tl_position_to[0] + (lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_to[1] - (lane_offset_y)) * zoomMultiplier + changeY));
				}

				g2.setColor(Color.RED);
				if (tl_to.getStatus().equals("G")) {
					g2.setColor(Color.GREEN);
				}

				if (intersection_to.isAt(current_road.getX1(), current_road.getY1())) {
					g2.drawLine(
							(int) ((tl_position_from[0] - (offset_x + lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_from[1] + (offset_y + lane_offset_y)) * zoomMultiplier + changeY),
							(int) ((tl_position_from[0] - (lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_from[1] + (lane_offset_y)) * zoomMultiplier + changeY));
				} else {
					g2.drawLine(
							(int) ((tl_position_to[0] - (offset_x + lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_to[1] + (offset_y + lane_offset_y)) * zoomMultiplier + changeY),
							(int) ((tl_position_to[0] - (lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_to[1] + (lane_offset_y)) * zoomMultiplier + changeY));
				}

				g2.setStroke(defaultStroke);

				// lane lines
				if (j > 1) {
					g2.setStroke(dashed);
					g2.setColor(Color.WHITE);

					g2.draw(new Line2D.Double((int) (current_road.getX1() - lane_offset_x) * zoomMultiplier + changeX,
							(int) (current_road.getY1() + lane_offset_y) * zoomMultiplier + changeY,
							(int) (current_road.getX2() - lane_offset_x) * zoomMultiplier + changeX,
							(int) (current_road.getY2() + lane_offset_y) * zoomMultiplier + changeY));

					g2.draw(new Line2D.Double((int) (current_road.getX1() + lane_offset_x) * zoomMultiplier + changeX,
							(int) (current_road.getY1() - lane_offset_y) * zoomMultiplier + changeY,
							(int) (current_road.getX2() + lane_offset_x) * zoomMultiplier + changeX,
							(int) (current_road.getY2() - lane_offset_y) * zoomMultiplier + changeY));
				}
			}

			g2.setStroke(defaultStroke);

			// draws the intersections
			intersectionSize = 30;
		}
		
		// fill intersection leftouts
		if (streetMap.intersectionCount() > 1) {
			for (Intersection inter : streetMap.getIntersections()) {
				Polygon intersection_filling = new Polygon();
				if (chosen_line_intersections.containsKey(inter)) {
					for (Point point : Geometry.convexHull(chosen_line_intersections.get(inter))) {
						if (!intersection_filling.contains((int) (point.x * zoomMultiplier + changeX), (int) (point.y * zoomMultiplier + changeY))) {
							intersection_filling.addPoint(
									(int) (point.x * zoomMultiplier + changeX), 
									(int) (point.y * zoomMultiplier + changeY)
							);
						}
					}
					
					g2.setColor(Color.decode(road_color));
					g2.fill(intersection_filling);
					
				}
			}			
		}

		// draws the cars
		for (Car c : simulation.getCars()) {
			if (!c.inTraffic()) continue;
			
			c.calculateOffset(c.getCurrentOriginIntersection(), c.getCurrentDestinationIntersection(), this.laneSize);

			double car_center_x = c.getPositionX() + c.getOffsetX();
			double car_center_y = c.getPositionY() + c.getOffsetY();
			
			// starting left bottom, clockwise
			double[] car_rectangle = {
					car_center_x - (int) (c.getVehicleLength() / 2),
					car_center_y + (int) (this.car_width / 2),
					
					car_center_x - (int) (c.getVehicleLength() / 2),
					car_center_y - (int) (this.car_width / 2),
					
					car_center_x + (int) (c.getVehicleLength() / 2),
					car_center_y - (int) (this.car_width / 2),
					
					car_center_x + (int) (c.getVehicleLength() / 2),
					car_center_y + (int) (this.car_width / 2),
			};
			
			// subtract 180 to have orientation correct!
			car_rectangle = Geometry.rotateRectangleAroundCenter(car_rectangle, Geometry.toDegrees(c.getAngle()) - 180);  

			g2.setColor(c.getColor());
			Polygon car_polygon = new Polygon();
			for (int i = 0; i < 8; i += 2) {
				car_polygon.addPoint((int) (car_rectangle[i] * zoomMultiplier + changeX), (int) (car_rectangle[i+1]* zoomMultiplier + changeY)); 
			}
			g2.fill(car_polygon);
		}

		// draws intersection tooltip
		g2.setColor(Color.red);
		if (drawRed != null) {
			String text = "X: " + drawRed.getXCoord() + " Y: " + drawRed.getYCoord() + "\n" + "Connects "
					+ drawRed.getConnections().size() + " roads.";

			g2.fillOval(
					(int) ((drawRed.getXCoord() - (maxIntersectionSize / 2) - 3) * zoomMultiplier + changeX
							- (indicator_size / 4 * zoomMultiplier)),
					(int) ((drawRed.getYCoord() - (maxIntersectionSize / 2) - 3) * zoomMultiplier + changeY
							- (indicator_size / 4 * zoomMultiplier)),
					(int) ((indicator_size) * zoomMultiplier), (int) ((indicator_size) * zoomMultiplier));

			drawToolTip(g2, text, drawRed.getXCoord(), drawRed.getYCoord());
		}
	}

	// draws tooltip
	public void drawToolTip(Graphics2D graphics, String text, int x, int y) {
		int i = 0;
		if (i == 0) {
			i = 1;
		}
		String[] list = text.split("\n");
		int max = 0;
		for (String s : list) {
			if (s.length() > max) {
				max = s.length();
			}
		}

		i = list.length;
		int lastX = x + (int) (maxIntersectionSize / 2);
		int lastY = y - (int) (maxIntersectionSize / 2) + 10;

		RoundRectangle2D r = new RoundRectangle2D.Float((int) (lastX * zoomMultiplier + changeX),
				(int) (lastY * zoomMultiplier + changeY), max * 8, i * 16, 10, 10);
		graphics.fill(r);
		graphics.draw(r);
		graphics.setColor(Color.WHITE);

		// padding
		lastX = lastX + 10;
		lastY = lastY + 15;
		for (String s : list) {
			graphics.drawString(s, (int) (lastX * zoomMultiplier + changeX), (int) (lastY * zoomMultiplier + changeY));
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
