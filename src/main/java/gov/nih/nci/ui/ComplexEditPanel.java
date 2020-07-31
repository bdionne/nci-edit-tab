package gov.nih.nci.ui;

import static gov.nih.nci.ui.NCIEditTabConstants.MERGE_TARGET;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.cls.OWLClassDescriptionFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.transferhandler.ComplexEditTransferHandler;
import gov.nih.nci.utils.PropertyCheckUtil;



public class ComplexEditPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;
    
    private NCIOWLFrameList<OWLAnnotationSubject> upperPanelAnn;
    private OWLFrameList<OWLClass> upperPanelClass;
    
    private NCIOWLFrameList<OWLAnnotationSubject> lowerPanelAnn;
    private OWLFrameList<OWLClass> lowerPanelClass;
    
    private JSplitPane upperSplitPane;
    private JSplitPane lowerSplitPane;
    
    private JPanel upperPanel;
    private JPanel lowerPanel;
    
    private JLabel upperLabel;
    private JLabel lowerLabel;
    
    private JPanel radioButtonPanel;
    
    private JRadioButton splitButton;
    
    public void setSplit() {
    	splitButton.setSelected(true);
    }
    
    public void setClone() {
    	cloneButton.setSelected(true);
    }

    private JRadioButton cloneButton;
    
    private JRadioButton mergeButton;
    
    private JRadioButton dualButton;
    
    private ButtonGroup radioButtonGroup;
    
    private JPanel buttonPanel;
    
    private JButton saveButton;
    
    private JButton clearButton;
    
    public ComplexEditPanel(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        //new FilteredAnnotationsFrame(owlEditorKit, propsToExclude, readOnlyProperties), read_only)
        
        Set<OWLAnnotationProperty> imm_props = new HashSet<OWLAnnotationProperty>();
        imm_props.addAll(NCIEditTab.currentTab().getImmutableProperties());
        imm_props.add(NCIEditTabConstants.PREF_NAME);
        
        this.upperPanelAnn = new NCIOWLFrameList<OWLAnnotationSubject>(editorKit,
        		new FilteredAnnotationsFrame(owlEditorKit, new HashSet<>(),
        				imm_props));
           
        this.lowerPanelAnn = new NCIOWLFrameList<OWLAnnotationSubject>(editorKit,
        		new FilteredAnnotationsFrame(owlEditorKit, new HashSet<>(),
        				imm_props));
        this.lowerPanelClass = new OWLFrameList<OWLClass>(owlEditorKit, new OWLClassDescriptionFrame(owlEditorKit));
        this.upperPanelClass = new OWLFrameList<OWLClass>(owlEditorKit, new OWLClassDescriptionFrame(owlEditorKit));       
        
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout());        

        upperPanel = new JPanel(new BorderLayout());
        upperLabel = new JLabel("Source");
        upperPanel.add(upperLabel, BorderLayout.NORTH);
        
               
        JScrollPane upperUpperComp = new JScrollPane(upperPanelAnn);
        upperUpperComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
                
        JScrollPane upperLowerComp = new JScrollPane(upperPanelClass);
        upperLowerComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        upperSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperUpperComp, upperLowerComp);
		upperSplitPane.setOneTouchExpandable(true);
		upperSplitPane.setDividerLocation(280);
		
		upperPanel.add(upperSplitPane, BorderLayout.CENTER);
        
       
        
        
        lowerPanel = new JPanel(new BorderLayout());
        lowerLabel = new JLabel("Target");
        lowerPanel.add(lowerLabel, BorderLayout.NORTH);
        
        
        JScrollPane lowerUpperComp = new JScrollPane(lowerPanelAnn);        
        lowerUpperComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        
        JScrollPane lowerLowerComp = new JScrollPane(lowerPanelClass);
        lowerLowerComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                
        lowerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, lowerUpperComp, lowerLowerComp);
		lowerSplitPane.setOneTouchExpandable(true);
		lowerSplitPane.setDividerLocation(280);
		
		lowerPanel.add(lowerSplitPane, BorderLayout.CENTER);
        
        
        
        upperSplitPane.setTransferHandler(new ComplexEditTransferHandler(this));
    	lowerSplitPane.setTransferHandler(new ComplexEditTransferHandler(this));
        
        JSplitPane splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, upperPanel, lowerPanel);
               
		splitPane3.setOneTouchExpandable(true);
		splitPane3.setDividerLocation(280);

		add(splitPane3, BorderLayout.CENTER);
		add(createJButtonPanel(), BorderLayout.SOUTH);
		add(createRadioButtonPanel(), BorderLayout.NORTH);
        
    }
    
    

    private JPanel createJButtonPanel() {
		buttonPanel = new JPanel();
		saveButton = new JButton("Save");
		saveButton.setEnabled(false);
		
		saveButton.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	if (saveButton.getText().equals("Merge")) {
            		
            		if (NCIEditTab.currentTab().merge()) {
            			saveButton.setText("Save");
            			owlEditorKit.getWorkspace().getViewManager().bringViewToFront(
            		               "nci-edit-tab.ComplexEditView");
            		}
            	} else {
            		//List<String> messageList = new ArrayList<String>();
            		//NCIEditTab tab = NCIEditTab.currentTab();
            		PropertyCheckUtil propCheckUtil = new PropertyCheckUtil();
            		if ((propCheckUtil.syncFullSyn(lowerPanelClass.getRootObject())) &&
            				(propCheckUtil.syncFullSyn(upperPanelClass.getRootObject())) &&
            				//(NCIEditTab.currentTab().syncDefinition(lowerPanelClass.getRootObject())) &&
            				propCheckUtil.syncDefinition(lowerPanelClass.getRootObject()) &&
            				//(NCIEditTab.currentTab().syncDefinition(upperPanelClass.getRootObject()))) {
            				propCheckUtil.syncDefinition(upperPanelClass.getRootObject())) {

            			if (NCIEditTab.currentTab().commitChanges(true)) {
            				NCIEditTab.currentTab().completeOp();

            				reset();
            			}
            		}
            	}
            	
            }
        });     
		
		clearButton = new JButton("Clear");
		clearButton.setEnabled(false);
		
		clearButton.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	NCIEditTab.currentTab().undoChanges();            	
            	NCIEditTab.currentTab().setEditInProgress(false);
            	NCIEditTab.currentTab().refreshNavTree();
            	NCIEditTab.currentTab().cancelOp();
            	reset();
            	
            }
        });     
		
		buttonPanel.add(saveButton);
		buttonPanel.add(clearButton);
		return buttonPanel;
	}
    
    public void reset() {

    	setEnableUnselectedRadioButtons(true);
    	//Execute when button is pressed
    	upperPanelAnn.setRootObject(null);
    	lowerPanelAnn.setRootObject(null);
    	upperPanelClass.setRootObject(null);
    	lowerPanelClass.setRootObject(null);
    	radioButtonGroup.clearSelection();
    	
    	upperLabel.setText("Source");
    	lowerLabel.setText("Target");
    	
    	saveButton.setText("Save");    	
    	disableButtons();
    }
    private JPanel createRadioButtonPanel() {
    	
    	radioButtonPanel = new JPanel();
    	splitButton = new JRadioButton("Split");
    	cloneButton = new JRadioButton("Copy");
    	mergeButton = new JRadioButton("Merge");
    	dualButton = new JRadioButton("Dual Edits");
    	radioButtonGroup = new ButtonGroup();
    	radioButtonGroup.add(splitButton);
    	radioButtonGroup.add(cloneButton);
    	radioButtonGroup.add(mergeButton);
    	radioButtonGroup.add(dualButton);
    	radioButtonPanel.add(splitButton);
    	radioButtonPanel.add(cloneButton);
    	radioButtonPanel.add(mergeButton);
    	radioButtonPanel.add(dualButton);
    	
    	ActionListener cbl = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JRadioButton sb = (JRadioButton) e.getSource();
				if (isRetiring()) {
					JOptionPane.showMessageDialog(new Frame(), "Retirement in progress.", "Warning", JOptionPane.WARNING_MESSAGE);
					radioButtonGroup.clearSelection();
				} else {
					if (sb.equals(mergeButton)) {
						upperLabel.setText("Retiring Concept");
						lowerLabel.setText("Surviving Concept");
						//setOp(ComplexEditType.MERGE);
					} else if (sb.equals(splitButton)) {
						upperLabel.setText("Split From");
						lowerLabel.setText("Split To");
						//setOp(ComplexEditType.SPLIT);
					} else if (sb.equals(cloneButton)) {
						upperLabel.setText("Clone From");
						lowerLabel.setText("Clone To");
						//setOp(ComplexEditType.CLONE);

					} else if (sb.equals(dualButton)) {
						//setOp(ComplexEditType.DUAL);
						
					}
				}
			}
    		
    	};
    	splitButton.addActionListener(cbl);
    	mergeButton.addActionListener(cbl);
    	cloneButton.addActionListener(cbl);
    	dualButton.addActionListener(cbl);
    	return radioButtonPanel;
    }
    
    public boolean isSplitBtnSelected() {
    	return splitButton.isSelected();
    }
    
    public boolean isCloneBtnSelected() {
    	return cloneButton.isSelected();
    }

    public boolean isMergeBtnSelected() {
    	return mergeButton.isSelected();
    }
    
    public boolean isDualBtnSelected() {
    	return dualButton.isSelected();
    }
    
    
    
    public void setEnableUnselectedRadioButtons(boolean enable) {
    	
    	if (!isSplitBtnSelected()) {
    		splitButton.setEnabled(enable);
    	}
    	if (!isCloneBtnSelected()) {
    		cloneButton.setEnabled(enable);
    	}
    	if (!isMergeBtnSelected()) {
    		mergeButton.setEnabled(enable);
    	}
    	if (!isDualBtnSelected()) {
    		dualButton.setEnabled(enable);
    	}
    	
    }
    
    
    public OWLEditorKit getEditorKit() {
    	return owlEditorKit;
    }
    
    public void dispose() {
		upperPanelAnn.dispose();
		upperPanelClass.dispose();
		lowerPanelAnn.dispose();
		lowerPanelClass.dispose();
		
	}
    
    public void dropOnComp(Component c, OWLClass cls) {
    	if (NCIEditTab.currentTab().isPreMerged(cls)) {
    		OWLClass target = findTarget(cls);
    		
    		setRootObjects(cls, target);
    		
    		
    		NCIEditTab.currentTab().setTarget(target);   		
    		NCIEditTab.currentTab().setSource(cls);
    		
    	} else if (c.equals(this.upperSplitPane)) {
    		this.upperPanelAnn.setRootObject(cls.getIRI());
    		this.upperPanelClass.setRootObject(cls);
    		NCIEditTab.currentTab().setSource(cls);
    		
    	} else {
    		this.lowerPanelAnn.setRootObject(cls.getIRI());
    		this.lowerPanelClass.setRootObject(cls);
    		NCIEditTab.currentTab().setTarget(cls);
    		
    	}
    	
    	checkStatus();
    	
    }
    
    private OWLClass findTarget(OWLClass cls) {
    	String target_code = NCIEditTab.currentTab().getPropertyValue(cls, MERGE_TARGET).get();
    	return NCIEditTab.currentTab().getClass(target_code);
    }
    
    private void checkStatus() {
    	if (isMergeBtnSelected()) {
    		NCIEditTab.currentTab().getCurrentOp().setType(ComplexEditType.MERGE);
    	}
    	if (isDualBtnSelected()) {
    		NCIEditTab.currentTab().getCurrentOp().setType(ComplexEditType.DUAL);
    	}
    	if (NCIEditTab.currentTab().readyMerge() && isMergeBtnSelected()) {
    		saveButton.setText("Merge");
    		enableButtons();
    	} else {
    		enableClear();
    	}
    }
    
	public void setRootObjects(OWLClass top, OWLClass bot) {
		this.upperPanelAnn.setRootObject(top.getIRI());
		this.upperPanelClass.setRootObject(top);
		this.lowerPanelAnn.setRootObject(bot.getIRI());
		this.lowerPanelClass.setRootObject(bot);
		
	}
	
	public void enableButtons() {
    	saveButton.setEnabled(true);
    	clearButton.setEnabled(true);
    	
    }
		
	public void enableClear() {
    	clearButton.setEnabled(true);    	
    }
    
    public void disableButtons() {
    	saveButton.setEnabled(false);
    	clearButton.setEnabled(false);
    	
    }
    
    private boolean isRetiring() {
    	return NCIEditTab.currentTab().getCurrentOp().isRetiring();
    }
	
	
}
