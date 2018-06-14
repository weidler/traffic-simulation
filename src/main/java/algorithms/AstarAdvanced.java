package algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import car.Car;
import datastructures.Intersection;
import datastructures.StreetMap;
import road.Road;

public final class AstarAdvanced {

	
	
	private static double weightValueEmpirical = 3;	

	/*public static void setWeightValue(double multplier)
	{
		weightValuePoisson = weightValuePoisson * multplier;
	}
	public static double getWeightValue()
	{
		return weightValuePoisson;
	}*/
	private AstarAdvanced() {
		// for ml
		//Random r = new Random();

		//weightValueEmpirical = r.nextInt(1-0) + 0;

	}

	public static ArrayList<Intersection> createPath(Intersection start, Intersection end, StreetMap streetmap, HashMap<Road, ArrayList<Car>> carList, String schedule) {
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
				Road r = streetmap.getRoadByCoordinates(currentParent.getXCoord(), currentParent.getYCoord(), currentConnected.getXCoord(), currentConnected.getYCoord());
				boolean oke = true;
				if(r.isOneWay())
				{
					if(r.getDirection() != currentConnected)
					{
						oke = false;
					}
				}
				if(oke)
				{
					if (!closedList.contains(currentConnected)) {
						double g = currentParent.getConnections().get(i).getRoad().getLength() + currentParent.getG();
						double h = Math.sqrt(Math.pow(currentConnected.getXCoord() - end.getXCoord(), 2)
								+ Math.pow(currentConnected.getYCoord() - end.getYCoord(), 2));
						double distribution = (carList.get(r).size() * 8)/r.getLength();
						System.out.println("carlist: "+carList.get(r).size() *8+ " length: "+ r.getLength());
						double d = distribution * weightValueEmpirical;
						double s = streetmap.getRoads().get(i).getAverageSpeed();						
						int curRoadCount = 0;						
						int carCount = 0;							
						Road curRoad = r;
						for(Car car : carList.get(curRoad))
						{ 
							Intersection target = car.getCurrentDestinationIntersection();
							
							if(target == currentConnected && target != null)
							{
								
								carCount++;
							}
						}							
						if(r == curRoad)
						{
							curRoadCount = carCount;
						}
						if(carCount == 0)
						{
							s = r.getAllowedMaxSpeed();
						}
						double distance = h + g + d - s;
						if (distance < 0)
							distance = 0;
	
						openList.add(currentConnected);
						System.out.println("s: "+s+ " d: "+ d);
						if (currentConnected.getParent() == null || currentConnected.getG() > g) {
							currentConnected.setParent(currentParent);
							currentConnected.setCost(distance);
							currentConnected.setG(g);
							currentConnected.setH(h);
						}
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
