package gov.nih.nci.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.usage.UsageFilter;
import org.protege.editor.owl.ui.usage.UsagePreferences;
import org.semanticweb.owlapi.model.*;

import gov.nih.nci.ui.NCIEditTab;

public class AxiomSorter implements OWLAxiomVisitor, OWLEntityVisitor, OWLPropertyExpressionVisitor {

	private OWLAxiom currentAxiom;

	private Set<UsageFilter> filters = null;

	private OWLEntity entity;

	private Map<OWLEntity, Set<OWLAxiom>> axiomsByEntityMap;

	private Set<OWLAxiom> additionalAxioms = new HashSet<>();

	private OWLModelManager owlModelManager;

	private OWLOntology ont;

	ArrayList<OWLQuantifiedObjectRestriction> osvf = new ArrayList<OWLQuantifiedObjectRestriction>();

	public AxiomSorter(OWLModelManager man) {
		owlModelManager = man;
	}



	public Map<OWLAnnotationProperty, Set<String>> computeAnnotations(OWLClass inst) { 
		
		HashMap<OWLAnnotationProperty, Set<String>> fixups = new HashMap<OWLAnnotationProperty, Set<String>>();

		entity = (OWLEntity) inst;

		filters = UsagePreferences.getInstance().getActiveFilters();

		axiomsByEntityMap = new TreeMap<>(owlModelManager.getOWLObjectComparator());

		ont = owlModelManager.getActiveOntology();

		Set<OWLAxiom> axioms = ont.getReferencingAxioms(inst);
		for (OWLAxiom ax : axioms) {
			setAxiom(ax);
			ax.accept(this);
		}

		for (OWLEntity et : axiomsByEntityMap.keySet()) {
			System.out.println("The entity key " + et);    		
			Set<OWLAxiom> axs = axiomsByEntityMap.get(et);
			for (OWLAxiom ax : axs) {
				System.out.println("An axiom for the key are: " + ax);
				if (ax instanceof OWLEquivalentClassesAxiom) {
					for (OWLClassExpression desc : ((OWLEquivalentClassesAxiom) ax).getClassExpressions()) {
						System.out.println("An exp: " + desc);
						osvf = new ArrayList<OWLQuantifiedObjectRestriction>();
						extractRestrictions(desc, inst);
						for (OWLQuantifiedObjectRestriction os : osvf) {
							System.out.println("OSVF: " + os);
							String val = os.getProperty().asOWLObjectProperty().getIRI().getShortForm() + "|"
									+ "some" + "|" + et.asOWLClass().getIRI().getShortForm();
							fixups = addToFixups(fixups, NCIEditTab.DEP_IN_ROLE, val);
							
							System.out.println("OLD_SOURCE_ROLE:" + val);
						}

					}
				} else if (ax instanceof OWLSubClassOfAxiom) {
					OWLClassExpression exp = ((OWLSubClassOfAxiom) ax).getSuperClass();
					if (exp.isAnonymous()) {
						System.out.println("An exp: " + exp);
						osvf = new ArrayList<OWLQuantifiedObjectRestriction>();
						extractRestrictions(exp, inst);
						for (OWLQuantifiedObjectRestriction os : osvf) {
							System.out.println("OSVFSUBS: " + os);
							String val = os.getProperty().asOWLObjectProperty().getIRI().getShortForm() + "|"
									+ "some" + "|" + et.asOWLClass().getIRI().getShortForm();
							fixups = addToFixups(fixups, NCIEditTab.DEP_IN_ROLE, val);
							System.out.println("OLD_SOURCE_ROLE:" + val );
						}
					} else {
						String val = et.asOWLClass().getIRI().getShortForm();
						fixups = addToFixups(fixups, NCIEditTab.DEP_CHILD, val);
						System.out.println("OLD_CHILD:" + val );
					}

				}
			}

		}

		return fixups;
	}
	
	public HashMap<OWLAnnotationProperty, Set<String>> addToFixups(HashMap<OWLAnnotationProperty, Set<String>> fixups, OWLAnnotationProperty prop, String val) {
		Set<String> ss = fixups.get(prop);
		if (ss == null) {
			ss = new HashSet<String>();
			fixups.put(prop,  ss);
		}
		ss.add(val);
		return fixups;
	}

