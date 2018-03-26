package datastructures;

public class Connection {

	private Road road;
	private Intersection destination;
	private TrafficLight trafficlight;
	
	public Connection(Road road, Intersection destination, TrafficLight trafficlight) {
		this.road = road;
		this.destination = destination;
		
		if (trafficlight == null) {
			this.trafficlight = new TrafficLight(road, destination);
		} else {
			this.trafficlight = trafficlight;
		}
	}

	// GETTER / SETTER
	
	public Road getRoad() {
		return road;
		
	}

	public void setRoad(Road road) {
		this.road = road;
	}

	public Intersection getDestination() {
		return destination;
	}

	public void setDestination(Intersection destination) {
		this.destination = destination;
	}

	public TrafficLight getTrafficlight() {
		return trafficlight;
	}

	public void setTrafficlight(TrafficLight trafficlight) {
		this.trafficlight = trafficlight;
	}

	
	@Override
	public String toString() {
		return "Connection [road=" + road + ", destination=" + destination + ", trafficlight=" + trafficlight + "]";
	}
}
