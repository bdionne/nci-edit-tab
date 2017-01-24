package gov.nih.nci.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;

import edu.stanford.protege.csv.export.OwlClassExpressionVisitor;
import gov.nih.nci.ui.NCIEditTab;

public class RoleReplacer extends OwlClassExpressionVisitor {
	
	private OWLModelManager owlModelManager;	
	private OWLDataFactory dataFact;
	private OWLOntology ont;
	
	String roleName = null;
	String modifier = null;
	String filler = null;	
	String new_filler = null;
	
	private static String MOD = "modify";
	private static String REMOVE = "remove";
	private String operation = null;
	
	
	public RoleReplacer(OWLModelManager man) {
		owlModelManager = man;
		dataFact = man.getOWLDataFactory();
	}
		
	public List<OWLOntologyChange> removeRole(OWLClass cls, String roleName, String modifier, String filler) {

		operation = REMOVE;
		this.roleName = roleName;
		this.modifier = modifier;
		this.filler = filler;

		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		ont = owlModelManager.getActiveOntology();


		Set<OWLSubClassOfAxiom> sub_axioms = ont.getSubClassAxiomsForSubClass(cls);

		for (OWLSubClassOfAxiom ax : sub_axioms) {
			OWLClassExpression exp = ax.getSuperClass();

			if (exp.isAnonymous()) {
				OWLClassExpression child = ax.getSubClass();
				OWLClassExpression new_exp = null;
				exp.accept(this);
				new_exp = this.getNewExpression();
				if (new_exp != null) {
					changes.add(new RemoveAxiom(ont, ax));
					OWLSubClassOfAxiom new_ax = dataFact.getOWLSubClassOfAxiom(child, new_exp);
					changes.add(new AddAxiom(ont, new_ax));
				} else {
					// exp may have been a singleton
					changes.add(new RemoveAxiom(ont, ax));						
				}
			}
		}

		if (changes.isEmpty()) {

			Set<OWLEquivalentClassesAxiom> eq_axioms = ont.getEquivalentClassesAxioms(cls);

			for (OWLEquivalentClassesAxiom eq : eq_axioms) {
				changes.add(new RemoveAxiom(ont, eq));
				Set<OWLClassExpression> newExps = new HashSet<OWLClassExpression>();
				for (OWLClassExpression eq_exp : eq.getClassExpressions()) {
					eq_exp.accept(this);
					if (getNewExpression() != null) {
						newExps.add(getNewExpression());
					}
				}
				if (newExps.size() > 1) {
					OWLEquivalentClassesAxiom new_ax = dataFact.getOWLEquivalentClassesAxiom(newExps);
					changes.add(new AddAxiom(ont, new_ax));	
				}
			}
		}


		return changes;

	}
	
	// The assumption in modify is that the old and the new are of the same time, so they reamin in the same
	// class expression
	public List<OWLOntologyChange> modifyRole(OWLClass cls, String roleName, String modifier, String filler,
			String new_modifier, String new_filler) {

		operation = MOD;

		this.roleName = roleName;
		this.modifier = modifier;
		this.filler = filler;
		this.new_filler = new_filler;

		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

		ont = owlModelManager.getActiveOntology();


		Set<OWLSubClassOfAxiom> sub_axioms = ont.getSubClassAxiomsForSubClass(cls);

		for (OWLSubClassOfAxiom ax : sub_axioms) {
			OWLClassExpression exp = ax.getSuperClass();

			if (exp.isAnonymous()) {
				OWLClassExpression child = ax.getSubClass();
				OWLClassExpression new_exp = null;
				exp.accept(this);
				new_exp = this.getNewExpression();
				if (new_exp != null) {
					changes.add(new RemoveAxiom(ont, ax));
					OWLSubClassOfAxiom new_ax = dataFact.getOWLSubClassOfAxiom(child, new_exp);
					changes.add(new AddAxiom(ont, new_ax));
				}
			}
		}
		if (changes.isEmpty()) {

			Set<OWLEquivalentClassesAxiom> eq_axioms = ont.getEquivalentClassesAxioms(cls);

			for (OWLEquivalentClassesAxiom eq : eq_axioms) {
				changes.add(new RemoveAxiom(ont, eq));
				Set<OWLClassExpression> newExps = new HashSet<OWLClassExpression>();
				for (OWLClassExpression eq_exp : eq.getClassExpressions()) {
					eq_exp.accept(this);
					newExps.add(getNewExpression());
				}
				OWLEquivalentClassesAxiom new_ax = dataFact.getOWLEquivalentClassesAxiom(newExps);
				changes.add(new AddAxiom(ont, new_ax));				
			}
		}
		return changes;
	}
	
	
	private OWLClassExpression newExpression;
	
