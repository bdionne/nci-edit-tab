package gov.nih.nci.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;

public class MListIconButton extends MListButton {
	
	public static final Color ROLL_OVER_COLOR = new Color(0, 0, 0);
	private IconButton iconButton;
	
	public MListIconButton(String type, String iconfilename, String tooltiptext, ActionListener  listener) {
        //super("Add", ROLL_OVER_COLOR, actionListener);
		super(type, ROLL_OVER_COLOR, listener);
        iconButton = new IconButton(type, iconfilename, tooltiptext, listener);
    }
	
	public void paintButtonContent(Graphics2D g) {
		if(iconButton != null) {
			//Image iconImage = iconButton.image;
			int w = getBounds().width;
	        int h = getBounds().height;
	        int x = getBounds().x;
	        int y = getBounds().y;

	        //Font font = g.getFont().deriveFont(Font.BOLD, OWLRendererPreferences.getInstance().getFontSize());
	        //g.setFont(font);
	        //FontMetrics fontMetrics = g.getFontMetrics(font);
	        //final Rectangle stringBounds = fontMetrics.getStringBounds(iconImage, g).getBounds();
	        //int baseline = fontMetrics.getLeading() + fontMetrics.getAscent();
	        //g.drawString(ANNOTATE_STRING, x + w / 2 - stringBounds.width / 2, y + (h - stringBounds.height) / 2 + baseline );
			g.drawImage(iconButton.image, x + w / 2 - 10, y + (h - 20) / 2, null);
			
		}
	}

}
