package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import gov.nih.nci.ui.dialog.BatchProcessingDialog;

public class BatchProcessOutputPanel extends JPanel implements ActionListener{
	
	private static final Logger log = Logger.getLogger(BatchProcessOutputPanel.class);

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
			String outputStr = textarea.getText();
			JFileChooser fc = new JFileChooser();
			//to do - add file filter
			int select = fc.showSaveDialog(BatchProcessOutputPanel.this);
			if (select == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            String outputfile = file.getAbsolutePath();
	            try {
					BufferedWriter writer =  new BufferedWriter(new FileWriter(
							outputfile));
					
					writer.write(outputStr);
					writer.close();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					log.error(e1.getMessage(), e1);
					
				}
			}
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
