package graphical_interface;

public class Simulation {

	private boolean run = false;
	public Simulation()
	{
		
		
	}
	
	public void start()
	{
		run = true;
		System.out.println("start");
	}
	public void stop()
	{
		run = false;
		System.out.println("stop");
	}
}
