package gov.nih.nci.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;

public class EditPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;
    
    //private OWLFrameList<OWLAnnotationSubject> upperPanelList;
    
    //private OWLFrameList<OWLAnnotationSubject> lowerPanelList;
    
    
    public EditPanel(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        //this.upperPanelList = upperPanelList;
        //this.lowerPanelList = lowerPanelList;
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout());
        
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.setBorder(BorderFactory.createTitledBorder("Description:"));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        JScrollPane panel1 = new JScrollPane();// will add table to it
        panel1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("Complex Properties", panel1);

        JScrollPane panel2 = new JScrollPane();//will add tree or list to it
        panel2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("General", panel2);
        
        
        upperPanel.add(tabbedPane);
        
        JScrollPane lowerComp = new JScrollPane();// will add description list to it
        lowerComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        lowerPanel.add(lowerComp);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(280);

		add(splitPane, BorderLayout.CENTER);
		
    }
    

    public OWLEditorKit getEditorKit() {
    	return owlEditorKit;
    }
    


}
