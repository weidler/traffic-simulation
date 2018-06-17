package graphical_interface;

import java.awt.*;

import javax.swing.JPanel;

import core.Simulation;
import geometry.Point;
import util.Time;

public class InfoPanel extends JPanel {

	Simulation sim;
	private Font large_front;
	private Font small_font;
	
	public InfoPanel(Simulation sim) {
		this.sim = sim;
		
		this.large_front = new Font("TimesRoman", Font.PLAIN, 32);
		this.small_font = new Font("TimesRoman", Font.PLAIN, 12);
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(Color.WHITE);
        
        String time_string = Time.toSixtyMinuteFormat(Time.secondsToHours(sim.getRealisticTime()));
        String day_string = "Day " + Integer.toString(sim.getCurrentDay() + 1) + " of " +  sim.getExperimentWrapper().currentExperiment().getSimulationLengthInDays() + ", " + sim.getNumbCars() + " cars";
        
        int y_diff = 10;
        
        g.setFont(this.large_front);
        FontMetrics fm = g.getFontMetrics();
        Point time_coords = getCenteredTextCoords(time_string, fm);
        g.drawString(time_string, (int) time_coords.x, (int) time_coords.y - g.getFontMetrics(this.small_font).getHeight() / 2);
        
        g.setFont(this.small_font);
        fm = g.getFontMetrics();
        Point day_coords = getCenteredTextCoords(day_string, fm);
        g.drawString(day_string, (int) day_coords.x, (int) day_coords.y + fm.getHeight() / 2 + y_diff);
        
        //g.dispose();
    }
	
	private Point getCenteredTextCoords(String text, FontMetrics fm) {
		Point p = new Point(
			(getWidth() - fm.stringWidth(text)) / 2,
			(getHeight() - fm.getHeight()) / 2 + fm.getAscent()	
		);
		
		return p;
	}

}
