package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.cls.OWLClassDescriptionFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.dialog.ComplexPropChooser;
import gov.nih.nci.ui.event.ComplexEditType;

public class EditPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;
	
	private Set<OWLAnnotationProperty> complexProps;
	
	private Set<OWLAnnotationProperty> propsToExclude;
	
	private Set<OWLAnnotationProperty> readOnlyProperties;
	
	private OWLFrameList<OWLClass> list;
	
	private OWLFrameList<OWLAnnotationSubject> gen_props;
	
	private OWLClass currentClass = null;
	
	;
    
    private List<PropertyTablePanel> tablePanelList = new ArrayList<PropertyTablePanel>();
    
    JTabbedPane tabbedPane;
    
    JScrollPane descrPane;
    
    private JTextField prefNameText;
    private String origPref = "";
    private JTextField codeText;
	
	private JPanel buttonPanel;
	
	private JButton saveButton;
    
    private JButton cancelButton;
    
    public EditPanel(OWLEditorKit editorKit) {
    	
        this.owlEditorKit = editorKit;
        
        complexProps = NCIEditTab.currentTab().getComplexProperties();
        propsToExclude = new HashSet<OWLAnnotationProperty>();
        propsToExclude.addAll(complexProps);
        propsToExclude.add(NCIEditTabConstants.PREF_NAME);
        propsToExclude.add(NCIEditTabConstants.CODE_PROP);
        
        
        readOnlyProperties = NCIEditTab.currentTab().getImmutableProperties();
        
        createUI();
        
    }
    
    private void createUI() {
    	setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        
        JPanel complexPropertyPanel = new JPanel();
        complexPropertyPanel.setLayout(new BoxLayout(complexPropertyPanel, BoxLayout.Y_AXIS));
        JScrollPane compPropSP = new JScrollPane(complexPropertyPanel);
       
        //tabbedPane.addTab("Complex Properties", complexPropertyPanel);
        tabbedPane.addTab("Complex Properties", compPropSP);
        
        JPanel genPropPanel = new JPanel();
        genPropPanel.setLayout(new BorderLayout());
        
        JLabel prefNamLabel = new JLabel("Preferred Name");        
        prefNameText = new JTextField("preferred name");
        prefNameText.setVisible(true);
        
        prefNameText.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (prefNameText.getText().equals(origPref)) {
					if (!NCIEditTab.currentTab().isEditing()) {
						disableButtons();
					}

				} else {
					if (!NCIEditTab.currentTab().isEditing()) {
						NCIEditTab.currentTab().setEditInProgress(true);
						NCIEditTab.currentTab().setCurrentlyEditing(currentClass);						
						enableButtons();
					}
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (prefNameText.getText().equals(origPref)) {
					if (!NCIEditTab.currentTab().isEditing()) {
						disableButtons();
					}

				} else {
					if (!NCIEditTab.currentTab().isEditing()) {
						enableButtons();
					}
				}

			}

			@Override
			public void changedUpdate(DocumentEvent e) {}
        	
        });
        
        JPanel prefTxt = new JPanel();
        prefTxt.setLayout(new BorderLayout());
        
        prefTxt.add(prefNamLabel, BorderLayout.WEST);
        prefTxt.add(prefNameText, BorderLayout.CENTER);
        
        JLabel classCode = new JLabel("Code");
        codeText = new JTextField("code");
        codeText.setVisible(true);
        codeText.setEditable(false);
        
        JPanel codeTxt = new JPanel();
        codeTxt.setLayout(new BorderLayout());
        
        codeTxt.add(classCode, BorderLayout.WEST);
        codeTxt.add(codeText, BorderLayout.EAST);
        
        prefTxt.add(codeTxt, BorderLayout.EAST);
                
        genPropPanel.add(prefTxt, BorderLayout.NORTH);
        
        gen_props = new OWLFrameList<OWLAnnotationSubject>(owlEditorKit, 
        		new FilteredAnnotationsFrame(owlEditorKit, propsToExclude, readOnlyProperties));

                
        JScrollPane generalSP = new JScrollPane(gen_props);//will add tree or list to it
        generalSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        genPropPanel.add(generalSP, BorderLayout.CENTER);
        
        tabbedPane.addTab("General", genPropPanel);
        
        Iterator<OWLAnnotationProperty> it = complexProps.iterator();
        while(it.hasNext()) {
        	addComplexPropertyTable(complexPropertyPanel, (OWLAnnotationProperty) it.next());
        }        
        
        list = new OWLFrameList<>(owlEditorKit, new OWLClassDescriptionFrame(owlEditorKit));
        
        descrPane = new JScrollPane(list);// will add description list to it
        descrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                
        tabbedPane.addTab("Description", descrPane);
                
        add(tabbedPane, BorderLayout.CENTER);
		
		add(createJButtonPanel(), BorderLayout.SOUTH);
		
		setVisible(true);
    }
    
    public void setSelectedClass(OWLClass cls) {
    	if (cls != null) {

    		this.currentClass = cls;

    		Optional<String> ps = NCIEditTab.currentTab().getRDFSLabel(cls);

    		if (ps.isPresent()) {
    			origPref = ps.get();
    			prefNameText.setText(ps.get());    	     
    		} else {
    			prefNameText.setText("Missing");
    		}

    		Optional<String> cps = NCIEditTab.currentTab().getCode(cls);

    		if (cps.isPresent()) {
    			codeText.setText(cps.get());
    		} else {
    			codeText.setText("nocode");
    		}

    		List<PropertyTablePanel> tablePanelList = getPropertyTablePanelList();
    		for (PropertyTablePanel tablePanel : tablePanelList) {
    			tablePanel.setSelectedCls(cls);
    		}
    		list.setRootObject(cls);
    		if (cls != null) {
    			gen_props.setRootObject(cls.getIRI());
    		}

    		if (NCIEditTab.currentTab().isRetiring()) {
    			tabbedPane.setSelectedComponent(descrPane);

    		}
    	}
    }
    
    public OWLClass getSelectedClass() {
    	return this.currentClass;
    }
    
    public void addNewComplexProp() {
    	ComplexPropChooser chooser = new ComplexPropChooser(NCIEditTab.currentTab().getComplexProperties());
    	OWLAnnotationProperty c_prop = chooser.showDialog(owlEditorKit, "Choosing Complex Property");
    	for (PropertyTablePanel panel : tablePanelList) {
    		if (panel.getComplexProp().equals(c_prop)) {
    			panel.createNewProp();
    		}
    	}
    }
    
    private void addComplexPropertyTable(JPanel complexPropertyPanel, OWLAnnotationProperty complexProperty) {
    	Optional<String> tableName = NCIEditTab.currentTab().getRDFSLabel(complexProperty);
    	PropertyTablePanel tablePanel = new PropertyTablePanel(this.owlEditorKit, complexProperty, tableName.get());
    	complexPropertyPanel.add(tablePanel);
    	tablePanelList.add(tablePanel);
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
		saveButton.setEnabled(false);
		
		saveButton.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	// Do the save
                if (shouldSave()) {
                	NCIEditTab.currentTab().syncPrefName(prefNameText.getText());
                	NCIEditTab.currentTab().commitChanges();
                	submitHistory();
                	origPref = prefNameText.getText();
                	
                	
                }
            	
            }
        });     
		
		cancelButton = new JButton("Clear");
		cancelButton.setEnabled(false);
		
		cancelButton.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	NCIEditTab.currentTab().undoChanges();            	
            	NCIEditTab.currentTab().setEditInProgress(false);
            	NCIEditTab.currentTab().editClass();
            	disableButtons();
            	
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
    
    public void submitHistory() {
    	OWLClass cls = list.getRootObject();
    	String c = cls.getIRI().getShortForm();
    	String n = NCIEditTab.currentTab().getRDFSLabel(cls).get();
    	String op = ComplexEditType.MODIFY.toString();
    	String ref = "";
    	NCIEditTab.currentTab().putHistory(c, n, op, ref);
    }
    
    public void disposeView() {
    	list.dispose();
    	gen_props.dispose();
    }
    
    public void enableButtons() {
    	saveButton.setEnabled(true);
    	cancelButton.setEnabled(true);    	
    }
    
    public void disableButtons() {
    	saveButton.setEnabled(false);
    	cancelButton.setEnabled(false);    	
    }
}
