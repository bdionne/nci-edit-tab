package gov.nih.nci.utils.batch;

import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.NCIEditTab;

public class ParentsProcessor extends EditProcessor {
	
	String parent_id = null;
	String type = null;
	OWLClass par_class = null;
	
	String new_parent_id = null;
	String new_type = null;
	OWLClass new_par_class = null;

	public ParentsProcessor(NCIEditTab t) {
		super(t);
		// TODO Auto-generated constructor stub
	}
	
	public Vector<String> validateData(Vector<String> v) {
		
		Vector<String> w = super.validateData(v);

		if (classToEdit != null) {
			try {

				parent_id = (String) v.elementAt(2);
				type = (String) v.elementAt(3);
				
				par_class = tab.getClass(parent_id);
				
				if (par_class == null) {
					String error_msg = " -- parent concept " + parent_id
							+ " does not exist.";
					w.add(error_msg);
					return w;
				}
				
				switch (operation) {
				case DELETE:
					if (!tab.hasParent(classToEdit, par_class, type)) {
						String error_msg = " -- parent concept " + parent_id
								+ " does not exist on class.";
						w.add(error_msg);
						return w;
					}
					break;
				case MODIFY:
					if (!tab.hasParent(classToEdit, par_class, type)) {
						String error_msg = " -- parent concept " + parent_id
								+ " does not exist on class.";
						w.add(error_msg);
						return w;
					}
					
					new_parent_id = (String) v.elementAt(4);
					new_type = (String) v.elementAt(5);					
					new_par_class = tab.getClass(new_parent_id);
					
					if (new_par_class == null) {
						String error_msg = " -- new parent concept " + new_parent_id
								+ " does not exist.";
						w.add(error_msg);
						return w;
					} else if (tab.hasParent(classToEdit, new_par_class, new_type)) {
						String error_msg = " -- new parent concept " + parent_id
								+ " already exists as parent on class.";
						w.add(error_msg);
						return w;
						
					}
					break;
				case NEW:
					if (tab.hasParent(classToEdit, par_class, type)) {
						String error_msg = " -- new parent concept " + parent_id
								+ " already exists as parent on class.";
						w.add(error_msg);
						return w;
						
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
			tab.removeParent(classToEdit, par_class, type);
			break;
		case MODIFY:
			tab.removeParent(classToEdit, par_class, type);
			tab.addParent(classToEdit, new_par_class, new_type);
			
			break;
		case NEW:
			tab.addParent(classToEdit, par_class, type);
			break;
		default:
			break;			
		}
		return true;
	}

}
