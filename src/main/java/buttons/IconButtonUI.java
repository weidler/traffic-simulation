package buttons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class IconButtonUI extends DefaultButtonUI {

	public IconButtonUI() {
		super();
	}

	protected void setParameterDefaults() {
		this.hoverColor   = null;
		this.normalColor  = null;
		this.pressedColor = null;
		btnFontColor = Color.DARK_GRAY;
	}

	public void mouseEntered(MouseEvent e) {
		((JButton) e.getComponent()).setForeground(Color.LIGHT_GRAY);
	}

	public void mouseExited(MouseEvent e) {
		((JButton) e.getComponent()).setForeground(this.btnFontColor);
	}


}
