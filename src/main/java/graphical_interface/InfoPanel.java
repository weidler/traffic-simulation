package graphical_interface;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import core.Simulation;
import datatype.Point;
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
        
        g.setColor(Color.WHITE);
        
        String time_string = Time.toSixtyMinuteFormat(Time.secondsToHours(sim.getRealisticTime()));
        String day_string = "Day " + Integer.toString(sim.getCurrentDay()) + ", " + sim.getNumbCars() + " cars";
        String performance_string = "Current Performance : " + (Math.round(sim.getRealTimeUtilization() * 1000) / 1000);
        
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
