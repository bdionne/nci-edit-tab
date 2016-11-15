package gov.nih.nci.utils.batch;

import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.NCIEditTab;

public class RolesProcessor extends EditProcessor {

	Vector<String> supportedRoles = null;
	Vector<String> supportedAssociations = null;
	
	String role_iri = null;
	String mod = null;
	String filler = null;
	String type = null;
	String new_mod = null;
	String new_filler = null;
	String new_type = null;
	
	public RolesProcessor(NCIEditTab t) {
		super(t);
		supportedRoles = tab.getSupportedRoles();
		supportedAssociations = tab.getSupportedAssociations();
	}

	public Vector<String> validateData(Vector<String> v) {
		Vector<String> w = super.validateData(v);

		if (classToEdit != null) {
			try {

				role_iri = (String) v.elementAt(2);
				mod = (String) v.elementAt(3);
				filler = (String) v.elementAt(4);
				type = (String) v.elementAt(5);
				
				

				if (!supportedRoles.contains(role_iri)) {
					String error_msg = " -- role " + role_iri
							+ " is not identifiable.";
					w.add(error_msg);
					return w;
				}
				
				boolean role_exists = hasRole(classToEdit, role_iri, mod, filler, type);

				switch (operation) {
				case DELETE:
					if (!role_exists) {
						w.addElement(roleError(role_iri, mod, filler, type, "does not exist."));
					}
					break;
				case MODIFY:
					new_mod = (String) v.elementAt(6);
					new_filler = (String) v.elementAt(7);
					new_type = (String) v.elementAt(8);
					
					if (!role_exists) {
						w.addElement(roleError(role_iri, mod, filler, type, "does not exist."));
					}
					
					boolean new_role_exists = hasRole(classToEdit, role_iri, new_mod, new_filler, new_type); 
					
					if (new_role_exists) {
						w.addElement(roleError(role_iri, new_mod, new_filler, new_type, "already exists."));
						
					}					
					break;
				case NEW:
					if (role_exists) {
						w.addElement(roleError(role_iri, mod, filler, type, "already exists."));
					}					
					break;
				default:
					break;
				}
			} catch (Exception e) {
				w.add("Exception caught" + e.toString());
			}
		}

		return w;
	}

	public boolean processData(Vector<String> data) {
		switch(operation) {
		case DELETE:
			tab.removeRole(classToEdit, role_iri, mod, filler, type);
			break;
		case MODIFY:
			tab.removeRole(classToEdit, role_iri, mod, filler, type);
			tab.addRole(classToEdit, role_iri, mod, filler, type);
			break;
		case NEW:
			tab.addRole(classToEdit, role_iri, mod, filler, type);
			break;
		default:
			break;			
		}
		return true;
	}
	
	private String roleError(String role_iri, String mod, String filler, String type, String msg)  {
		String error_msg = " -- role " + "("
				+ role_iri + ", "
				+ mod + ", "
				+ filler + ", "
				+ type + ") " + msg;
		return error_msg;
		}
	
	

	private boolean hasRole(OWLClass cls, String roleName, String modifier, String filler_iri, String type) {
		return true;
	}
	

}
