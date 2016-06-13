package gov.nih.nci.ui;

import static org.semanticweb.owlapi.search.Searcher.annotationObjects;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.client.ClientSession;
import org.protege.editor.owl.client.LocalHttpClient;
import org.protege.editor.owl.client.api.Client;
import org.protege.editor.owl.client.api.exception.ClientRequestException;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.history.HistoryManager;
import org.protege.editor.owl.server.api.exception.AuthorizationException;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;


import gov.nih.nci.ui.event.ComplexEditType;
import gov.nih.nci.ui.event.EditTabChangeEvent;
import gov.nih.nci.ui.event.EditTabChangeListener;
import gov.nih.nci.utils.MetaprojectMock;

public class NCIEditTab extends OWLWorkspaceViewsTab {
	private static final Logger log = Logger.getLogger(NCIEditTab.class);
	private static final long serialVersionUID = -4896884982262745722L;
	
	private static NCIEditTab tab;
	
	public static NCIEditTab currentTab() {
		return tab;
	}
	
	private OWLClass split_source;
	private OWLClass split_target;
	
	public OWLClass getSplitSource() {
		return split_source;
	}
	
	public OWLClass getSplitTarget() {
		return split_target;
	}
		
	// use undo/redo facility
	private HistoryManager history;
	
	private MetaprojectMock mock;
	
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
	
	
	/** create a map of property names, .ie. their rdfs:labels, in order to look up properties
	 * to populate specific collections
	 * 
	 */
	private Map<String, OWLAnnotationProperty> label_map = new HashMap<String, OWLAnnotationProperty>();
	
	private Set<OWLAnnotationProperty> complex_properties = new HashSet<OWLAnnotationProperty>();
	
	private Map<OWLAnnotationProperty, Set<OWLAnnotationProperty>> required_annotation_dependencies =
			new HashMap<OWLAnnotationProperty, Set<OWLAnnotationProperty>>();

	private Map<OWLAnnotationProperty, Set<OWLAnnotationProperty>> optional_annotation_dependencies =
			new HashMap<OWLAnnotationProperty, Set<OWLAnnotationProperty>>();
	
	private Set<OWLAnnotationProperty> immutable_properties = new HashSet<OWLAnnotationProperty>();
			

	public NCIEditTab() {
		setToolTipText("Custom Editor for NCI");
		tab = this;
	}
	
	public Set<OWLAnnotationProperty> getComplexProperties() {
		return complex_properties;
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
		 */
		ont_listen = new OWLModelManagerListener() {

			@Override
			public void handleChange(OWLModelManagerChangeEvent event) {
				if (event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED) {
					ontology = getOWLModelManager().getActiveOntology();
					initProperties();
				}				
			}			
		};
		
		this.getOWLEditorKit().getOWLModelManager().addListener(ont_listen);
		
		// assuming we can gain access to the metaproject at this point
		// and the project will have been selected, etc...
		mock = new MetaprojectMock();	
				
		history = this.getOWLModelManager().getHistoryManager();
		
		
	}
    
    /** Anyone can pre-merge so there is no need for a separate operation. A pre-merged class exists
     * as a subclass of the pre-merged root, so we can readily distinguish a pre-merge from a merge
     * and there is no need for a separate operation
     * 
     */
    public boolean canMerge() {
    	return true;
    }
    
    /** Anyone can pre-retire so there is no need for a separate pre-retire step. There is just retire.
     * If the class is already a chle of the "PreRetired" root class, then the retirement is ready for
     * review and only an admin user can do so. 
     * 
     */
    public boolean canRetire() {
    	return true;
    }
    
    public boolean canSplit() {
    	return true;
    }
    
    public void splitClass(OWLClass from, OWLClass newClass) {
    	split_source = from;
    	split_target = newClass;
    	this.fireChange(new EditTabChangeEvent(this, ComplexEditType.SPLIT));   	
    	
    }
    
    public boolean canClone() {
    	return true;
    }

	@Override
	public void dispose() {
		this.getOWLModelManager().removeListener(ont_listen);
		super.dispose();
		log.info("Disposed of NCI Edit Tab");
	}
	
	private void initProperties() {
		Client sess = ClientSession.getInstance(this.getOWLEditorKit()).getActiveClient();
		
		System.out.println("look");		
		
		Set<OWLAnnotationProperty> annProps = ontology.getAnnotationPropertiesInSignature();
		
		/** There must be an easier way to check the rdfs:labels of annotation properties 
		 *  We'll be getting these from IRIs anyway. **/
		for (OWLAnnotationProperty annp : annProps) {
			for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(annp.getIRI()), ontology.getOWLOntologyManager().getOWLDataFactory()
	                .getRDFSLabel())) {
				OWLAnnotationValue av = annotation.getValue();
				com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
				if (ol.isPresent()) {
					label_map.put(ol.get().getLiteral(), annp);
					
				}			
			}
		}
		
		Set<String> cprop_names = mock.getComplexAnnotationProperties();
		populate_collection(cprop_names, complex_properties);
		
		Set<String> ro_pnames = mock.getImmutableAnnotationProperties();
		populate_collection(ro_pnames, immutable_properties);
		
		
		Map<String, Set<String>> ann_deps = mock.getRequiredAnnotationsForAnnotation();
		initialize_dependencies(ann_deps, required_annotation_dependencies);
		
		ann_deps = mock.getOptionalAnnotationAnnotations();
		initialize_dependencies(ann_deps, optional_annotation_dependencies);		
		
	}
	
	private void populate_collection(Set<String> names, Set<OWLAnnotationProperty> props) {
		for (String name : names) {
			OWLAnnotationProperty p = label_map.get(name);
			if (p != null) {
				props.add(p);
			}		
			
		}
		
	}
	
	private void initialize_dependencies(Map<String, Set<String>> ann_deps, 
			Map<OWLAnnotationProperty, Set<OWLAnnotationProperty>> dependency_map) {
		
		for (String ads : ann_deps.keySet()) {
			Set<OWLAnnotationProperty> ps = new HashSet<OWLAnnotationProperty>();
			OWLAnnotationProperty p = label_map.get(ads);
			if (p != null) {
				Set<String> deps = ann_deps.get(ads);
				for (String s : deps) {
					OWLAnnotationProperty p1 = label_map.get(s);
					if (p1 != null) {
						ps.add(p1);

					}
				}
				dependency_map.put(p, ps);

			}
		}
		
		
	}
	
	public Optional<String> getRDFSLabel(OWLAnnotationProperty prop) {
		  
		for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(prop.getIRI()), ontology.getOWLOntologyManager().getOWLDataFactory()
		                 .getRDFSLabel())) {
			OWLAnnotationValue av = annotation.getValue();
		    com.google.common.base.Optional<OWLLiteral> ol = av.asLiteral();
		    if (ol.isPresent()) {
		     return Optional.of(ol.get().getLiteral());
		     
		    }   
		}
		return Optional.empty();		  
		  
	}
	
}
