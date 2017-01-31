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
import static gov.nih.nci.ui.NCIEditTabConstants.FULL_SYN;
import static gov.nih.nci.ui.NCIEditTabConstants.IMMUTABLE_PROPS;
import static gov.nih.nci.ui.NCIEditTabConstants.LABEL_PROP;
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE;
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE_SOURCE;
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE_TARGET;
import static gov.nih.nci.ui.NCIEditTabConstants.PREF_NAME;
import static gov.nih.nci.ui.NCIEditTabConstants.PRE_MERGE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.PRE_RETIRE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.RETIRE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.SEMANTIC_TYPE;
import static gov.nih.nci.ui.NCIEditTabConstants.SPLIT_FROM;
import static gov.nih.nci.ui.event.ComplexEditType.CLONE;
import static gov.nih.nci.ui.event.ComplexEditType.MODIFY;
import static gov.nih.nci.ui.event.ComplexEditType.PREMERGE;
import static gov.nih.nci.ui.event.ComplexEditType.PRERETIRE;
import static gov.nih.nci.ui.event.ComplexEditType.RETIRE;
import static gov.nih.nci.ui.event.ComplexEditType.SPLIT;
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
import org.protege.editor.owl.server.api.CommitBundle;
import org.protege.editor.owl.server.policy.CommitBundleImpl;
import org.protege.editor.owl.server.versioning.Commit;
import org.protege.editor.owl.server.versioning.api.ChangeHistory;
import org.protege.editor.owl.server.versioning.api.DocumentRevision;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.protege.editor.owl.ui.renderer.OWLEntityAnnotationValueRenderer;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.search.lucene.SearchContext;
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
import org.semanticweb.owlapi.model.OWLProperty;
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
import edu.stanford.protege.search.lucene.tab.engine.BasicQuery;
import edu.stanford.protege.search.lucene.tab.engine.FilteredQuery;
import edu.stanford.protege.search.lucene.tab.engine.NegatedQuery;
import edu.stanford.protege.search.lucene.tab.engine.NestedQuery;
import edu.stanford.protege.search.lucene.tab.engine.QueryType;
import edu.stanford.protege.search.lucene.tab.engine.SearchTabManager;
import edu.stanford.protege.search.lucene.tab.engine.SearchTabResultHandler;
import edu.stanford.protege.search.lucene.tab.ui.BasicQueryPanel;
import edu.stanford.protege.search.lucene.tab.ui.MatchCriteria;
import edu.stanford.protege.search.lucene.tab.ui.NegatedQueryPanel;
import edu.stanford.protege.search.lucene.tab.ui.NestedQueryPanel;
import edu.stanford.protege.search.lucene.tab.ui.QueryPanel;
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
	
	private OWLClass source;
	private OWLClass target;
	private OWLClass class_to_retire;
	
	private boolean editInProgress = false;
	private OWLClass currentlySelected = null;
	private OWLClass currentlyEditing = null;
	private boolean isNew = false;
	
	
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
	}
	
	public void setCurrentlyEditing(OWLClass cls, boolean refresh) { 
		currentlyEditing = cls;
		if (refresh)
			refreshNavTree();
	}
	
	public OWLClass getCurrentlyEditing() { return currentlyEditing; }
	
	public OWLClass getCurrentlySelected() { return currentlySelected; }
	
	public void setNew(boolean b) {
		isNew = b;
	}
	
	public boolean isNew() {
		return isNew;
	}
	
	
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
		return (!inComplexOp() && !isEditing());
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
		setCurrentlyEditing(source, true);
		undoChanges();
		navTree.setSelectedEntity(source);
		source = null;
		target = null;
		
	}
	
	public void cancelMerge() {		
		
		setEditInProgress(false);
		// go back to previous selected class
		if (source != null) {			
			navTree.setSelectedEntity(source);			
		}
		
		if (target != null) {			
			navTree.setSelectedEntity(target);			
		}
		undoChanges();
		
		source = null;
		target = null;		
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
		navTree.setSelectedEntity(source);
		source = null;
		target = null;		
	}
	
	public void completeMerge() {
		setEditInProgress(false);
		navTree.setSelectedEntity(target);
		source = null;
		target = null;		
	}
	
	public OWLClass getSplitSource() {
		return source;
	}
	
	public OWLClass getSplitTarget() {
		return target;
	}
	
	public OWLClass getRetireClass() {
		return class_to_retire;		
	}
	
	public OWLClass getMergeSource() {
		return source;
	}
	
	public OWLClass getMergeTarget() {
		return target;
	}
	
	public void setMergeSource(OWLClass cls) {
		current_op = ComplexEditType.MERGE;
		source = cls;
	}
	
	public void setMergeTarget(OWLClass cls) {
		current_op = ComplexEditType.MERGE;
		target = cls;
	}
	
	public boolean inComplexOp() {
		return current_op != null;
	}
		
	// use undo/redo facility
	private SessionRecorder history;
	
	private ClientSession clientSession = null;
	
	public String getUserId() {
		return clientSession.getActiveClient().getUserInfo().getId();
	}
	
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
    	return (this.source != null) &&
    			(this.target != null);
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
    	
    	if (isPreMerged(source)) {
    		
    		Set<OWLAnnotationAssertionAxiom> props = ontology.getAnnotationAssertionAxioms(source.getIRI());    		
    		for (OWLAnnotationAssertionAxiom p : props) {
    			if (p.getProperty().equals(MERGE_TARGET)) {
    				changes.add(new RemoveAxiom(ontology, p));
    			}
    			
    		}
    		props = ontology.getAnnotationAssertionAxioms(target.getIRI());    		
    		for (OWLAnnotationAssertionAxiom p : props) {
    			if (p.getProperty().equals(MERGE_SOURCE)) {
    				changes.add(new RemoveAxiom(ontology, p));
    			}
    			
    		}
    		changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
					df.getOWLSubClassOfAxiom(source, RETIRE_ROOT))); 
			// finalize merge
			// remove parents and roles, OLD_ROLE OLD_PARENT
			// retarget inbound roles and children
			// reuse retire logic for this
			changes.addAll(finalizeMerge());
			changes.add(new AddAxiom(ontology, df.getDeprecatedOWLAnnotationAssertionAxiom(source.getIRI())));
    		
    	} else {
    		if (switchMergeSourceTarget()) {
    			int result = JOptionPane.showOptionDialog(null, "Retiring Concept is created after the Surviving Concept. Do you want to switch them?", 
						"Switch Retiring and Surviving Concept", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (result == JOptionPane.OK_OPTION) {
					OWLClass temp = target;
	    			setMergeTarget(source);
	    			setMergeSource(temp);
	    			this.fireChange(new EditTabChangeEvent(this, ComplexEditType.MERGE));
					return false;
				} 
				
    		} 
    		// TODO:
    		String editornote = "Merge into " + getRDFSLabel(target).get() + "(" + target.getIRI().getShortForm() + ")";
    		editornote += ", " + clientSession.getActiveClient().getUserInfo().getName();
    		
    		String designnote = "See '" + getRDFSLabel(target).get() + "(" + target.getIRI().getShortForm() + ")" + "'";
    		
    		List<OWLOntologyChange> dcs = addNotes(editornote, designnote, source);
    		
    		if (dcs.isEmpty()) {
    			fireChange(new EditTabChangeEvent(this, ComplexEditType.MERGE));
				return false;
    		}
    		
    		changes.addAll(dcs);
    		
    		changes.addAll(mergeAttrs());

    		// if workflow modeler, add MERGE_TARGET/SOURCE props and tree under pre-merged    		
    		if (!isWorkFlowManager()) {

    			OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(MERGE_TARGET, 
    					source.getIRI(), 
    					df.getOWLLiteral(target.getIRI().getShortForm()));
    			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));

    			ax = df.getOWLAnnotationAssertionAxiom(MERGE_SOURCE, 
    					target.getIRI(), 
    					df.getOWLLiteral(source.getIRI().getShortForm()));
    			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));

    			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
    					df.getOWLSubClassOfAxiom(source, PRE_MERGE_ROOT))); 
    		} else {
    			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
    					df.getOWLSubClassOfAxiom(source, RETIRE_ROOT)));
    			changes.add(new AddAxiom(ontology, df.getDeprecatedOWLAnnotationAssertionAxiom(source.getIRI())));
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
    	Optional<String> mergeSourceCode = getCode(source);
    	Optional<String> mergeTargetCode = getCode(target);
    	if (mergeSourceCode.isPresent() &&
    			mergeTargetCode.isPresent()) {
    		return ((LocalHttpClient) clientSession.getActiveClient()).codeIsLessThan(mergeSourceCode.get(), mergeTargetCode.get());
    	} else {
    		return false;
    	}
    }
    
    List<OWLOntologyChange> finalizeMerge() {
    	
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
    	
    	changes.addAll((new ReferenceReplace(getOWLModelManager())).retargetRefs(source, target)); 
    	
    	
    	// from retire panel, refactor
    	Set<OWLSubClassOfAxiom> sub_axioms = ontology.getSubClassAxiomsForSubClass(source);
        
        for (OWLSubClassOfAxiom ax1 : sub_axioms) {
        	OWLClassExpression exp = ax1.getSuperClass();
        	changes = addParentRoleAssertions(changes, exp, source);
        	changes.add(new RemoveAxiom(ontology, ax1));        	
        }
        
        Set<OWLEquivalentClassesAxiom> equiv_axioms = ontology.getEquivalentClassesAxioms(source);
        
        for (OWLEquivalentClassesAxiom ax1 : equiv_axioms) {
        	Set<OWLClassExpression> exps = ax1.getClassExpressions();
        	for (OWLClassExpression exp : exps) {
        		changes = addParentRoleAssertions(changes, exp, source);
        	}
        	changes.add(new RemoveAxiom(ontology, ax1));        	
        }  
        
        Set<OWLAnnotationAssertionAxiom> assocs = ontology.getAnnotationAssertionAxioms((OWLAnnotationSubject) source.getIRI());
        
        for (OWLAnnotationAssertionAxiom ax1 : assocs) {
        	
        	if (isAssociation(ax1.getProperty())) {
        		String val = ax1.getProperty().getIRI().getShortForm() + "|"
        				+ ax1.getValue().asIRI().get().getShortForm();
        		OWLLiteral lit =  df.getOWLLiteral(val);
        		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(DEP_ASSOC, source.getIRI(), lit);
        		changes.add(new AddAxiom(ontology, ax));
        		changes.add(new RemoveAxiom(ontology, ax1));
        	}
        }
    	return changes;    	
    }
    
    public List<OWLOntologyChange> mergeAttrs() {

    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

    	Map<IRI, IRI> replacementIRIMap = new HashMap<>();
    	replacementIRIMap.put(source.getIRI(), target.getIRI());

    	OWLObjectDuplicator dup = new OWLObjectDuplicator(getOWLModelManager().getOWLDataFactory(), replacementIRIMap);            

    	changes.addAll(duplicateClassAxioms(source, dup));            
    	changes.addAll(duplicateAnnotations(source, dup));
    	
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
    
    public List<OWLClass> completeRetire(Map<OWLAnnotationProperty, Set<String>> fixups) {
    	
    	List<OWLClass> old_parents = new ArrayList<OWLClass>();
    	
    	String user = clientSession.getActiveClient().getUserInfo().getName().toString();
    	String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    	
    	String editornote = "Retired on: " + timestamp + " by " + user;
        String designnote = "Retired on: " + timestamp;
        // TODO: removing prefix, discuss with Gilberto, I think it's unnecessary
        //String prefix = "preretire_annotation";        
        
    	List<OWLOntologyChange> changes = addNotes(editornote, designnote, class_to_retire);
    	if (changes.isEmpty()) {
    		fireChange(new EditTabChangeEvent(this, ComplexEditType.RETIRE));			
			return old_parents;    		
    	}
    	
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();    	
        
        Set<OWLSubClassOfAxiom> sub_axioms = ontology.getSubClassAxiomsForSubClass(class_to_retire);
        
        for (OWLSubClassOfAxiom ax1 : sub_axioms) {
        	OWLClassExpression exp = ax1.getSuperClass();
        	if (exp instanceof OWLClass) {
        		old_parents.add((OWLClass) exp);
        	}
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
        
        return old_parents;
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
    	fireChange(new EditTabChangeEvent(this, ComplexEditType.RETIRE));
    }
    
    public void addComplex(OWLClass selectedClass) {
    	fireChange(new EditTabChangeEvent(this, ComplexEditType.ADD_PROP));    	
    }
    
    public void selectClass(OWLClass cls) {
    	currentlySelected = cls;
    	fireChange(new EditTabChangeEvent(this, ComplexEditType.SELECTED));    	
    }
    
    public void classModified() {
    	fireChange(new EditTabChangeEvent(this, ComplexEditType.MODIFY));    	
    }   
    	
    public boolean isWorkFlowManager() { 
    	if (clientSession.getActiveClient() != null) {
    		try {
    			Role wfm = ((LocalHttpClient) clientSession.getActiveClient()).getRole(new RoleIdImpl("mp-project-manager"));
    			return clientSession.getActiveClient().getActiveRoles().contains(wfm);
    		} catch (ClientRequestException e) {
    			e.printStackTrace();
    		}
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
    	//return isSubClass(cls, RETIRE_ROOT);
    	if (cls != null) {
    		OWLAnnotationProperty deprecated = this.lookUpShort("deprecated");
    		Optional<String> bool = getPropertyValue(cls, deprecated);
    		if (bool.isPresent()) {
    			return bool.get().equals("true");
    		}
    	}
    	return false;
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
    	editInProgress = false;
    	navTree.setSelectedEntity(this.class_to_retire);
    	navTree.refreshTree();
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.RETIRE));    	
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
    			
    			fireChange(new EditTabChangeEvent(this, ComplexEditType.COMMIT));
    			
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
    		// TODO: This class could be null
    		String label = "";
    		if (currentlyEditing != null) {
    			label = getRDFSLabel(currentlyEditing).get();
    		}
    		if (type == MODIFY) {
    			comment = label + "(" +
    					currentlyEditing.getIRI().getShortForm() + ") - " +
    					type.name();
    		} else if (type == SPLIT)  {
    			comment = label + "(" +
    					source.getIRI().getShortForm() + " -> " +
    					target.getIRI().getShortForm() + ") - " +
    					type.name();

    		} else if (type == ComplexEditType.MERGE)  {
    			comment = label + "(" +
    					source.getIRI().getShortForm() + " -> " +
    					target.getIRI().getShortForm() + ") - " +
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

    	source = selectedClass;
    	target = newClass;

    	this.fireChange(new EditTabChangeEvent(this, current_op));
    	
    	setEditInProgress(true);
		setCurrentlyEditing(target, true);
		
    	
    	mngr.applyChanges(changes);
    	refreshNavTree();
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
    			batch_changes.addAll(changes);
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
    	List<OWLOntologyChange> changes = history.getUncommittedChanges();
    	List<IRI>  subjects = findUniqueSubjects(changes);
    	if (!subjects.isEmpty()) {
    		OWLClass cls = null;
    		if (getCurrentlyEditing() != null) {
    			IRI currentIRI = getCurrentlyEditing().getIRI();
    			if ((subjects.contains(currentIRI) && subjects.size() > 1 &&
    					!isRetiring() &&
    					!isSplitting() &&
    					!isMerging() &&
    					!isCloning()) ||
    					subjects.size() > 2) {
    				int result = JOptionPane.showOptionDialog(null, "Class already being edited. Do you want to proceed with this edit?", 
    						"Proceed or stay with existing edit?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
    				if (result == JOptionPane.CANCEL_OPTION) {
    					backOutChange(); 
    					refreshNavTree();
    				} else if (result == JOptionPane.OK_OPTION) {
    					history.stopTalking();
    					undoChanges();
    					history.startTalking();
    					setCurrentlyEditing(null, true);
    					getOWLModelManager().applyChange(changes.get(changes.size() - 1));  			
    					
    				}
    			}
    		} else {
    			// not editing anything yet
    			// should only be one subject
    			for (IRI iri : subjects) {
        			Set<OWLEntity> classes = ontology.getEntitiesInSignature(iri);
        			for (OWLEntity ent : classes) {
            			cls = ent.asOWLClass();
            			
            		}
        			
        		}
    			setCurrentlyEditing(cls, true);
        		classModified();
    		} 		
    	
    	} else {
    		if (changes.isEmpty()) {
    			this.setCurrentlyEditing(null, true);
    		}
    	}
    	/**
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
    	**/
    }
    
    private List<IRI> findUniqueSubjects(List<OWLOntologyChange> changes) {
    	List<IRI> result = new ArrayList<IRI>();
    	for (OWLOntologyChange change : changes) {
    		if (change.isAxiomChange()) {
    			
    			OWLAxiom ax = change.getAxiom();
    			IRI subj = null;
    			
    			if (ax instanceof OWLAnnotationAssertionAxiom) {
    				subj = (IRI) ((OWLAnnotationAssertionAxiom) ax).getSubject();
    				System.out.println("The subject is: " + subj);
    				
    			} else if (ax instanceof OWLSubClassOfAxiom) {
    				subj = ((OWLSubClassOfAxiom) ax).getSubClass().asOWLClass().getIRI();
    				System.out.println("The subject is: " + subj);    				
    			}
    			if (subj != null) {
    				if (result.contains(subj)) {
    				} else {
    					result.add(subj);
    				}
    			}
    		}
    	}
    	return result;
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
	
	public boolean checkType(String propName, String value) {
		OWLAnnotationProperty prop = lookUpShort(propName);
		IRI type = getDataType(prop);
		if (type.toString().endsWith("-enum")) {
			List<String> vals = getEnumValues(type);
			return vals.contains(value);			
		}
		// TODO: flesh out with more types as requirements come in
		
		return true;		
	}
	
	public boolean hasParent(OWLClass cls, OWLClass par_cls) {

		Set<OWLSubClassOfAxiom> sub_axioms = ontology.getSubClassAxiomsForSubClass(cls);

		for (OWLSubClassOfAxiom ax1 : sub_axioms) {
			OWLClassExpression exp = ax1.getSuperClass();

			if (getParent(exp, par_cls) != null) {
				return true;
			} 
		}

		// didn't find primitive parent, check defined

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
	
	
	public boolean hasRole(OWLClass cls, String roleName, String mod, String filler) {

		Set<OWLSubClassOfAxiom> sub_axioms = ontology.getSubClassAxiomsForSubClass(cls);

		for (OWLSubClassOfAxiom ax1 : sub_axioms) {
			OWLClassExpression exp = ax1.getSuperClass();

			if (getRole(exp, roleName, mod, filler) != null) {
				return true;
			}        	
		}
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
	
	public void removeParent(OWLClass cls, OWLClass par_cls) {
		ParentRemover par_rem = new ParentRemover(getOWLEditorKit().getModelManager());
		List<OWLOntologyChange> changes = par_rem.removeParent(cls, par_cls);
		getOWLModelManager().applyChanges(changes);

	}

	public void addParent(OWLClass cls, OWLClass par_cls) {
		OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		
		OWLAxiom ax = df.getOWLSubClassOfAxiom(cls, par_cls);	
				
		changes.add(new AddAxiom(ontology, ax));
		
		this.getOWLEditorKit().getModelManager().applyChanges(changes);

	}
	
	public void removeRole(OWLClass cls, String roleName, String modifier, String filler) {
		RoleReplacer role_rep = new RoleReplacer(getOWLEditorKit().getModelManager());
		List<OWLOntologyChange> changes = role_rep.removeRole(cls, roleName, modifier, filler);
		getOWLModelManager().applyChanges(changes);

	}

	public void addRole(OWLClass cls, String roleName, String modifier, String filler) {
		
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
		
		OWLAxiom ax = df.getOWLSubClassOfAxiom(cls, exp);
		
		changes.add(new AddAxiom(ontology, ax));
		
		this.getOWLEditorKit().getModelManager().applyChanges(changes);

	}
	
	public void modifyRole(OWLClass cls, String roleName, String modifier, String filler,
			String new_modifier, String new_filler) {
		
			// no type change
			RoleReplacer role_rep = new RoleReplacer(getOWLModelManager());
			List<OWLOntologyChange> changes = role_rep.modifyRole(cls, roleName, modifier, filler, 
					new_modifier, new_filler);
			this.getOWLEditorKit().getModelManager().applyChanges(changes);
		

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
	
	public List<String> getRequiredQualifiers(String prop_iri) {
		OWLAnnotationProperty prop = this.lookUpShort(prop_iri);
		Set<OWLAnnotationProperty> req_props = getRequiredAnnotationsForAnnotation(prop);
		List<String> res = new ArrayList<String>();
		for (OWLAnnotationProperty p : req_props) {
			res.add(p.getIRI().getShortForm());
		}
		return res;
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
		
		OWLAxiom ax;
		
		IRI type = this.getDataType(prop);
		if (type.getShortForm().equals("anyURI")) {
			IRI val;
			if (value.startsWith("http:")) {
				val = IRI.create(value);				
			} else {
				val = IRI.create(CODE_PROP.getIRI().getNamespace() + value);
			}
			
			ax = df.getOWLAnnotationAssertionAxiom(prop, ocl.getIRI(), val);
		} else {
			OWLLiteral lval = df.getOWLLiteral(value);			
			ax = df.getOWLAnnotationAssertionAxiom(prop, ocl.getIRI(), lval);			
		}	
		
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
		
		IRI iri;
		if (code.startsWith("http:")) {
			iri = IRI.create(code);			
		} else {
			iri = IRI.create(CODE_PROP.getIRI().getNamespace() + code);			
		}
		
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
    	if (operation.equalsIgnoreCase(NCIEditTabConstants.EDIT) ||
    			operation.equalsIgnoreCase(NCIEditTabConstants.ADD)) {
    		if (!NCIEditTab.currentTab().validPrefName(ann_vals.get("Value"))) {
    			JOptionPane.showMessageDialog(this, "Preferred name cannot contain :, !, ? or @", "Warning", JOptionPane.WARNING_MESSAGE);
    			return false; 
    		}
    	}

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
    	//getOWLModelManager().applyChanges(changes);
    	
    	if (checkForDups(cls, complex_prop) && syncFullSyn(cls, complex_prop, changes)) {     		
    		getOWLModelManager().applyChanges(changes);
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
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	syncPrefName(this.currentlyEditing, preferred_name, changes);
    }
    
    private void syncPrefName(OWLClass cls, String preferred_name, List<OWLOntologyChange> changes) {
    	//retrieve rdfs:label and adjust if needed
    	if (!getRDFSLabel(cls).equals(preferred_name)) {
    		for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(cls.getIRI())) {
    			if (ax.getProperty().equals(NCIEditTabConstants.LABEL_PROP)) {
    				changes.add(new RemoveAxiom(ontology, ax));
    				OWLLiteral pref_name_val = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(preferred_name);
    				OWLAxiom ax2 = ontology.getOWLOntologyManager().getOWLDataFactory()
    						.getOWLAnnotationAssertionAxiom(LABEL_PROP, cls.getIRI(), pref_name_val);
    				changes.add(new AddAxiom(ontology, ax2));
    			} else if (ax.getProperty().equals(NCIEditTabConstants.PREF_NAME)) {
    				changes.add(new RemoveAxiom(ontology, ax));
    				OWLLiteral pref_name_val = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(preferred_name);
    				OWLAxiom ax2 = ontology.getOWLOntologyManager().getOWLDataFactory()
    						.getOWLAnnotationAssertionAxiom(PREF_NAME, cls.getIRI(), pref_name_val);
    				changes.add(new AddAxiom(ontology, ax2));
    				
    			}
    			
    		}   		
    	}
    	
    }
    
    public boolean syncFullSyn(OWLClass cls, OWLAnnotationProperty prop, List<OWLOntologyChange> changes) {
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
    		syncPrefName(cls, assertions.get(0).getValue().asLiteral().get().getLiteral(), changes);
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
    
    public boolean validPrefName(String name) {
    	if (name.contains("?") ||
    			name.contains("!") ||
    			name.contains("@") ||
    			name.contains(":")) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    public boolean existsPrefName(String name) {
    	SearchTabManager searchManager = (SearchTabManager) getOWLEditorKit().getSearchManager();
        
        BasicQuery.Factory queryFactory = new BasicQuery.Factory(new SearchContext(getOWLEditorKit()), searchManager);
        
        
        OWLProperty property = NCIEditTabConstants.LABEL_PROP;
        QueryType queryType = QueryType.EXACT_MATCH_STRING;
        String value = name;
        
        BasicQuery basicQuery = queryFactory.createQuery(property, queryType, value);
        
        FilteredQuery.Builder builder = new FilteredQuery.Builder();
        builder.add(basicQuery);
        FilteredQuery userQuery = builder.build(true);
        
        class MySearchTabResultHandler implements SearchTabResultHandler {
        	private boolean exists = false;
        	
        	private boolean ready = false;

			public void searchFinished(Collection<OWLEntity> searchResults) {
				exists = !searchResults.isEmpty();
				ready = true;
			}
			
			public boolean exists() { return exists; }
			public boolean ready() { return ready; }
        	
        };
        
        MySearchTabResultHandler srh = new MySearchTabResultHandler();
        
        searchManager.performSearch(userQuery, srh);
        
        while (!srh.ready()) {
        	try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        
    	return srh.exists(); 
    }

    
    
    
}
