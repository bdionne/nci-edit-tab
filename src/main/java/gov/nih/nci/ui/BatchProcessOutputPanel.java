package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class BatchProcessOutputPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JTextArea textarea;
	private JButton savebutton;
	private JButton clearbutton;
	private JButton closebutton;
	
	public BatchProcessOutputPanel(){
		createUI();
	}
	
	private void createUI() {
        
    	setLayout(new BorderLayout());
    	
        textarea = new JTextArea();       
        JScrollPane sp = new JScrollPane(textarea);
        add(sp, BorderLayout.CENTER);
        
        JPanel buttonpanel = new JPanel();
        
        savebutton = new JButton("Save");
        savebutton.addActionListener(this);
        clearbutton = new JButton("Clear");
        clearbutton.addActionListener(this);
        closebutton = new JButton("Close");
        closebutton.addActionListener(this);
        
        buttonpanel.add(savebutton);
        buttonpanel.add(clearbutton);
        buttonpanel.add(closebutton);
        
        add(buttonpanel, BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(200, 250));
        setVisible(true);
        
    }

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == savebutton){
			
		} else if (e.getSource() == clearbutton){
			textarea.setText(null);
		} else if (e.getSource() == closebutton){
			this.setVisible(false);
		}
	}
	
	public JTextArea getTextArea(){
		return textarea;
	}
}
