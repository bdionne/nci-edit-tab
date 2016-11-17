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

public class ParentRemover extends OwlClassExpressionVisitor {
	
	private OWLModelManager owlModelManager;	
	private OWLDataFactory dataFact;
	private OWLOntology ont;
	
	private OWLClass cls = null;
	private OWLClass par_cls = null;
	
	
	
	
	public ParentRemover(OWLModelManager man) {
		owlModelManager = man;
		dataFact = man.getOWLDataFactory();
	}
		
	public List<OWLOntologyChange> removeParent(OWLClass cls, OWLClass par_cls, String type) {

		this.cls = cls;
		this.par_cls = par_cls;

		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		ont = owlModelManager.getActiveOntology();

		if (type.equalsIgnoreCase("P")) {
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
				} else if (exp.asOWLClass().equals(par_cls)) {
					changes.add(new RemoveAxiom(ont, ax));					
				}
			}
		} else if (type.equalsIgnoreCase("D")) {
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
	
	private OWLClassExpression newExpression;
	
	private OWLClassExpression getNewExpression() {
		return newExpression;
	}

	
	@Override
	public void visit(OWLClass ce) {
		if (ce.equals(par_cls)) {
			newExpression = null;
		} else {
			newExpression = ce;
		}
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
		newExpression = ce;
	}

	@Override
	public void visit(OWLObjectAllValuesFrom ce) {
		newExpression = ce;		
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
