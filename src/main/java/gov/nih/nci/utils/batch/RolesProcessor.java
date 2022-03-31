package gov.nih.nci.utils.batch;

import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.NCIEditTab;

public class RolesProcessor extends EditProcessor {

	Set<String> supportedRoles = null;
	
	String role_iri = null;
	String mod = null;
	String filler = null;
	String new_mod = null;
	String new_filler = null;
	
	public RolesProcessor(NCIEditTab t) {
		super(t);
		supportedRoles = tab.getSupportedRoles();
	}

	public ArrayList<Vector<String>> validateData(Vector<String> v) {
		ArrayList<Vector<String>> err_warn = super.validateData(v);
		
		Vector<String> w = err_warn.get(0);

		if (classToEdit != null) {
			try {

				role_iri = (String) v.elementAt(2);
				mod = (String) v.elementAt(3);
				filler = (String) v.elementAt(4);
				
				

				if (!supportedRoles.contains(role_iri)) {
					String error_msg = " -- role " + role_iri
							+ " is not identifiable.";
					w.add(error_msg);
					return err_warn;
				}
				
				boolean role_exists = hasRole(classToEdit, role_iri, mod, filler);

				switch (operation) {
				case DELETE:
					if (!role_exists) {
						w.addElement(roleError(role_iri, mod, filler, "does not exist."));
					}
					break;
				case MODIFY:
					new_mod = (String) v.elementAt(5);
					new_filler = (String) v.elementAt(6);
					
					if (!role_exists) {
						w.addElement(roleError(role_iri, mod, filler, "does not exist."));
					}
					
					boolean new_role_exists = hasRole(classToEdit, role_iri, new_mod, new_filler); 
					
					if (new_role_exists) {
						w.addElement(roleError(role_iri, new_mod, new_filler, "already exists."));
						
					}					
					break;
				case NEW:
					if (role_exists) {
						w.addElement(roleError(role_iri, mod, filler, "already exists."));
					}					
					break;
				default:
					break;
				}
			} catch (Exception e) {
				w.add("Exception caught" + e.toString());
			}
		}

		return err_warn;
	}

	public boolean processData(Vector<String> data) {
		switch(operation) {
		case DELETE:
			tab.removeRole(classToEdit, role_iri, mod, filler);
			break;
		case MODIFY:
			tab.modifyRole(classToEdit, role_iri, mod, filler, new_mod, new_filler);
			break;
		case NEW:
			tab.addRole(classToEdit, role_iri, mod, filler);
			break;
		default:
			break;			
		}
		return true;
	}
	
	private String roleError(String role_iri, String mod, String filler, String msg)  {
		String error_msg = " -- role " + "("
				+ role_iri + ", "
				+ mod + ", "
				+ filler + ") " + msg;
		return error_msg;
	}
	
	

	private boolean hasRole(OWLClass cls, String roleName, String modifier, String filler_iri) {
		return tab.hasRole(cls, roleName, modifier, filler_iri);
	}
	

}
