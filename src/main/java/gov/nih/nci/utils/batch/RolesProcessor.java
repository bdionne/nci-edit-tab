package gov.nih.nci.utils.batch;

import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.NCIEditTab;

public class RolesProcessor extends EditProcessor {

	Vector<String> supportedRoles = null;
	Vector<String> supportedAssociations = null;
	
	public RolesProcessor(NCIEditTab t) {
		super(t);
		supportedRoles = tab.getSupportedRoles();
		supportedAssociations = tab.getSupportedAssociations();
		// TODO Auto-generated constructor stub
	}

	public Vector<String> validateData(Vector<String> v) {
		return super.validateData(v);
	}

	public boolean processData(Vector<String> data) {
		return true;
	}
	
	

	private boolean hasRole(OWLClass cls, String roleName, OWLClass filler) {
		return true;
	}
	
	private boolean hasAssociation(OWLClass cls, String assocName, OWLClass value) {
		return true;
	}
	

}
