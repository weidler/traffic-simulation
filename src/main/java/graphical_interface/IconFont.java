package graphical_interface;

import javax.swing.*;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class IconFont {

	public static Font getFontAwesome() {
		Font font = null;
		String fName = "fa.otf";
		try {
			InputStream is = IconFont.class.getResourceAsStream(fName);
			font = Font.createFont(Font.TRUETYPE_FONT, is);
			font = font.deriveFont(Font.PLAIN, 14);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Font Awesome not loaded.");
			font = new Font("serif", Font.PLAIN, 24);
		}

		return font;
	}
}