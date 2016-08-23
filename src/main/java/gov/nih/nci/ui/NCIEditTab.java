package gov.nih.nci.ui;

import static gov.nih.nci.ui.NCIEditTabConstants.CODE_PROP;
import static gov.nih.nci.ui.NCIEditTabConstants.COMPLEX_PROPS;
import static gov.nih.nci.ui.NCIEditTabConstants.DEP_ASSOC;
import static gov.nih.nci.ui.NCIEditTabConstants.DEP_CHILD;
import static gov.nih.nci.ui.NCIEditTabConstants.DEP_IN_ASSOC;
import static gov.nih.nci.ui.NCIEditTabConstants.DEP_IN_ROLE;
import static gov.nih.nci.ui.NCIEditTabConstants.DEP_PARENT;
import static gov.nih.nci.ui.NCIEditTabConstants.DEP_ROLE;
import static gov.nih.nci.ui.NCIEditTabConstants.DESIGN_NOTE;
import static gov.nih.nci.ui.NCIEditTabConstants.EDITOR_NOTE;
import static gov.nih.nci.ui.NCIEditTabConstants.IMMUTABLE_PROPS;
import static gov.nih.nci.ui.NCIEditTabConstants.LABEL_PROP;
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE;
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE_SOURCE;
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE_TARGET;
import static gov.nih.nci.ui.NCIEditTabConstants.PREF_NAME;
import static gov.nih.nci.ui.NCIEditTabConstants.PRE_MERGE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.PRE_RETIRE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.RETIRE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.SPLIT_FROM;
import static org.semanticweb.owlapi.search.Searcher.annotationObjects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.client.ClientSession;
import org.protege.editor.owl.client.LocalHttpClient;
import org.protege.editor.owl.client.SessionRecorder;
import org.protege.editor.owl.client.api.exception.AuthorizationException;
import org.protege.editor.owl.client.api.exception.ClientRequestException;
import org.protege.editor.owl.client.event.ClientSessionChangeEvent;
import org.protege.editor.owl.client.event.ClientSessionChangeEvent.EventCategory;
import org.protege.editor.owl.client.event.ClientSessionListener;
import org.protege.editor.owl.client.event.CommitOperationEvent;
import org.protege.editor.owl.client.util.ClientUtils;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.history.HistoryManager;
import org.protege.editor.owl.model.history.UndoManagerListener;
import org.protege.editor.owl.server.api.CommitBundle;
import org.protege.editor.owl.server.policy.CommitBundleImpl;
import org.protege.editor.owl.server.versioning.Commit;
import org.protege.editor.owl.server.versioning.api.ChangeHistory;
import org.protege.editor.owl.server.versioning.api.DocumentRevision;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.protege.editor.owl.ui.renderer.OWLEntityAnnotationValueRenderer;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.owlapi.inference.cls.ChildClassExtractor;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;

import edu.stanford.protege.metaproject.api.Operation;
import edu.stanford.protege.metaproject.api.Project;
import edu.stanford.protege.metaproject.api.ProjectOptions;
import edu.stanford.protege.metaproject.api.Role;
import edu.stanford.protege.metaproject.impl.RoleIdImpl;
import gov.nih.nci.ui.dialog.NCIClassCreationDialog;
import gov.nih.nci.ui.dialog.NoteDialog;
import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.EditTabChangeEvent;
import gov.nih.nci.ui.event.EditTabChangeListener;
import gov.nih.nci.utils.ReferenceReplace;

public class NCIEditTab extends OWLWorkspaceViewsTab implements ClientSessionListener, UndoManagerListener {
	private static final Logger log = Logger.getLogger(NCIEditTab.class);
	private static final long serialVersionUID = -4896884982262745722L;
	
	private static NCIEditTab tab;
	
	public static NCIEditTab currentTab() {
		return tab;
	}
	
	private OWLClass split_source;
	private OWLClass split_target;
	private OWLClass merge_source;
	private OWLClass merge_target;
	private OWLClass retire_class;
	
	private boolean isRetiring = false;
	
	
	public boolean isRetiring() {
		return isRetiring;
	}
	
	public void cancelRetire() {
		retire_class = null;
		isRetiring = false;
	}
	
	public boolean isMerging() {
		return (merge_source != null) &&
				(merge_target != null);
	}
	
	public boolean isSplitting() {
		return (split_source != null) &&
				(split_target != null);
	}
	
	public OWLClass getSplitSource() {
		return split_source;
	}
	
	public OWLClass getSplitTarget() {
		return split_target;
	}
	
	public OWLClass getRetireClass() {
		return retire_class;		
	}
	
	public void setMergeSource(OWLClass cls) {
		this.merge_source = cls;
	}
	
	public void setMergeTarget(OWLClass cls) {
		this.merge_target = cls;
	}
		
	// use undo/redo facility
	private SessionRecorder history;
	
	private ClientSession clientSession = null;
	
	private OWLOntology ontology;
	
	private OWLModelManagerListener ont_listen;
	
	private static ArrayList<EditTabChangeListener> event_listeners = new ArrayList<EditTabChangeListener>();
	
	
	
	public static void addListener(EditTabChangeListener l) {
		event_listeners.add(l);
	}
	
	public static void removeListener(EditTabChangeListener l) {
		event_listeners.remove(l);
	}
	
