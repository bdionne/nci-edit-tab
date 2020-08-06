package gov.nih.nci.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;


public class NCIPropertyCheck implements RuleService {

	@Override
	public boolean isFullSynSavable(OWLClass cls, OWLOntology ontology, OWLAnnotationProperty full_syn, List message) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
    	List<OWLAnnotationAssertionAxiom> assertions = new ArrayList<OWLAnnotationAssertionAxiom>();

    	for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(cls.getIRI())) {
    		if (ax.getProperty().equals(full_syn)) {
    			if (isQualsPTNCI(ax)) {
    				assertions.add(ax);
    			} 
    		}
    	}
    	if ((assertions.size() != 1)) {
    		message.add("Warning");
    		message.add("One and only one PT with source NCI is allowed in Full Syn.");
    		return false;
    	} 
    	message.add(assertions.get(0).getValue().asLiteral().get().getLiteral());
    	return true;
	}
	
	@Override
	public boolean isQualsPTNCI(OWLAnnotationAssertionAxiom ax) {
		return ((getAnnotationValue(ax, "term-group").equals("PT") ||
				getAnnotationValue(ax, "term-group").equals("AQ") ||
				getAnnotationValue(ax, "term-group").equals("HD")) &&
				getAnnotationValue(ax, "term-source").equals("NCI"));
	}

	@Override
	public boolean isDefNCI(OWLAnnotationAssertionAxiom ax) {
		String ann_val = getAnnotationValue(ax, "def-source");
    	if (ann_val.equalsIgnoreCase("none")) {
    		ann_val = getAnnotationValue(ax, "P378");
    		
    	}
    	return ann_val.equalsIgnoreCase("NCI");
	}
	
	private String getAnnotationValue(OWLAnnotationAssertionAxiom axiom, String annProp) {
    	Set<OWLAnnotation> anns = axiom.getAnnotations();
    	for (OWLAnnotation ann : anns) {
    		if (ann.getProperty().getIRI().getShortForm().equalsIgnoreCase(annProp)) {
    			return ann.getValue().asLiteral().get().getLiteral();
    		}
    	}
    	return "None";   	
    	
    }

	@Override
	public boolean isNCIPtFullSyn(String prop_iri, Map<String, String> qualifiers) {
		//OWLAnnotationProperty p = tab.lookUpShort(prop_iri);
    	boolean isIt = false;
    	//if (prop.equals(fullSyn)) {
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
    	return isIt;
	}

}
