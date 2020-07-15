package gov.nih.nci.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import gov.nih.nci.curator.CuratorReasonerPreferences;
import gov.nih.nci.curator.utils.RolesVisitor;
import gov.nih.nci.curator.utils.StatedParentVisitor;

public class CuratorChecks {

	private OWLOntology ont;
	
	private StatedParentVisitor spar_visitor;
	
	private RolesVisitor roles_visitor;
	
	private boolean checkRedundantParents = false;
	
	private boolean checkDisjointParents = false;
	
	private boolean checkUnsupportedConstructs = false;
	
	public CuratorChecks(OWLOntology o) {
		ont = o;
		spar_visitor = new StatedParentVisitor(null, ont);
		roles_visitor = new RolesVisitor(null, ont);
		
		checkRedundantParents =
				CuratorReasonerPreferences.getInstance().
				isEnabled(CuratorReasonerPreferences.OptionalEditChecksTask.CHECK_REDUNDANT_PARENT);
		checkDisjointParents =
				CuratorReasonerPreferences.getInstance().
				isEnabled(CuratorReasonerPreferences.OptionalEditChecksTask.CHECK_DISJOINT_CLASSES);
		checkUnsupportedConstructs =
				CuratorReasonerPreferences.getInstance().
				isEnabled(CuratorReasonerPreferences.OptionalEditChecksTask.CHECK_UNSUPPORTED_CONSTRUCTS);
	}
	
	public Set<OWLClass> getStatedParents(OWLClass c) {
		spar_visitor.setEntity(c);
		c.accept(spar_visitor);
		return spar_visitor.parents;
		
	}
	
	public boolean checkOk(List<OWLOntologyChange> changes) {
		
		boolean ok = true;

		for (OWLOntologyChange change : changes) {
			if (change.isAddAxiom()) {
				if (change.isAxiomChange()) {
					OWLAxiom ax = change.getAxiom();
					if (checkOkAxiom(ax, ont)) {
						continue;
					} else {
						return false;
					}
				}
			}
		}

		return ok;

	}
	
	private boolean checkOkAxiom(OWLAxiom ax, OWLOntology ont) {
		if (ax.isOfType(AxiomType.SUBCLASS_OF)) {
			OWLSubClassOfAxiom subax = (OWLSubClassOfAxiom) ax;
			OWLClass cls = subax.getSubClass().asOWLClass(); 
			boolean unsupportedConstructsOk = true;
			boolean redundantParentsOk = true;
			boolean disjointRootsOk = true;

			if (checkUnsupportedConstructs) {
				unsupportedConstructsOk = checkUnsupportedConstructs(cls);

			}
			if (!subax.getSuperClass().isAnonymous()) {
				OWLClass newParent = subax.getSuperClass().asOWLClass();



				if (checkRedundantParents) {
					redundantParentsOk = checkRedundantParents(cls, newParent);

				}

				if (this.checkDisjointParents) {
					disjointRootsOk = checkDisjointRoots(cls);

				}



			}
			return redundantParentsOk &&
					unsupportedConstructsOk &&
					disjointRootsOk;
		}
		return true;
    }
	
	private boolean checkUnsupportedConstructs(OWLClass cls) {
		roles_visitor.setEntity(cls, false);
		cls.accept(roles_visitor);
		if (!roles_visitor.bad_constructs.isEmpty()) {
			if (JOptionPane.showConfirmDialog(null,
					"NCI Curator does not support these language constructs!",
					"Ontology Project Changed",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) ==
					JOptionPane.OK_OPTION) {
				return true;
			} else {
				return false;
			}
			
		}
		return true;
	}
	
	private boolean checkRedundantParents(OWLClass cls, OWLClass newParent) {
		boolean found = false;
		try {
			found = findPath(cls, newParent);
		} catch (Exception ex) {
			found = true;    				
		}
		if (found) {
			// ask user if redundant parent ok or NOT
			if (JOptionPane.showConfirmDialog(null,
					"Asserted parent is already parent of existing parent!",
					"Ontology Project changed",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) ==
					JOptionPane.OK_OPTION) {
				return true;
			} else {
				return false;
			}
		}
		// not found then it's ok
		return !found;
		
	}
	
	private boolean checkDisjointRoots(OWLClass cls) {
		boolean found = false;
		if (findRoots(cls, new HashSet<OWLClass>()).size() > 1 ) {
			if (JOptionPane.showConfirmDialog(null,
					"Adding this parent creates multiple disjoint roots!",
					"Ontology Project changed",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) ==
					JOptionPane.OK_OPTION) {
				return true;
			} else {
				return false;
			}
		}
		return !found;
	}
    
    private Set<OWLClass> findRoots(OWLClass cls, Set<OWLClass> res) {
    	
    	Set<OWLClass> roots = new HashSet<OWLClass>();
    	    	
        Set<OWLClass> assertedParents = getStatedParents(cls);
        
        if (assertedParents.isEmpty()) {
        	roots.add(cls);
        	return roots;        	
        } else {
        	for (OWLClass ap : assertedParents) {
        		Set<OWLClass> rps = findRoots(ap, res);
        		for (OWLClass r : rps) {
        			if (res.contains(r)) {

        			} else {
        				res.add(r);
        			}
        		}
    		}
        	
        }
        return res;
    }
    
    private boolean findPath(OWLClass src, OWLClass target) throws Exception {
    	Set<OWLClass> assertedParents = getStatedParents(src);
    	
		for (OWLClass ap : assertedParents) {
			Set<OWLClass> app = getStatedParents(ap);
			if (app.contains(target)) {
				throw new Exception();				
			} else {
				findPath(ap, target);
			}
		}
		return false;
    	
    }
    
    

}