	private OWLClassExpression getNewExpression() {
		return newExpression;
	}

	
	@Override
	public void visit(OWLClass ce) {
		newExpression = ce;
		
	}
	
	@Override
	public void visit(OWLObjectIntersectionOf ce) {
		Set<OWLClassExpression> newConjs = new HashSet<OWLClassExpression>();

		Set<OWLClassExpression> conjs = ce.asConjunctSet();
		for (OWLClassExpression c : conjs) {
			c.accept(this);
			if (getNewExpression() != null) {
				newConjs.add(getNewExpression());
			}
		}
		newExpression = dataFact.getOWLObjectIntersectionOf(newConjs);
	}

	@Override
	public void visit(OWLObjectUnionOf ce) {
		Set<OWLClassExpression> newDisjs = new HashSet<OWLClassExpression>();

		Set<OWLClassExpression> disjs = ce.asDisjunctSet();
		for (OWLClassExpression c : disjs) {
			c.accept(this);
			if (getNewExpression() != null) {
				newDisjs.add(getNewExpression());
			}
		}
		newExpression = dataFact.getOWLObjectIntersectionOf(newDisjs);		
	}

	@Override
	public void visit(OWLObjectComplementOf ce) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OWLObjectSomeValuesFrom ce) {
		if (ce.getProperty().asOWLObjectProperty().getIRI().getShortForm().equals(roleName) &&
				(ce.getFiller().asOWLClass().getIRI().getShortForm().equals(filler)) &&
				(modifier.equals("some"))) {
			if (operation.equals(MOD)) {

				newExpression = dataFact.getOWLObjectSomeValuesFrom(ce.getProperty(), 
						NCIEditTab.currentTab().getClass(new_filler));
			} if (operation.equals(REMOVE)) {
				newExpression = null;				
			} else {
				newExpression = ce;
			}

		} else {
			newExpression = ce;
		}
	}

	@Override
	public void visit(OWLObjectAllValuesFrom ce) {
		if (ce.getProperty().asOWLObjectProperty().getIRI().getShortForm().equals(roleName) &&
				(ce.getFiller().asOWLClass().getIRI().getShortForm().equals(filler)) &&
				(modifier.equals("only"))) {
			
			if (operation.equals(MOD)) {

				newExpression = dataFact.getOWLObjectAllValuesFrom(ce.getProperty(), 
						NCIEditTab.currentTab().getClass(new_filler));
			} if (operation.equals(REMOVE)) {
				newExpression = null;				
			} else {
				newExpression = ce;
			}

		} else {
			newExpression = ce;
		}
		
		
	}



	@Override
	public void visit(OWLObjectHasValue ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLObjectMinCardinality ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLObjectExactCardinality ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLObjectMaxCardinality ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLObjectHasSelf ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLObjectOneOf ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLDataSomeValuesFrom ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLDataAllValuesFrom ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLDataHasValue ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLDataMinCardinality ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLDataExactCardinality ce) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visit(OWLDataMaxCardinality ce) {
		// TODO Auto-generated method stub
		
	}




}
