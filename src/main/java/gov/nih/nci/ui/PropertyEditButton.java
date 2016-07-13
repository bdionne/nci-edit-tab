package gov.nih.nci.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import org.protege.editor.core.ui.list.MListAddButton;

public  class PropertyEditButton extends JButton {
	

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int BUTTON_DIMENSION = 16;
	
	public static final Color DELETE_ROLL_OVER_COLOR = new Color(240, 40, 40);
	public static final Color ADD_ROLL_OVER_COLOR = Color.GREEN.darker();
	public static final Color EDIT_ROLL_OVER_COLOR = new Color(20, 80, 210);
	
	private String name;
	
    private Color rollOverColor = EDIT_ROLL_OVER_COLOR;

    private Rectangle bounds = new Rectangle();

    private Object rowObject;
    
    public boolean mouseDown = false;
    
    //private BufferedImage image_edit;



    
    public PropertyEditButton(String name ) {
        
    	/*try {
            image_edit = ImageIO.read(new File("C:\\app\\git\\metaproject-gui\\protege-client\\target\\edit.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    	this.name = name;
    	setContentAreaFilled(false);
    	setOpaque(false);
        setFocusPainted(false);
       // setIconTextGap(0);
      // setBorderPainted(false);
       // setBorder(null);
        setText(null);
        
        if(name == "Add"){
        	this.rollOverColor = ADD_ROLL_OVER_COLOR;
        }
        
        if(name == "Edit"){
        	this.rollOverColor = EDIT_ROLL_OVER_COLOR;
        }
        
        if(name == "Delete"){
        	this.rollOverColor = DELETE_ROLL_OVER_COLOR;
        }
        
        
        this.setSize(this.getButtonDimension());
        
        MouseListener mouseButtonListener = new MouseAdapter() {
            
			@Override
            public void mousePressed(MouseEvent e) {
				PropertyEditButton.this.mouseDown = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	//PropertyEditButton.this.handleMouseClick(e);
            	PropertyEditButton.this.mouseDown = false;
            }

            @Override
            public void mouseExited(MouseEvent event) {
                // leave the component cleanly
            	PropertyEditButton.this.repaint();
            }
        };
        this.addMouseListener(mouseButtonListener);
        
    }

    public PropertyEditButton(String name, Color rollOverColor) {
        this.name = name;
        this.rollOverColor = rollOverColor;
        this.setSize(this.getButtonDimension());
        
        setContentAreaFilled(false);
        
        
      //  setBorder(null);
        setText(null);
        
        MouseAdapter mouseButtonListener = new MouseAdapter() {
            
			@Override
            public void mousePressed(MouseEvent e) {
				PropertyEditButton.this.mouseDown = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	//PropertyEditButton.this.handleMouseClick(e);
            	PropertyEditButton.this.mouseDown = false;
            }

            @Override
            public void mouseExited(MouseEvent event) {
                // leave the component cleanly
            	PropertyEditButton.this.repaint();
            }
        };
        this.addMouseListener(mouseButtonListener);
        
    }

    protected int getSizeMultiple() {
        return 4;
    }



    public String getName() {
        return name;
    }


    public Object getRowObject() {
        return rowObject;
    }


    public void setRowObject(Object rowObject) {
        this.rowObject = rowObject;
    }


    public Color getRollOverColor() {
        return rollOverColor;
    }

    public int getButtonDimension() {
        Font font = getFont();
        if(font == null) {
            return BUTTON_DIMENSION;
        }
        else {
            FontMetrics fontMetrics = getFontMetrics(font);
            int height = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + fontMetrics.getLeading();
            if(height < 20) {
                height = 20;
            }
            return height;
        }
    }


    @Deprecated
    public void setBounds(Rectangle bounds) {
        this.bounds = new Rectangle(bounds);
    }
    
    public void setLocation(int x, int y) {
        this.bounds.x = x;
        this.bounds.y = y;
    }
    
    public void setSize(int size) {
        int normalisedSize = Math.round(size / getSizeMultiple() * 1.0f) * getSizeMultiple();
        this.bounds.width = normalisedSize;
        this.bounds.height = normalisedSize;
    }


    public Rectangle getBounds() {
        return bounds;
    }

    public Color getBackground() {
        return Color.LIGHT_GRAY;
    //	return Color.RED;
    }

    /**
     * Paints the button content. For convenience, the graphics origin will be
     * the top left corner of the button
     * @param g The graphics which should be used for rendering
     * the content
     */
    public  void paintButtonContent(Graphics2D g){
    	
    }
    
    private Color getButtonColor() {
        Point pt = this.getMousePosition();
        if (pt == null) {
            return getBackground();
        }
        if (getBounds().contains(pt)) {
            if (this.mouseDown) {
               // return Color.DARK_GRAY;
            	return Color.RED;
            }
            else {
                return getRollOverColor();
            }
        }
        return getBackground();
    }
    
   /* public void paintCompi(Graphics2D g) {
    
    	if (name.equals("Add")) {
    		MListAddButton addBtn = new MListAddButton(null);
    		addBtn.paintButtonContent(g);
    	}
    }*/
    //@Override
    protected void paintComponent(Graphics g) {
    	Graphics2D g2 = (Graphics2D)g.create();
    	
      //  super.paintComponent(g);
    	
         g2.setColor(this.getButtonColor());
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);


       
        if(name.equals("Add")){
        	
        	int size = getBounds().height;
            int thickness = (Math.round(size / 8.0f) / 2) * 2;
            
            int x = getBounds().x;
            int y = getBounds().y;

            int insetX = size / 4;
            int insetY = size / 4;
            int insetHeight = size / 2;
            int insetWidth = size / 2;
            g2.fillRect(x + size / 2  - thickness / 2, y + insetY, thickness, insetHeight);
            g2.fillRect(x + insetX, y + size / 2 - thickness / 2, insetWidth, thickness);
        }
        
        if(name.equals("Edit")){
        	Rectangle bounds = getBounds();
            int x = bounds.x;
            int y = bounds.y;
            int size = bounds.width;
            int quarterSize = (Math.round(bounds.width / 4.0f) / 2) * 2;
            g2.fillOval(x + size / 2 - quarterSize, y + size / 2 - quarterSize, 2 * quarterSize, 2 * quarterSize);
            g2.setColor(getBackground());
            g2.fillOval(x + size / 2 - quarterSize / 2, y + size / 2 - quarterSize / 2, quarterSize, quarterSize);
        	
        }
        
        if(name.equals("Delete")){
        	//Graphics2D g = (Graphics2D) gIn.create();
        	//Graphics2D g2 = (Graphics2D)g;
            int size = getBounds().height;
            int thickness = (Math.round(size / 8.0f) / 2) * 2;

            int x = getBounds().x;
            int y = getBounds().y;

            g2.rotate(Math.PI / 4, x + size / 2, y + size / 2);
            
            int insetX = size / 4;
            int insetY = size / 4;
            int insetHeight = size / 2;
            int insetWidth = size / 2;
            g2.fillRect(x + size / 2  - thickness / 2, y + insetY, thickness, insetHeight);
            g2.fillRect(x + insetX, y + size / 2 - thickness / 2, insetWidth, thickness);
        }
       // g.setColor(this.getButtonColor());
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.getButtonDimension(), this.getButtonDimension());
    }

}
