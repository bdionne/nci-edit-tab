package gov.nih.nci.ui;

import static org.semanticweb.owlapi.search.Searcher.annotationObjects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.history.HistoryManager;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;

import com.google.common.base.Optional;

import gov.nih.nci.utils.MetaprojectMock;

public class NCIEditTab extends OWLWorkspaceViewsTab {
	private static final Logger log = Logger.getLogger(NCIEditTab.class);
	private static final long serialVersionUID = -4896884982262745722L;
		
	// use undo/redo facility
	private HistoryManager history;
	
	private MetaprojectMock mock;
	
	private OWLOntology ontology;
	
	private OWLModelManagerListener ont_listen;
	
	private Map<String, OWLAnnotationProperty> label_map = new HashMap<String, OWLAnnotationProperty>();
	
	private static Set<OWLAnnotationProperty> complex_properties = new HashSet<OWLAnnotationProperty>();
	
	private static Map<OWLAnnotationProperty, Set<OWLAnnotationProperty>> annotation_dependencies =
			new HashMap<OWLAnnotationProperty, Set<OWLAnnotationProperty>>();
			

	public NCIEditTab() {
		setToolTipText("Custom Editor for NCI");
	}
	
	public static Set<OWLAnnotationProperty> getComplexProperties() {
		return complex_properties;
	}
	
	public static Set<OWLAnnotationProperty> getRequiredAnnotationsForAnnotation(OWLAnnotationProperty annp) {
		return annotation_dependencies.get(annp);
		
	}
	
	

    @Override
	public void initialise() {
		super.initialise();
		log.info("NCI Edit Tab initialized");
		
		ont_listen = new OWLModelManagerListener() {

			@Override
			public void handleChange(OWLModelManagerChangeEvent event) {
				if (event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED) {
					ontology = getOWLModelManager().getActiveOntology();
					initProperties();
				}				
			}			
		};
		
		this.getOWLModelManager().addListener(ont_listen);
		
		mock = new MetaprojectMock();	
				
		history = this.getOWLModelManager().getHistoryManager();
		
	}
    
    /** Anyone can pre-merge so there is no need for a separate operation. A pre-merged class exists
     * as a subclass of the pre-merged root, so we can readily distinguish a pre-merge from a merge
     * and there is no need for a separate operation
     * 
     */
    public static boolean canMerge() {
    	return true;
    }
    
    /** Anyone can pre-retire so there is no need for a separate pre-retire step. There is just retire.
     * If the class is already a chle of the "PreRetired" root class, then the retirement is ready for
     * review and only an admin user can do so. 
     * 
     */
    public static boolean canRetire() {
    	return true;
    }

	@Override
	public void dispose() {
		this.getOWLModelManager().removeListener(ont_listen);
		super.dispose();
		log.info("Disposed of NCI Edit Tab");
	}
	
	private void initProperties() {
		
		
		
		Set<OWLAnnotationProperty> annProps = ontology.getAnnotationPropertiesInSignature();
		
		/** There must be an easier way to check the rdfs:labels of annotation properties **/
		for (OWLAnnotationProperty annp : annProps) {
			for (OWLAnnotation annotation : annotationObjects(ontology.getAnnotationAssertionAxioms(annp.getIRI()), ontology.getOWLOntologyManager().getOWLDataFactory()
	                .getRDFSLabel())) {
				OWLAnnotationValue av = annotation.getValue();
				Optional<OWLLiteral> ol = av.asLiteral();
				if (ol.isPresent()) {
					label_map.put(ol.get().getLiteral(), annp);
					
				}			
			}
		}
		
		Set<String> cprop_names = mock.getComplexAnnotationProperties();
		
		for (String name : cprop_names) {
			OWLAnnotationProperty p = label_map.get(name);
			if (p != null) {
				complex_properties.add(p);
			}		
			
		}
		
		
		
		Map<String, Set<String>> ann_deps = mock.getRequiredAnnotationsForAnnotation();
		
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
				annotation_dependencies.put(p, ps);
					
				}
			}	
		
	}
}
