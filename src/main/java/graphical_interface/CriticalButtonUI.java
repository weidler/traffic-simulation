package graphical_interface;

import java.awt.Color;

public class CriticalButtonUI extends DefaultButtonUI {

	public CriticalButtonUI() {
		super();
	}
	
	protected void setParameterDefaults() {
    	this.hoverColor   = Color.decode("#cc5757");
        this.normalColor  = Color.decode("#ad5459");
        this.pressedColor = Color.decode("#ad5459");
	}

}
