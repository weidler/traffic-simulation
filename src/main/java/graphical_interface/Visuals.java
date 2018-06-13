package graphical_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import car.Car;
import core.Simulation;
import datastructures.Intersection;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import datatype.Line;
import road.Road;
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

		// Containers for stuff to be drawn
		HashMap<RoadType, ArrayList<Polygon>> road_asphalts = new HashMap<RoadType, ArrayList<Polygon>>();
		for (RoadType type : RoadType.values()) road_asphalts.put(type, new ArrayList<Polygon>());
		ArrayList<Line2D> outer_lines = new ArrayList<Line2D>();
		ArrayList<Line2D> lane_separation_line = new ArrayList<Line2D>();
		HashMap<Color, ArrayList<Line2D>> tl_lines = new HashMap<Color, ArrayList<Line2D>>();
		tl_lines.put(Color.RED, new ArrayList<Line2D>());
		tl_lines.put(Color.GREEN, new ArrayList<Line2D>());
		HashMap<Road, Line2D> mid_lines = new HashMap<Road, Line2D>();

		// calculate drawing positions
		for (int i = 0; i < roads.size(); i++) {
			Road current_road = roads.get(i);
			Intersection[] connected_intersections = current_road.getIntersections();
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
			Point to_right = new Point(current_road.getX1() - outer_offset_x, current_road.getY1() + outer_offset_y);
			Point from_right = new Point(current_road.getX2() - outer_offset_x, current_road.getY2() + outer_offset_y);
			Line outer_line_right = new Line(to_right, from_right);

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
				Point line_intersection_right_from;
				if (!Geometry.liesLeft(ncr_origin_x, ncr_origin_y, intersection_from.getXCoord(),
						intersection_from.getYCoord(), // is clockwise road option a to the right?
						intersection_from.getXCoord() - (precalculated_offsets.get(next_clockwise_road_from)[0]
								* next_clockwise_road_from.getLanes()),
						intersection_from.getYCoord() + (precalculated_offsets.get(next_clockwise_road_from)[1]
								* next_clockwise_road_from.getLanes()))) {

					line_intersection_right_from = Geometry.intersection(new Line(from_x_right, from_y_right, to_x_right,
							to_y_right),
							new Line(ncr_origin_x - (precalculated_offsets.get(next_clockwise_road_from)[0]
									* next_clockwise_road_from.getLanes()),
							ncr_origin_y + (precalculated_offsets.get(next_clockwise_road_from)[1]
									* next_clockwise_road_from.getLanes()),
							intersection_from.getXCoord() - (precalculated_offsets.get(next_clockwise_road_from)[0]
									* next_clockwise_road_from.getLanes()),
							intersection_from.getYCoord() + (precalculated_offsets.get(next_clockwise_road_from)[1]
									* next_clockwise_road_from.getLanes())));
				} else {
					line_intersection_right_from = Geometry.intersection(new Line(from_x_right, from_y_right, to_x_right,
							to_y_right),
							new Line(ncr_origin_x + (precalculated_offsets.get(next_clockwise_road_from)[0]
									* next_clockwise_road_from.getLanes()),
							ncr_origin_y - (precalculated_offsets.get(next_clockwise_road_from)[1]
									* next_clockwise_road_from.getLanes()),
							intersection_from.getXCoord() + (precalculated_offsets.get(next_clockwise_road_from)[0]
									* next_clockwise_road_from.getLanes()),
							intersection_from.getYCoord() - (precalculated_offsets.get(next_clockwise_road_from)[1]
									* next_clockwise_road_from.getLanes())));
				}

				// left line needs to cross counterclockwise right
				Point line_intersection_left_from;
				if (Geometry.liesLeft(nccr_origin_x, nccr_origin_y, intersection_from.getXCoord(),
						intersection_from.getYCoord(),
						intersection_from.getXCoord() + (precalculated_offsets.get(next_counterclockwise_road_from)[0]
								* next_counterclockwise_road_from.getLanes()),
						intersection_from.getYCoord() - (precalculated_offsets.get(next_counterclockwise_road_from)[1]
								* next_counterclockwise_road_from.getLanes()))) {

					line_intersection_left_from = Geometry.intersection(new Line(from_x_left, from_y_left, to_x_left, to_y_left),
							new Line(nccr_origin_x + (precalculated_offsets.get(next_counterclockwise_road_from)[0]
									* next_counterclockwise_road_from.getLanes()),
							nccr_origin_y - (precalculated_offsets.get(next_counterclockwise_road_from)[1]
									* next_counterclockwise_road_from.getLanes()),
							intersection_from.getXCoord()
									+ (precalculated_offsets.get(next_counterclockwise_road_from)[0]
											* next_counterclockwise_road_from.getLanes()),
							intersection_from.getYCoord()
									- (precalculated_offsets.get(next_counterclockwise_road_from)[1]
											* next_counterclockwise_road_from.getLanes())));
				} else {
					line_intersection_left_from = Geometry.intersection(new Line(from_x_left, from_y_left, to_x_left, to_y_left),
							new Line(nccr_origin_x - (precalculated_offsets.get(next_counterclockwise_road_from)[0]
									* next_counterclockwise_road_from.getLanes()),
							nccr_origin_y + (precalculated_offsets.get(next_counterclockwise_road_from)[1]
									* next_counterclockwise_road_from.getLanes()),
							intersection_from.getXCoord()
									- (precalculated_offsets.get(next_counterclockwise_road_from)[0]
											* next_counterclockwise_road_from.getLanes()),
							intersection_from.getYCoord()
									+ (precalculated_offsets.get(next_counterclockwise_road_from)[1]
											* next_counterclockwise_road_from.getLanes())));
				}

				from_x_right = line_intersection_right_from.x;
				from_y_right = line_intersection_right_from.y;

				from_x_left = line_intersection_left_from.x;
				from_y_left = line_intersection_left_from.y;

				chosen_line_intersections.get(intersection_from).add(line_intersection_left_from);
				chosen_line_intersections.get(intersection_from).add(line_intersection_right_from);
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
				Point line_intersection_right_to;
				if (Geometry.liesLeft(nccr_origin_x, nccr_origin_y, intersection_to.getXCoord(),
						intersection_to.getYCoord(),
						intersection_to.getXCoord() - (precalculated_offsets.get(next_counterclockwise_road_to)[0]
								* next_counterclockwise_road_to.getLanes()),
						intersection_to.getYCoord() + (precalculated_offsets.get(next_counterclockwise_road_to)[1]
								* next_counterclockwise_road_to.getLanes()))) {

					line_intersection_right_to = Geometry.intersection(new Line(from_x_right, from_y_right, to_x_right,
							to_y_right),
							new Line(nccr_origin_x - (precalculated_offsets.get(next_counterclockwise_road_to)[0]
									* next_counterclockwise_road_to.getLanes()),
							nccr_origin_y + (precalculated_offsets.get(next_counterclockwise_road_to)[1]
									* next_counterclockwise_road_to.getLanes()),
							intersection_to.getXCoord() - (precalculated_offsets.get(next_counterclockwise_road_to)[0]
									* next_counterclockwise_road_to.getLanes()),
							intersection_to.getYCoord() + (precalculated_offsets.get(next_counterclockwise_road_to)[1]
									* next_counterclockwise_road_to.getLanes())));
				} else {
					line_intersection_right_to = Geometry.intersection(new Line(from_x_right, from_y_right, to_x_right,
							to_y_right),
							new Line(nccr_origin_x + (precalculated_offsets.get(next_counterclockwise_road_to)[0]
									* next_counterclockwise_road_to.getLanes()),
							nccr_origin_y - (precalculated_offsets.get(next_counterclockwise_road_to)[1]
									* next_counterclockwise_road_to.getLanes()),
							intersection_to.getXCoord() + (precalculated_offsets.get(next_counterclockwise_road_to)[0]
									* next_counterclockwise_road_to.getLanes()),
							intersection_to.getYCoord() - (precalculated_offsets.get(next_counterclockwise_road_to)[1]
									* next_counterclockwise_road_to.getLanes())));
				}

				// left line needs to cross clockwise right
				Point line_intersection_left_to;
				if (!Geometry.liesLeft(ncr_origin_x, ncr_origin_y, intersection_to.getXCoord(),
						intersection_to.getYCoord(),
						intersection_to.getXCoord() + (precalculated_offsets.get(next_clockwise_road_to)[0]
								* next_clockwise_road_to.getLanes()),
						intersection_to.getYCoord() - (precalculated_offsets.get(next_clockwise_road_to)[1]
								* next_clockwise_road_to.getLanes()))) {

					line_intersection_left_to = Geometry.intersection(new Line(from_x_left, from_y_left, to_x_left, to_y_left),
							new Line(ncr_origin_x + (precalculated_offsets.get(next_clockwise_road_to)[0]
									* next_clockwise_road_to.getLanes()),
							ncr_origin_y - (precalculated_offsets.get(next_clockwise_road_to)[1]
									* next_clockwise_road_to.getLanes()),
							intersection_to.getXCoord() + (precalculated_offsets.get(next_clockwise_road_to)[0]
									* next_clockwise_road_to.getLanes()),
							intersection_to.getYCoord() - (precalculated_offsets.get(next_clockwise_road_to)[1]
									* next_clockwise_road_to.getLanes())));
				} else {
					line_intersection_left_to = Geometry.intersection(new Line(from_x_left, from_y_left, to_x_left, to_y_left),
							new Line(ncr_origin_x - (precalculated_offsets.get(next_clockwise_road_to)[0]
									* next_clockwise_road_to.getLanes()),
							ncr_origin_y + (precalculated_offsets.get(next_clockwise_road_to)[1]
									* next_clockwise_road_to.getLanes()),
							intersection_to.getXCoord() - (precalculated_offsets.get(next_clockwise_road_to)[0]
									* next_clockwise_road_to.getLanes()),
							intersection_to.getYCoord() + (precalculated_offsets.get(next_clockwise_road_to)[1]
									* next_clockwise_road_to.getLanes())));
				}

				to_x_right = line_intersection_right_to.x;
				to_y_right = line_intersection_right_to.y;

				to_x_left = line_intersection_left_to.x;
				to_y_left = line_intersection_left_to.y;

				chosen_line_intersections.get(intersection_to).add(line_intersection_left_to);
				chosen_line_intersections.get(intersection_to).add(line_intersection_right_to);
			}

			// natural ends of lanes
			Line midline = new Line(current_road.getPointA(), current_road.getPointB());
			Point projected_to_left = new Point(to_x_left, to_y_left).projectOn(midline);
			Point projected_to_right = new Point(to_x_right, to_y_right).projectOn(midline);
			Point closer_to = new Point(to_x_left, to_y_left);
			Point closer_to_projected = projected_to_left;
			if (new Line(projected_to_right, midline.B).length() < new Line(projected_to_left, midline.B).length()) {
				closer_to = new Point(to_x_right, to_y_right);
				closer_to_projected = projected_to_right;
			}

			Point projected_from_left = new Point(from_x_left, from_y_left).projectOn(midline);
			Point projected_from_right = new Point(from_x_right, from_y_right).projectOn(midline);
			Point closer_from = new Point(from_x_left, from_y_left);
			Point closer_from_projected = projected_from_left;
			if (new Line(projected_from_right, midline.A).length() < new Line(projected_from_left, midline.A).length()) {
				closer_from = new Point(from_x_right, from_y_right);
				closer_from_projected = projected_from_right;
			}

			Line end_of_road_to = new Line(closer_to, closer_to_projected);
			Line end_of_road_from = new Line(closer_from, closer_from_projected);

			// road background polygons
			Polygon bg_polygon = new Polygon();
			bg_polygon.addPoint((int) (to_x_right * zoomMultiplier + changeX), (int) (to_y_right * zoomMultiplier + changeY));
			bg_polygon.addPoint((int) (from_x_right * zoomMultiplier + changeX), (int) (from_y_right * zoomMultiplier + changeY));
			bg_polygon.addPoint((int) (from_x_left * zoomMultiplier + changeX), (int) (from_y_left * zoomMultiplier + changeY));
			bg_polygon.addPoint((int) (to_x_left * zoomMultiplier + changeX), (int) (to_y_left * zoomMultiplier + changeY));
			road_asphalts.get(current_road.getRoadType()).add(bg_polygon);

			// road outer lines
			outer_lines.add(new Line2D.Double((int) (to_x_right) * zoomMultiplier + changeX,
					(int) (to_y_right) * zoomMultiplier + changeY, (int) (from_x_right) * zoomMultiplier + changeX,
					(int) (from_y_right) * zoomMultiplier + changeY));
			outer_lines.add(new Line2D.Double((int) (to_x_left) * zoomMultiplier + changeX,
					(int) (to_y_left) * zoomMultiplier + changeY, (int) (from_x_left) * zoomMultiplier + changeX,
					(int) (from_y_left) * zoomMultiplier + changeY));
			
			// mid line
			if (!current_road.isOneWay()) {
				Line mid_line = new Line(current_road.getPointA(), current_road.getPointB());
				if ((intersection_from.getConnections().size() > 2) && !(intersection_to.getConnections().size() > 2)) {
					mid_line = new Line(mid_line.intersectionWith(end_of_road_from), mid_line.A);
				} else if (!(intersection_from.getConnections().size() > 2) && (intersection_to.getConnections().size() > 2)) {
					mid_line = new Line(mid_line.B, mid_line.intersectionWith(end_of_road_to));
				} else if ((intersection_from.getConnections().size() > 2) && (intersection_to.getConnections().size() > 2)) {
					mid_line = new Line(mid_line.intersectionWith(end_of_road_from), mid_line.intersectionWith(end_of_road_to));
				}

				mid_lines.put(current_road, new Line2D.Double((int) mid_line.A.x * zoomMultiplier + changeX,
						(int) mid_line.A.y * zoomMultiplier + changeY,
						(int) mid_line.B.x * zoomMultiplier + changeX,
						(int) mid_line.B.y * zoomMultiplier + changeY));
			}

			// Lanes
			for (int j = 1; j <= current_road.getLanes(); j++) {
				double lane_offset_x = offset_x * (j - 1);
				double lane_offset_y = offset_y * (j - 1);
				
				TrafficLight tl_from = intersection_from.getTrafficLightsApproachingFrom(intersection_to, j);
				TrafficLight tl_to = intersection_to.getTrafficLightsApproachingFrom(intersection_from, j);

				Point tl_position_to = closer_from_projected;
				Point tl_position_from = closer_to_projected;

				Color col = Color.RED;
				if (tl_from.getStatus().equals("G")) {
					col = Color.GREEN;
				}

				if (intersection_from.isAt(current_road.getX1(), current_road.getY1())) {
					tl_lines.get(col).add(new Line2D.Double(
							(int) ((tl_position_from.x + (offset_x + lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_from.y - (offset_y + lane_offset_y)) * zoomMultiplier + changeY),
							(int) ((tl_position_from.x + (lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_from.y - (lane_offset_y)) * zoomMultiplier + changeY)));
				} else {
					tl_lines.get(col).add(new Line2D.Double(
							(int) ((tl_position_to.x + (offset_x + lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_to.y - (offset_y + lane_offset_y)) * zoomMultiplier + changeY),
							(int) ((tl_position_to.x + (lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_to.y - (lane_offset_y)) * zoomMultiplier + changeY)));
				}

				col = Color.RED;
				if (tl_to.getStatus().equals("G")) {
					col = Color.GREEN;
				}

				if (intersection_to.isAt(current_road.getX1(), current_road.getY1())) {
					tl_lines.get(col).add(new Line2D.Double(
							(int) ((tl_position_from.x - (offset_x + lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_from.y + (offset_y + lane_offset_y)) * zoomMultiplier + changeY),
							(int) ((tl_position_from.x - (lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_from.y + (lane_offset_y)) * zoomMultiplier + changeY)));
				} else {
					tl_lines.get(col).add(new Line2D.Double(
							(int) ((tl_position_to.x - (offset_x + lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_to.y + (offset_y + lane_offset_y)) * zoomMultiplier + changeY),
							(int) ((tl_position_to.x - (lane_offset_x)) * zoomMultiplier + changeX),
							(int) ((tl_position_to.y + (lane_offset_y)) * zoomMultiplier + changeY)));
				}

				// lane lines
				if (j > 1) {
					Point point_a = new Point((current_road.getX1() - lane_offset_x), (current_road.getY1() + lane_offset_y));
					Point point_b = new Point((current_road.getX2() - lane_offset_x), (current_road.getY2() + lane_offset_y));

					Point new_point_a = (new Line(point_a, point_b)).intersectionWith(end_of_road_to);
					Point new_point_b = (new Line(point_a, point_b)).intersectionWith(end_of_road_from);

					lane_separation_line.add(new Line2D.Double(
							 (int) new_point_a.x * zoomMultiplier + changeX,
							(int) new_point_a.y * zoomMultiplier + changeY,
							(int) new_point_b.x * zoomMultiplier + changeX,
							(int) new_point_b.y * zoomMultiplier + changeY));

					point_a = new Point((current_road.getX1() + lane_offset_x), (current_road.getY1() - lane_offset_y));
					point_b = new Point((current_road.getX2() + lane_offset_x), (current_road.getY2() - lane_offset_y));

					new_point_a = (new Line(point_a, point_b)).intersectionWith(end_of_road_from);
					new_point_b = (new Line(point_a, point_b)).intersectionWith(end_of_road_to);

					lane_separation_line.add(new Line2D.Double(
							(int) new_point_a.x * zoomMultiplier + changeX,
							(int) new_point_a.y * zoomMultiplier + changeY,
							(int) new_point_b.x * zoomMultiplier + changeX,
							(int) new_point_b.y * zoomMultiplier + changeY));
				}
			}

			g2.setStroke(defaultStroke);
			intersectionSize = 30;
		}

		// Draw asphalt
		for (RoadType type : road_asphalts.keySet()) {
			if (type == RoadType.ROAD) g2.setColor(Color.decode(road_color));
			else if (type == RoadType.DIRT_ROAD) g2.setColor(Color.decode(dirtroad_color));
			else if (type == RoadType.HIGHWAY) g2.setColor(Color.decode(highway_color));
			for (Polygon pol : road_asphalts.get(type)) {
				g2.fill(pol);
			}
		}

		if (streetMap.intersectionCount() > 1) {
			for (Intersection inter : streetMap.getIntersections()) {
				Polygon intersection_filling = new Polygon();
				if (chosen_line_intersections.containsKey(inter)) {
					for (Point point : Geometry.orderPointsClockwise((chosen_line_intersections.get(inter)), inter.getPoint())) {
						if (!intersection_filling.contains((int) (point.x * zoomMultiplier + changeX), (int) (point.y * zoomMultiplier + changeY))) {
							intersection_filling.addPoint(
									(int) (point.x * zoomMultiplier + changeX),
									(int) (point.y * zoomMultiplier + changeY)
							);
						}
					}

					RoadType type = inter.getMostCommonRoadType();
					if (type == RoadType.ROAD) g2.setColor(Color.decode(road_color));
					else if (type == RoadType.DIRT_ROAD) g2.setColor(Color.decode(dirtroad_color));
					else if (type == RoadType.HIGHWAY) g2.setColor(Color.decode(highway_color));
					g2.fill(intersection_filling);

				}
			}
		}

		// Draw Lane lines
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(2));
		for (Road road : mid_lines.keySet()) {
			if (road.getLanes() > 1 && !road.isOneWay()) g2.setStroke(new BasicStroke(2));
			else g2.setStroke(dashed);
			g2.draw(mid_lines.get(road));
		}

		g2.setStroke(dashed);
		for (Line2D line : lane_separation_line) g2.draw(line);

		g2.setStroke(defaultStroke);
		g2.setStroke(new BasicStroke(2));
		for (Line2D line : outer_lines) g2.draw(line);

		// Draw TLs
		double stroke_width = this.laneSize * zoomMultiplier / 2;
		g2.setStroke(new BasicStroke((int) (stroke_width)));
		for (Color col : tl_lines.keySet()) {
			g2.setColor(col);
			for (Line2D line : tl_lines.get(col)) g2.draw(line);
		}


		// draws the cars; Creating a deep copy of the car list in order to prevent 
		// concurrent modification errors occuring because the simulation alters the 
		// list in a different thread. might look hacky, but my research showed this
		// is common practice
		for (ArrayList<Car> cars: simulation.getCars().values()) {
			for (Car c : new ArrayList<Car>(cars)) {
				if (c != null && !c.inTraffic() && !c.reachedDestination()) continue;

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
