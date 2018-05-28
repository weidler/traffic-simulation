package graphical_interface;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import core.Simulation;
import util.Time;

public class InfoPanel extends JPanel {

	Simulation sim;
	
	public InfoPanel(Simulation sim) {
		this.sim = sim;
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 32));
        g.drawString(Time.toSixtyMinuteFormat(Time.secondsToHours(sim.getRealisticTime())), this.getX() + (this.getWidth()/2), 25);
    }

}
