package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.protege.editor.owl.OWLEditorKit;
import gov.nih.nci.ui.dialog.BatchProcessingDialog;

public class BatchProcessOutputPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private OWLEditorKit owlEditorKit;
	
	private JTextArea textarea;
	private JButton batchbutton;
	
	public BatchProcessOutputPanel(OWLEditorKit editorKit){
		this.owlEditorKit = editorKit;
		createUI();
	}
	
	private void createUI() {
        
    	setLayout(new BorderLayout());
    	
        textarea = new JTextArea();       
        JScrollPane sp = new JScrollPane(textarea);  
        //sp.setVisible(false);
        add(sp, BorderLayout.CENTER);
        
        JPanel buttonpanel = new JPanel();
        
        batchbutton = new JButton("Launch Batch Process");
        batchbutton.addActionListener(this);
        
        buttonpanel.add(batchbutton);
        
        add(buttonpanel, BorderLayout.SOUTH);
        setVisible(true);
        
    }

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == batchbutton){
			BatchProcessingDialog dl = new BatchProcessingDialog(this, NCIEditTab.currentTab());
			//dl.setPreferredSize(new Dimension(400, 400));
			//dl.pack();
			//dl.setVisible(true);
		}
	}
	
	public JTextArea getTextArea(){
		return textarea;
	}
}
