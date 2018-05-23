package algorithms;

import java.util.ArrayList;

import datastructures.Intersection;
import datastructures.StreetMap;

public final class AStar {

	private AStar() {
	}

	public static ArrayList<Intersection> createPath(Intersection start, Intersection end, StreetMap streetmap) {
		ArrayList<Intersection> openList = new ArrayList<>();
		ArrayList<Intersection> closedList = new ArrayList<>();

		closedList.add(start);
		start.setParent(start);

		start.setG(0);
		start.setH(Math.sqrt(
				Math.pow(start.getXCoord() - end.getXCoord(), 2) + Math.pow(start.getYCoord() - end.getYCoord(), 2)));

		boolean foundTarget = false;

		while (!foundTarget) {
			Intersection currentParent = null;
			currentParent = closedList.get(closedList.size() - 1);

			for (int i = 0; i < currentParent.getConnections().size(); i++) {
				Intersection currentConnected = currentParent.getConnections().get(i).getDestination();
				if (!closedList.contains(currentConnected)) {
					double g = currentParent.getConnections().get(i).getRoad().getLength() + currentParent.getG();
					double h = Math.sqrt(Math.pow(currentConnected.getXCoord() - end.getXCoord(), 2)
							+ Math.pow(currentConnected.getYCoord() - end.getYCoord(), 2));
					double distance = h + g;

					openList.add(currentConnected);

					if (currentConnected.getParent() == null || currentConnected.getG() > g) {
						currentConnected.setParent(currentParent);
						currentConnected.setCost(distance);
						currentConnected.setG(g);
						currentConnected.setH(h);
					}
				}
			}
			Intersection lowestOpen = null;

			for (int i = 0; i < openList.size() - 1; i++) {
				if (lowestOpen == null || lowestOpen.getCost() > openList.get(i).getCost()) {
					lowestOpen = openList.get(i);
				}
			}
			if (openList.size() == 1) lowestOpen = openList.get(0);

			if (!(lowestOpen == null)) {
				closedList.add(lowestOpen);
				openList.remove(lowestOpen);
			}

			if (!(lowestOpen == null) && lowestOpen.equals(end)) {
				foundTarget = true;
			}
		}

		ArrayList<Intersection> path = new ArrayList<Intersection>();
		path.add(end);
		while (!(path.get(path.size() - 1).getParent() == path.get(path.size() - 1))) {
			path.add(path.get(path.size() - 1).getParent());
		}

		for (Intersection intersection : streetmap.getIntersections()) {
			intersection.resetParent();
			intersection.resetCost();
		}

		ArrayList<Intersection> path2 = new ArrayList<Intersection>();
		while (!path.isEmpty()) {
			path2.add(path.get(path.size() - 1));
			path.remove(path.get(path.size() - 1));
		}
		return path2;
	}
}
