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
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE_SOURCE;
import static gov.nih.nci.ui.NCIEditTabConstants.MERGE_TARGET;
import static gov.nih.nci.ui.NCIEditTabConstants.PREF_NAME;
import static gov.nih.nci.ui.NCIEditTabConstants.PRE_MERGE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.PRE_RETIRE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.RETIRE_CONCEPTS_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.RETIRE_ROOT;
import static gov.nih.nci.ui.NCIEditTabConstants.SEMANTIC_TYPE;
import static gov.nih.nci.ui.NCIEditTabConstants.SPLIT_FROM;
import static gov.nih.nci.ui.event.ComplexEditType.*;
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
import org.protege.editor.owl.OWLEditorKit;
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
import org.protege.editor.owl.client.event.CommitOperationListener;
import org.protege.editor.owl.client.ui.UserLoginPanel;
import org.protege.editor.owl.client.util.ClientUtils;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.find.OWLEntityFinder;
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
import edu.stanford.protege.search.lucene.tab.engine.QueryType;
import edu.stanford.protege.search.lucene.tab.engine.SearchTabManager;
import edu.stanford.protege.search.lucene.tab.engine.SearchTabResultHandler;
import edu.stanford.protege.search.lucene.tab.ui.LuceneUiUtils;
import gov.nih.nci.ui.action.ComplexOperation;
import gov.nih.nci.ui.dialog.NCIClassCreationDialog;
import gov.nih.nci.ui.dialog.NoteDialog;
import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.EditTabChangeEvent;
import gov.nih.nci.ui.event.EditTabChangeListener;
import gov.nih.nci.utils.NCIClassSearcher;
import gov.nih.nci.utils.ParentRemover;
import gov.nih.nci.utils.ReferenceReplace;
import gov.nih.nci.utils.RoleReplacer;

public class NCIEditTab extends OWLWorkspaceViewsTab implements ClientSessionListener, UndoManagerListener, CommitOperationListener {
	private static final Logger log = Logger.getLogger(NCIEditTab.class);
	private static final long serialVersionUID = -4896884982262745722L;
	
	private static NCIEditTab tab;	
	
	public static NCIEditTab currentTab() {
		return tab;
	}
	
	private static NCIToldOWLClassHierarchyViewComponent navTree = null;
	
	public static void setNavTree(NCIToldOWLClassHierarchyViewComponent t) { navTree = t;}
	
	public void refreshNavTree() {
		navTree.setSelectedEntity(current_op.getCurrentlyEditing());
		navTree.refreshTree();
	}
	
	private Set<OWLAnnotationProperty> annProps = null;
	
	private boolean editInProgress = false;
	private OWLClass currentlySelected = null;
	private boolean isNew = false;
	
	
	private boolean inBatchMode = false;
	private ArrayList<OWLOntologyChange> batch_changes = new ArrayList<OWLOntologyChange>();
	
	private ArrayList<List<String>> batch_history = new ArrayList<List<String>>();
	
	private void addBatchHistory(OWLClass cls, String n, ComplexEditType typ) {
		
		String c = cls.getIRI().getShortForm().toString();
		List<String> rec = new ArrayList<String>();
		rec.add(c);
		rec.add(n);
		rec.add(typ.toString());
		rec.add("");
		
		batch_history.add(rec);
	}
	