	private void fireChange(EditTabChangeEvent ev) {
		
		for (EditTabChangeListener l : event_listeners) {
			l.handleChange(ev);
		}		
	}	
	
	private Set<OWLAnnotationProperty> complex_properties = new HashSet<OWLAnnotationProperty>();
	
	private Map<OWLAnnotationProperty, Set<OWLAnnotationProperty>> required_annotation_dependencies =
			new HashMap<OWLAnnotationProperty, Set<OWLAnnotationProperty>>();

	private Map<OWLAnnotationProperty, Set<OWLAnnotationProperty>> optional_annotation_dependencies =
			new HashMap<OWLAnnotationProperty, Set<OWLAnnotationProperty>>();
	
	private Set<OWLAnnotationProperty> immutable_properties = new HashSet<OWLAnnotationProperty>();
	
	private Set<OWLAnnotationProperty> associations = new HashSet<OWLAnnotationProperty>();
	
	public boolean isAssociation(OWLAnnotationProperty p) {
		return associations.contains(p);
	}
	
				

	public NCIEditTab() {
		setToolTipText("Custom Editor for NCI");
		tab = this;
	}
	
	public String generateCode() {
		LocalHttpClient cl = (LocalHttpClient) clientSession.getActiveClient();
		String code = "NONE"; 
		
		try {
			code = cl.getCode();
		} catch (Exception e) {
			code = UUID.randomUUID().toString();
		}
		return code;
	}
	
	public Set<OWLAnnotationProperty> getComplexProperties() {
		return complex_properties;
	}
	
	public Set<OWLAnnotationProperty> getImmutableProperties() {
		return immutable_properties;
	}
	
	public Set<OWLAnnotationProperty> getRequiredAnnotationsForAnnotation(OWLAnnotationProperty annp) {
		return required_annotation_dependencies.get(annp);
		
	}
	
	public Set<OWLAnnotationProperty> getOptionalAnnotationsForAnnotation(OWLAnnotationProperty annp) {
		return optional_annotation_dependencies.get(annp);
		
	}

    @Override
	public void initialise() {
		super.initialise();
		log.info("NCI Edit Tab initialized");
		
		/** NOTE: We'd like to see this called once when the ontology is opened, currently it's called a couple
		 * of additional times when the app initializes.
		 * 
		 */		 
		ont_listen = new OWLModelManagerListener() {

			@Override
			public void handleChange(OWLModelManagerChangeEvent event) {
				if (event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED) {
					if (clientSession == null) {
						// only initialize the ClientSession once, it maintains the
						// switches between ontologies
						clientSession = ClientSession.getInstance(getOWLEditorKit());
						
						history = SessionRecorder.getInstance(getOWLEditorKit());
						addListeners();
				        
					}
					ontology = getOWLModelManager().getActiveOntology();
				}				
			}			
		};
		
		
		this.getOWLEditorKit().getOWLModelManager().addListener(ont_listen);
			
	}
    
   
    
    /** Anyone can pre-merge so there is no need for a separate operation. A pre-merged class exists
     * as a subclass of the pre-merged root, so we can readily distinguish a pre-merge from a merge
     * and there is no need for a separate operation
     * 
     */
    public boolean readyMerge() {
    	return (this.merge_source != null) &&
    			(this.merge_target != null);
    }
    
    public boolean canMerge() {
    	return true;
    }
    
    public boolean canMerge(OWLClass cls) {
    	boolean can = clientSession.getActiveClient().canPerformProjectOperation(MERGE.getId()); 
    	if (can) {
    		if (isPreMerged(cls)) {
    			return isWorkFlowManager();    			
    		} else if (isPreRetired(cls)) {
    			return false;
    		} else if (isRetired(cls)) {
    			return false;
    		}
    	}
    	return can;
    }
    
