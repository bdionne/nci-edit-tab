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
import gov.nih.nci.utils.AxiomSorter;



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
			fixups = (new AxiomSorter(owlEditorKit.getModelManager())).computeAnnotations(inst);
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
    				NCIEditTab.currentTab().commitChanges(ComplexEditType.RETIRE);
    				upperPanelList.setRootObject(null);
        			usage_panel.setOWLEntity(null);
    			} else if (retireButton.getText().equals("Approve")) {
    				approveRetire();
    			} else {
    				// proceed to retire
    				completeRetire();
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
    
    private List<OWLOntologyChange> addParentRoleAssertions(List<OWLOntologyChange> changes, OWLClassExpression exp) {
    	if (exp instanceof OWLClass) {
    		if (this.classToRetire.equals(exp)) {
    			// noop
    		} else {
    			OWLClass ocl  = (OWLClass) exp;
    			String name = ocl.getIRI().getShortForm();
    			OWLLiteral val = df.getOWLLiteral(name);
    			OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(NCIEditTab.DEP_PARENT, classToRetire.getIRI(), val);
    			changes.add(new AddAxiom(ont, ax));  
    		}
    	} else if (exp instanceof OWLQuantifiedObjectRestriction) {
    		OWLQuantifiedObjectRestriction qobj = (OWLQuantifiedObjectRestriction) exp;
    		OWLClassExpression rexp = qobj.getFiller();

    		String fval;
    		if (rexp instanceof OWLClass) {
    			fval = ((OWLClass) rexp).getIRI().getShortForm();
    		} else {
    			fval = mngr.getRendering(rexp);
    		}

    		String quant = "some";
    		if (exp instanceof OWLObjectSomeValuesFrom) {
    			quant = "some";
    		} else if (exp instanceof OWLObjectAllValuesFrom) {
    			quant = "only";
    		}
    		String val = qobj.getProperty().asOWLObjectProperty().getIRI().getShortForm() + "|"
    				+ quant + "|" + fval;
    		OWLLiteral lit = df.getOWLLiteral(val);
    		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(NCIEditTab.DEP_ROLE, classToRetire.getIRI(), lit);
    		changes.add(new AddAxiom(ont, ax));
    	} else if (exp instanceof OWLObjectIntersectionOf) {
    		OWLObjectIntersectionOf oio = (OWLObjectIntersectionOf) exp;
    		Set<OWLClassExpression> conjs = oio.asConjunctSet();
    		for (OWLClassExpression c : conjs) {
    			changes = addParentRoleAssertions(changes, c);
    		}
    	} else if (exp instanceof OWLObjectUnionOf) {
    		OWLObjectUnionOf oio = (OWLObjectUnionOf) exp;
    		Set<OWLClassExpression> conjs = oio.asDisjunctSet();
    		for (OWLClassExpression c : conjs) {
    			changes = addParentRoleAssertions(changes, c);
    		}
    	}
    	return changes;

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
    
    
    public void completeRetire() {
    	
    	String editornote = "";
        String designnote = "";
        String prefix = "preretire_annotation";

        
        NoteDialog dlg = new NoteDialog(NCIEditTab.currentTab(), editornote, designnote,
                                        prefix);
        
        editornote = dlg.getEditorNote();
        designnote = dlg.getDesignNote();
        
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();  
    	
    	OWLLiteral val = df.getOWLLiteral(editornote);
		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(NCIEditTab.EDITOR_NOTE, classToRetire.getIRI(), val);
		changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
		
		val = df.getOWLLiteral(designnote);
		ax = df.getOWLAnnotationAssertionAxiom(NCIEditTab.DESIGN_NOTE, classToRetire.getIRI(), val);
		changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
        
        Set<OWLSubClassOfAxiom> sub_axioms = ont.getSubClassAxiomsForSubClass(classToRetire);
        
        for (OWLSubClassOfAxiom ax1 : sub_axioms) {
        	OWLClassExpression exp = ax1.getSuperClass();
        	changes = addParentRoleAssertions(changes, exp);
        	changes.add(new RemoveAxiom(ont, ax1));
        	
        }
        
        Set<OWLEquivalentClassesAxiom> equiv_axioms = ont.getEquivalentClassesAxioms(classToRetire);
        
        for (OWLEquivalentClassesAxiom ax1 : equiv_axioms) {
        	Set<OWLClassExpression> exps = ax1.getClassExpressions();
        	for (OWLClassExpression exp : exps) {
        		changes = addParentRoleAssertions(changes, exp);
        	}
        	changes.add(new RemoveAxiom(ont, ax1));
        	
        }
        
        for (OWLAnnotationProperty p : fixups.keySet()) {
        	for (String s : fixups.get(p)) {
        		OWLLiteral val1 = df.getOWLLiteral(s);
        		OWLAxiom ax1 = df.getOWLAnnotationAssertionAxiom(p, classToRetire.getIRI(), val1);
        		changes.add(new AddAxiom(mngr.getActiveOntology(), ax1));
        		
        	}
        	
        }
        if (NCIEditTab.currentTab().isWorkFlowManager()) {
        	changes.add(new AddAxiom(mngr.getActiveOntology(),
        			df.getOWLSubClassOfAxiom(classToRetire, NCIEditTab.RETIRE_ROOT)));

        } else {
        	changes.add(new AddAxiom(mngr.getActiveOntology(),
        			df.getOWLSubClassOfAxiom(classToRetire, NCIEditTab.PRE_RETIRE_ROOT))); 
        }
        
        mngr.applyChanges(changes);
        
        
        
        
        
        
        
    
    }
}
