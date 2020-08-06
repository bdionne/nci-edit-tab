package gov.nih.nci.api;

import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public interface RuleService {

	public boolean isQualsPTNCI(OWLAnnotationAssertionAxiom ax);
	
	public boolean isDefNCI(OWLAnnotationAssertionAxiom ax);
	
	public boolean isNCIPtFullSyn(String prop_iri, Map<String, String> qualifiers);
	
	public boolean isFullSynSavable(OWLClass cls, OWLOntology ontology, OWLAnnotationProperty full_syn, List message);
	
}
