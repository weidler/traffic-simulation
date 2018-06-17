package buttons;

import javax.swing.*;
import java.awt.*;

public class MenuButton extends JButton {

	public MenuButton() {
	}

	public MenuButton(Icon icon) {
		super(icon);
	}

	public MenuButton(String s) {
		super(s);
	}

	public MenuButton(Action action) {
		super(action);
	}

	public MenuButton(String s, Icon icon) {
		super(s, icon);
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	}
}
