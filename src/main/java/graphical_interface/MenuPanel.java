package graphical_interface;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
	public MenuPanel(LayoutManager layoutManager) {
		super(layoutManager);
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
}
