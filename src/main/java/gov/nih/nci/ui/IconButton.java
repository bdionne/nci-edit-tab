package gov.nih.nci.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.client.diff.ui.GuiUtils;

public class IconButton extends JButton {
	
	Image image;
	BufferedImage icon = null;
	
	private String type;

	IconButton(String type, String iconfilename, String tooltiptext, ActionListener  listener) {
            //super();
		this.type = type;
    	
        ClassLoader classLoader = IconButton.class.getClassLoader();
        try {
            icon = ImageIO.read(checkNotNull(classLoader.getResource(iconfilename)));
        } catch (IOException e) {
            ErrorLogPanel.showErrorDialog(e);
        }
        
        image = icon.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    	
    	//ImageIcon icon = new ImageIcon("EditIconSmall.png");
           // image = icon.getImage();
            //imageObserver = icon.getImageObserver();
            setPreferredSize(new Dimension(18, 18));
            setIcon( new ImageIcon(icon ));
            setMargin( new Insets(0,0,0,0));
            setIconTextGap(0);
            //setBorderPainted(false);
            //setBorder(null);
            setText(null);
            setToolTipText(tooltiptext);
            addActionListener(listener);
        }
	
	public String getType(){
		
		return type;
	}

}
