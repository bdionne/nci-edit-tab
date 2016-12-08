package gov.nih.nci.ui;

import static gov.nih.nci.ui.event.ComplexEditType.*;
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
import static gov.nih.nci.ui.NCIEditTabConstants.FULL_SYN;
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE;
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE_SOURCE;
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE_TARGET;
import static gov.nih.nci.ui.NCIEditTabConstants.PREF_NAME;
import static gov.nih.nci.ui.NCIEditTabConstants.PRE_MERGE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.PRE_RETIRE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.RETIRE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.SEMANTIC_TYPE;
import static gov.nih.nci.ui.NCIEditTabConstants.SPLIT_FROM;
import static org.semanticweb.owlapi.search.Searcher.annotationObjects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
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
import org.protege.editor.owl.client.LocalHttpClient.UserType;
import org.protege.editor.owl.client.SessionRecorder;
import org.protege.editor.owl.client.api.exception.AuthorizationException;
import org.protege.editor.owl.client.api.exception.ClientRequestException;
import org.protege.editor.owl.client.api.exception.LoginTimeoutException;
import org.protege.editor.owl.client.event.ClientSessionChangeEvent;
import org.protege.editor.owl.client.event.ClientSessionChangeEvent.EventCategory;
import org.protege.editor.owl.client.event.ClientSessionListener;
import org.protege.editor.owl.client.event.CommitOperationEvent;
import org.protege.editor.owl.client.ui.UserLoginPanel;
import org.protege.editor.owl.client.util.ClientUtils;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.history.HistoryManager;
import org.protege.editor.owl.model.history.UndoManagerListener;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultHandler;
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
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;

import edu.stanford.protege.metaproject.api.AuthToken;
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
import gov.nih.nci.utils.NCIClassSearcher;
import gov.nih.nci.utils.ParentRemover;
import gov.nih.nci.utils.ReferenceReplace;
import gov.nih.nci.utils.RoleReplacer;

public class NCIEditTab extends OWLWorkspaceViewsTab implements ClientSessionListener, UndoManagerListener {
	private static final Logger log = Logger.getLogger(NCIEditTab.class);
	private static final long serialVersionUID = -4896884982262745722L;
	
	private static NCIEditTab tab;	
	
	public static NCIEditTab currentTab() {
		return tab;
	}
	
	private static NCIToldOWLClassHierarchyViewComponent navTree = null;
	
	public static void setNavTree(NCIToldOWLClassHierarchyViewComponent t) { navTree = t;}
	
	public void refreshNavTree() {
		navTree.setSelectedEntity(this.currentlyEditing);
		navTree.refreshTree();
	}
	
	private Set<OWLAnnotationProperty> annProps = null;
	
	private OWLClass split_source;
	private OWLClass split_target;
	private OWLClass merge_source;
	private OWLClass merge_target;
	private OWLClass class_to_retire;
	
	private boolean editInProgress = false;
	private OWLClass currentlyEditing = null;
	
	
	private boolean inBatchMode = false;
	private ArrayList<OWLOntologyChange> batch_changes = new ArrayList<OWLOntologyChange>();
	
	public void applyChanges() {
		if (!batch_changes.isEmpty()) {
			this.getOWLEditorKit().getOWLModelManager().applyChanges(batch_changes);
			this.batch_changes.clear();
		}
	}
	
	
	
	public void enableBatchMode() { 
		inBatchMode = true;
		history.stopTalking();
	}
	
	public void disableBatchMode() { 
		inBatchMode = false;
		history.startTalking();
	}
	
	public boolean isEditing() {
		return editInProgress;
	}
	
	public void setEditInProgress(boolean b) {
		editInProgress = b;
		currentlyEditing = null;
	}
	
	public void setCurrentlyEditing(OWLClass cls) { 
		currentlyEditing = cls;
	}
	
	public OWLClass getCurrentlyEditing() { return currentlyEditing; }
	
	//private boolean isRetiring = false;
	
	private ComplexEditType current_op = null;
	
	public void setOp(ComplexEditType op) {
		current_op = op;
	}
	
	public boolean isRetiring() {
		return (current_op == RETIRE || current_op == PRERETIRE);
	}
	
	public boolean isMerging() {
		return (current_op == ComplexEditType.MERGE || current_op == PREMERGE);
	}
	
	public boolean isSplitting() {
		return (current_op == ComplexEditType.SPLIT);
	}
	
	public boolean isCloning() {
		return (current_op == ComplexEditType.CLONE);
	}
	
	public boolean isFree() {
		return (!this.inComplexOp() && !this.isEditing());
	}
	
	public void cancelRetire() {
		class_to_retire = null;
		current_op = null;
		editInProgress = false;
		refreshNavTree();
	}
	
	public void cancelOp() {
		if (current_op == SPLIT || current_op == CLONE) {
			cancelSplit();
		}
		if (current_op == ComplexEditType.MERGE) {
			cancelMerge();
			
		}
		current_op = null;
		
	}
	
	public void cancelSplit() {	
		setEditInProgress(false);
		// go back to previous selected class
		setCurrentlyEditing(split_source);
		//refreshNavTree();
		undoChanges();
		navTree.setSelectedEntity(split_source);
		split_source = null;
		split_target = null;
		
	}
	
	public void cancelMerge() {		
		
		setEditInProgress(false);
		// go back to previous selected class
		if (merge_source != null) {
			
			navTree.setSelectedEntity(merge_source);
			
		}
		
		if (merge_target != null) {
			
			navTree.setSelectedEntity(merge_target);
			
		}
		
		//refreshNavTree();
		undoChanges();
		
		merge_source = null;
		merge_target = null;
		
	}
	
	public void completeOp() {
		if (current_op == SPLIT || current_op == CLONE) {
			completeSplit();
		}
		if (current_op == ComplexEditType.MERGE) {
			completeMerge();
			
		}
		current_op = null;
		
	}
	
	public void completeSplit() {
		setEditInProgress(false);
		navTree.setSelectedEntity(split_source);
		split_source = null;
		split_target = null;		
	}
	
	public void completeMerge() {
		setEditInProgress(false);
		navTree.setSelectedEntity(merge_target);
		merge_source = null;
		merge_target = null;
		
	}
	
	
	
	
		
	public OWLClass getSplitSource() {
		return split_source;
	}
	
	public OWLClass getSplitTarget() {
		return split_target;
	}
	
	public OWLClass getRetireClass() {
		return class_to_retire;		
	}
	
	public OWLClass getMergeSource() {
		return merge_source;
	}
	
	public OWLClass getMergeTarget() {
		return merge_target;
	}
	
	public void setMergeSource(OWLClass cls) {
		current_op = ComplexEditType.MERGE;
		merge_source = cls;
	}
	
	public void setMergeTarget(OWLClass cls) {
		current_op = ComplexEditType.MERGE;
		merge_target = cls;
	}
	
	public boolean inComplexOp() {
		return current_op != null;
	}
		
	// use undo/redo facility
	private SessionRecorder history;
	
	private ClientSession clientSession = null;
	
	private OWLOntology ontology;
	
	private static ArrayList<EditTabChangeListener> event_listeners = new ArrayList<EditTabChangeListener>();
	
	
	
	public static void addListener(EditTabChangeListener l) {
		event_listeners.add(l);
	}
	
