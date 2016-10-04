package gov.nih.nci.ui;

import static gov.nih.nci.ui.NCIEditTabConstants.MERGE_TARGET;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.frame.cls.OWLClassDescriptionFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.transferhandler.ComplexEditTransferHandler;



public class ComplexEditPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;
    
    private OWLFrameList<OWLAnnotationSubject> upperPanelAnn;
    private OWLFrameList<OWLClass> upperPanelClass;
    
    private OWLFrameList<OWLAnnotationSubject> lowerPanelAnn;
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
    	enableButtons();
    }

    private JRadioButton cloneButton;
    
    private JRadioButton mergeButton;
    
    private ButtonGroup radioButtonGroup;
    
    private JPanel buttonPanel;
    
    private JButton saveButton;
    
    private JButton clearButton;
    
    public ComplexEditPanel(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        this.upperPanelAnn = new OWLFrameList<OWLAnnotationSubject>(editorKit,
        		new FilteredAnnotationsFrame(owlEditorKit, new HashSet<>(),
        				NCIEditTab.currentTab().getImmutableProperties()));        
        this.lowerPanelAnn = new OWLFrameList<OWLAnnotationSubject>(editorKit,
        		new FilteredAnnotationsFrame(owlEditorKit, new HashSet<>(),
        				NCIEditTab.currentTab().getImmutableProperties()));
        this.lowerPanelClass = new OWLFrameList<>(owlEditorKit, new OWLClassDescriptionFrame(owlEditorKit));
        this.upperPanelClass = new OWLFrameList<>(owlEditorKit, new OWLClassDescriptionFrame(owlEditorKit));       
        
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
        
        //splitPane3.setTransferHandler(new ListTransferHandler(this));
       
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
            		NCIEditTab.currentTab().merge();
            		saveButton.setText("Save");
            	} else {
            		NCIEditTab.currentTab().commitChanges();
            		NCIEditTab.currentTab().completeSplit();
            		
            		setEnableUnselectedRadioButtons(true);
                	//Execute when button is pressed
                	upperPanelAnn.setRootObject(null);
                	lowerPanelAnn.setRootObject(null);
                	upperPanelClass.setRootObject(null);
                	lowerPanelClass.setRootObject(null);
                	radioButtonGroup.clearSelection();
                	
                	upperLabel.setText("Source");
                	lowerLabel.setText("Target");
                	
            		disableButtons();
            	}
            	
            }
        });     
		
		clearButton = new JButton("Clear");
		clearButton.setEnabled(false);
		
		clearButton.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
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
            	NCIEditTab.currentTab().cancelSplit();
            	disableButtons();
            }
        });     
		
		buttonPanel.add(saveButton);
		buttonPanel.add(clearButton);
		return buttonPanel;
	}
    
    private JPanel createRadioButtonPanel() {
    	
    	radioButtonPanel = new JPanel();
    	splitButton = new JRadioButton("Split");
    	cloneButton = new JRadioButton("Copy");
    	mergeButton = new JRadioButton("Merge");
    	radioButtonGroup = new ButtonGroup();
    	radioButtonGroup.add(splitButton);
    	radioButtonGroup.add(cloneButton);
    	radioButtonGroup.add(mergeButton);
    	radioButtonPanel.add(splitButton);
    	radioButtonPanel.add(cloneButton);
    	radioButtonPanel.add(mergeButton);
    	
    	ActionListener cbl = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JRadioButton sb = (JRadioButton) e.getSource();
				if (sb.equals(mergeButton)) {
					upperLabel.setText("Retiring Concept");
					lowerLabel.setText("Surviving Concept");
					NCIEditTab.currentTab().setMergeBegin(true);
					
				} else if (sb.equals(splitButton)) {
					upperLabel.setText("Split From");
					lowerLabel.setText("Split To");
					//NCIEditTab.currentTab().setSplitBegin(true);
					
				} else if (sb.equals(cloneButton)) {
					upperLabel.setText("Clone From");
					lowerLabel.setText("Clone To");
					
				}
				// TODO Auto-generated method stub
				
			}
    		
    	};
    	splitButton.addActionListener(cbl);
    	mergeButton.addActionListener(cbl);
    	cloneButton.addActionListener(cbl);
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
    		
    		
    		NCIEditTab.currentTab().setMergeTarget(target);   		
    		NCIEditTab.currentTab().setMergeSource(cls);
    		
    	} else if (c.equals(this.upperSplitPane)) {
    		this.upperPanelAnn.setRootObject(cls.getIRI());
    		this.upperPanelClass.setRootObject(cls);
    		NCIEditTab.currentTab().setMergeSource(cls);
    		
    	} else {
    		this.lowerPanelAnn.setRootObject(cls.getIRI());
    		this.lowerPanelClass.setRootObject(cls);
    		NCIEditTab.currentTab().setMergeTarget(cls);
    		
    	}
    	
    	checkStatus();
    	
    }
    
    private OWLClass findTarget(OWLClass cls) {
    	String target_code = NCIEditTab.currentTab().getProperty(cls, MERGE_TARGET).get();
    	return NCIEditTab.currentTab().getClass(target_code);
    }
    
    private void checkStatus() {
    	if (NCIEditTab.currentTab().readyMerge()) {
    		saveButton.setText("Merge");
    		enableButtons();
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
    
    public void disableButtons() {
    	saveButton.setEnabled(false);
    	clearButton.setEnabled(false);
    	
    }
	
	
}