	public void applyChanges() {
		if (!batch_changes.isEmpty()) {
			this.getOWLEditorKit().getOWLModelManager().applyChanges(batch_changes);
			this.batch_changes.clear();
			for (List<String> rec : batch_history) {
				this.putHistory(rec.get(0), rec.get(1), rec.get(2), rec.get(3));
			}
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
		current_op.setCurrentlyEditing(cls);
		if (refresh)
			refreshNavTree();
	}
	
	public OWLClass getCurrentlyEditing() { return current_op.getCurrentlyEditing(); }
	
	public OWLClass getCurrentlySelected() { return currentlySelected; }
	
	public void setNew(boolean b) {
		isNew = b;
	}
	
	public boolean isNew() {
		return isNew;
	}
	
	private ComplexOperation current_op = new ComplexOperation();
	
	public void setOp(ComplexEditType op) {
		current_op.setType(op);
	}
	
	public boolean isFree() {
		return (!inComplexOp() && !isEditing());
	}
	
	public void cancelRetire() {
		current_op = new ComplexOperation();
		editInProgress = false;
		refreshNavTree();
	}
	
	public void cancelOp() {
		ComplexEditType t = current_op.getType();
		if (t == SPLIT || t == CLONE) {
			cancelSplit();
		}
		if (t == ComplexEditType.MERGE) {
			cancelMerge();			
		}
		current_op = new ComplexOperation();		
	}
	
	public void cancelSplit() {	
		setEditInProgress(false);
		// go back to previous selected class
		OWLClass s = current_op.getSource();
		setCurrentlyEditing(s, true);
		undoChanges();
		navTree.setSelectedEntity(s);
		current_op.cancelSplit();
		
	}
	
	public void cancelMerge() {		
		
		setEditInProgress(false);
		// go back to previous selected class
		if (current_op.getSource() != null) {			
			navTree.setSelectedEntity(current_op.getSource());			
		}
		
		if (current_op.getTarget() != null) {			
			navTree.setSelectedEntity(current_op.getTarget());			
		}
		undoChanges();
		current_op.cancelMerge();
			
	}
	
	public void completeOp() {
		ComplexEditType t = current_op.getType();
		if (t == SPLIT || t == CLONE) {
			completeSplit();
		}
		if (t == ComplexEditType.MERGE) {
			completeMerge();
			
		}
		current_op = new ComplexOperation();
		
	}
	
	public void completeSplit() {
		setEditInProgress(false);
		navTree.setSelectedEntity(current_op.getSource());
		current_op.completeSplit();
			
	}
	
	public void completeMerge() {
		setEditInProgress(false);
		navTree.setSelectedEntity(current_op.getTarget());
		current_op.completeMerge();
			
	}
	
	public OWLClass getRetireClass() {
		return current_op.getRetireClass();		
	}
	
	
	public void setSource(OWLClass cls) {
		//current_op.setType(ComplexEditType.MERGE);
		current_op.setSource(cls);
	}
	
	public void setTarget(OWLClass cls) {
		//current_op.setType(ComplexEditType.MERGE);
		current_op.setTarget(cls);
	}
	
	public boolean inComplexOp() {
		return current_op.inComplexOp();
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
    	return current_op.readyToMerge();
    }
    
    public boolean canMerge() {
    	return true;
    }    
    
    public boolean merge() {
    	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
    	
    	OWLClass source = current_op.getSource();
    	OWLClass target = current_op.getTarget();
    	
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
	    			setTarget(source);
	    			setSource(temp);
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
    	Optional<String> mergeSourceCode = getCode(current_op.getSource());
    	Optional<String> mergeTargetCode = getCode(current_op.getTarget());
    	if (mergeSourceCode.isPresent() &&
    			mergeTargetCode.isPresent()) {
    		return ((LocalHttpClient) clientSession.getActiveClient()).codeIsLessThan(mergeSourceCode.get(), mergeTargetCode.get());
    	} else {
    		return false;
    	}
    }
    
    List<OWLOntologyChange> finalizeMerge() {
    	
    	OWLClass source = current_op.getSource();
    	OWLClass target = current_op.getTarget();
    	
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
    	
    	OWLClass source = current_op.getSource();
    	OWLClass target = current_op.getTarget();

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
    
    public void completeRetire(Map<OWLAnnotationProperty, Set<String>> fixups) {
    	
    	List<OWLClass> old_parents = new ArrayList<OWLClass>();
    	
    	String user = clientSession.getActiveClient().getUserInfo().getName().toString();
    	String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    	
    	String editornote = "Retired on: " + timestamp + " by " + user;
        String designnote = "Retired on: " + timestamp;
        // TODO: removing prefix, discuss with Gilberto, I think it's unnecessary
        //String prefix = "preretire_annotation"; 
        
        OWLClass class_to_retire = current_op.getRetireClass();
        
    	List<OWLOntologyChange> changes = addNotes(editornote, designnote, class_to_retire);
    	if (changes.isEmpty()) {
    		fireChange(new EditTabChangeEvent(this, ComplexEditType.RETIRE));
    		current_op.setRetireParents(old_parents);
    		return;
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
        		if (exp instanceof OWLClass) {
            		old_parents.add((OWLClass) exp);
            	}
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
        
        current_op.setRetireParents(old_parents);
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
    	
    	if (cls.equals(PRE_RETIRE_ROOT) ||
    			cls.equals(PRE_MERGE_ROOT) ||
    			cls.equals(RETIRE_ROOT) ||
    			cls.equals(RETIRE_CONCEPTS_ROOT)) {
    		return false;    				
    	}

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
    	current_op.setRetireClass(selectedClass);
    	if (isWorkFlowManager()) {
    		current_op.setType(RETIRE);
    	} else {
    		current_op.setType(PRERETIRE);
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
    
    public void resetState() {
    	fireChange(new EditTabChangeEvent(this, ComplexEditType.RESET));
    	current_op = new ComplexOperation();
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
    
    public boolean isRetireRoot(OWLClass cls) {
    	return cls.equals(RETIRE_ROOT);    	
    }
    
    public boolean isRetireConceptsRoot(OWLClass cls) {
    	return cls.equals(RETIRE_CONCEPTS_ROOT);    	
    }
    
    public boolean isRetired(OWLClass cls) {
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
    	navTree.setSelectedEntity(current_op.getRetireClass());
    	navTree.refreshTree();
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.RETIRE));    	
    }
    
    public void completeRetire() {
        current_op = new ComplexOperation();
    }
    
    public boolean isNotSpecialRoot(OWLClass cls) {
    	return !(isPreRetired(cls) || isRetired(cls)
    			|| isPreMerged(cls) || isRetireRoot(cls) 
    			|| isRetireConceptsRoot(cls));
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
    	
    	ComplexEditType type = getCurrentOp().getType();
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
    		OWLClass source = current_op.getSource();
        	OWLClass target = current_op.getTarget();
        	OWLClass currentlyEditing = current_op.getCurrentlyEditing();
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

    		} else if (type == ComplexEditType.DUAL)  {
    			comment = label + "(" +
    					source.getIRI().getShortForm() + " & " +
    					target.getIRI().getShortForm() + ") - " +
    					type.name();

    		} else if (type == ComplexEditType.RETIRE)  {
    			comment = label + "(" +
    					current_op.getRetireClass().getIRI().getShortForm() + ") - " +
    					type.name();

    		}
    	}
    	
		Commit commit = ClientUtils.createCommit(clientSession.getActiveClient(), comment, changes);
		DocumentRevision base = clientSession.getActiveVersionOntology().getHeadRevision();
		CommitBundle commitBundle = new CommitBundleImpl(base, commit);
		ChangeHistory hist = clientSession.getActiveClient().commit(clientSession.getActiveProject(), commitBundle);
		clientSession.getActiveVersionOntology().update(hist);
		// submit history after the commit but before you broadcast the news
		if (!inBatchMode) 
			submitHistory();
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
    
    
    
    public ComplexOperation getCurrentOp() {
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

    	current_op.setSource(selectedClass);
    	current_op.setTarget(newClass);

    	this.fireChange(new EditTabChangeEvent(this, current_op.getType()));
    	
    	setEditInProgress(true);
		setCurrentlyEditing(current_op.getTarget(), true);
		
    	
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
    			addBatchHistory(newClass, prefName.get(), CREATE);
    		} else {
    			mngr.applyChanges(changes);
    		}

    		return newClass;
    	} else {
    		return null;
    	}

    }
        
    public boolean canClone(OWLClass cls) {
    	return isNotSpecialRoot(cls);
    }

	@Override
	public void dispose() {
		clientSession.removeListener(this);
		super.dispose();
		log.info("Disposed of NCI Edit Tab");
	}
	
	public void addListeners() {
		clientSession.addListener(this);
		clientSession.addCommitOperationListener(this);
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
	public void operationPerformed(CommitOperationEvent event) {
		resetHistory();		
	}

    @Override
    public void stateChanged(HistoryManager source) {
    	List<OWLOntologyChange> changes = history.getUncommittedChanges();
    	List<OWLClass>  subjects = findUniqueSubjects(changes);
    	if (!subjects.isEmpty()) {
    		if (current_op.isChangedEditFocus(subjects)) {
    			int result = JOptionPane.showOptionDialog(null, "Class already being edited. Do you want to proceed with this edit?", 
    					"Proceed or stay with existing edit?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
    			if (result == JOptionPane.CANCEL_OPTION) {
    				backOutChange(); 
    				refreshNavTree();
    			} else if (result == JOptionPane.OK_OPTION) {
    				history.stopTalking();
    				undoChanges();
    				history.startTalking();
    				resetState();
    				getOWLModelManager().applyChange(changes.get(changes.size() - 1));  			

    			}
    		} else {
    			setCurrentlyEditing(current_op.getCurrentlyEditing(), true);
    			classModified();
    			
    		}
    	} else {            	
        	setEditInProgress(false);
        	selectClass(getCurrentlyEditing());
        	refreshNavTree();
        	resetState();
    	}
    	
    	
    }
    
    private List<OWLClass> findUniqueSubjects(List<OWLOntologyChange> changes) {
    	List<OWLClass> result = new ArrayList<OWLClass>();
    	for (OWLOntologyChange change : changes) {
    		if (change.isAxiomChange()) {
    			
    			OWLAxiom ax = change.getAxiom();
    			OWLClass subj = null;
    			
    			if (ax instanceof OWLAnnotationAssertionAxiom) {
    				IRI iri = (IRI) ((OWLAnnotationAssertionAxiom) ax).getSubject();
    				Set<OWLEntity> classes = ontology.getEntitiesInSignature(iri);
    				for (OWLEntity ent : classes) {
    					if (ent.isOWLClass()) {
    						subj = ent.asOWLClass();
    						// take the first, it should be unique
    						break;
    					}
    				}    				
    			} else if (ax instanceof OWLSubClassOfAxiom) {
    				subj = ((OWLSubClassOfAxiom) ax).getSubClass().asOWLClass();
    			} else if (ax instanceof OWLEquivalentClassesAxiom) {
    				Set<OWLClassExpression> exps = ((OWLEquivalentClassesAxiom) ax).getClassExpressions();
    				OWLClass tmp = null;
    				boolean found_existing_subject = false;
    				for (OWLClassExpression exp : exps) {
    					if (exp instanceof OWLClass) {
    						if (result.contains(exp.asOWLClass())) {
    							found_existing_subject = true;
    							break;    							
    						} else {
    							tmp = exp.asOWLClass();
    						}
    					}
    				}
    				if (!found_existing_subject) {
    					subj = tmp;
    				}
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
					RETIRE_CONCEPTS_ROOT  = findOWLClass("retired_concepts_root", opts);
					
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
		
		if (inBatchMode) {
			batch_changes.addAll(changes);
			addBatchHistory(cls, this.getRDFSLabel(cls).get(), MODIFY);
		} else {
			getOWLEditorKit().getModelManager().applyChanges(changes);
		}

	}

	public void addParent(OWLClass cls, OWLClass par_cls) {
		OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		
		OWLAxiom ax = df.getOWLSubClassOfAxiom(cls, par_cls);	
				
		changes.add(new AddAxiom(ontology, ax));
		
		if (inBatchMode) {
			batch_changes.addAll(changes);
			addBatchHistory(cls, this.getRDFSLabel(cls).get(), MODIFY);
		} else {
			getOWLEditorKit().getModelManager().applyChanges(changes);
		}

	}
	
	public void removeRole(OWLClass cls, String roleName, String modifier, String filler) {
		RoleReplacer role_rep = new RoleReplacer(getOWLEditorKit().getModelManager());
		List<OWLOntologyChange> changes = role_rep.removeRole(cls, roleName, modifier, filler);
		
		if (inBatchMode) {
			batch_changes.addAll(changes);
			addBatchHistory(cls, this.getRDFSLabel(cls).get(), MODIFY);
		} else {
			getOWLEditorKit().getModelManager().applyChanges(changes);
		}

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
		

		if (inBatchMode) {
			batch_changes.addAll(changes);
			addBatchHistory(cls, this.getRDFSLabel(cls).get(), MODIFY);
		} else {
			getOWLEditorKit().getModelManager().applyChanges(changes);
		}

	}
	
	public void modifyRole(OWLClass cls, String roleName, String modifier, String filler,
			String new_modifier, String new_filler) {
		
			// no type change
			RoleReplacer role_rep = new RoleReplacer(getOWLModelManager());
			List<OWLOntologyChange> changes = role_rep.modifyRole(cls, roleName, modifier, filler, 
					new_modifier, new_filler);
			
			if (inBatchMode) {
				batch_changes.addAll(changes);
				addBatchHistory(cls, this.getRDFSLabel(cls).get(), MODIFY);
			} else {
				getOWLEditorKit().getModelManager().applyChanges(changes);
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
			} else if (is_required(prop)) {
				String def_val = getDefaultValue(getDataType(prop));
				OWLAnnotation new_ann = df.getOWLAnnotation(prop, df.getOWLLiteral(def_val));
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
			if (isRequired(p)) {
				res.add(p.getIRI().getShortForm());
			}
		}
		return res;
	}
	
	private boolean isRequired(OWLAnnotationProperty prop) {
		OWLAnnotationProperty defawlt = this.lookUpShort("required");
		Optional<String> def_prop = this.getPropertyValue(prop, defawlt);
		if (def_prop.isPresent()) {
			return (def_prop.get().equals("true"));

		}
		return false;
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
		if (oobj != null) {
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
		} else {
			return Optional.empty();
		}
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
			if (av instanceof IRI) {
				values.add(av.toString());
			} else {
				com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
				if (ol.isPresent()) {
					values.add(ol.get().getLiteral());
				} 
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

		if (prop.equals(NCIEditTabConstants.PREF_NAME)) {
			syncPrefNameLabelFullSyn(ocl, value, changes);
		}
		

		if (inBatchMode) {
			batch_changes.addAll(changes);
			this.addBatchHistory(ocl, this.getRDFSLabel(ocl).get(), MODIFY);
		} else {
			getOWLEditorKit().getModelManager().applyChanges(changes);
		}
		
	}
	
	public void removeAnnotationFromClass(OWLClass ocl, OWLAnnotationProperty prop, String value) {
		
		OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		
	
		
		OWLAxiom ax = null;
		
		IRI dtyp = this.getDataType(prop);
		if (dtyp.getShortForm().equals("anyURI")) {
			ax = df.getOWLAnnotationAssertionAxiom(prop, ocl.getIRI(), IRI.create(value));			
		} else if (dtyp.getShortForm().equals("string")) {
			ax = df.getOWLAnnotationAssertionAxiom(prop, ocl.getIRI(), df.getOWLLiteral(value));			
		} else {
			ax = df.getOWLAnnotationAssertionAxiom(prop, ocl.getIRI(), df.getOWLLiteral(value,
					df.getOWLDatatype(dtyp)));
			
		}
		
		changes.add(new RemoveAxiom(ontology, ax));
		
		if (inBatchMode) {
			batch_changes.addAll(changes);
			addBatchHistory(ocl, this.getRDFSLabel(ocl).get(), MODIFY);
		} else {
			getOWLEditorKit().getModelManager().applyChanges(changes);
		}
		
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
    		if (complex_prop.equals(getFullSyn()) &&
    				!NCIEditTab.currentTab().validPrefName(ann_vals.get("Value"))) {
    			JOptionPane.showMessageDialog(this, "Preferred name cannot contain ! or ?", "Warning", JOptionPane.WARNING_MESSAGE);
    			return false; 
    		}
    	}

    	if (operation.equalsIgnoreCase(NCIEditTabConstants.EDIT)) {

    		Set<OWLAnnotation> anns = old_axiom.getAnnotations();
    		Set<OWLAnnotation> new_anns = new HashSet<OWLAnnotation>(); 


    		for (OWLAnnotation annax : anns) {
    			String cv = annax.getProperty().getIRI().getShortForm();
    			String new_val = ann_vals.get(cv);
    			if (!new_val.isEmpty()) {

    				OWLAnnotation new_ann = df.getOWLAnnotation(annax.getProperty(), df.getOWLLiteral(new_val));
    				new_anns.add(new_ann); 
    			} 
    		}


    		Set<OWLAnnotationProperty> req_props = this.getConfiguredAnnotationsForAnnotation(complex_prop);

    		for (OWLAnnotationProperty prop : req_props) {
    			String new_val = ann_vals.get(prop.getIRI().getShortForm());

    			if (!new_val.isEmpty()) {
    				OWLAnnotation new_ann = df.getOWLAnnotation(prop, df.getOWLLiteral(new_val));
    				new_anns.add(new_ann);

    			} else {
    				if (is_required(prop)) {
    					JOptionPane.showMessageDialog(this, "Complex property missing required qualifier " +
    							prop.getIRI().getShortForm(), "Warning", JOptionPane.WARNING_MESSAGE);
    					return false; 
    				}
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
    			if (!val.isEmpty()) {
    				OWLAnnotation new_ann = df.getOWLAnnotation(prop, df.getOWLLiteral(val));
    				anns.add(new_ann);

    			}  else {
    				if (is_required(prop)) {
    					JOptionPane.showMessageDialog(this, "Complex property missing required qualifier " +
    							prop.getIRI().getShortForm(), "Warning", JOptionPane.WARNING_MESSAGE);
    					return false; 
    				}
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
    		//this.backOutChange();
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
    	syncPrefNameLabelFullSyn(current_op.getCurrentlyEditing(), preferred_name, changes);
    	ontology.getOWLOntologyManager().applyChanges(changes);
    }
    
    private void syncPrefNameLabelFullSyn(OWLClass cls, String preferred_name, List<OWLOntologyChange> changes) {
    	//retrieve rdfs:label and adjust if needed
    	if (!getRDFSLabel(cls).equals(preferred_name)) {
    		OWLLiteral pref_name_val = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(preferred_name);
    		for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(cls.getIRI())) {
    			if (ax.getProperty().equals(NCIEditTabConstants.LABEL_PROP)) {
    				changes.add(new RemoveAxiom(ontology, ax));
    				
    				OWLAxiom ax2 = ontology.getOWLOntologyManager().getOWLDataFactory()
    						.getOWLAnnotationAssertionAxiom(LABEL_PROP, cls.getIRI(), pref_name_val);
    				changes.add(new AddAxiom(ontology, ax2));
    			} else if (ax.getProperty().equals(NCIEditTabConstants.PREF_NAME)) {
    				changes.add(new RemoveAxiom(ontology, ax));
    				OWLAxiom ax2 = ontology.getOWLOntologyManager().getOWLDataFactory()
    						.getOWLAnnotationAssertionAxiom(PREF_NAME, cls.getIRI(), pref_name_val);
    				changes.add(new AddAxiom(ontology, ax2));
    				
    			} else if (ax.getProperty().equals(NCIEditTabConstants.FULL_SYN)) {
    				if (isQualsPTNCI(ax)) {
    					changes.add(new RemoveAxiom(ontology, ax));
    					
    					OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
    					
    					OWLAxiom new_axiom = 
    							df.getOWLAnnotationAssertionAxiom(NCIEditTabConstants.FULL_SYN, cls.getIRI(), pref_name_val);
    					
    					Set<OWLAnnotation> anns = ax.getAnnotations();    					
    					  					
    					OWLAxiom new_new_axiom = new_axiom.getAxiomWithoutAnnotations().getAnnotatedAxiom(anns);
    							
    					changes.add(new AddAxiom(ontology, new_new_axiom));
    					
    					
    				}
    			}
    			
    		}   		
    	}
    	
    }
    
    private void syncPrefNameLabel(OWLClass cls, String preferred_name, List<OWLOntologyChange> changes) {
    	//retrieve rdfs:label and adjust if needed
    	if (!getRDFSLabel(cls).equals(preferred_name)) {
    		OWLLiteral pref_name_val = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(preferred_name);
    		for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(cls.getIRI())) {
    			if (ax.getProperty().equals(NCIEditTabConstants.LABEL_PROP)) {
    				changes.add(new RemoveAxiom(ontology, ax));
    				
    				OWLAxiom ax2 = ontology.getOWLOntologyManager().getOWLDataFactory()
    						.getOWLAnnotationAssertionAxiom(LABEL_PROP, cls.getIRI(), pref_name_val);
    				changes.add(new AddAxiom(ontology, ax2));
    			} else if (ax.getProperty().equals(NCIEditTabConstants.PREF_NAME)) {
    				changes.add(new RemoveAxiom(ontology, ax));
    				OWLAxiom ax2 = ontology.getOWLOntologyManager().getOWLDataFactory()
    						.getOWLAnnotationAssertionAxiom(PREF_NAME, cls.getIRI(), pref_name_val);
    				changes.add(new AddAxiom(ontology, ax2));    				
    			}    			
    		}   		
    	}    	
    }
    
    private boolean isQualsPTNCI(OWLAnnotationAssertionAxiom ax) {
    	return ((getAnnotationValue(ax, "term-group").equals("PT") ||
				getAnnotationValue(ax, "term-group").equals("AQ") ||
				getAnnotationValue(ax, "term-group").equals("HD")) &&
				getAnnotationValue(ax, "term-source").equals("NCI"));
    	
    }
    
    public boolean syncFullSyn(OWLClass cls, OWLAnnotationProperty prop, List<OWLOntologyChange> changes) {
    	OWLAnnotationProperty full_syn = getFullSyn();
    	if (!prop.equals(full_syn)) {
    		return true;
    	}

    	List<OWLAnnotationAssertionAxiom> assertions = new ArrayList<OWLAnnotationAssertionAxiom>();

    	for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(cls.getIRI())) {
    		if (ax.getProperty().equals(full_syn)) {
    			if (isQualsPTNCI(ax)) {
    				assertions.add(ax);
    			} 
    		}
    	}
    	// check the new change
    	checkPtNciFullSyn(changes, assertions);
    	
    	if ((assertions.size() != 1)) {
    		JOptionPane.showMessageDialog(this, "One and only one PT with source NCI is allowed.", "Warning", JOptionPane.WARNING_MESSAGE);
    		return false;
    	} else {
    		syncPrefNameLabel(cls, assertions.get(0).getValue().asLiteral().get().getLiteral(), changes);
    		selectClass(cls);
    		return true;
    	}
    }
    
    private void checkPtNciFullSyn(List<OWLOntologyChange> changes, List<OWLAnnotationAssertionAxiom> assertions) {
    	for (OWLOntologyChange c : changes) {
    		if (c.isAddAxiom()) {
    			OWLAxiom ax = c.getAxiom();
    			OWLAnnotationAssertionAxiom aax = (OWLAnnotationAssertionAxiom) ax;
    			if (isQualsPTNCI(aax)) {
    				assertions.add(aax);
    				
    			}  			
    		} else if (c.isRemoveAxiom()) {
    			OWLAxiom ax = c.getAxiom();
    			OWLAnnotationAssertionAxiom aax = (OWLAnnotationAssertionAxiom) ax;
    			if (isQualsPTNCI(aax)) {
    				//new action was a delete, so remove one, doesn't matter which
    				assertions.remove(aax);
    				
    			} 
    			
    		}
    	}
    }
    
    public boolean isNCIPtFullSyn(String prop_iri, Map<String, String> qualifiers) {
    	OWLAnnotationProperty p = lookUpShort(prop_iri);
    	boolean isIt = false;
    	if (p.equals(getFullSyn())) {
    		String tg = qualifiers.get("term-group");
    		String ts = qualifiers.get("term-source");
    		if (tg != null &&
    				ts != null &&
    				(tg.equals("PT") ||
    						tg.equals("AQ") ||
    						tg.equals("HD"))
    						&&
    				ts.equals("NCI")) {
    			isIt = true;
    		}
    	}
    	return isIt;
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
    			name.contains("!")) {
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
        	
        	private OWLModelManager mgr;
        	
        	private String searchString;
        	
        	public MySearchTabResultHandler(OWLEditorKit kit, String str) {
        		mgr = kit.getModelManager();
        		searchString = str;
        	}

			public void searchFinished(Collection<OWLEntity> searchResults) {
				OWLEntityFinder finder = mgr.getOWLEntityFinder();
	        	Set<OWLEntity> foundEntities = new HashSet<OWLEntity>();
				Set<OWLEntity> ents = finder.getMatchingOWLEntities(searchString);
        		for (OWLEntity ent : ents) {
        			String cs = mgr.getRendering(ent);
        			String ucs = unescape(cs);
        			if (ucs.toLowerCase().equals(searchString.toLowerCase())) {
        				foundEntities.add(ent);
        			}
        		}

				exists = !foundEntities.isEmpty();
				ready = true;
			}
			
			public boolean exists() { return exists; }
			public boolean ready() { return ready; }
        	
        };
        
        MySearchTabResultHandler srh = new MySearchTabResultHandler(getOWLEditorKit(), name);
        
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
    
    private String unescape(String s) {
    	if (s.startsWith("'") &&
    			s.endsWith("'")) {
    		return s.substring(1, s.length() - 1);
    	}
    	return s;
    }
    
    public void submitHistory() {
    	if (current_op.isRetiring()) {
    		submitRetireHistory();
    	} else if (current_op.isCloning() || current_op.isSplitting() ||
    			current_op.isMerging() || current_op.isDual()) {
    		submitComplexHistory();
    	} else {
    		submitEditHistory();
    	}
    }
    
    
    public void submitComplexHistory() {
    	
    	OWLClass cls = getCurrentOp().getSource();
		OWLClass ref_cls = getCurrentOp().getTarget();
    	
    	String c = getCodeOrIRI(cls);
    	
    	String n = getRDFSLabel(cls).get();
    	String op = getCurrentOp().toString();
    	
    	String ref = getCodeOrIRI(ref_cls);
    	
    	String ref_n = getRDFSLabel(ref_cls).get();
    	if (current_op.isSplitting()) {
    		putHistory(c, n, op, c);
    		putHistory(c, n, op, ref);
    		putHistory(ref, ref_n, ComplexEditType.CREATE.toString(), "");    		
    	} else if (current_op.isCloning()) {
    		putHistory(ref, ref_n, ComplexEditType.CREATE.toString(), "");    		
    	} else if (current_op.isMerging()) {
    		putHistory(ref, ref_n, op, ref);
    		putHistory(c, n, op, ref);
    		putHistory(c, n, ComplexEditType.RETIRE.toString(), "");    		
    	} else if (current_op.isDual()) {
    		putHistory(c, n, ComplexEditType.MODIFY.toString(), "");
    		putHistory(ref, ref_n, ComplexEditType.MODIFY.toString(), "");    		
    	}
    	
    }
    
    public void submitRetireHistory() {
    	OWLClass cls = current_op.getRetireClass();

    	String c = getCodeOrIRI(cls);

    	String n = getRDFSLabel(cls).get();
    	String op = getCurrentOp().toString();
    	String ref = "";
    	if (current_op.getRetireParents() != null) {
    		for (OWLClass clas : current_op.getRetireParents()) {
    			ref = getCodeOrIRI(clas);
    			putHistory(c, n, op, ref);
    		}
    	}
    }
    
    
    public void submitEditHistory() {
    	OWLClass cls = current_op.getCurrentlyEditing();
    	
    	String c = getCodeOrIRI(cls);
    	
    	String n = getRDFSLabel(cls).get();
    	String op = ComplexEditType.MODIFY.toString();
    	if (isNew()) {
    		op = ComplexEditType.CREATE.toString();
    	}
    	String ref = "";
    	putHistory(c, n, op, ref);
    	setNew(false);
    }
    
    private String getCodeOrIRI(OWLClass cls) {
    	String c;
    	Optional<String> cs = getCode(cls);
    	if (cs.isPresent()) {
    		c = cs.get();    		
    	} else {
    	  c = cls.getIRI().getShortForm();
    	}
    	return c;
    }
    

	

    
    
    
}