	public static void removeListener(EditTabChangeListener l) {
		event_listeners.remove(l);
	}
	
	public void fireChange(EditTabChangeEvent ev) {
		
		for (EditTabChangeListener l : event_listeners) {
			l.handleChange(ev);
		}		
	}	
	
	private Set<OWLAnnotationProperty> complex_properties = new HashSet<OWLAnnotationProperty>();
	
	private Set<OWLAnnotationProperty> not_equal_props = new HashSet<OWLAnnotationProperty>();
	
	private Map<OWLAnnotationProperty, Set<OWLAnnotationProperty>> configured_annotation_dependencies =
			new HashMap<OWLAnnotationProperty, Set<OWLAnnotationProperty>>();
	
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
	
	public List<String> generateCodes(int no) {
		LocalHttpClient cl = (LocalHttpClient) clientSession.getActiveClient();
		List<String> codes = new ArrayList<String>(); 
		
		try {
			codes = cl.getCodes(no);
		} catch (Exception e) {
			codes.add(UUID.randomUUID().toString());
		}
		return codes;
	}
	
	public Set<OWLAnnotationProperty> getComplexProperties() {
		return complex_properties;
	}
	
	public Set<OWLAnnotationProperty> getImmutableProperties() {
		return immutable_properties;
	}
	
	public boolean isReadOnlyProperty(String propName) {
		
		Set<OWLAnnotationProperty> props = getImmutableProperties();
		for (OWLAnnotationProperty prop : props) {
			if (prop.getIRI().getShortForm().equalsIgnoreCase(propName)) {
				return true;
			}
		}
		return false;
		
	}
	
	public Set<OWLAnnotationProperty> getRequiredAnnotationsForAnnotation(OWLAnnotationProperty annp) {
		return required_annotation_dependencies.get(annp);		
	}
	
	public Set<OWLAnnotationProperty> getConfiguredAnnotationsForAnnotation(OWLAnnotationProperty annp) {
		return configured_annotation_dependencies.get(annp);		
	}
	
	public Set<OWLAnnotationProperty> getOptionalAnnotationsForAnnotation(OWLAnnotationProperty annp) {
		return optional_annotation_dependencies.get(annp);		
	}
	
	
    @Override
	public void initialise() {    	
		super.initialise();
		log.info("NCI Edit Tab initialized");
				
		clientSession = ClientSession.getInstance(getOWLEditorKit());
		
		history = SessionRecorder.getInstance(getOWLEditorKit());
		
		addListeners();
		
		getOWLEditorKit().getOWLWorkspace().setClassSearcher(new NCIClassSearcher(this.getOWLEditorKit()));		
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
    	boolean can = clientSession.getActiveClient().getConfig().canPerformProjectOperation(MERGE.getId()); 
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
    
    public boolean merge() {
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
			changes.add(new AddAxiom(ontology, df.getDeprecatedOWLAnnotationAssertionAxiom(merge_source.getIRI())));
    		
    	} else {
    		if (switchMergeSourceTarget()) {
    			int result = JOptionPane.showOptionDialog(null, "Retiring Concept is created after the Surviving Concept. Do you want to switch them?", 
						"Switch Retiring and Surviving Concept", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (result == JOptionPane.OK_OPTION) {
					OWLClass temp = merge_target;
	    			setMergeTarget(merge_source);
	    			setMergeSource(temp);
	    			this.fireChange(new EditTabChangeEvent(this, ComplexEditType.MERGE));
					return false;
				} 
				
    		} 
    		// TODO:
    		String editornote = "Merge into " + getRDFSLabel(merge_target).get() + "(" + merge_target.getIRI().getShortForm() + ")";
    		editornote += ", " + clientSession.getActiveClient().getUserInfo().getName();
    		
    		String designnote = "See '" + getRDFSLabel(merge_target).get() + "(" + merge_target.getIRI().getShortForm() + ")" + "'";

    		//String prefix = "premerge_annotation";
    		
    		List<OWLOntologyChange> dcs = addNotes(editornote, designnote, merge_source);
    		
    		if (dcs.isEmpty()) {
    			fireChange(new EditTabChangeEvent(this, ComplexEditType.MERGE));
				return false;
    		}
    		
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
    			changes.add(new AddAxiom(ontology, df.getDeprecatedOWLAnnotationAssertionAxiom(merge_source.getIRI())));
    			// finalize merge
    			// remove parents and roles, OLD_ROLE OLD_PARENT
    			// retarget inbound roles and children
    			// reuse retire logic for this
    			changes.addAll(finalizeMerge());
    		}
    		
    		
    		
    	} 
    	getOWLModelManager().applyChanges(changes);
    	
    	return true;
    	
    }
    