	public void extractRestrictions(OWLClassExpression exp, OWLClass inst) {
		if (exp instanceof OWLObjectIntersectionOf) {
			OWLObjectIntersectionOf oio = (OWLObjectIntersectionOf) exp;
			Set<OWLClassExpression> conjs = oio.asConjunctSet();
			for (OWLClassExpression c : conjs) {
				extractRestrictions(c, inst);
			}

		} else if (exp instanceof OWLObjectUnionOf) {
			OWLObjectUnionOf oio = (OWLObjectUnionOf) exp;
			Set<OWLClassExpression> conjs = oio.asDisjunctSet();
			for (OWLClassExpression c : conjs) {
				extractRestrictions(c, inst);
			}

		} else if (exp instanceof OWLObjectSomeValuesFrom) {
			OWLObjectSomeValuesFrom svf = (OWLObjectSomeValuesFrom) exp;
			if (svf.getFiller().asOWLClass().equals(inst)) {
				osvf.add((OWLObjectSomeValuesFrom) exp);
			}
		} else if (exp instanceof OWLObjectAllValuesFrom) {
			OWLObjectAllValuesFrom svf = (OWLObjectAllValuesFrom) exp;
			if (svf.getFiller().asOWLClass().equals(inst)) {
				osvf.add((OWLObjectAllValuesFrom) exp);
			}
		}

	}

	private boolean isFilterSet(UsageFilter filter){
		return filters.contains(filter);
	}



	public void setAxiom(OWLAxiom axiom) {
		currentAxiom = axiom;
	}


	private void add(OWLEntity ent) {

		if (isFilterSet(UsageFilter.filterSelf) && entity.equals(ent)) {
			return;
		}
		//usageCount++;
		Set<OWLAxiom> axioms = axiomsByEntityMap.get(ent);
		if (axioms == null) {
			axioms = new HashSet<>();
			axiomsByEntityMap.put(ent, axioms);
		}
		axioms.add(currentAxiom);
	}


	public void visit(OWLClass cls) {
		add(cls);
	}


	public void visit(OWLDatatype dataType) {
		add(dataType);
	}


	public void visit(OWLNamedIndividual individual) {
		add(individual);
	}


	public void visit(OWLDataProperty property) {
		add(property);
	}


	public void visit(OWLObjectProperty property) {
		add(property);
	}


	public void visit(OWLAnnotationProperty property) {
		add(property);
	}


	public void visit(OWLObjectInverseOf property) {
		property.getInverse().accept(this);
	}


