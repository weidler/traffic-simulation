package graphical_interface;

import java.awt.Color;

public class ImportantButtonUI extends DefaultButtonUI {
	
	public ImportantButtonUI() {
		super();
	}

    protected void setParameterDefaults() {
    	this.hoverColor   = Color.decode("#53b262");
        this.normalColor  = Color.decode("#59af67");
        this.pressedColor = Color.decode("#59af67");
	}


}
