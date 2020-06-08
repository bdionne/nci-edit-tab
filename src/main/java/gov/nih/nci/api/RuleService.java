package gov.nih.nci.api;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

public interface RuleService {

	public boolean isQualsPTNCI(OWLAnnotationAssertionAxiom ax);
	
	public boolean isDefNCI(OWLAnnotationAssertionAxiom ax);
}
