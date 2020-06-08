package gov.nih.nci.api;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

public class NCIPropertyCheck implements RuleService {

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

}