    private boolean switchMergeSourceTarget() {
    	boolean switchMergeSourceTarget = false;
    	String delims = "-";
    	String mergeSourceCode = merge_source.getIRI().getShortForm();
    	String mergeTargetCode = merge_target.getIRI().getShortForm();
    	String[] sourceCdSplit = mergeSourceCode.split(delims);
    	String[] targetCdSplit = mergeTargetCode.split(delims);
    	
    	if (sourceCdSplit.length == 3 && targetCdSplit.length == 3) {
    		if (sourceCdSplit[0].equals(targetCdSplit[0]) && sourceCdSplit[2].equals(targetCdSplit[2])) {
    			int sourceCdNum = Integer.parseInt(sourceCdSplit[1]);
    			int targetCdNum = Integer.parseInt(targetCdSplit[1]);
    			if (sourceCdNum < targetCdNum) {
    				switchMergeSourceTarget = true;
    			}
    		}
    	}
    	return switchMergeSourceTarget;
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
    
    public List<OWLOntologyChange> addNotes(String editornote, String designnote, OWLClass cls) {
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	NoteDialog dlg = new NoteDialog(currentTab(), editornote, designnote);

    	if (dlg.OKBtnPressed()) {
    		editornote = dlg.getEditorNote();
    		designnote = dlg.getDesignNote();

    		OWLLiteral val = df.getOWLLiteral(editornote);
    		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(EDITOR_NOTE, cls.getIRI(), val);
    		changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));

    		val = df.getOWLLiteral(designnote);
    		ax = df.getOWLAnnotationAssertionAxiom(DESIGN_NOTE, cls.getIRI(), val);
    		changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
    	}
    	return changes;

    }
    
    public boolean completeRetire(Map<OWLAnnotationProperty, Set<String>> fixups) {
    	
    	String user = clientSession.getActiveClient().getUserInfo().getName().toString();
    	String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    	
    	String editornote = "Retired on: " + timestamp + " by " + user;
        String designnote = "Retired on: " + timestamp;
        // TODO: removing prefix, discuss with Gilberto, I think it's unnecessary
        //String prefix = "preretire_annotation";        
        
    	List<OWLOntologyChange> changes = addNotes(editornote, designnote, class_to_retire);
    	if (changes.isEmpty()) {
    		fireChange(new EditTabChangeEvent(this, ComplexEditType.RETIRE));			
			return false;    		
    	}
    	
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();    	
        
        Set<OWLSubClassOfAxiom> sub_axioms = ontology.getSubClassAxiomsForSubClass(class_to_retire);
        
        for (OWLSubClassOfAxiom ax1 : sub_axioms) {
        	OWLClassExpression exp = ax1.getSuperClass();
        	changes = addParentRoleAssertions(changes, exp, class_to_retire);
        	changes.add(new RemoveAxiom(ontology, ax1));
        	
        }
        
        Set<OWLEquivalentClassesAxiom> equiv_axioms = ontology.getEquivalentClassesAxioms(class_to_retire);
        
        for (OWLEquivalentClassesAxiom ax1 : equiv_axioms) {
        	Set<OWLClassExpression> exps = ax1.getClassExpressions();
        	for (OWLClassExpression exp : exps) {
        		changes = addParentRoleAssertions(changes, exp, class_to_retire);
        	}
        	changes.add(new RemoveAxiom(ontology, ax1));
        	
        }
        
        Set<OWLAnnotationAssertionAxiom> assocs = ontology.getAnnotationAssertionAxioms((OWLAnnotationSubject) class_to_retire.getIRI());
        
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
        		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(DEP_ASSOC, class_to_retire.getIRI(), lit);
        		changes.add(new AddAxiom(ontology, ax));

        		changes.add(new RemoveAxiom(ontology, ax1));
        	}


        }
        
        for (OWLAnnotationProperty p : fixups.keySet()) {
        	for (String s : fixups.get(p)) {
        		OWLLiteral val1 = df.getOWLLiteral(s);
        		OWLAxiom ax1 = df.getOWLAnnotationAssertionAxiom(p, class_to_retire.getIRI(), val1);
        		changes.add(new AddAxiom(ontology, ax1));
        		
        	}
        	
        }
        if (currentTab().isWorkFlowManager()) {
        	changes.add(new AddAxiom(ontology,
        			df.getOWLSubClassOfAxiom(class_to_retire, RETIRE_ROOT)));
        	changes.add(new AddAxiom(ontology, df.getDeprecatedOWLAnnotationAssertionAxiom(class_to_retire.getIRI())));

        } else {
        	changes.add(new AddAxiom(ontology,
        			df.getOWLSubClassOfAxiom(class_to_retire, PRE_RETIRE_ROOT))); 
        }
        
        getOWLModelManager().applyChanges(changes);
        
        return true;
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
    	boolean can = clientSession.getActiveClient().getConfig().canPerformProjectOperation(NCIEditTabConstants.RETIRE.getId()); 
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
    	class_to_retire = selectedClass;
    	if (isWorkFlowManager()) {
    		current_op = RETIRE;
    	} else {
    		current_op = PRERETIRE;
    	}
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.RETIRE)); 
    	
    }
    
    public void addComplex(OWLClass selectedClass) {
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.ADD_PROP));
    	
    }
    
    public void selectClass(OWLClass cls) {
    	this.currentlyEditing = cls;
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.SELECTED));
    	
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
    
    public boolean isWorkFlowModeler() {
    	return !isSysAdmin();
    }
    
    public boolean isSysAdmin() {
    	return (((LocalHttpClient) clientSession.getActiveClient()).getClientType() == UserType.ADMIN);
    	
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
    	//before ontology is open user may click on Thing
    	if (ontology != null) {

    		if ((sub != null) && sub.equals(sup)) {
    			return true;
    		}

    		Set<OWLSubClassOfAxiom> subs = ontology.getSubClassAxiomsForSubClass(sub);
    		for (OWLSubClassOfAxiom s : subs) {
    			if (!s.getSuperClass().isAnonymous()) {
    				if (s.getSuperClass().asOWLClass().equals(sup)) {
    					return true;
    				}
    			}    		
    		}
    	}
    	return false;

    }
    
    public void updateRetire() {
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.RETIRE));
    	editInProgress = false;
		refreshNavTree();
    	
    }
    
    public void completeRetire() {
    	class_to_retire = null;
        current_op = null;
    }
    
    public boolean canSplit(OWLClass cls) {
    	return !(isPreRetired(cls) || isRetired(cls)
    			|| isPreMerged(cls));
    }
    
    public void undoChanges() {
    	while (history.canUndo()) {
    		history.undo();
    	}
    	
    }
    
    public void backOutChange() {
    	if (history.canUndo()) {
    		history.undo();
    	}
    }
    
    public void commitChanges() {
    	
    	ComplexEditType type = getCurrentOp();
    	if (type == null) {
    		type = ComplexEditType.MODIFY;
    	}
    	
    	List<OWLOntologyChange> changes = history.getUncommittedChanges();

    	if (changes.size() > 0) {    		
    		try {
    			
    			doCommit(changes, type);
    			
    			getOWLEditorKit().getSearchManager().updateIndex(changes);
    			
    		} catch (ClientRequestException e) {
    			if (e instanceof LoginTimeoutException) {
                    showErrorDialog("Commit error", e.getMessage(), e);
                    Optional<AuthToken> authToken = UserLoginPanel.showDialog(getOWLEditorKit(), getOWLEditorKit().getWorkspace());
                    if (authToken.isPresent() && authToken.get().isAuthorized()) {
                    	try {
							doCommit(changes, type);
						} catch (Exception e1) {
							
							showErrorDialog("Retry of commit failed", e1.getMessage(), e1);
						}
                        
                    }
                }
                else {
                    showErrorDialog("Commit error", e.getMessage(), e);
                }
    			
    		} catch (AuthorizationException e) {
    			showErrorDialog("This should not occur", e.getMessage(), e);
			}
    	}
        
    }
    
    private void doCommit(List<OWLOntologyChange> changes, ComplexEditType type) throws ClientRequestException, 
    AuthorizationException, ClientRequestException {
    	String comment = "";
    	comment = type.name();
    	if (!this.inBatchMode) {
    		String label = getRDFSLabel(currentlyEditing).get();
    		if (type == MODIFY) {
    			comment = label + "(" +
    					currentlyEditing.getIRI().getShortForm() + ") - " +
    					type.name();
    		} else if (type == SPLIT)  {
    			comment = label + "(" +
    					split_source.getIRI().getShortForm() + " -> " +
    					split_target.getIRI().getShortForm() + ") - " +
    					type.name();

    		} else if (type == ComplexEditType.MERGE)  {
    			comment = label + "(" +
    					merge_source.getIRI().getShortForm() + " -> " +
    					merge_target.getIRI().getShortForm() + ") - " +
    					type.name();

    		} else if (type == ComplexEditType.RETIRE)  {
    			comment = label + "(" +
    					this.class_to_retire.getIRI().getShortForm() + ") - " +
    					type.name();

    		}
    	}
    	
		Commit commit = ClientUtils.createCommit(clientSession.getActiveClient(), comment, changes);
		DocumentRevision base = clientSession.getActiveVersionOntology().getHeadRevision();
		CommitBundle commitBundle = new CommitBundleImpl(base, commit);
		ChangeHistory hist = clientSession.getActiveClient().commit(clientSession.getActiveProject(), commitBundle);
		clientSession.getActiveVersionOntology().update(hist);
		resetHistory();
		clientSession.fireCommitPerformedEvent(new CommitOperationEvent(
                hist.getHeadRevision(),
                hist.getMetadataForRevision(hist.getHeadRevision()),
                hist.getChangesForRevision(hist.getHeadRevision())));
		JOptionPane.showMessageDialog(this, "Class saved successfully", "Class Save", JOptionPane.INFORMATION_MESSAGE);
		fireChange(new EditTabChangeEvent(this, ComplexEditType.COMMIT));
		
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
    
    
    
    public ComplexEditType getCurrentOp() {
    	return this.current_op;
    }
    
    public void splitClass(OWLClass newClass, OWLClass selectedClass, boolean clone_p) {    	

    	OWLModelManager mngr = getOWLModelManager();
    	OWLDataFactory df = mngr.getOWLDataFactory();
    	
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	
    	
    	
    	if (clone_p) {
    		
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

    	split_source = selectedClass;
    	split_target = newClass;

    	this.fireChange(new EditTabChangeEvent(this, current_op));
    	
    	setEditInProgress(true);
		setCurrentlyEditing(split_target);
		refreshNavTree();
    	
    	mngr.applyChanges(changes); 
    }
    
    public OWLClass createNewChild(OWLClass selectedClass, Optional<String> prefName, Optional<String> code, boolean dontApply) {

    	NCIClassCreationDialog<OWLClass> dlg = new NCIClassCreationDialog<OWLClass>(getOWLEditorKit(),
    			"Please enter a class name", OWLClass.class, prefName, code, dontApply);

    	boolean proceed = false;

    	if (prefName.isPresent()) {
    		proceed = true;
    	} else {
    		proceed = dlg.showDialog();
    	}

    	if (proceed) {
    		OWLClass newClass = dlg.getNewClass();
    		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    		if (dontApply) {
    			changes = dlg.getOntChanges();
    		}
    		OWLModelManager mngr = getOWLModelManager();
    		OWLDataFactory df = mngr.getOWLDataFactory();

    		if (!df.getOWLThing().equals(selectedClass)){
    			OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(newClass, selectedClass);
    			changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
    			
    			Optional<OWLAnnotationValue> sem_typ = this.getSemanticType(selectedClass);
    			if (sem_typ.isPresent()) {
    				OWLAnnotationValue sem_typ_val = sem_typ.get();
    				OWLAxiom sem_typ_ax = 
    						df.getOWLAnnotationAssertionAxiom(SEMANTIC_TYPE, newClass.getIRI(), sem_typ_val);
    				changes.add(new AddAxiom(mngr.getActiveOntology(), sem_typ_ax));
    			}
    		}
    		if (dontApply) {
    			this.batch_changes.addAll(changes);
    		} else {
    			mngr.applyChanges(changes);
    		}

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
		clientSession.removeListener(this);
		super.dispose();
		log.info("Disposed of NCI Edit Tab");
	}
	
	public void addListeners() {
		clientSession.addListener(this);
		history.addUndoManagerListener(this);
	}
	
	public void handleChange(ClientSessionChangeEvent event) {
		
		if (event.hasCategory(EventCategory.OPEN_PROJECT)) {
			ontology = getOWLModelManager().getActiveOntology();
			initProperties();
			fireUpViews();
		}
	}

    @Override
    public void stateChanged(HistoryManager source) {
    	if (history.getLoggedChanges().isEmpty()) {

    	} else {
    		if (!inBatchMode) {
    			// TODO: Need to filter out events coming from Annotation and Entities tabs
    			//if (editInProgress)
    			if (!inComplexOp() ||
    					isRetiring())
    				fireChange(new EditTabChangeEvent(this, ComplexEditType.MODIFY));    			
    		}
    	}
    }
	
	public void resetHistory() {
		history.reset();		
	}
	
	private void initProperties() {
		
		getOWLEditorKit().getSearchManager().disableIncrementalIndexing();
		
		LocalHttpClient lhc = (LocalHttpClient) clientSession.getActiveClient();
		if (lhc != null) {
			Project project = lhc.getCurrentProject();

			if (project != null) {
				// get all annotations from ontology to use for lookup
				annProps = ontology.getAnnotationPropertiesInSignature();
				
				// populate associations
				for (OWLAnnotationProperty p : annProps) {
					Set<OWLAnnotationPropertyRangeAxiom> ranges = ontology.getAnnotationPropertyRangeAxioms(p);
	        		for (OWLAnnotationPropertyRangeAxiom rax : ranges) {
	        			//System.out.println("The range: " + rax.toString());
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
							OWLAnnotationProperty p = lookUp(cp);
							if (p != null) {
								complex_properties.add(p);
							} else {
								not_found_props.add(cp);
							}
							
							// now get dependencies
							// cprops are what is in the config file
							Set<OWLAnnotationProperty> cprops = new HashSet<OWLAnnotationProperty>();
							// dprops are required, they have the required annotation set to true
							Set<OWLAnnotationProperty> dprops = new HashSet<OWLAnnotationProperty>();
							// oprops are options, they do not have the rquired annotation or they have it set to false
							Set<OWLAnnotationProperty> oprops = new HashSet<OWLAnnotationProperty>();
							Set<String> dependents = opts.getValues(cp);
							if (dependents != null) {
								for (String dp : dependents) {
									OWLAnnotationProperty dpProp = lookUp(dp);
									if (dpProp != null) {
										// always add to cprops
										cprops.add(dpProp);
										if (is_required(dpProp)) {
											dprops.add(dpProp);
										} else {
											oprops.add(dpProp);
										}
									} else {
										not_found_props.add(dp);
									}								
								}
								configured_annotation_dependencies.put(p, cprops);
								required_annotation_dependencies.put(p, dprops);
								optional_annotation_dependencies.put(p, oprops);
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
							OWLAnnotationProperty p = lookUp(ip);
							if (p != null) {
								immutable_properties.add(p);
							}
							
						}
					}
					
					// set constants for split/merge/retirement
					MERGE_SOURCE = getSingleProperty("merge_source", opts);
					MERGE_TARGET = getSingleProperty("merge_target", opts);
					SPLIT_FROM = getSingleProperty("split_from", opts);
					
					DEP_PARENT = getSingleProperty("deprecated_parent", opts);
					DEP_CHILD = getSingleProperty("deprecated_child", opts);
					DEP_ROLE = getSingleProperty("deprecated_role", opts);
					DEP_IN_ROLE = getSingleProperty("deprecated_in_role", opts);
					DEP_ASSOC = getSingleProperty("deprecated_assoc", opts);
					DEP_IN_ASSOC = getSingleProperty("deprecated_in_assoc", opts);
					
					PRE_MERGE_ROOT = findOWLClass("premerged_root", opts);
					PRE_RETIRE_ROOT = findOWLClass("preretired_root", opts);
					RETIRE_ROOT  = findOWLClass("retired_root", opts);
					
					DESIGN_NOTE = getSingleProperty("design_note", opts);
					EDITOR_NOTE = getSingleProperty("editor_note", opts);	
					
					CODE_PROP = getSingleProperty("code_prop", opts);
					LABEL_PROP = getSingleProperty("label_prop", opts);
					FULL_SYN = getSingleProperty("fully_qualified_syn", opts);
					immutable_properties.add(LABEL_PROP);
					PREF_NAME = getSingleProperty("pref_name", opts);
					
					SEMANTIC_TYPE = getSingleProperty("semantic_type", opts); 
					
					
				}

			}
			try {

				for (Operation op : clientSession.getActiveClient().getActiveOperations()) {
					//System.out.println(op.toString());

				}

			} catch (ClientRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.fireChange(new EditTabChangeEvent(this, ComplexEditType.INIT_PROPS));
		}
		
	}
	
	private boolean is_required(OWLAnnotationProperty prop) {
		OWLAnnotationProperty p = this.lookUpShort("required");
		Optional<String> b_val = getPropertyValue(prop, p);
		if (b_val.isPresent()) {
			return Boolean.parseBoolean(b_val.get());
		}
		return false;
	}
	
	public String getDefault(OWLDatatype prop) {
		if (prop == null) {return null;}
		OWLAnnotationProperty p = this.lookUpShort("default");
		Optional<String> val = getPropertyValue(prop, p);
		if (val.isPresent()) {
			return val.get();
		}
		return null;
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
	
	OWLAnnotationProperty getSingleProperty(String ps, ProjectOptions opts) {
		OWLAnnotationProperty prop = null;
		Set<String> ss = opts.getValues(ps);
		if (ss != null) {
			prop = lookUp((String) ss.toArray()[0]);
		}
		return prop;
	}
	
	OWLAnnotationProperty lookUp(String iri) {
		IRI cpIRI = IRI.create(iri);
		for (OWLAnnotationProperty ap : annProps) {
			if (ap.getIRI().equals(cpIRI)) {
				IRI dt = getDataType(ap);
				if (dt != null) {
					//System.out.println(cpIRI + " it's type: " + dt);
				} else {
					//System.out.println(cpIRI);

				}
				return ap;	
			}
		}
		return null;
	}
	
	OWLDatatype lookUpDataType(String iri) {
		IRI cpIRI = IRI.create(iri);
		Set<OWLDatatype> d_types = ontology.getDatatypesInSignature();
		
		for (OWLDatatype d_t : d_types) {
			if (d_t.getIRI().getShortForm().equals(iri)) {
				return d_t;
			}
		}
		return null;
		
	}
	
	public OWLAnnotationProperty lookUpShort(String shortName) {
		
		for (OWLAnnotationProperty ap : annProps) {
			if (ap.getIRI().getShortForm().equals(shortName)) {				
				return ap;	
			}
		}
		return null;
		
	}
	
	public boolean supportsProperty(String shortPropName) {
		return lookUpShort(shortPropName) != null;
	}
	
	public boolean hasPropertyValue(OWLClass cls, String propName, String value) {
		OWLAnnotationProperty prop = lookUpShort(propName);
		List<String> values = getPropertyValues(cls, prop);
		return values.contains(value);		
	}
	
	public boolean hasParent(OWLClass cls, OWLClass par_cls, String type) {
		if (type.equalsIgnoreCase("P")) {
			Set<OWLSubClassOfAxiom> sub_axioms = ontology.getSubClassAxiomsForSubClass(cls);
	        
	        for (OWLSubClassOfAxiom ax1 : sub_axioms) {
	        	OWLClassExpression exp = ax1.getSuperClass();
	        	
	        	if (getParent(exp, par_cls) != null) {
	        		return true;
	        	}        	
	        	
	        }
	        return false;
			
		} else if (type.equalsIgnoreCase("D")) {
			Set<OWLEquivalentClassesAxiom> equiv_axioms = ontology.getEquivalentClassesAxioms(cls);
	        
	        for (OWLEquivalentClassesAxiom ax1 : equiv_axioms) {
	        	Set<OWLClassExpression> exps = ax1.getClassExpressions();
	        	for (OWLClassExpression exp : exps) {
	        		if (getParent(exp, par_cls) != null) {
		        		return true;
		        	}
	        		
	        	}
	        }
	        return false;
			
		} else {
			return false;
		}
	}
	
	private OWLClass getParent(OWLClassExpression exp, OWLClass par_cls) {
		
		OWLClass result = null;
		
    	if (exp instanceof OWLClass) {
    		if (exp.asOWLClass().equals(par_cls)) {
    			result = par_cls;
    		}
    		
    	} else if (exp instanceof OWLQuantifiedObjectRestriction) {
    		    		
    	} else if (exp instanceof OWLObjectIntersectionOf) {
    		OWLObjectIntersectionOf oio = (OWLObjectIntersectionOf) exp;
    		Set<OWLClassExpression> conjs = oio.asConjunctSet();
    		for (OWLClassExpression c : conjs) {
    			result = getParent(c, par_cls);
    		}
    	} else if (exp instanceof OWLObjectUnionOf) {
    		OWLObjectUnionOf oio = (OWLObjectUnionOf) exp;
    		Set<OWLClassExpression> conjs = oio.asDisjunctSet();
    		for (OWLClassExpression c : conjs) {
    			result = getParent(c, par_cls);
    		}
    	}
    	return result;
		
	}
	
	
	public boolean hasRole(OWLClass cls, String roleName, String mod, String filler, String type) {
		if (type.equalsIgnoreCase("P")) {
			Set<OWLSubClassOfAxiom> sub_axioms = ontology.getSubClassAxiomsForSubClass(cls);
	        
	        for (OWLSubClassOfAxiom ax1 : sub_axioms) {
	        	OWLClassExpression exp = ax1.getSuperClass();
	        	
	        	if (getRole(exp, roleName, mod, filler) != null) {
	        		return true;
	        	}
	        	
	        	
	        }
	        return false;
			
		} else if (type.equalsIgnoreCase("D")) {
			Set<OWLEquivalentClassesAxiom> equiv_axioms = ontology.getEquivalentClassesAxioms(cls);
	        
	        for (OWLEquivalentClassesAxiom ax1 : equiv_axioms) {
	        	Set<OWLClassExpression> exps = ax1.getClassExpressions();
	        	for (OWLClassExpression exp : exps) {
	        		if (getRole(exp, roleName, mod, filler) != null) {
		        		return true;
		        	}
	        		
	        	}
	        }
	        return false;
			
		} else {
			return false;
		}
	}
	
	private OWLQuantifiedObjectRestriction getRole(OWLClassExpression exp, String roleName,
			String modifier, String filler) {
		OWLQuantifiedObjectRestriction result = null;
    	if (exp instanceof OWLClass) {
    		
    	} else if (exp instanceof OWLQuantifiedObjectRestriction) {
    		
    		
    		OWLQuantifiedObjectRestriction qobj = (OWLQuantifiedObjectRestriction) exp;
    		
    		if (!qobj.getProperty().asOWLObjectProperty().getIRI().getShortForm().equalsIgnoreCase(roleName)) {
    			return null;
    		};
    		
    		OWLClassExpression rexp = qobj.getFiller();

    		if (rexp instanceof OWLClass) {
    			if (!((OWLClass) rexp).getIRI().getShortForm().equals(filler)) {
    				return null;
    			};
    		} else {
    			return null;
    		}

    		if ((exp instanceof OWLObjectSomeValuesFrom) &&
    				!modifier.equalsIgnoreCase("some")) {
    			return null;
    		} else if ((exp instanceof OWLObjectAllValuesFrom) &&
    				!modifier.equalsIgnoreCase("only")) {
    			return null;
    		}
    		
    		result = qobj;
    		
    	} else if (exp instanceof OWLObjectIntersectionOf) {
    		OWLObjectIntersectionOf oio = (OWLObjectIntersectionOf) exp;
    		Set<OWLClassExpression> conjs = oio.asConjunctSet();
    		for (OWLClassExpression c : conjs) {
    			result = getRole(c, roleName, modifier, filler);
    		}
    	} else if (exp instanceof OWLObjectUnionOf) {
    		OWLObjectUnionOf oio = (OWLObjectUnionOf) exp;
    		Set<OWLClassExpression> conjs = oio.asDisjunctSet();
    		for (OWLClassExpression c : conjs) {
    			result = getRole(c, roleName, modifier, filler);
    		}
    	}
    	return result;
		
	}
	
	public void removeParent(OWLClass cls, OWLClass par_cls, String type) {
		ParentRemover par_rem = new ParentRemover(getOWLEditorKit().getModelManager());
		List<OWLOntologyChange> changes = par_rem.removeParent(cls, par_cls, type);
		getOWLModelManager().applyChanges(changes);

	}

	public void addParent(OWLClass cls, OWLClass par_cls, String type) {
		OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		
		OWLAxiom ax = null;		
		
		if (type.equalsIgnoreCase("P")) {
			ax = df.getOWLSubClassOfAxiom(cls, par_cls);
			
		} else if (type.equalsIgnoreCase("D")) {
			ax = df.getOWLEquivalentClassesAxiom(cls, par_cls);			
		}
		
		changes.add(new AddAxiom(ontology, ax));
		
		this.getOWLEditorKit().getModelManager().applyChanges(changes);

	}
	
	public void removeRole(OWLClass cls, String roleName, String modifier, String filler, String type) {
		RoleReplacer role_rep = new RoleReplacer(getOWLEditorKit().getModelManager());
		List<OWLOntologyChange> changes = role_rep.removeRole(cls, roleName, modifier, filler, type);
		getOWLModelManager().applyChanges(changes);

	}

	public void addRole(OWLClass cls, String roleName, String modifier, String filler, String type) {
		
		OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		
		OWLObjectProperty o_p = getRoleShort(roleName);
		OWLClass fill_cls = getClass(filler);
		
		OWLClassExpression exp = null;
		
		if (modifier.equals("some")) {
			exp = df.getOWLObjectSomeValuesFrom(o_p, fill_cls);			
		} else {
			exp = df.getOWLObjectAllValuesFrom(o_p, fill_cls);			
		}
		
		OWLAxiom ax = null;
		
		
		if (type.equalsIgnoreCase("P")) {
			ax = df.getOWLSubClassOfAxiom(cls, exp);
			
		} else if (type.equalsIgnoreCase("D")) {
			ax = df.getOWLEquivalentClassesAxiom(cls, exp);			
		}
		
		changes.add(new AddAxiom(ontology, ax));
		
		this.getOWLEditorKit().getModelManager().applyChanges(changes);

	}
	
	public void modifyRole(OWLClass cls, String roleName, String modifier, String filler, String type,
			String new_modifier, String new_filler, String new_type) {
		if (type.equals(new_type)) {
			// no type change
			RoleReplacer role_rep = new RoleReplacer(getOWLModelManager());
			List<OWLOntologyChange> changes = role_rep.modifyRole(cls, roleName, modifier, filler, type, 
					new_modifier, new_filler, new_type);
			this.getOWLEditorKit().getModelManager().applyChanges(changes);
		} else {
			removeRole(cls, roleName, modifier, filler, type);
			addRole(cls, roleName, new_modifier, new_filler, new_type);
		}

	}
	
	public void removeComplexAnnotationProperty(OWLClass cls, String propName, 
			String value, Map<String, String> annotations) {
		
		OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		
		OWLAnnotationAssertionAxiom ann_ax = getComplexPropertyValueAssertion(cls, propName, value, annotations);
		
        changes.add(new RemoveAxiom(ontology, ann_ax));
		
		getOWLModelManager().applyChanges(changes);
		
	}
	
	public void addComplexAnnotationProperty(OWLClass cls, String propName, 
			String value, Map<String, String> annotations) {
		
		OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		
		OWLAnnotationProperty complex_prop = lookUpShort(propName);
		
		OWLAxiom new_axiom = df.getOWLAnnotationAssertionAxiom(complex_prop, cls.getIRI(), df.getOWLLiteral(value));

		Set<OWLAnnotation> anns = new HashSet<OWLAnnotation>();
		Set<OWLAnnotationProperty> req_props = getConfiguredAnnotationsForAnnotation(complex_prop);

		for (OWLAnnotationProperty prop : req_props) {
			String val = annotations.get(prop.getIRI().getShortForm());
			if (val != null) {
				OWLAnnotation new_ann = df.getOWLAnnotation(prop, df.getOWLLiteral(val));
				anns.add(new_ann);
			}
		}

		OWLAxiom new_new_axiom = new_axiom.getAxiomWithoutAnnotations().getAnnotatedAxiom(anns);

		changes.add(new AddAxiom(ontology, new_new_axiom));
		
		getOWLModelManager().applyChanges(changes);

	}
	
	public boolean hasComplexPropertyValue(OWLClass cls, String propName, String value, Map<String, String> annotations) {
		
		return (getComplexPropertyValueAssertion(cls, propName, 
				value, annotations) != null);
		
	}
	
	public OWLAnnotationAssertionAxiom getComplexPropertyValueAssertion(OWLClass cls, String propName, String value, Map<String, String> annotations) {
		OWLAnnotationProperty prop = lookUpShort(propName);
		boolean found = false;
		for (OWLAnnotationAssertionAxiom ann_ax : ontology.getAnnotationAssertionAxioms(cls.getIRI())) {
			OWLAnnotation ann = ann_ax.getAnnotation();
			if (ann.getProperty().equals(prop)) {
				OWLAnnotationValue av = ann.getValue();
				com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
				if (ol.isPresent()) {
					if (ol.get().getLiteral().equals(value)) {
						// we have a possible
						found = true;
						Set<OWLAnnotation> quals = ann_ax.getAnnotations();
						for (OWLAnnotation q_a : quals) {
							String name = q_a.getProperty().getIRI().getShortForm();
							String val = q_a.getValue().asLiteral().get().getLiteral();
							if (annotations.containsKey(name)) {
								if (val.equals(annotations.get(name))) {
									// ok
								} else {
									found = false;
								}
							}
						}
						if (found) {
							return ann_ax;
						}
					}
				}
			}
		}
		return null;
	}
		
	
	private boolean topOrBot(OWLNamedObject obj) {
		if (getOWLEditorKit().getOWLModelManager().getOWLDataFactory().getOWLThing().equals(obj) ||
				getOWLEditorKit().getOWLModelManager().getOWLDataFactory().getOWLNothing().equals(obj)) {
			return true;
		}
		return false;
	}
	
	public Optional<String> getRDFSLabel(OWLNamedObject oobj) {
		// TODO: fall back to IRI if no label
		if (oobj == null) {
			return Optional.empty();
		}
		if (topOrBot(oobj)) {
			return Optional.of(oobj.getIRI().getShortForm());			
		}
		if (ontology != null) {
			for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(oobj.getIRI()), ontology.getOWLOntologyManager().getOWLDataFactory()
					.getRDFSLabel())) {
				OWLAnnotationValue av = annotation.getValue();
				com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
				if (ol.isPresent()) {
					return Optional.of(ol.get().getLiteral());
				}
			}
		}

		if (!topOrBot(oobj)) {

			JOptionPane.showMessageDialog(this, oobj.getIRI().getShortForm() + " requires an rdfs:label, using IRI short form instead",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}
		return Optional.of(oobj.getIRI().getShortForm());


	}

	public Optional<String> getCode(OWLNamedObject oobj) {
		// TODO: fall back to IRI if no label
		for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(oobj.getIRI()), NCIEditTabConstants.CODE_PROP)) {
			OWLAnnotationValue av = annotation.getValue();
			com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
			if (ol.isPresent()) {
				return Optional.of(ol.get().getLiteral());
			}
		}

		if (!topOrBot(oobj)) {

			JOptionPane.showMessageDialog(this, oobj.getIRI().getShortForm() + " should have a code property, using IRI short form instead",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}
		return Optional.of(oobj.getIRI().getShortForm());

	}
	
	public Optional<OWLAnnotationValue> getSemanticType(OWLClass cls) {
		
		for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(cls.getIRI()), SEMANTIC_TYPE)) {
			OWLAnnotationValue av = annotation.getValue();
			return Optional.of(av);
			
		}
		return Optional.empty();		

	}
	
	public OWLAnnotation createAnnotation(String name, String value) {
		
		OWLDataFactory df = getOWLModelManager().getOWLDataFactory();		
        return df.getOWLAnnotation(tab.lookUpShort(name), df.getOWLLiteral(value));
		
	}
	
	public Set<OWLAnnotation> getAnnotations(OWLClass cls) {
		Set<OWLAnnotation> res = new HashSet<OWLAnnotation>();

		for (OWLAnnotationAssertionAxiom ax : EntitySearcher.getAnnotationAssertionAxioms(cls, ontology)) {
			res.add(ax.getAnnotation());
		}
		
		return res;			
	}
	
	public Set<OWLAnnotation> getDependentAnnotations(OWLClass cls, OWLAnnotationProperty prop) {
		
		for (OWLAnnotationAssertionAxiom ax : EntitySearcher.getAnnotationAssertionAxioms(cls, ontology)) {
			OWLAnnotation annot = ax.getAnnotation();
			if (annot.getProperty().equals(prop)) {
				return ax.getAnnotations();				
			}
		}
		return new HashSet<OWLAnnotation>();
	}
	
	// TODO: Need an all props here
	
	public List<String> getPropertyValues(OWLNamedObject oobj, OWLAnnotationProperty prop) {
		
		List<String> values = new ArrayList<String>();
		  
		for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(oobj.getIRI()), prop)) {
			OWLAnnotationValue av = annotation.getValue();
			com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
			if (ol.isPresent()) {
				values.add(ol.get().getLiteral());
			}   
		}
		
		return values;		  
		  
	}
	
	public Optional<String> getPropertyValue(OWLNamedObject oobj, OWLAnnotationProperty prop) {
		  
		for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(oobj.getIRI()), prop)) {
			OWLAnnotationValue av = annotation.getValue();
		    com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
		    if (ol.isPresent()) {
		     return Optional.of(ol.get().getLiteral());
		     
		    }   
		}
		
		return Optional.empty();		  
		  
	}
	
	public void addAnnotationToClass(OWLClass ocl, OWLAnnotationProperty prop, String value) {
		
		OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		
		OWLLiteral lit_val = df.getOWLLiteral(value);
		
		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(prop, ocl.getIRI(), lit_val);
		
		changes.add(new AddAxiom(ontology, ax));
		
		this.getOWLEditorKit().getModelManager().applyChanges(changes);
	}
	
	public void removeAnnotationToClass(OWLClass ocl, OWLAnnotationProperty prop, String value) {
		
		OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		
		OWLLiteral lit_val = df.getOWLLiteral(value);
		
		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(prop, ocl.getIRI(), lit_val);
		
		changes.add(new RemoveAxiom(ontology, ax));
		
		this.getOWLEditorKit().getModelManager().applyChanges(changes);
		
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
			return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		} else if (type.equalsIgnoreCase("user-system")) {
			return clientSession.getActiveClient().getUserInfo().getName().toString();
		} else if (type.endsWith("enum")) {
			return getDefault(lookUpDataType(type));
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


	
    public boolean complexPropOp(String operation, OWLClass cls, OWLAnnotationProperty complex_prop, 
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


    		Set<OWLAnnotationProperty> req_props = this.getConfiguredAnnotationsForAnnotation(complex_prop);

    		for (OWLAnnotationProperty prop : req_props) {
    			String new_val = ann_vals.get(prop.getIRI().getShortForm());

    			if (new_val != null) {
    				OWLAnnotation new_ann = df.getOWLAnnotation(prop, df.getOWLLiteral(new_val));
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
    		Set<OWLAnnotationProperty> req_props = getConfiguredAnnotationsForAnnotation(complex_prop);

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
    	
    	if (checkForDups(cls, complex_prop) && syncFullSyn(cls, complex_prop)) {
    		return true;
    	} else {
    		this.backOutChange();
    		return false;
    	}
    	
    }
	
	// Methods needed by BatchEditTask
	
	public OWLClass getClassByName(String name) {
		OWLClass cls = null;
		return cls;
	}
	
	public Set<String> getSupportedRoles() {
		Set<OWLObjectProperty> o_props = ontology.getObjectPropertiesInSignature();
		Set<String> res = new HashSet<String>();
		for (OWLObjectProperty op : o_props) {
			res.add(op.getIRI().getShortForm());
		}
		
		return res;
	}
	
	public OWLObjectProperty getRoleShort(String roleName) {
		Set<OWLObjectProperty> o_props = ontology.getObjectPropertiesInSignature();
		
		for (OWLObjectProperty op : o_props) {
			if (op.getIRI().getShortForm().equals(roleName)) {
				return op;
			}
		}
		
		return null;
	}
	
	public Set<String> getSupportedAssociations() {
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
	
	public List<OWLClass> getDirectSuperClasses(OWLClass cls) {
		Set<OWLSubClassOfAxiom> sups = ontology.getSubClassAxiomsForSubClass(cls);
		List<OWLClass> res = new ArrayList<OWLClass>();
		for (OWLSubClassOfAxiom ax : sups) {
			if (!ax.getSuperClass().isAnonymous()) {
				res.add(ax.getSuperClass().asOWLClass());
			}
		}
		return res;
	}
	
	
	Set<String> getLogicalRes(OWLClass cls, String type) {
		Set<String> res = new HashSet<String>();
		Set<OWLSubClassOfAxiom> sub_axioms = ontology.getSubClassAxiomsForSubClass(cls);

		for (OWLSubClassOfAxiom ax1 : sub_axioms) {
			OWLClassExpression exp = ax1.getSuperClass();
			res = getParentRoleExps(res, exp, type, false, cls);

		}

		Set<OWLEquivalentClassesAxiom> equiv_axioms = ontology.getEquivalentClassesAxioms(cls);

		for (OWLEquivalentClassesAxiom ax1 : equiv_axioms) {
			Set<OWLClassExpression> exps = ax1.getClassExpressions();
			for (OWLClassExpression exp : exps) {
				res = getParentRoleExps(res, exp, type, true, cls);
			}
		}
		return res;

	}
	
	private boolean isParentType(String type) {
		return type.equalsIgnoreCase("parents");
	}
	
	private boolean isRoleType(String type) {
		return type.equalsIgnoreCase("roles");
	}
	
    
    private Set<String> getParentRoleExps(Set<String> res, OWLClassExpression exp, String type,
    		boolean defined, OWLClass cls) {
    	
    	if ((exp instanceof OWLClass) && (isParentType(type)) &&
    			!(exp.asOWLClass().equals(cls))) {
    		Optional<String> label = getRDFSLabel(exp.asOWLClass());
    		if (label.isPresent()) {
    			res.add(label.get());
    		} else {
    			res.add(exp.asOWLClass().getIRI().getShortForm());
    		}
    	} else if ((exp instanceof OWLQuantifiedObjectRestriction) &&
    			(isRoleType(type))) {

    		OWLQuantifiedObjectRestriction qobj = (OWLQuantifiedObjectRestriction) exp;
    		OWLClassExpression rexp = qobj.getFiller();

    		String fval;
    		if (rexp instanceof OWLClass) {
    			fval = ((OWLClass) rexp).getIRI().getShortForm();
    		} else {
    			fval = rexp.toString();
    		}

    		String quant = "some";
    		if (exp instanceof OWLObjectSomeValuesFrom) {
    			quant = "some";
    		} else if (exp instanceof OWLObjectAllValuesFrom) {
    			quant = "only";
    		}
    		String val = quant + " " + qobj.getProperty().asOWLObjectProperty().getIRI().getShortForm() + " "
    				+ fval;
    		
    		if (defined) {
    			val += " [defined]";
    		}

    		res.add(val);

    	} else if (exp instanceof OWLObjectIntersectionOf) {
    		OWLObjectIntersectionOf oio = (OWLObjectIntersectionOf) exp;
    		Set<OWLClassExpression> conjs = oio.asConjunctSet();
    		for (OWLClassExpression c : conjs) {
    			res = getParentRoleExps(res, c, type, defined, cls);
    		}
    	} else if (exp instanceof OWLObjectUnionOf) {
    		OWLObjectUnionOf oio = (OWLObjectUnionOf) exp;
    		Set<OWLClassExpression> conjs = oio.asDisjunctSet();
    		for (OWLClassExpression c : conjs) {
    			res = getParentRoleExps(res, c, type, defined, cls);
    		}
    	}
    	return res;

    }
    
    // make sure there is a FULL_SYN property with group PT and an rdfs:label
    // that has the same value as the preferred_name property
    // TODO: Add FULL_SYN without creating cycle
    public void syncPrefName(String preferred_name) {
    	List<OWLOntologyChange> changes = new ArrayList<>();
    	//retrieve rdfs:label and adjust if needed
    	if (!getRDFSLabel(currentlyEditing).equals(preferred_name)) {
    		for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(currentlyEditing.getIRI())) {
    			if (ax.getProperty().equals(NCIEditTabConstants.LABEL_PROP)) {
    				changes.add(new RemoveAxiom(ontology, ax));
    				OWLLiteral pref_name_val = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(preferred_name);
    				OWLAxiom ax2 = ontology.getOWLOntologyManager().getOWLDataFactory()
    						.getOWLAnnotationAssertionAxiom(LABEL_PROP, currentlyEditing.getIRI(), pref_name_val);
    				changes.add(new AddAxiom(ontology, ax2));
    			} else if (ax.getProperty().equals(NCIEditTabConstants.PREF_NAME)) {
    				changes.add(new RemoveAxiom(ontology, ax));
    				OWLLiteral pref_name_val = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(preferred_name);
    				OWLAxiom ax2 = ontology.getOWLOntologyManager().getOWLDataFactory()
    						.getOWLAnnotationAssertionAxiom(PREF_NAME, currentlyEditing.getIRI(), pref_name_val);
    				changes.add(new AddAxiom(ontology, ax2));
    				
    			}
    			
    		}   		
    	}
    	ontology.getOWLOntologyManager().applyChanges(changes);
    }
    
    public boolean syncFullSyn(OWLClass cls, OWLAnnotationProperty prop) {
    	OWLAnnotationProperty full_syn = getFullSyn();
    	if (!prop.equals(full_syn)) {
    		return true;
    	}

    	List<OWLAnnotationAssertionAxiom> assertions = new ArrayList<OWLAnnotationAssertionAxiom>();

    	for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(cls.getIRI())) {
    		if (ax.getProperty().equals(full_syn)) {
    			if ((getAnnotationValue(ax, "term-group").equals("PT")) &&
    					(getAnnotationValue(ax, "term-source").equals("NCI"))) {
    				assertions.add(ax);
    			} 
    		}
    	}
    	if (assertions.size() != 1) {
    		JOptionPane.showMessageDialog(this, "One and only one PT with source NCI is allowed.", "Warning", JOptionPane.WARNING_MESSAGE);
    		return false;
    	} else {
    		syncPrefName(assertions.get(0).getValue().asLiteral().get().getLiteral());
    		selectClass(cls);
    		return true;
    	}
    }
    
    // true means there are no dups, all ok    
    public boolean checkForDups(OWLClass cls, OWLAnnotationProperty prop) {
    	
    	List<OWLAnnotationAssertionAxiom> assertions = new ArrayList<OWLAnnotationAssertionAxiom>();
    	
    	Set<HashMap<String, String>> list_ann_vals = new HashSet<HashMap<String, String>>();
    	
    	// round them all up into map
    	for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(cls.getIRI())) {
    		if (ax.getProperty().equals(prop)) {
    			HashMap<String, String> vals = new HashMap<String, String>();
    			
    			vals.put("Value", ax.getValue().asLiteral().toString());
    			
    			Set<OWLAnnotation> anns = ax.getAnnotations();
    			for (OWLAnnotation ann : anns) {
    				if (ann.getProperty().getIRI().getShortForm().equals("Definition_Review_Date") ||
    						ann.getProperty().getIRI().getShortForm().equals("Definition_Reviewer_Name")) {
    					// ignore
    				} else {
    					vals.put(ann.getProperty().getIRI().getShortForm(), ann.getValue().asLiteral().toString());
    					
    				}
    			}
    			list_ann_vals.add(vals);
    		}
    	}
    	return noDups(list_ann_vals);
    }
    
    private boolean noDups(Set<HashMap<String, String>> ann_rows) {
    	
    	for (HashMap<String, String> row : ann_rows) {
    		
    		for (HashMap<String, String> row1 : ann_rows) {
    			// exclude check for row against itself
    			if (!row.equals(row1)) {
    				boolean found_dup = true;
    				for (String row_s : row.keySet()) {
    					if ((row1.get(row_s) != null) &&
    							(row.get(row_s) != null) &&
    							!row1.get(row_s).equals(row.get(row_s))) {
    						found_dup = false;    					
    					}
    				}
    				if (found_dup) {
    					JOptionPane.showMessageDialog(this, "Duplicates not allowed.", "Warning", JOptionPane.WARNING_MESSAGE);
    					return false;
    				}
    			}

    		}
    		
    	}    
    	return true;
    }
    
    String getAnnotationValue(OWLAnnotationAssertionAxiom axiom, String annProp) {
    	Set<OWLAnnotation> anns = axiom.getAnnotations();
    	for (OWLAnnotation ann : anns) {
    		if (ann.getProperty().getIRI().getShortForm().equalsIgnoreCase(annProp)) {
    			return ann.getValue().asLiteral().get().getLiteral();
    		}
    	}
    	return "None";   	
    	
    }
    
    
    
    public OWLAnnotationProperty getFullSyn() {
    	
		return FULL_SYN;
    	
    }
   
    
    
    
}
