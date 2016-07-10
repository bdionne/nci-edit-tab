package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.frame.cls.OWLClassDescriptionFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.event.ComplexEditType;

public class EditPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;
	
	private Set<OWLAnnotationProperty> complexProperties;
	
	private OWLFrameList<OWLClass> list;
	
	private OWLFrameList<OWLAnnotationSubject> gen_props;
	
	;
    
    private List<PropertyTablePanel> tablePanelList = new ArrayList<PropertyTablePanel>();
    
	private JSplitPane splitPane;
	
	private JPanel buttonPanel;
	
	private JButton saveButton;
    
    private JButton cancelButton;
    
    public EditPanel(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        
        complexProperties = NCIEditTab.currentTab().getComplexProperties();
        
        createUI();
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.setBorder(BorderFactory.createTitledBorder("Description:"));
        
        JPanel complexPropertyPanel = new JPanel();
        complexPropertyPanel.setLayout(new BoxLayout(complexPropertyPanel, BoxLayout.Y_AXIS));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Complex Properties", complexPropertyPanel);
        
        gen_props = new OWLFrameList<OWLAnnotationSubject>(owlEditorKit, new FilteredAnnotationsFrame(owlEditorKit, complexProperties));

        JScrollPane panel2 = new JScrollPane(gen_props);//will add tree or list to it
        panel2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("General", panel2);
        
        Iterator it = complexProperties.iterator();
        while(it.hasNext()) {
        	addComplexPropertyTable(complexPropertyPanel, (OWLAnnotationProperty)it.next());
        }
        
        upperPanel.add(tabbedPane);
        
        list = new OWLFrameList<>(owlEditorKit, new OWLClassDescriptionFrame(owlEditorKit));
        
        JScrollPane lowerComp = new JScrollPane(list);// will add description list to it
        lowerComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        lowerPanel.add(lowerComp);
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(0.6);

		add(splitPane, BorderLayout.CENTER);
		
		add(createJButtonPanel(), BorderLayout.SOUTH);
		
		setVisible(true);
		restoreDefaults();

        
    }
    
    public void setSelectedClass(OWLClass cls) {

    	List<PropertyTablePanel> tablePanelList = getPropertyTablePanelList();
    	for (PropertyTablePanel tablePanel : tablePanelList) {
    		tablePanel.setSelectedCls(cls);
    	}
    	list.setRootObject(cls);
    	if (cls != null) {
    		gen_props.setRootObject(cls.getIRI());
    	}
		
    }
    
    private void addComplexPropertyTable(JPanel complexPropertyPanel, OWLAnnotationProperty complexProperty) {
    	Optional<String> tableName = NCIEditTab.currentTab().getRDFSLabel(complexProperty);
    	PropertyTablePanel tablePanel = new PropertyTablePanel(this.owlEditorKit, complexProperty, tableName.get());
    	complexPropertyPanel.add(tablePanel);
    	tablePanelList.add(tablePanel);
    }
    
    private void restoreDefaults() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	splitPane.setDividerLocation(splitPane.getSize().height /2);
            }
        });
    }    

    public OWLEditorKit getEditorKit() {
    	return owlEditorKit;
    }
    
    public List<PropertyTablePanel> getPropertyTablePanelList() {
    	return tablePanelList;
    }
    
    private JPanel createJButtonPanel() {
		buttonPanel = new JPanel();
		saveButton = new JButton("Save");
		saveButton.setEnabled(true);
		
		saveButton.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	// Do the save
                if (shouldSave()) {
                	NCIEditTab.currentTab().commitChanges();
                }
            	
            }
        });     
		
		cancelButton = new JButton("Clear");
		cancelButton.setEnabled(true);
		
		cancelButton.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	NCIEditTab.currentTab().undoChanges();
            	
            }
        });     
		
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}
    
    public boolean shouldSave() {
    	if (NCIEditTab.currentTab().isRetiring()) {
    		NCIEditTab.currentTab().updateRetire();
    		return false;
    	} else {
    		return true;
    	}
    }
    
    public void disposeView() {
    	list.dispose();
    	gen_props.dispose();
    }

}