	public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLAnnotationAssertionAxiom axiom) {
		if (axiom.getSubject() instanceof IRI){
			IRI subjectIRI = (IRI)axiom.getSubject();
			for (OWLOntology ont : owlModelManager.getActiveOntologies()){
				if (ont.containsClassInSignature(subjectIRI)){
					add(owlModelManager.getOWLDataFactory().getOWLClass(subjectIRI));
				}
				if (ont.containsObjectPropertyInSignature(subjectIRI)){
					add(owlModelManager.getOWLDataFactory().getOWLObjectProperty(subjectIRI));
				}
				if (ont.containsDataPropertyInSignature(subjectIRI)){
					add(owlModelManager.getOWLDataFactory().getOWLDataProperty(subjectIRI));
				}
				if (ont.containsIndividualInSignature(subjectIRI)){
					add(owlModelManager.getOWLDataFactory().getOWLNamedIndividual(subjectIRI));
				}
				if (ont.containsAnnotationPropertyInSignature(subjectIRI)){
					add(owlModelManager.getOWLDataFactory().getOWLAnnotationProperty(subjectIRI));
				}
				if (ont.containsDatatypeInSignature(subjectIRI)){
					add(owlModelManager.getOWLDataFactory().getOWLDatatype(subjectIRI));
				}
			}
		}
	}


	public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		((OWLEntity) axiom.getSubProperty()).accept(this);
	}


	public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
		((OWLEntity) axiom.getProperty()).accept(this);
	}


	public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
		((OWLEntity) axiom.getProperty()).accept(this);
	}


	public void visit(OWLClassAssertionAxiom axiom) {
		if (!axiom.getIndividual().isAnonymous()){
			axiom.getIndividual().asOWLNamedIndividual().accept(this);
		}
	}


	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		if (!axiom.getSubject().isAnonymous()){
			axiom.getSubject().asOWLNamedIndividual().accept(this);
		}
	}


	public void visit(OWLDataPropertyDomainAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLDataPropertyRangeAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLSubDataPropertyOfAxiom axiom) {
		axiom.getSubProperty().accept(this);
	}


	public void visit(OWLDeclarationAxiom axiom) {
		axiom.getEntity().accept(this);
	}


	public void visit(OWLDifferentIndividualsAxiom axiom) {
		for (OWLIndividual ind : axiom.getIndividuals()) {
			if (!ind.isAnonymous()){
				ind.asOWLNamedIndividual().accept(this);
			}
		}
	}


	public void visit(OWLDisjointClassesAxiom axiom) {
		boolean hasBeenIndexed = false;
		if (!isFilterSet(UsageFilter.filterDisjoints)){
			for (OWLClassExpression desc : axiom.getClassExpressions()) {
				if (!desc.isAnonymous()) {
					desc.asOWLClass().accept(this);
					hasBeenIndexed = true;
				}
			}
		}
		if (!hasBeenIndexed){
			additionalAxioms.add(axiom);
			//usageCount++;
		}            
	}


	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		if (!isFilterSet(UsageFilter.filterDisjoints)){
			for (OWLDataPropertyExpression prop : axiom.getProperties()) {
				prop.accept(this);
			}
		}
	}


	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		if (!isFilterSet(UsageFilter.filterDisjoints)){
			for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
				prop.accept(this);
			}
		}
	}


	public void visit(OWLDisjointUnionAxiom axiom) {
		if (!isFilterSet(UsageFilter.filterDisjoints)){
			axiom.getOWLClass().accept(this);
		}
	}


	public void visit(OWLEquivalentClassesAxiom axiom) {
		boolean hasBeenIndexed = false;
		for (OWLClassExpression desc : axiom.getClassExpressions()) {
			if (!desc.isAnonymous()) {
				desc.asOWLClass().accept(this);
				hasBeenIndexed = true;
			}
		}
		if (!hasBeenIndexed){
			additionalAxioms.add(axiom);
			//usageCount++;
		}

	}


	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		for (OWLDataPropertyExpression prop : axiom.getProperties()) {
			prop.accept(this);
		}
	}


	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
			prop.accept(this);
		}
	}


	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
			prop.accept(this);
		}
	}


	public void visit(OWLHasKeyAxiom axiom) {
		//@@TODO implement
	}


	public void visit(OWLDatatypeDefinitionAxiom axiom) {
		axiom.getDatatype().accept(this);
	}


	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		if (!axiom.getSubject().isAnonymous()){
			axiom.getSubject().asOWLNamedIndividual().accept(this);
		}
	}


	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		if (!axiom.getSubject().isAnonymous()){
			axiom.getSubject().asOWLNamedIndividual().accept(this);
		}
	}


	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		if (!axiom.getSubject().isAnonymous()){
			axiom.getSubject().asOWLNamedIndividual().accept(this);
		}
	}


	public void visit(OWLSubPropertyChainOfAxiom axiom) {
		axiom.getSuperProperty().accept(this);
	}


	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLSubObjectPropertyOfAxiom axiom) {
		axiom.getSubProperty().accept(this);
	}


	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLSameIndividualAxiom axiom) {
		for (OWLIndividual ind : axiom.getIndividuals()) {
			if (!ind.isAnonymous()){
				ind.asOWLNamedIndividual().accept(this);
			}
		}
	}


	public void visit(OWLSubClassOfAxiom axiom) {
		if (!axiom.getSubClass().isAnonymous()) {
			if (!isFilterSet(UsageFilter.filterNamedSubsSupers) ||
					(!axiom.getSubClass().equals(entity) && !axiom.getSuperClass().equals(entity))){
				axiom.getSubClass().asOWLClass().accept(this);
			}
		}
		else{
			additionalAxioms.add(axiom);
			//usageCount++;
		}
	}


	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		axiom.getProperty().accept(this);
	}


	public void visit(SWRLRule rule) {
	}
}

