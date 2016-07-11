package gov.nih.nci.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.frame.OWLAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;

import gov.nih.nci.ui.dialog.NoteDialog;
import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.utils.ReferenceFinder;



public class RetirePanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OWLEditorKit owlEditorKit;	

    private OWLModelManager mngr; 
    private OWLDataFactory df;    	
	private OWLOntology ont;	
	private OWLClass classToRetire = null;    
    private Map<OWLAnnotationProperty, Set<String>> fixups;
    
    
    private OWLFrameList<OWLAnnotationSubject> upperPanelList;
    
    private UsagePanel usage_panel = null;
    
    private JPanel buttonPanel;
    
    private JButton retireButton;
    
    private JButton cancelButton;
    
    public RetirePanel(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        mngr = owlEditorKit.getOWLModelManager();
        df = mngr.getOWLDataFactory();    	
    	ont = mngr.getActiveOntology();
        upperPanelList = new OWLFrameList<OWLAnnotationSubject>(owlEditorKit, new OWLAnnotationsFrame(owlEditorKit));
        usage_panel = new UsagePanel(owlEditorKit);
        createUI();
        // initialize UsagePanel with a blank
        this.setOWLClass(null);
    }


    private void createUI() {
        setLayout(new BorderLayout());
        
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel lowerPanel = new JPanel(new BorderLayout());
        
        JScrollPane upperComp = new JScrollPane(upperPanelList);
        upperComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        upperPanel.add(upperComp);
        
        JScrollPane lowerComp = new JScrollPane(usage_panel);
        lowerComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        lowerPanel.add(lowerComp);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(280);

		add(splitPane, BorderLayout.CENTER);
		add(createJButtonPanel(), BorderLayout.SOUTH);
        
    }
    
    public void dispose() {
		upperPanelList.dispose();
		
	}
    
    
    public void setOWLClass(OWLClass inst) {
    	
    	// always update the usages panel
    	usage_panel.setOWLEntity(inst);
    	if (inst == null) {
    		return;
    	}
    	
    	if ((classToRetire != null) &&
    			(inst.equals(classToRetire))) {
    		
    	} else {
    		// either first time in or a new class
    		classToRetire = inst;
			upperPanelList.setRootObject(inst.getIRI()); 
			fixups = (new ReferenceFinder(owlEditorKit.getModelManager())).computeAnnotations(inst);
			if (NCIEditTab.currentTab().isPreRetired(classToRetire)) {
				retireButton.setText("Approve");
			} else {
				retireButton.setText("Retire");
			}
    		
    	}
    }  
    
    private JPanel createJButtonPanel() {
    	buttonPanel = new JPanel();
    	retireButton = new JButton("Retire");
    	retireButton.setEnabled(true);

    	retireButton.addActionListener(new ActionListener() {

    		public void actionPerformed(ActionEvent e)
    		{
    			if (usage_panel.getCount() > 1) {
    				warnUsages();
    			} else if (retireButton.getText().equals("Save")) {
    				// TODO: refactor and move type check to edit tab
    				NCIEditTab.currentTab().commitChanges();
    				upperPanelList.setRootObject(null);
        			usage_panel.setOWLEntity(null);
    			} else if (retireButton.getText().equals("Approve")) {
    				approveRetire();
    			} else {
    				// proceed to retire
    				NCIEditTab.currentTab().completeRetire(fixups);
    				retireButton.setText("Save");
    				
    			}


    		}
    	});     

    	cancelButton = new JButton("Cancel");
    	cancelButton.setEnabled(true);

    	cancelButton.addActionListener(new ActionListener() {

    		public void actionPerformed(ActionEvent e)
    		{
    			//setEnableUnselectedRadioButtons(true);
    			//Execute when button is pressed
    			upperPanelList.setRootObject(null);
    			usage_panel.setOWLEntity(null);
    			NCIEditTab.currentTab().undoChanges();
    			// TODO: What? lowerPanelList.setRootObject(null);
    		}
    	});     

    	buttonPanel.add(retireButton);
    	buttonPanel.add(cancelButton);
    	return buttonPanel;
    }
    
    public void warnUsages() {
    	JOptionPane.showMessageDialog(this, "Can't retire until all usages are repaired", "Warning", JOptionPane.WARNING_MESSAGE);
    	
    	
    }
    
    
    public void approveRetire() {
    	
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	
    	Set<OWLSubClassOfAxiom> subs = ont.getSubClassAxiomsForSubClass(classToRetire);
    	for (OWLSubClassOfAxiom s : subs) {
    		if (s.getSuperClass().asOWLClass().equals(NCIEditTab.PRE_RETIRE_ROOT)) {
    			changes.add(new RemoveAxiom(ont, s));
    		}
    	}
    	changes.add(new AddAxiom(mngr.getActiveOntology(),
    			df.getOWLSubClassOfAxiom(classToRetire, NCIEditTab.RETIRE_ROOT)));
    	
    	mngr.applyChanges(changes);
        
        this.retireButton.setText("Save");
    	
    	
    }
    
    
    
}