    public void merge() {
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
    	
    	if (isPreMerged(merge_source)) {
    		
    		Set<OWLAnnotationAssertionAxiom> props = ontology.getAnnotationAssertionAxioms(merge_source.getIRI());    		
    		for (OWLAnnotationAssertionAxiom p : props) {
    			if (p.getProperty().equals(MERGE_TARGET)) {
    				changes.add(new RemoveAxiom(ontology, p));
    			}
    			
    		}
    		props = ontology.getAnnotationAssertionAxioms(merge_target.getIRI());    		
    		for (OWLAnnotationAssertionAxiom p : props) {
    			if (p.getProperty().equals(MERGE_SOURCE)) {
    				changes.add(new RemoveAxiom(ontology, p));
    			}
    			
    		}
    		changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
					df.getOWLSubClassOfAxiom(merge_source, RETIRE_ROOT))); 
			// finalize merge
			// remove parents and roles, OLD_ROLE OLD_PARENT
			// retarget inbound roles and children
			// reuse retire logic for this
			changes.addAll(finalizeMerge());
    		
    	} else {
    		
    		String editornote = "Merge into " + getRDFSLabel(merge_target).get() + "(" + merge_target.getIRI().getShortForm() + ")";
    		editornote += ", " + clientSession.getActiveClient().getUserInfo().getName();
    		
    		String designnote = "See '" + getRDFSLabel(merge_target).get() + "(" + merge_target.getIRI().getShortForm() + ")" + "'";

    		String prefix = "premerge_annotation";
    		
    		List<OWLOntologyChange> dcs = addNotes(editornote, designnote, prefix, merge_source);
    		
    		changes.addAll(dcs);
    		
    		changes.addAll(this.mergeAttrs());

            
            
    		// if workflow modeler, add MERGE_TARGET/SOURCE props and tree under pre-merged
    		
    		if (!isWorkFlowManager()) {

    			OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(MERGE_TARGET, 
    					merge_source.getIRI(), 
    					df.getOWLLiteral(merge_target.getIRI().getShortForm()));
    			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));

    			ax = df.getOWLAnnotationAssertionAxiom(MERGE_SOURCE, 
    					merge_target.getIRI(), 
    					df.getOWLLiteral(merge_source.getIRI().getShortForm()));
    			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));

    			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
    					df.getOWLSubClassOfAxiom(merge_source, PRE_MERGE_ROOT))); 
    		} else {
    			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
    					df.getOWLSubClassOfAxiom(merge_source, RETIRE_ROOT))); 
    			// finalize merge
    			// remove parents and roles, OLD_ROLE OLD_PARENT
    			// retarget inbound roles and children
    			// reuse retire logic for this
    			changes.addAll(finalizeMerge());
    		}
    		
    		
    		
    	} 
    	getOWLModelManager().applyChanges(changes);
    	
    }
    
    List<OWLOntologyChange> finalizeMerge() {
    	
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
    	
    	changes.addAll((new ReferenceReplace(getOWLModelManager())).retargetRefs(merge_source, merge_target)); 
    	
    	
    	// from retire panel, refactor
    	Set<OWLSubClassOfAxiom> sub_axioms = ontology.getSubClassAxiomsForSubClass(merge_source);
        
        for (OWLSubClassOfAxiom ax1 : sub_axioms) {
        	OWLClassExpression exp = ax1.getSuperClass();
        	changes = addParentRoleAssertions(changes, exp, merge_source);
        	changes.add(new RemoveAxiom(ontology, ax1));
        	
        }
        
        Set<OWLEquivalentClassesAxiom> equiv_axioms = ontology.getEquivalentClassesAxioms(merge_source);
        
        for (OWLEquivalentClassesAxiom ax1 : equiv_axioms) {
        	Set<OWLClassExpression> exps = ax1.getClassExpressions();
        	for (OWLClassExpression exp : exps) {
        		changes = addParentRoleAssertions(changes, exp, merge_source);
        	}
        	changes.add(new RemoveAxiom(ontology, ax1));
        	
        }  
        
        Set<OWLAnnotationAssertionAxiom> assocs = ontology.getAnnotationAssertionAxioms((OWLAnnotationSubject) merge_source.getIRI());
        
        for (OWLAnnotationAssertionAxiom ax1 : assocs) {
        	
        	if (isAssociation(ax1.getProperty())) {
        		String val = ax1.getProperty().getIRI().getShortForm() + "|"
        				+ ax1.getValue().asIRI().get().getShortForm();
        		OWLLiteral lit =  df.getOWLLiteral(val);
        		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(DEP_ASSOC, merge_source.getIRI(), lit);
        		changes.add(new AddAxiom(ontology, ax));
        		changes.add(new RemoveAxiom(ontology, ax1));
        	}
        }
    	return changes;    	
    }
    
    public List<OWLOntologyChange> mergeAttrs() {

    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

    	Map<IRI, IRI> replacementIRIMap = new HashMap<>();
    	replacementIRIMap.put(merge_source.getIRI(), merge_target.getIRI());

    	OWLObjectDuplicator dup = new OWLObjectDuplicator(getOWLModelManager().getOWLDataFactory(), replacementIRIMap);            

    	changes.addAll(duplicateClassAxioms(merge_source, dup));            
    	changes.addAll(duplicateAnnotations(merge_source, dup));
    	
    	return changes;
    }
    
    public List<OWLOntologyChange> addNotes(String editornote, String designnote, String prefix, OWLClass cls) {
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	NoteDialog dlg = new NoteDialog(currentTab(), editornote, designnote,
                prefix);

    	editornote = dlg.getEditorNote();
    	designnote = dlg.getDesignNote();       	


    	OWLLiteral val = df.getOWLLiteral(editornote);
    	OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(EDITOR_NOTE, cls.getIRI(), val);
    	changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));

    	val = df.getOWLLiteral(designnote);
    	ax = df.getOWLAnnotationAssertionAxiom(DESIGN_NOTE, cls.getIRI(), val);
    	changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
    	return changes;

    }
    
    public void completeRetire(Map<OWLAnnotationProperty, Set<String>> fixups) {		
    	
    	String editornote = "";
        String designnote = "";
        String prefix = "preretire_annotation";        
        
    	List<OWLOntologyChange> changes = addNotes(editornote, designnote, prefix, retire_class);
    	
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();    	
        
        Set<OWLSubClassOfAxiom> sub_axioms = ontology.getSubClassAxiomsForSubClass(retire_class);
        
        for (OWLSubClassOfAxiom ax1 : sub_axioms) {
        	OWLClassExpression exp = ax1.getSuperClass();
        	changes = addParentRoleAssertions(changes, exp, retire_class);
        	changes.add(new RemoveAxiom(ontology, ax1));
        	
        }
        
        Set<OWLEquivalentClassesAxiom> equiv_axioms = ontology.getEquivalentClassesAxioms(retire_class);
        
        for (OWLEquivalentClassesAxiom ax1 : equiv_axioms) {
        	Set<OWLClassExpression> exps = ax1.getClassExpressions();
        	for (OWLClassExpression exp : exps) {
        		changes = addParentRoleAssertions(changes, exp, retire_class);
        	}
        	changes.add(new RemoveAxiom(ontology, ax1));
        	
        }
        
        Set<OWLAnnotationAssertionAxiom> assocs = ontology.getAnnotationAssertionAxioms((OWLAnnotationSubject) retire_class.getIRI());
        
        for (OWLAnnotationAssertionAxiom ax1 : assocs) {
        	// TODO: check that annotation is an association
        	System.out.println("The value of the annotation is: " + ax1.getValue());
        	boolean found = false;

        	if (ax1.getProperty().isOWLAnnotationProperty()) {
        		Set<OWLAnnotationPropertyRangeAxiom> ranges = ontology.getAnnotationPropertyRangeAxioms(ax1.getProperty());
        		for (OWLAnnotationPropertyRangeAxiom rax : ranges) {
        			System.out.println("The range: " + rax.toString());
        			if (rax.getRange().getShortForm().equals("anyURI")) {
        				found = true;
        			}
        		}
        	}


        	if (found) {

        		String val = ax1.getProperty().getIRI().getShortForm() + "|"
        				+ ax1.getValue().asIRI().get().getShortForm();
        		OWLLiteral lit = df.getOWLLiteral(val);
        		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(DEP_ASSOC, retire_class.getIRI(), lit);
        		changes.add(new AddAxiom(ontology, ax));

        		changes.add(new RemoveAxiom(ontology, ax1));
        	}


        }
        
        for (OWLAnnotationProperty p : fixups.keySet()) {
        	for (String s : fixups.get(p)) {
        		OWLLiteral val1 = df.getOWLLiteral(s);
        		OWLAxiom ax1 = df.getOWLAnnotationAssertionAxiom(p, retire_class.getIRI(), val1);
        		changes.add(new AddAxiom(ontology, ax1));
        		
        	}
        	
        }
        if (currentTab().isWorkFlowManager()) {
        	changes.add(new AddAxiom(ontology,
        			df.getOWLSubClassOfAxiom(retire_class, RETIRE_ROOT)));

        } else {
        	changes.add(new AddAxiom(ontology,
        			df.getOWLSubClassOfAxiom(retire_class, PRE_RETIRE_ROOT))); 
        }
        
        getOWLModelManager().applyChanges(changes);
        
        retire_class = null;
        isRetiring = false;
        
        
        
        
        
        
        
    
    }
    
    private List<OWLOntologyChange> addParentRoleAssertions(List<OWLOntologyChange> changes, OWLClassExpression exp, OWLClass cls) {
    	OWLModelManager mngr = getOWLModelManager();
    	OWLDataFactory df = mngr.getOWLDataFactory();
    	if (exp instanceof OWLClass) {
    		if (cls.equals(exp)) {
    			// noop
    		} else {
    			OWLClass ocl  = (OWLClass) exp;
    			String name = ocl.getIRI().getShortForm();
    			OWLLiteral val = df.getOWLLiteral(name);
    			OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(DEP_PARENT, cls.getIRI(), val);
    			changes.add(new AddAxiom(ontology, ax));  
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
    		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(DEP_ROLE, cls.getIRI(), lit);
    		changes.add(new AddAxiom(ontology, ax));
    	} else if (exp instanceof OWLObjectIntersectionOf) {
    		OWLObjectIntersectionOf oio = (OWLObjectIntersectionOf) exp;
    		Set<OWLClassExpression> conjs = oio.asConjunctSet();
    		for (OWLClassExpression c : conjs) {
    			changes = addParentRoleAssertions(changes, c, cls);
    		}
    	} else if (exp instanceof OWLObjectUnionOf) {
    		OWLObjectUnionOf oio = (OWLObjectUnionOf) exp;
    		Set<OWLClassExpression> conjs = oio.asDisjunctSet();
    		for (OWLClassExpression c : conjs) {
    			changes = addParentRoleAssertions(changes, c, cls);
    		}
    	}
    	return changes;

    }
    
    /** Anyone can pre-retire so there is no need for a separate pre-retire step. There is just retire.
     * If the class is already a chle of the "PreRetired" root class, then the retirement is ready for
     * review and only an admin user can do so. 
     * 
     */
    public boolean canRetire(OWLClass cls) {
    	boolean can = clientSession.getActiveClient().canPerformProjectOperation(NCIEditTabConstants.RETIRE.getId()); 
    	if (can) {
    		if (isPreRetired(cls)) {
    			return isWorkFlowManager();    			
    		} else if (isRetired(cls)) {
    			return false;
    		}
    	}
    	return can;
    }
    
    public void retire(OWLClass selectedClass) {
    	retire_class = selectedClass;
    	isRetiring = true;
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.RETIRE)); 
    	
    }
    
    public void addComplex(OWLClass selectedClass) {
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.ADD_PROP));
    	
    }
    
    public boolean isWorkFlowManager() {    	
    	try {
    		Role wfm = ((LocalHttpClient) clientSession.getActiveClient()).getRole(new RoleIdImpl("mp-project-manager"));
			return clientSession.getActiveClient().getActiveRoles().contains(wfm);
		} catch (ClientRequestException e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    public boolean isPreRetired(OWLClass cls) {
    	return isSubClass(cls, PRE_RETIRE_ROOT);    	
    }
    
    public boolean isPreMerged(OWLClass cls) {
    	return isSubClass(cls, PRE_MERGE_ROOT);    	
    }
    
    public boolean isRetired(OWLClass cls) {
    	return isSubClass(cls, RETIRE_ROOT);
    }
    
    public boolean isSubClass(OWLClass sub, OWLClass sup) {
    	    	
    	Set<OWLSubClassOfAxiom> subs = ontology.getSubClassAxiomsForSubClass(sub);
    	for (OWLSubClassOfAxiom s : subs) {
    		if (!s.getSuperClass().isAnonymous()) {
    			if (s.getSuperClass().asOWLClass().equals(sup)) {
    				return true;
    			}
    		}    		
    	}
    	return false;    	
    }
    
    public void updateRetire() {
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.RETIRE));
    	
    }
    
    public void completeRetire() {
    	this.isRetiring = false;
    }
    
    public boolean canSplit(OWLClass cls) {
    	return !(isPreRetired(cls) || isRetired(cls));
    }
    
    public void undoChanges() {
    	while (history.canUndo()) {
    		history.undo();
    	}
    }
    
    public void commitChanges() {
    	
    	ComplexEditType type = this.getComplexEditType();
    	
    	List<OWLOntologyChange> changes = history.getUncommittedChanges();

    	if (changes.size() > 0) {
    		
    		

    		String comment = type.name();
    		Commit commit = ClientUtils.createCommit(clientSession.getActiveClient(), comment, changes);

    		DocumentRevision base;
    		try {
    			base = clientSession.getActiveVersionOntology().getHeadRevision();
    			CommitBundle commitBundle = new CommitBundleImpl(base, commit);
    			ChangeHistory hist = clientSession.getActiveClient().commit(clientSession.getActiveProject(), commitBundle);
    			clientSession.getActiveVersionOntology().update(hist);
    			resetHistory();
    			clientSession.fireCommitPerformedEvent(new CommitOperationEvent(
                        hist.getHeadRevision(),
                        hist.getMetadataForRevision(hist.getHeadRevision()),
                        hist.getChangesForRevision(hist.getHeadRevision())));
    		} catch (ClientRequestException e) {
    			showErrorDialog("Commit error", e.getMessage(), e);
    		} catch (AuthorizationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        
    }
    
    private void showErrorDialog(String title, String message, Throwable t) {
        JOptionPaneEx.showConfirmDialog(getOWLEditorKit().getWorkspace(), title, new JLabel(message),
                JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null);
    }
    
    public void putHistory(String c, String n, String op, String ref) {
    	try {
			((LocalHttpClient) clientSession.getActiveClient()).putEVSHistory(c, n, op, ref);
		} catch (ClientRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    
    
    private ComplexEditType getComplexEditType() {
    	ComplexEditType type = ComplexEditType.MODIFY;
    	if (currentTab().isRetiring()) {
    		if (currentTab().isWorkFlowManager()) {
    			type = ComplexEditType.RETIRE;
    		} else {
    			type = ComplexEditType.PRERETIRE;

    		}
    	}
    	if (currentTab().isMerging()) {
    		if (currentTab().isWorkFlowManager()) {
    			type = ComplexEditType.MERGE;
    		} else {
    			type = ComplexEditType.PREMERGE;

    		}

    	}
    	if (currentTab().isSplitting()) {
    		type = ComplexEditType.SPLIT;
    	}
    	return type;

    }
    
    public void splitClass(OWLClass newClass, List<OWLOntologyChange> changes, OWLClass selectedClass, boolean clone_p) {    	

    	OWLModelManager mngr = getOWLModelManager();
    	OWLDataFactory df = mngr.getOWLDataFactory();
    	
    	if (clone_p) {
    		// do nothing
    	} else {
    		OWLLiteral fromCode = df.getOWLLiteral(selectedClass.getIRI().getShortForm());
    		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(SPLIT_FROM, newClass.getIRI(), fromCode);
    		changes.add(new AddAxiom(ontology, ax));
    	}
    	
    	Map<IRI, IRI> replacementIRIMap = new HashMap<>();
    	replacementIRIMap.put(selectedClass.getIRI(), newClass.getIRI());

    	OWLObjectDuplicator dup = new OWLObjectDuplicator(mngr.getOWLDataFactory(), replacementIRIMap);            

    	changes.addAll(duplicateClassAxioms(selectedClass, dup));            
    	changes.addAll(duplicateAnnotations(selectedClass, dup));

    	mngr.applyChanges(changes);            

    	split_source = selectedClass;
    	split_target = newClass;

    	//logChanges(changes);
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.SPLIT)); 
    }
    
    public OWLClass createNewChild(OWLClass selectedClass, Optional<String> prefName) {

    	NCIClassCreationDialog<OWLClass> dlg = new NCIClassCreationDialog<OWLClass>(getOWLEditorKit(),
    			"Please enter a class name", OWLClass.class, prefName);

    	boolean proceed = false;

    	if (prefName.isPresent()) {
    		proceed = true;
    	} else {
    		proceed = dlg.showDialog();
    	}

    	if (proceed) {
    		OWLClass newClass = dlg.getNewClass();
    		List<OWLOntologyChange> changes = dlg.getOntChanges();
    		OWLModelManager mngr = getOWLModelManager();
    		OWLDataFactory df = mngr.getOWLDataFactory();

    		if (!df.getOWLThing().equals(selectedClass)){
    			OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(newClass, selectedClass);
    			changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
    		}

    		mngr.applyChanges(changes);

    		return newClass;
    	} else {
    		return null;
    	}

    }
        
    public boolean canClone(OWLClass cls) {
    	return canSplit(cls);
    }

	@Override
	public void dispose() {
		this.getOWLModelManager().removeListener(ont_listen);
		clientSession.removeListener(this);
		super.dispose();
		log.info("Disposed of NCI Edit Tab");
	}
	
	public void addListeners() {
		clientSession.addListener(this);
		history.addUndoManagerListener(this);
	}
	
	public void handleChange(ClientSessionChangeEvent event) {
		if (event.hasCategory(EventCategory.SWITCH_ONTOLOGY)) {
			initProperties();
			
		}
	}
	
	public void resetHistory() {
		history.reset();		
	}
	
	
	
	private void initProperties() {
		
		LocalHttpClient lhc = (LocalHttpClient) clientSession.getActiveClient();
		if (lhc != null) {
			Project project = lhc.getCurrentProject();

			if (project != null) {
				// get all annotations from ontology to use for lookup
				Set<OWLAnnotationProperty> annProps = ontology.getAnnotationPropertiesInSignature();
				
				// populate associations
				for (OWLAnnotationProperty p : annProps) {
					Set<OWLAnnotationPropertyRangeAxiom> ranges = ontology.getAnnotationPropertyRangeAxioms(p);
	        		for (OWLAnnotationPropertyRangeAxiom rax : ranges) {
	        			System.out.println("The range: " + rax.toString());
	        			if (rax.getRange().getShortForm().equals("anyURI")) {
	        				associations.add(p);
	        			}
	        		}
					
				}
				
				

				Optional<ProjectOptions> options = project.getOptions();
				
				Set<String> not_found_props = new HashSet<String>();

				if (options.isPresent()) {
					ProjectOptions opts = options.get();
					Set<String> complex_props = opts.getValues(COMPLEX_PROPS);
					if (complex_props != null) {						
						for (String cp : complex_props) {
							OWLAnnotationProperty p = lookup(cp, annProps);
							if (p != null) {
								complex_properties.add(p);
							} else {
								not_found_props.add(cp);
							}
							
							// now get dependencies
							Set<OWLAnnotationProperty> dprops = new HashSet<OWLAnnotationProperty>();
							Set<String> dependents = opts.getValues(cp);
							if (dependents != null) {
								for (String dp : dependents) {
									OWLAnnotationProperty dpProp = lookup(dp, annProps);
									if (dpProp != null) {
										dprops.add(dpProp);
									}								
								}
								required_annotation_dependencies.put(p, dprops);
							}							
						}
						if (not_found_props.size() > 0) {
							String msg = "Missing Properties: \n";
							for (String prop : not_found_props) {
								msg += prop + "\n";
							}
							JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
						}
					}
					Set<String> imm_props = opts.getValues(IMMUTABLE_PROPS);
					if (imm_props != null) {
						for (String ip : imm_props) {
							OWLAnnotationProperty p = lookup(ip, annProps);
							if (p != null) {
								immutable_properties.add(p);
							}
							
						}
					}
					
					// set constants for split/merge/retirement
					MERGE_SOURCE = getSingleProperty("merge_source", opts, annProps);
					MERGE_TARGET = getSingleProperty("merge_target", opts, annProps);
					SPLIT_FROM = getSingleProperty("split_from", opts, annProps);
					
					DEP_PARENT = getSingleProperty("deprecated_parent", opts, annProps);
					DEP_CHILD = getSingleProperty("deprecated_child", opts, annProps);
					DEP_ROLE = getSingleProperty("deprecated_role", opts, annProps);
					DEP_IN_ROLE = getSingleProperty("deprecated_in_role", opts, annProps);
					DEP_ASSOC = getSingleProperty("deprecated_assoc", opts, annProps);
					DEP_IN_ASSOC = getSingleProperty("deprecated_in_assoc", opts, annProps);
					
					PRE_MERGE_ROOT = findOWLClass("premerged_root", opts);
					PRE_RETIRE_ROOT = findOWLClass("preretired_root", opts);
					RETIRE_ROOT  = findOWLClass("retired_root", opts);
					
					DESIGN_NOTE = getSingleProperty("design_note", opts, annProps);
					EDITOR_NOTE = getSingleProperty("editor_note", opts, annProps);	
					
					CODE_PROP = getSingleProperty("code_prop", opts, annProps);
					LABEL_PROP = getSingleProperty("label_prop", opts, annProps);
					PREF_NAME = getSingleProperty("pref_name", opts, annProps);
					
					
				}

			}
			try {

				for (Operation op : clientSession.getActiveClient().getActiveOperations()) {
					System.out.println(op.toString());

				}

			} catch (ClientRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	OWLClass findOWLClass(String opt, ProjectOptions opts) {
		OWLClass cls = null;
		Set<String> ss = opts.getValues(opt);
		if (ss != null) {
			IRI iri = IRI.create((String) ss.toArray()[0]);
			Set<OWLEntity> classes = ontology.getEntitiesInSignature(iri);
			for (OWLEntity et : classes) {
				cls = et.asOWLClass();
			}
		}
		return cls;
	}
	
	OWLAnnotationProperty getSingleProperty(String ps, ProjectOptions opts, Set<OWLAnnotationProperty> annProps) {
		OWLAnnotationProperty prop = null;
		Set<String> ss = opts.getValues(ps);
		if (ss != null) {
			prop = lookup((String) ss.toArray()[0], annProps);
		}
		return prop;
	}
	
	OWLAnnotationProperty lookup(String iri, Set<OWLAnnotationProperty> annProps) {
		IRI cpIRI = IRI.create(iri);
		for (OWLAnnotationProperty ap : annProps) {
			if (ap.getIRI().equals(cpIRI)) {
				IRI dt = getDataType(ap);
				if (dt != null) {
					System.out.println(cpIRI + " it's type: " + dt);
				} else {
					System.out.println(cpIRI);

				}
				return ap;	
			}
		}
		return null;
	}
	
	public Optional<String> getRDFSLabel(OWLNamedObject oobj) {

		for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(oobj.getIRI()), ontology.getOWLOntologyManager().getOWLDataFactory()
				.getRDFSLabel())) {
			OWLAnnotationValue av = annotation.getValue();
			com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
			if (ol.isPresent()) {
				return Optional.of(ol.get().getLiteral());

			}   
		}

		return Optional.empty();		  

	}
	
	public Optional<String> getProperty(OWLNamedObject oobj, OWLAnnotationProperty prop) {
		  
		for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(oobj.getIRI()), prop)) {
			OWLAnnotationValue av = annotation.getValue();
		    com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
		    if (ol.isPresent()) {
		     return Optional.of(ol.get().getLiteral());
		     
		    }   
		}
		
		return Optional.empty();		  
		  
	}
	
	public OWLClass getClass(String code) {
		OWLClass cls = null;
		
		IRI iri = IRI.create(CODE_PROP.getIRI().getNamespace() + code);
		
		Set<OWLEntity> classes = ontology.getEntitiesInSignature(iri);
		for (OWLEntity et : classes) {
			cls = et.asOWLClass();
		}
		return cls;		
	}
	
	public IRI getDataType(OWLAnnotationProperty prop) {
		Set<OWLAnnotationPropertyRangeAxiom> types = ontology.getAnnotationPropertyRangeAxioms(prop);
		
		for (OWLAnnotationPropertyRangeAxiom ax : types) {
			return ax.getRange();
		}
		return null;
	}
	
	public String getDefaultValue(IRI iri) {
		String type = iri.getShortForm();
		if (type.equalsIgnoreCase("date-time-system")) {
			return LocalDateTime.now().toString();
		} else if (type.equalsIgnoreCase("user-system")) {
			return clientSession.getActiveClient().getUserInfo().getName().toString();
		} else if (type.endsWith("enum")) {
			return getEnumValues(iri).get(0);
		}
		return "";
	}
	
	public List<String> getEnumValues(IRI enumtype) {

		List<String> results = new ArrayList<String>();
		OWLDatatype dt = getOWLModelManager().getOWLDataFactory().getOWLDatatype(enumtype);		
		Set<OWLDatatypeDefinitionAxiom> dda = getOWLModelManager().getActiveOntology().getDatatypeDefinitions(dt);

		for (OWLDatatypeDefinitionAxiom ax : dda) {
			OWLDataOneOf done = (OWLDataOneOf) ax.getDataRange();
			Set<OWLLiteral> vals = done.getValues();
			for (OWLLiteral lit : vals) {
				results.add(lit.getLiteral());
			}

		}
		return results;
	}
	
	private List<OWLOntologyChange> duplicateClassAxioms(OWLClass selectedClass, OWLObjectDuplicator dup) {
		List<OWLOntologyChange> changes = new ArrayList<>();

		OWLOntology ont = getOWLModelManager().getActiveOntology();

		for (OWLAxiom ax : ont.getAxioms(selectedClass)) {
			if (ax.isLogicalAxiom() && !(ax instanceof OWLDisjointClassesAxiom)) {
				OWLAxiom duplicatedAxiom = dup.duplicateObject(ax);
				changes.add(new AddAxiom(ont, duplicatedAxiom));
			}
		}

		return changes;
	}

    private List<OWLOntologyChange> duplicateAnnotations(OWLClass selectedClass, OWLObjectDuplicator dup) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        OWLModelManagerEntityRenderer ren = getOWLModelManager().getOWLEntityRenderer();
        List<IRI> annotIRIs = null;
        String selectedClassName = null;
        if (ren instanceof OWLEntityAnnotationValueRenderer){
            selectedClassName = getOWLModelManager().getRendering(selectedClass);
            annotIRIs = OWLRendererPreferences.getInstance().getAnnotationIRIs();
        }

        LiteralExtractor literalExtractor = new LiteralExtractor();

        OWLOntology ont = getOWLModelManager().getActiveOntology();
        
        for (OWLAnnotationAssertionAxiom ax : EntitySearcher.getAnnotationAssertionAxioms(selectedClass, ont)) {
        	final OWLAnnotation annot = ax.getAnnotation();
        	if (annotIRIs == null || !annotIRIs.contains(annot.getProperty().getIRI())) {
        		if (okToCopy(annot.getProperty())) {
        			String label = literalExtractor.getLiteral(annot.getValue());
        			if (label == null || !label.equals(selectedClassName)){
        				OWLAxiom duplicatedAxiom = dup.duplicateObject(ax);
        				changes.add(new AddAxiom(ont, duplicatedAxiom));
        			}
        		}
        	}
        }
        
        return changes;
    }
    
    private boolean okToCopy(OWLAnnotationProperty prop) {
    	return !(prop.equals(CODE_PROP) ||
    			 prop.equals(LABEL_PROP) ||
    			 prop.equals(PREF_NAME));
    }


    class LiteralExtractor implements OWLAnnotationValueVisitor {

        private String label;

        public String getLiteral(OWLAnnotationValue value){
            label = null;
            value.accept(this);
            return label;
        }

        public void visit(IRI iri) {
            // do nothing
        }


        public void visit(OWLAnonymousIndividual owlAnonymousIndividual) {
            // do nothing
        }


        public void visit(OWLLiteral literal) {
            label = literal.getLiteral();
        }
    }


	@Override
	public void stateChanged(HistoryManager source) {
		for (List<OWLOntologyChange> changes : source.getLoggedChanges()) {
			System.out.println("A list of changes");
			for (OWLOntologyChange change : changes) {
				System.out.println("A change: " + change.toString());
			}
		}
		
	}
	
	public void complexPropOp(String operation, OWLClass cls, OWLAnnotationProperty complex_prop, 
			OWLAnnotationAssertionAxiom old_axiom, HashMap<String, String> ann_vals) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		OWLDataFactory df = getOWLModelManager().getOWLDataFactory();

		if (operation.equalsIgnoreCase(NCIEditTabConstants.EDIT)) {
			
			Set<OWLAnnotation> anns = old_axiom.getAnnotations();
			Set<OWLAnnotation> new_anns = new HashSet<OWLAnnotation>(); 
			
			for (OWLAnnotation annax : anns) {
				String cv = annax.getProperty().getIRI().getShortForm();
				String new_val = ann_vals.get(cv);
				if (new_val != null) {
					
					OWLAnnotation new_ann = df.getOWLAnnotation(annax.getProperty(), df.getOWLLiteral(new_val));
					new_anns.add(new_ann); 
				}
			}

			OWLAxiom new_axiom = df.getOWLAnnotationAssertionAxiom(old_axiom.getProperty(), cls.getIRI(),
					df.getOWLLiteral(ann_vals.get("Value")), new_anns);


			changes.add(new RemoveAxiom(ontology, old_axiom));
			changes.add(new AddAxiom(ontology, new_axiom));
		} else if (operation.equalsIgnoreCase(NCIEditTabConstants.DELETE)) {
			changes.add(new RemoveAxiom(ontology, old_axiom));
			
		} else if (operation.equalsIgnoreCase(NCIEditTabConstants.ADD)) {
			OWLAxiom new_axiom = df.getOWLAnnotationAssertionAxiom(complex_prop, cls.getIRI(), df.getOWLLiteral(ann_vals.get("Value")));
			
			Set<OWLAnnotation> anns = new HashSet<OWLAnnotation>();
			Set<OWLAnnotationProperty> req_props = this.getRequiredAnnotationsForAnnotation(complex_prop);
			
			for (OWLAnnotationProperty prop : req_props) {
				String val = ann_vals.get(prop.getIRI().getShortForm());
				if (val != null) {
					OWLAnnotation new_ann = df.getOWLAnnotation(prop, df.getOWLLiteral(val));
					anns.add(new_ann);
					
				}
			}
			
			OWLAxiom new_new_axiom = new_axiom.getAxiomWithoutAnnotations().getAnnotatedAxiom(anns);
			
			
			changes.add(new AddAxiom(ontology, new_new_axiom));
			
		}


		getOWLModelManager().applyChanges(changes);
	}
	
	// Methods needed by BatchEditTask
	
	public OWLClass getClassByName(String name) {
		OWLClass cls = null;
		return cls;
	}
	
	public Vector<String> getSupportedRoles() {
		return null;
	}
	
	public Vector<String> getSupportedAnnotationProperties() {
		return null;
	}
	
	public Vector<String> getSupportedAssociations() {
		return null;
	}
	
	public List<OWLClass> getDirectSubClasses(OWLClass cls) {
		ChildClassExtractor childClassExtractor = new ChildClassExtractor();
		
		childClassExtractor.setCurrentParentClass(cls);
		for (OWLAxiom ax : ontology.getReferencingAxioms(cls)) {
            if (ax.isLogicalAxiom()) {
                ax.accept(childClassExtractor);
            }
        }
        Set<OWLClass> cset = childClassExtractor.getResult();
       
		List<OWLClass> res = new ArrayList<OWLClass>();
		
		for (OWLClass c : cset) {
			res.add(c);
		}
		
		return res;
	}
}
