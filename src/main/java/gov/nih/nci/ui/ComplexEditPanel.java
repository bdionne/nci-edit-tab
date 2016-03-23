package gov.nih.nci.ui;

import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;



public class ComplexEditPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;
    
    private OWLFrameList<OWLAnnotationSubject> upperPanelList;
    
    private OWLFrameList<OWLAnnotationSubject> lowerPanelList;
    
    private JPanel radioButtonPanel;
    
    private JRadioButton splitButton;

    private JRadioButton cloneButton;
    
    private JRadioButton mergeButton;
    
    private JRadioButton retireButton;
    
    private JPanel buttonPanel;
    
    private JButton saveButton;
    
    private JButton clearButton;
    
    public ComplexEditPanel(OWLEditorKit editorKit, OWLFrameList<OWLAnnotationSubject> upperPanelList, OWLFrameList<OWLAnnotationSubject> lowerPanelList) {
        this.owlEditorKit = editorKit;
        this.upperPanelList = upperPanelList;
        this.lowerPanelList = lowerPanelList;
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout());
        
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel lowerPanel = new JPanel(new BorderLayout());
        
        JScrollPane upperComp = new JScrollPane(upperPanelList);
        upperComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        upperPanel.add(upperComp);
        
        JScrollPane lowerComp = new JScrollPane(lowerPanelList);
        lowerComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        lowerPanel.add(lowerComp);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(280);

		add(splitPane, BorderLayout.CENTER);
		add(createJButtonPanel(), BorderLayout.SOUTH);
		add(createRadioButtonPanel(), BorderLayout.NORTH);
        
    }
    
    private JPanel createJButtonPanel() {
		buttonPanel = new JPanel();
		saveButton = new JButton("Save");
		saveButton.setEnabled(true);
		clearButton = new JButton("Clear");
		clearButton.setEnabled(true);
		
		buttonPanel.add(saveButton);
		buttonPanel.add(clearButton);
		return buttonPanel;
	}
    
    private JPanel createRadioButtonPanel() {
    	radioButtonPanel = new JPanel();
    	splitButton = new JRadioButton("Split");
    	cloneButton = new JRadioButton("Copy");
    	mergeButton = new JRadioButton("Merge");
    	retireButton = new JRadioButton("Retire");
    	ButtonGroup btnGrp = new ButtonGroup();
    	btnGrp.add(splitButton);
    	btnGrp.add(cloneButton);
    	btnGrp.add(mergeButton);
    	btnGrp.add(retireButton);
    	radioButtonPanel.add(splitButton);
    	radioButtonPanel.add(cloneButton);
    	radioButtonPanel.add(mergeButton);
    	radioButtonPanel.add(retireButton);
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
    
    public boolean isRetireBtnSelected() {
    	return retireButton.isSelected();
    }
    
    public OWLEditorKit getEditorKit() {
    	return owlEditorKit;
    }
    
    public OWLFrameList getUpperPanelList() {
    	return upperPanelList;
    }
    
    public OWLFrameList getLowerPanelList() {
    	return lowerPanelList;
    }
    
}
