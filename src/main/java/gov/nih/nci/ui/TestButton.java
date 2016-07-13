package gov.nih.nci.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.client.diff.ui.GuiUtils;

public class TestButton extends JButton {
	Image image;
	BufferedImage icon = null;

    TestButton(String iconfilename, String tooltiptext, ActionListener  listener) {
            //super();
    	
        ClassLoader classLoader = GuiUtils.class.getClassLoader();
        try {
            icon = ImageIO.read(checkNotNull(classLoader.getResource(iconfilename)));
        } catch (IOException e) {
            ErrorLogPanel.showErrorDialog(e);
        }
        image = icon.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    	
    	//ImageIcon icon = new ImageIcon("EditIconSmall.png");
           // image = icon.getImage();
            //imageObserver = icon.getImageObserver();
            setPreferredSize(new Dimension(20, 20));
            setIcon( new ImageIcon(icon ));
            setMargin( new Insets(0,0,0,0));
            setIconTextGap(0);
            //setBorderPainted(false);
            //setBorder(null);
            setText(null);
            setToolTipText(tooltiptext);
            
            this.addActionListener(listener);
        }
    
    public static JPanel createEditPanel(){
    	
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	
    	JTextArea area = new JTextArea("test string");
    	area.setPreferredSize(new Dimension(200, 100));
    	
    	JPanel areaPanel = new JPanel(new BorderLayout());
    	
    	areaPanel.add(new JLabel("Term"), BorderLayout.NORTH);
    	areaPanel.add(new JScrollPane(area), BorderLayout.CENTER);
    	areaPanel.setPreferredSize(new Dimension(200, 100));
    	
    	JPanel panel1 = new JPanel(new BorderLayout());
    	JPanel panel2 = new JPanel(new BorderLayout());
    	JPanel panel3 = new JPanel(new BorderLayout());
    	
    	
    	
    	JTextField textfield1 = new JTextField();
    	textfield1.setPreferredSize(new Dimension(50, 20));
    	
    	JTextField textfield2 = new JTextField();
    	textfield2.setPreferredSize(new Dimension(50, 20));
    	
    	JComboBox<String> combobox = new JComboBox<String>(new String[]{"val1", "val2", "val3"});   	
    	combobox.setPreferredSize(new Dimension(50, 20));
    	
    	JLabel label1 = new JLabel("Prop1");  	   	
    	label1.setPreferredSize(new Dimension(50, 20));
    	
    	JLabel label2 = new JLabel("Prop2");  	   	
    	label2.setPreferredSize(new Dimension(50, 20));
    	
    	JLabel label3 = new JLabel("Prop3");  	   	
    	label3.setPreferredSize(new Dimension(50, 20));
    	
    	panel1.add(label1, BorderLayout.WEST);
    	panel1.add(textfield1, BorderLayout.EAST);
    	
    	panel2.add(label2, BorderLayout.WEST);
    	panel2.add(textfield2, BorderLayout.EAST);
    	
    	panel3.add(label3, BorderLayout.WEST);
    	panel3.add(combobox, BorderLayout.EAST);
    	
    	panel1.setSize(200, 25);
    	panel2.setSize(200, 25);
    	panel3.setSize(200, 25);
    	
    	
    	
    	panel.add(areaPanel);
    	panel.add(panel1);
    	panel.add(panel2);
    	panel.add(panel3);
    	
    	//panel.setSize(200, areaPanel.getHeight() + panel1.getHeight() + panel2.getHeight() + panel3.getHeight() + 20);
    	
    	return panel;
    }
    
    public static JPanel creatLabelHeader(String labeltext, JButton b1, JButton b2, JButton b3){
    	
    	JPanel panel = new JPanel();
        
        panel.setLayout(new BorderLayout());
       // panel.setLayout(new BorderLayout());
      //  panel.setPreferredSize(new Dimension(f.getWidth(), 25));
        
        
        JLabel label = new JLabel(labeltext);
        label.setPreferredSize(new Dimension(100, 25));
        
        
        JPanel panel2 = new JPanel();
        
        panel2.setPreferredSize(new Dimension(80,25));
        panel2.add(b1);
        panel2.add(b2);
        panel2.add(b3);
        
        panel.add(label, BorderLayout.WEST);
        panel.add(panel2, BorderLayout.EAST);
        
        return panel;
    }

    public static void main(String[] a) {
        JFrame f = new JFrame("Test Window");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(300, 300));
        f.setLocation(500,300);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(f.getWidth(), 25));
       // panel.setLayout(new BorderLayout());
        
        JLabel label = new JLabel("Name");
        label.setPreferredSize(new Dimension(50, 25));
        
        JButton b1 = new PropertyEditButton("Add");
        
      //  JButton b1 = new IconButton("AddIconSmall.png", "Add", null);
       // JButton b2 = new IconButton("EditIconSmall.png", "Edit", null);
       // JButton b3 = new IconButton("DeleteIconSmall.png", "Delete", null);
        
        
        
        JPanel panel2 = new JPanel();
        panel2.setBackground(Color.WHITE);
        panel2.setPreferredSize(new Dimension(80,25));
        panel2.add(b1);
      //  panel2.add(b2);
      //  panel2.add(b3);
        
        panel.add(label, BorderLayout.WEST);
        panel.add(panel2, BorderLayout.EAST);
        
      //  JPanel panel = creatLabelHeader("Definition", new IconButton("AddIconSmall.png", "Add", null), new IconButton("EditIconSmall.png", "Edit", null), new IconButton("DeleteIconSmall.png", "Delete", null));
        
      //  JPanel panel = createEditPanel();
        f.getContentPane().add(panel, BorderLayout.NORTH);
        f.pack();      
        f.setVisible(true);
     }

     /*public void paint( Graphics g ) {
            super.paint( g );
            g.drawImage(image,  0 , 0 , image.getWidth(null) , image.getHeight(null) , imageObserver);
        }*/
    
    /*protected TestButton(){
    }

    @Override
        public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Image img = Toolkit.getDefaultToolkit().getImage("C:\\app\\git\\metaproject-gui\\protege-client\\target\\edit.gif");

        g2.drawImage(img, 45, 35, this);
        g2.finalize();
    }*/



}
