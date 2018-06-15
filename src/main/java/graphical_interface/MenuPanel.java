package graphical_interface;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

import geometry.Point;

public class MenuPanel extends JPanel {
	private Font large_font;
	private Font small_font;
	private int title_height;
	
	public MenuPanel(int title_height) {
		this.title_height = title_height;
		this.large_font = new Font("Calibri", Font.BOLD, 18);
		this.small_font = new Font("TimesRoman", Font.PLAIN, 12);
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        String title1 = "Traffic";
        String title2 = "Simulation";

        g.setFont(large_font);
        FontMetrics fm = g.getFontMetrics();
        Point title1_coords = getCenteredTextCoords(title1, fm);
        Point title2_coords = getCenteredTextCoords(title2, fm);
                
        g.drawString(title1, (int) title1_coords.x, (int) title1_coords.y - fm.getHeight()/2);
        g.drawString(title2, (int) title2_coords.x, (int) title2_coords.y + fm.getHeight()/2);
    }
	
	private Point getCenteredTextCoords(String text, FontMetrics fm) {
		Point p = new Point(
			(getWidth() - fm.stringWidth(text)) / 2,
			(title_height - fm.getHeight()) / 2 + fm.getAscent()	
		);
		
		return p;
	}

}
