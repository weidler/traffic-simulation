package datastructures;

public class TrafficLight {
	private String status;
	private int[] cycling_structure;

	public TrafficLight(int[] cycling_structure) {
		this.status = "R";
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		if (status != "R" && status != "G") {
			System.out.println("Illegal status '" + status + "'");
		}
		this.status = status;
	}
	
	public int[] getCyclingStructure() {
		return cycling_structure;
	}
	
	public void setCyclingStructure(int[] cycling_structure) {
		this.cycling_structure = cycling_structure;
	}
}
